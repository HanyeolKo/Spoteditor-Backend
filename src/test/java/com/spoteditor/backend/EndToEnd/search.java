package com.spoteditor.backend.EndToEnd;

import com.spoteditor.backend.config.RedisTestConfiguration;
import com.spoteditor.backend.config.TestJwtFilter;
import com.spoteditor.backend.config.TestSecurityConfig;
import com.spoteditor.backend.config.jwt.repository.RefreshTokenRepository;
import com.spoteditor.backend.global.entity.BaseEntity;
import com.spoteditor.backend.modules.place.entity.Address;
import com.spoteditor.backend.modules.placelog.entity.PlaceLog;
import com.spoteditor.backend.modules.placelog.entity.PlaceLogStatus;
import com.spoteditor.backend.modules.placelog.repository.PlaceLogRepository;
import com.spoteditor.backend.modules.placelog.service.PlaceLogPopularityService;
import com.spoteditor.backend.modules.user.entity.User;
import com.spoteditor.backend.modules.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Random;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import({RedisTestConfiguration.class, TestSecurityConfig.class, TestJwtFilter.class})
public class search {

    @Autowired
    private MockMvc mock;

    // 테스트용 MockBean 주입
    @MockBean
    private RefreshTokenRepository refreshTokenRepository;

    /*@BeforeEach
    void given() throws NoSuchFieldException, IllegalAccessException {
        //테스트 유저
        User user = User.builder()
                .name("테스트유저")
                .email("spoteditor@develop.com")
                .build();

        userRepository.save(user);

        PlaceLog placeLog1 = PlaceLog.builder()
                .name("test1")
                .address(new Address("ㅇㅇ", "ㅇㅇ", 1, 1, "서울", "송파구", "")) // Address 임베디드라면 이렇게
                .status(PlaceLogStatus.PUBLIC)
                .user(user) // 필요시 유저도 생성
                .build();
        placeLog1.setViews(randomIndex(1000));

        PlaceLog placeLog2 = PlaceLog.builder()
                .name("test2")
                .address(new Address("ㅇㅇ", "ㅇㅇ", 1, 1, "서울", "중구", "")) // Address 임베디드라면 이렇게
                .status(PlaceLogStatus.PUBLIC)
                .user(user) // 필요시 유저도 생성
                .build();
        placeLog2.setViews(randomIndex(1000));

        Field field = BaseEntity.class.getDeclaredField("createdAt");
        field.setAccessible(true);
        field.set(placeLog1, LocalDateTime.now().minusHours(5));
        field.set(placeLog2, LocalDateTime.now().minusHours(10));

        placeLog1.setPopularityScore(placeLogPopularityService.calculatePopulateScore(placeLog1.getViews(), randomIndex(100), placeLog1.getCreatedAt()));
        placeLog2.setPopularityScore(placeLogPopularityService.calculatePopulateScore(placeLog2.getViews(), randomIndex(100), placeLog2.getCreatedAt()));

        placeLogRepository.save(placeLog1);
        placeLogRepository.save(placeLog2);
    }*/

    @Test
    @DisplayName("주소 검색/인기순")
    void search_address() throws Exception {
        mock.perform(get("/api/search/placelogs/address")
                        .param("sido","서울")
                        .param("bname", "송파구")
                        .param("sort", "POPULARITY"))
                .andExpect(status().isOk())
                .andDo(print());
    }
    @Test
    @DisplayName("주소 검색/최신순")
    void search_address_RECENT() throws Exception {
        mock.perform(get("/api/search/placelogs/address")
                        .param("sido","서울")
                        .param("bname", "송파구")
                        .param("sort", "RECENT"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("이름 검색/인기순")
    void search_name() throws Exception {
        mock.perform(get("/api/search/placelogs/name")
                        .param("name","송파구")
                        .param("sort", "POPULARITY"))
                .andExpect(status().isOk())
                .andDo(print());
    }
    @Test
    @DisplayName("이름 검색/최신순")
    void search_name_recent() throws Exception {
        mock.perform(get("/api/search/placelogs/name")
                        .param("name","송파구")
                        .param("sort", "RECENT"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("인기도순 검색")
    void search_popularity() throws Exception {
        mock.perform(get("/api/placelogs/popularity"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    long randomIndex(int max){
        Random random = new Random();
        return random.nextInt(max)+1;
    }

}
