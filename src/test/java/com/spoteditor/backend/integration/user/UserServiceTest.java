package com.spoteditor.backend.integration.user;

import com.spoteditor.backend.config.RedisTestConfiguration;
import com.spoteditor.backend.modules.follow.repository.FollowRepository;
import com.spoteditor.backend.modules.notification.repository.NotificationRepository;
import com.spoteditor.backend.modules.placebookmark.repository.PlaceBookmarkRepository;
import com.spoteditor.backend.modules.placelog.repository.PlaceLogRepository;
import com.spoteditor.backend.modules.user.entity.User;
import com.spoteditor.backend.modules.user.repository.UserRepository;
import com.spoteditor.backend.modules.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(RedisTestConfiguration.class)
@Transactional
@Slf4j
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PlaceBookmarkRepository placeBookmarkRepository;
    @Autowired
    private FollowRepository followRepository;
    @Autowired
    private PlaceLogRepository placeLogRepository;
    @Autowired
    private NotificationRepository notificationRepository;


    @Test
    @DisplayName("유저 삭제시 연관 데이터가 함께 삭제")
    void 유저_삭제_통합테스트(){

        // 고한열 계정으로 테스트
        User user = userService.getUser(15L).user();

        log.info("삭제전 상태 : ");
        long bookmarkCount = placeBookmarkRepository.findBookmarkedPlaceIdsByUserId(user.getId()).size();
        long followings = followRepository.countFollowing(user.getId());
        long followers = followRepository.countFollower(user.getId());
        long logCount = placeLogRepository.findAllByUser(user).size();
        long notiCount = notificationRepository.findAllByUserIdAndRead(user.getId()).size() + notificationRepository.findAllByUserIdAndUnread(user.getId()).size();

        log.info("북마크: {}", bookmarkCount);
        log.info("팔로잉: {}", followings);
        log.info("팔로워: {}", followers);
        log.info("게시글: {}", logCount);
        log.info("알림: {}", notiCount);

        // when
        userService.deleteUser(user.getId());

        log.info("삭제후 상태 : ");
        bookmarkCount = placeBookmarkRepository.findBookmarkedPlaceIdsByUserId(user.getId()).size();
        followings = followRepository.countFollowing(user.getId());
        followers = followRepository.countFollower(user.getId());
        logCount = placeLogRepository.findAllByUser(user).size();
        notiCount = notificationRepository.findAllByUserIdAndRead(user.getId()).size() + notificationRepository.findAllByUserIdAndUnread(user.getId()).size();

        log.info("북마크: {}", bookmarkCount);
        log.info("팔로잉: {}", followings);
        log.info("팔로워: {}", followers);
        log.info("게시글: {}", logCount);
        log.info("알림: {}", notiCount);

        // then
        User deletedUser = userRepository.findById(user.getId()).orElseThrow();
        assertTrue(deletedUser.isDeleted());
        assertEquals(0, bookmarkCount);
        assertEquals(0, followings);
        assertEquals(0, followers);
        assertEquals(0, logCount);
        assertEquals(0, notiCount);
    }

}
