package com.haichann.url_shortener.clickevent;

import com.haichann.url_shortener.clickevent.entity.ClickEvent;
import com.haichann.url_shortener.clickevent.repository.ClickEventRepo;
import com.haichann.url_shortener.url.entity.UrlShorten;
import com.haichann.url_shortener.url.repository.UrlShortenRepo;
import com.haichann.url_shortener.user.entity.User;
import com.haichann.url_shortener.user.enums.UserStatus;
import com.haichann.url_shortener.user.repository.RoleRepo;
import com.haichann.url_shortener.user.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@RequiredArgsConstructor
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class ClickEventTests {
    private static final String STATS_PATH = "/api/v1/urls/{id}/stats";
    private static final String DESTINATION = "https://example.com/article";

    private final MockMvc mvc;
    private final ClickEventRepo clickEvents;
    private final UrlShortenRepo urls;
    private final UserRepo users;
    private final RoleRepo roles;

    private User owner;
    private UrlShorten link;

    @BeforeEach
    void createLink() {
        String username = UUID.randomUUID().toString();
        owner = users.save(User.builder()
                .username(username)
                .email(username + "@example.com")
                .role(roles.findByRoleName("USER").orElseThrow())
                .status(UserStatus.ACTIVE)
                .emailVerified(false)
                .avatarImageUrl("")
                .build());
        link = createLinkExpiringAt(LocalDateTime.now().plusDays(1));
    }

    @Test
    void successfulRedirectRecordsOneEventAndExpiredRedirectRecordsNone() throws Exception {
        UrlShorten expired = createLinkExpiringAt(LocalDateTime.now().minusDays(1));

        mvc.perform(get("/r/{code}", link.getShortCode()))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", DESTINATION));
        mvc.perform(get("/r/{code}", expired.getShortCode()))
                .andExpect(status().isNotFound());

        mvc.perform(get(STATS_PATH, link.getId()).with(user(owner.getEmail())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseData[*].clicks", containsInAnyOrder(0, 0, 0, 0, 0, 0, 1)));
        mvc.perform(get(STATS_PATH, expired.getId()).with(user(owner.getEmail())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseData[*].clicks", everyItem(is(0))));
    }

    @Test
    void statsGroupByDayAndExcludeOtherLinksAndOlderEvents() throws Exception {
        LocalDate today = LocalDate.now();
        LocalDateTime twoDaysAgo = today.minusDays(2).atTime(12, 0);
        UrlShorten anotherLink = createLinkExpiringAt(LocalDateTime.now().plusDays(1));
        clickEvents.saveAll(List.of(
                event(link, twoDaysAgo),
                event(link, twoDaysAgo.plusHours(1)),
                event(link, today.minusDays(4).atTime(12, 0)),
                event(anotherLink, twoDaysAgo)
        ));

        mvc.perform(get(STATS_PATH, link.getId()).param("days", "4").with(user(owner.getEmail())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseData.length()").value(4))
                .andExpect(jsonPath("$.responseData[1].date").value(twoDaysAgo.toLocalDate().toString()))
                .andExpect(jsonPath("$.responseData[1].clicks").value(2))
                .andExpect(jsonPath("$.responseData[0].clicks").value(0));
    }

    @Test
    void statsDoNotRevealLinksToNonOwners() throws Exception {
        var nonOwner = user("other@example.com");
        mvc.perform(get(STATS_PATH, link.getId()).with(nonOwner))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("RESOURCE_NOT_FOUND"));
        mvc.perform(get(STATS_PATH, Long.MAX_VALUE).with(user(owner.getEmail())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("RESOURCE_NOT_FOUND"));
        mvc.perform(get(STATS_PATH, link.getId()).with(user(owner.getEmail())).param("days", "31"))
                .andExpect(status().isBadRequest());
    }

    private UrlShorten createLinkExpiringAt(LocalDateTime expiration) {
        return urls.save(UrlShorten.builder()
                .user(owner)
                .shortCode(UUID.randomUUID().toString())
                .originalUrl(DESTINATION)
                .expirationDate(expiration)
                .isActive(true)
                .clickCount(10)
                .build());
    }

    private static ClickEvent event(UrlShorten url, LocalDateTime clickedAt) {
        return ClickEvent.builder().urlShorten(url).clickedAt(clickedAt).build();
    }
}
