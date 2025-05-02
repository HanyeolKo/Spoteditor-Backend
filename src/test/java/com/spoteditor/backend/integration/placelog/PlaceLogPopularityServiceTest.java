package com.spoteditor.backend.integration.placelog;

import com.spoteditor.backend.config.RedisConfiguration;
import com.spoteditor.backend.config.jwt.repository.RefreshTokenRepository;
import com.spoteditor.backend.global.entity.BaseEntity;
import com.spoteditor.backend.modules.placelog.entity.PlaceLog;
import com.spoteditor.backend.modules.placelog.entity.PlaceLogStatus;
import com.spoteditor.backend.modules.placelog.repository.PlaceLogRepository;
import com.spoteditor.backend.modules.placelog.service.PlaceLogPopularityRedisService;
import com.spoteditor.backend.modules.placelog.service.PlaceLogPopularityService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.time.LocalDateTime;

import static com.spoteditor.backend.modules.placelog.constants.RedisKey.PLACELOG_POPULARITY_REDIS_PREFIX;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(RedisConfiguration.class)
@Transactional
@Slf4j
public class PlaceLogPopularityServiceTest {

    @Autowired
    private PlaceLogRepository placeLogRepository;

    @Autowired
    private PlaceLogPopularityService placeLogPopularityService;

    @Autowired
    private PlaceLogPopularityRedisService placeLogPopularityRedisService;

    // 테스트용 MockBean 주입
    @MockBean
    private RedissonClient redissonClient;
    @MockBean
    private StringRedisTemplate stringRedisTemplate;
    @MockBean
    private RefreshTokenRepository refreshTokenRepository;
    
    @Test
    @DisplayName("조회수, 북마크 수와 Log 생성일자를 기반으로 감쇠율을 계산하여 전체 게시글의 인기도 점수를 갱신")
    void updateLogPopularityTest(){

        //given
        PlaceLog placeLog1 = PlaceLog.builder()
                .name("test")
                .description("test")
                .status(PlaceLogStatus.PUBLIC)
                .build();

        placeLog1.setViews(100);

        setCreatedAt(placeLog1, LocalDateTime.now().minusHours(5));

        PlaceLog placeLog2 = PlaceLog.builder()
                .name("test2")
                .description("test2")
                .status(PlaceLogStatus.PUBLIC)
                .build();

        placeLog2.setViews(50);
        setCreatedAt(placeLog2, LocalDateTime.now().minusHours(1));

        placeLogRepository.save(placeLog1);
        placeLogRepository.save(placeLog2);

        //when
        placeLogPopularityService.updatePopularityScoreOnRedis();

        //then
        System.out.println("인기순 출력 ㄱ");
        placeLogPopularityRedisService.getAllPopularityList().forEach(System.out::println);


    }

    // 생성일자 강제 설정
    private void setCreatedAt(PlaceLog placeLog, LocalDateTime createdAt) {
        try {
            Field field = BaseEntity.class.getDeclaredField("createdAt");
            field.setAccessible(true);
            field.set(placeLog, createdAt);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
