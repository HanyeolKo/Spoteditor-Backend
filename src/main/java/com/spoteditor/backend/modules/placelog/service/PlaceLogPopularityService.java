package com.spoteditor.backend.modules.placelog.service;

import com.spoteditor.backend.modules.placelog.entity.PlaceLog;
import com.spoteditor.backend.modules.placelog.repository.PlaceLogPopularityRedisRepository;
import com.spoteditor.backend.modules.placelog.repository.PlaceLogRepository;
import com.spoteditor.backend.modules.placelog.service.dto.PlaceLogWithBookmark;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PlaceLogPopularityService {

    private final PlaceLogRepository placeLogRepository;

    private final PlaceLogPopularityRedisRepository placeLogPopularityRedisRepository;

    /**
     * 인기도 스코어 게산
     * @param view  조회수
     * @param bookmark  북마크수
     * @param date  등록일
     * @param decayRate 시간감쇠율
     * @return
     */
    public float calculatePopulateScore(long view, long bookmark, LocalDateTime date, float decayRate) {

        LocalDateTime now = LocalDateTime.now();

        // 게시글 작성일시로 부터 경과 시간(시 단위)
        long hoursSincePosted = ChronoUnit.HOURS.between(date, now);

        // 시간 감쇠율
        float denominator = (float)Math.pow(hoursSincePosted + 2, decayRate);

        // 기본 점수 - view : 1, bookmark : 3
        float numerator = view + (bookmark * 3L);

        // 최종 스코어
        return numerator / denominator;
    }

    public float calculatePopulateScore(long view, long bookmark, LocalDateTime date) {
        return this.calculatePopulateScore(view, bookmark, date, 1.5f);
    }

    @Async
    public void updatePopularityScoreOnRedis(){

        // 게시글 전체 조회
        List<PlaceLogWithBookmark> placeLogWithBookmarks = placeLogRepository.findPlaceLogWithBookmarkCount(Integer.MAX_VALUE, 0);

        placeLogWithBookmarks.forEach(l -> {
            float popularityScore = calculatePopulateScore(l.viewCount(), l.bookmarkCount(), l.createAt());
            placeLogPopularityRedisRepository.updatePopulatorScore(l.placeLogId(), popularityScore);
        });
    }
}
