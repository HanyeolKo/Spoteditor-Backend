package com.spoteditor.backend.modules.placelog.service;

import com.spoteditor.backend.modules.placelog.entity.PlaceLog;
import com.spoteditor.backend.modules.placelog.repository.PlaceLogRepository;
import com.spoteditor.backend.modules.placelog.service.dto.PlaceLogWithBookmark;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PlaceLogPopularityService {

    private final PlaceLogRepository placeRepository;
    private final PlaceLogRepository placeLogRepository;

    private final PlaceLogPopularityRedisService placeLogPopularityRedisService;

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

    // 다시 사용할 여지가 있어 지우지 않고 남겨둠
    /**
     * 전체 로그 게시물의 인기도를 산정하여 업데이트
     * @param loadPerLogCount   한번에 처리할 로그 게시물 갯수(페이징)
     */
    @Transactional
    public void updateAllLogPopularity(int loadPerLogCount){
        int page = 0;
        int pageSize = loadPerLogCount;

        while(true){
            int offset = page * pageSize;
            List<PlaceLogWithBookmark> placeLogWithBookmarks = placeRepository.findPlaceLogWithBookmarkCount(pageSize, offset);

            if(placeLogWithBookmarks.isEmpty()){
                break;
            }

            for(PlaceLogWithBookmark placeLogWithBookmark : placeLogWithBookmarks){
                float popularityScore = calculatePopulateScore(placeLogWithBookmark.viewCount(), placeLogWithBookmark.bookmarkCount(), placeLogWithBookmark.createAt());

                PlaceLog placeLog = placeLogRepository.findById(placeLogWithBookmark.placeLogId()).orElseThrow(
                        () -> new RuntimeException("인기도 업데이트중 찾을 수 없는 로그를 발견했습니다. ID = " + placeLogWithBookmark.placeLogId())
                );
                //placeLog.setPopularityScore(popularityScore);     // 해당컬럼 삭제됨
            }
            page++;
        }
    }

    @Async
    public void updatePopularityScoreOnRedis(){

        // 게시글 전체 조회
        List<PlaceLogWithBookmark> placeLogWithBookmarks = placeRepository.findPlaceLogWithBookmarkCount(Integer.MAX_VALUE, 0);

        placeLogWithBookmarks.forEach(l -> {
            float popularityScore = calculatePopulateScore(l.viewCount(), l.bookmarkCount(), l.createAt());
            placeLogPopularityRedisService.updatePopulatorScore(l.placeLogId(), popularityScore);
        });
    }
}
