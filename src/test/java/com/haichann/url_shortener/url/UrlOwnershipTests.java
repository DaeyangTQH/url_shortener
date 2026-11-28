package com.haichann.url_shortener.url;

import com.haichann.url_shortener.clickevent.service.ClickEventService;
import com.haichann.url_shortener.common.exception.GlobalExceptionHandler;
import com.haichann.url_shortener.config.AppProperties;
import com.haichann.url_shortener.url.controller.RedirectController;
import com.haichann.url_shortener.url.controller.UrlController;
import com.haichann.url_shortener.url.entity.UrlShorten;
import com.haichann.url_shortener.url.helper.ShortCodeGenerator;
import com.haichann.url_shortener.url.mapper.UrlMapper;
import com.haichann.url_shortener.url.repository.UrlShortenRepo;
import com.haichann.url_shortener.url.service.impl.UrlServiceImpl;
import com.haichann.url_shortener.user.entity.User;
import com.haichann.url_shortener.user.repository.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.context.support.StaticMessageSource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UrlOwnershipTests {
    private static final String LINK_PATH = "/api/v1/urls/{id}";
    private static final long OWNED_LINK_ID = 42L;
    private static final long MISSING_LINK_ID = 99L;
    private static final Principal OWNER = principal("owner@example.com");
    private static final Principal OTHER_USER = principal("other@example.com");

    private MockMvc mvc;
    private UrlShortenRepo urls;

    @BeforeEach
    void setUp() {
        User owner = new User();
        owner.setId(1L);
        User otherUser = new User();
        otherUser.setId(2L);
        UserRepo users = mock(UserRepo.class);
        when(users.getUserByEmail(OWNER.getName())).thenReturn(Optional.of(owner));
        when(users.getUserByEmail(OTHER_USER.getName())).thenReturn(Optional.of(otherUser));

        UrlShorten link = UrlShorten.builder()
                .user(owner)
                .shortCode("aB3xY9k")
                .originalUrl("https://example.com/article")
                .isActive(true)
                .build();
        link.setId(OWNED_LINK_ID);
        urls = mock(UrlShortenRepo.class);
        when(urls.findByIdAndUserIdAndIsDeletedFalse(OWNED_LINK_ID, owner.getId()))
                .thenReturn(Optional.of(link));
        when(urls.findByShortCodeAndIsDeletedFalse("aB3xY9k")).thenReturn(Optional.of(link));

        UrlServiceImpl service = new UrlServiceImpl(
                urls, users, new ShortCodeGenerator(), mock(ClickEventService.class),
                Mappers.getMapper(UrlMapper.class),
                new AppProperties("http://localhost:8080", new AppProperties.OAuth2("http://localhost:5173"))
        );
        mvc = MockMvcBuilders.standaloneSetup(new UrlController(service), new RedirectController(service))
                .setControllerAdvice(new GlobalExceptionHandler(new StaticMessageSource()))
                .build();
    }

    @Test
    void anotherUsersLinkAndMissingLinkBothReturnNotFound() throws Exception {
        for (long id : new long[]{OWNED_LINK_ID, MISSING_LINK_ID}) {
            assertNotFound(get(LINK_PATH, id));
            assertNotFound(delete(LINK_PATH, id));
        }
    }

    @Test
    void ownerCanStillViewAndDeactivateLink() throws Exception {
        mvc.perform(get(LINK_PATH, OWNED_LINK_ID).principal(OWNER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseData.id").value(OWNED_LINK_ID));
        mvc.perform(delete(LINK_PATH, OWNED_LINK_ID).principal(OWNER))
                .andExpect(status().isOk());
    }

    @Test
    void redirectsWhenTheClickIsCounted() throws Exception {
        when(urls.incrementClicksIfActive(eq(OWNED_LINK_ID), any(LocalDateTime.class))).thenReturn(1);

        mvc.perform(get("/r/aB3xY9k"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "https://example.com/article"));
    }

    @Test
    void doesNotRedirectWhenLinkBecameUnavailableDuringUpdate() throws Exception {
        mvc.perform(get("/r/aB3xY9k"))
                .andExpect(status().isNotFound())
                .andExpect(header().doesNotExist("Location"));
    }

    private void assertNotFound(MockHttpServletRequestBuilder request) throws Exception {
        mvc.perform(request.principal(OTHER_USER))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("RESOURCE_NOT_FOUND"));
    }

    private static Principal principal(String email) {
        return UsernamePasswordAuthenticationToken.authenticated(email, null, List.of());
    }
}
