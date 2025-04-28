package com.spoteditor.backend.modules.placelog.scheduler;

import com.spoteditor.backend.modules.placelog.service.PlaceLogPopularityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class PopularityScheduler {

    private final PlaceLogPopularityService placeLogPopularityService;

    /**
     * 전체 게시글의 인기도 갱신
     */
    @Scheduled(fixedRate = 5 * 60 * 1000)   // 5분
    public void updatePopularity(){
        log.info("로그 인기도 갱신중...");

        placeLogPopularityService.updateAllLogPopularity(500);

        log.info("로그 인기도 갱신 완료.");
    }

}
