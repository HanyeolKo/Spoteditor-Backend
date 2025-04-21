package com.spoteditor.backend.modules.user.service;

import com.spoteditor.backend.modules.follow.repository.FollowRepository;
import com.spoteditor.backend.global.exception.ImageException;
import com.spoteditor.backend.global.exception.UserException;
import com.spoteditor.backend.modules.image.controller.dto.PlaceImageResponse;
import com.spoteditor.backend.modules.image.entity.PlaceImage;
import com.spoteditor.backend.modules.image.event.S3ImageAfterCommitEvent;
import com.spoteditor.backend.modules.image.event.S3ImageRollbackEvent;
import com.spoteditor.backend.modules.image.event.dto.S3Image;
import com.spoteditor.backend.modules.image.repository.PlaceImageRepository;
import com.spoteditor.backend.modules.image.service.PlaceImageService;
import com.spoteditor.backend.modules.user.entity.User;
import com.spoteditor.backend.modules.user.repository.UserRepository;
import com.spoteditor.backend.modules.user.service.dto.OtherUserResult;
import com.spoteditor.backend.modules.user.service.dto.UserResult;
import com.spoteditor.backend.modules.user.service.dto.UserUpdateCommand;
import com.spoteditor.backend.modules.user.service.dto.UserUpdateResult;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.spoteditor.backend.global.response.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final PlaceImageService imageService;
    private final PlaceImageRepository placeImageRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public UserResult getUser(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(NOT_FOUND_USER));

        if(user.isDeleted()) {
            throw new UserException(DELETED_USER);
        }

        Long follower = followRepository.countFollower(userId);
        Long following = followRepository.countFollowing(userId);

        return new UserResult(user, follower, following);
    }

    @Override
    @Transactional
    public OtherUserResult getOtherUser(Long userId, Long otherUserId) {

        UserResult userResult = getUser(otherUserId);
        boolean isFollowing;

        if(userId == null) {
            isFollowing = false;
        }
        else {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new UserException(NOT_FOUND_USER));

            if(user.isDeleted()) {
                throw new UserException(DELETED_USER);
            }

            isFollowing = followRepository.findIsFollowing(userId, otherUserId);
        }

        return OtherUserResult.from(userResult.user(), userResult.follower(), userResult.following(), isFollowing);
    }

    @Override
    @Transactional
    public UserUpdateResult updateUser(Long userId, UserUpdateCommand command) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(NOT_FOUND_USER));

        if(user.isDeleted()) {
            throw new UserException(DELETED_USER);
        }

        user.update(command.name(), command.description(), command.instagramId());

        // 이미지 바꿀 때
        List<S3Image> rollbackFiles = new ArrayList<>();
        List<S3Image> deleteFiles = new ArrayList<>();

        try {
            // 트랜잭션 실패시 새 이미지 롤백
            updateUserImage(user, command.originalFile(), command.uuid(), rollbackFiles, deleteFiles);
        } catch (ImageException e) {
            eventPublisher.publishEvent(new S3ImageRollbackEvent(rollbackFiles));
            throw e;
        }
        eventPublisher.publishEvent(new S3ImageAfterCommitEvent(deleteFiles));

        userRepository.save(user);

        return new UserUpdateResult(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(NOT_FOUND_USER));

        if(user.isDeleted()) {
            throw new UserException(DELETED_USER);
        }

        user.softDelete();
        userRepository.save(user);
    }

    private void updateUserImage(User user, String originalFile, String uuid, List<S3Image> rollbackFiles, List<S3Image> deleteFiles) {
        if(originalFile == null || uuid == null) return;

        // 기존 이미지 삭제
        if(user.getUploadImage() != null) {
            deleteFiles.add(S3Image.from(user.getUploadImage()));
            user.deleteImage();
        }

        PlaceImageResponse userImageResponse = imageService.uploadWithoutPlace(originalFile, uuid);
        rollbackFiles.add(S3Image.from(userImageResponse, uuid));

        PlaceImage newUserImage = placeImageRepository.findById(userImageResponse.imageId())
                .orElseThrow(() -> new ImageException(NOT_FOUND_IMAGE));

        user.addImage(newUserImage);
    }
}
