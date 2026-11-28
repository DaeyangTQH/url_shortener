package com.haichann.url_shortener.clickevent.repository;

import com.haichann.url_shortener.clickevent.dto.DailyClickCount;
import com.haichann.url_shortener.clickevent.entity.ClickEvent;
import com.haichann.url_shortener.common.base.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ClickEventRepo extends BaseRepository<ClickEvent, Long> {
    @Query("""
            select new com.haichann.url_shortener.clickevent.dto.DailyClickCount(
                cast(event.clickedAt as LocalDate), count(event))
            from ClickEvent event
            where event.urlShorten.id = :urlId
              and event.clickedAt >= :start
              and event.clickedAt < :end
            group by cast(event.clickedAt as LocalDate)
            order by cast(event.clickedAt as LocalDate)
            """)
    List<DailyClickCount> countByDay(
            @Param("urlId") Long urlId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}
