package com.spoteditor.backend.modules.placelog.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.spoteditor.backend.modules.placelog.constants.RedisKey.PLACELOG_POPULARITY_REDIS_PREFIX;

@Service
@RequiredArgsConstructor
public class PlaceLogPopularityRedisService {

    private final StringRedisTemplate stringRedisTemplate;

    public void updatePopulatorScore(long id, float socre){
        stringRedisTemplate.opsForZSet().add(PLACELOG_POPULARITY_REDIS_PREFIX, String.valueOf(id), socre);
    }

    public List<Long> getAllPopularityList(){
        return getPopularityList(0);
    }

    /**
     * 인기순 Place Log id 리스트
     * @param top 상위부터 가져올 갯수
     * @return
     */
    public List<Long> getPopularityList(int top){
        return stringRedisTemplate.opsForZSet().reverseRange(PLACELOG_POPULARITY_REDIS_PREFIX, 0, top - 1)
                .stream()
                .map(Long::valueOf)
                .toList();
    }

}
