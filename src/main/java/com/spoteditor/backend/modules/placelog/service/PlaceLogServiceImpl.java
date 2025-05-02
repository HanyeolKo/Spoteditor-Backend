package com.spoteditor.backend.modules.placelog.service;

import com.spoteditor.backend.global.exception.ImageException;
import com.spoteditor.backend.global.exception.PlaceException;
import com.spoteditor.backend.global.exception.PlaceLogException;
import com.spoteditor.backend.global.exception.UserException;
import com.spoteditor.backend.modules.image.controller.dto.PlaceImageResponse;
import com.spoteditor.backend.modules.image.entity.PlaceImage;
import com.spoteditor.backend.modules.image.event.S3ImageAfterCommitEvent;
import com.spoteditor.backend.modules.image.event.S3ImageRollbackEvent;
import com.spoteditor.backend.modules.image.event.dto.S3Image;
import com.spoteditor.backend.modules.image.repository.PlaceImageRepository;
import com.spoteditor.backend.modules.image.service.PlaceImageService;
import com.spoteditor.backend.modules.mapping.placelogplacemapping.entity.PlaceLogPlaceMapping;
import com.spoteditor.backend.modules.mapping.placelogplacemapping.repository.PlaceLogPlaceMappingRepository;
import com.spoteditor.backend.modules.mapping.placelogtagmapping.entity.PlaceLogTagMapping;
import com.spoteditor.backend.modules.mapping.placelogtagmapping.repository.PlaceLogTagMappingRepository;
import com.spoteditor.backend.modules.place.controller.dto.PlaceRegisterRequest;
import com.spoteditor.backend.modules.place.entity.Place;
import com.spoteditor.backend.modules.place.repository.PlaceRepository;
import com.spoteditor.backend.modules.place.service.dto.PlaceRegisterCommand;
import com.spoteditor.backend.modules.placebookmark.repository.PlaceBookmarkRepository;
import com.spoteditor.backend.modules.placelog.controller.dto.PlaceLogBookmarkResponse;
import com.spoteditor.backend.modules.placelog.controller.dto.PlaceLogPlaceRegisterRequest;
import com.spoteditor.backend.modules.placelog.entity.PlaceLog;
import com.spoteditor.backend.modules.placelog.entity.PlaceLogStatus;
import com.spoteditor.backend.modules.placelog.repository.PlaceLogRepository;
import com.spoteditor.backend.modules.placelog.service.dto.*;
import com.spoteditor.backend.modules.tag.dto.TagDto;
import com.spoteditor.backend.modules.tag.entity.Tag;
import com.spoteditor.backend.modules.tag.repository.TagRepository;
import com.spoteditor.backend.modules.user.entity.User;
import com.spoteditor.backend.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.spoteditor.backend.global.response.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlaceLogServiceImpl implements PlaceLogService {

    private final PlaceLogRepository placeLogRepository;
    private final UserRepository userRepository;
    private final PlaceRepository placeRepository;
    private final TagRepository tagRepository;
    private final PlaceLogTagMappingRepository placeLogTagMappingRepository;
    private final PlaceLogPlaceMappingRepository placeLogPlaceMappingRepository;
    private final PlaceImageService imageService;
    private final PlaceImageRepository placeImageRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final PlaceBookmarkRepository bookmarkRepository;

    @Override
    @Transactional
    public PlaceLogResult addPlaceLog(Long userId, PlaceLogRegisterCommand command) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(NOT_FOUND_USER));

        if(user.isDeleted()) {
            throw new UserException(DELETED_USER);
        }

        // 로그에 제목 있어야함
        if(command.name() == null || command.name().trim().isEmpty()) {
            throw new PlaceLogException(NO_PLACE_LOG_NAME);
        }

        // 장소는 1개 이상 등록
        if(command.placeRegisterRequests().isEmpty()) {
            throw new PlaceLogException(PLACE_MINIMUM_REQUIRED);
        }

        // 장소 최대 10개까지 등록
        if(command.placeRegisterRequests().size() > 10) {
            throw new PlaceLogException(PLACE_LIMIT_EXCEEDED);
        }

    // S3 무관한 작업
        // 로그 생성
        PlaceLog placeLog = PlaceLog.builder()
                .user(user)
                .name(command.name())
                .description(command.description())
                .status(command.status())
                .build();
        placeLogRepository.save(placeLog);

        // 태그 추가
        addPlaceLogTags(placeLog, command.tags());

    // S3 유관한 작업
        List<S3Image> rollbackFiles = new ArrayList<>();

        try {
            // 로그 이미지 추가
            addPlaceLogImage(placeLog, command.originalFile(), command.uuid(), rollbackFiles);
            // 장소 추가
            addPlaces(placeLog, user, command.placeRegisterRequests(), rollbackFiles);
        } catch (ImageException | PlaceException e) {
            eventPublisher.publishEvent(new S3ImageRollbackEvent(rollbackFiles));
            throw e;
        }
        List<Place> places = getPlaces(placeLog.getId());

        placeLog.updateAddress(places.get(0).getAddress());
        placeLogRepository.save(placeLog);

        List<Tag> tags = getTags(placeLog.getId());

        return PlaceLogResult.from(placeLog, places, tags);
    }

    @Override
    public PlaceLogResult getPlaceLog(Long userId, Long placeLogId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(NOT_FOUND_USER));

        PlaceLog placeLog = placeLogRepository.findById(placeLogId)
                .orElseThrow(() -> new PlaceLogException(NOT_FOUND_PLACE_LOG));

        // placeLog 주인 아닐 때 PlaceLog 가 private 이면 못봄
        if(!placeLog.getUser().getId().equals(userId) && placeLog.getStatus().equals(PlaceLogStatus.PRIVATE)) {
            throw new PlaceLogException(PRIVATE_PLACE_LOG);
        }

        return new PlaceLogResult(placeLog, getTags(placeLog.getId()), getPlaces(placeLog.getId()));
    }

    @Override
    public PlaceLogResult getPublicPlaceLog(Long placeLogId) {

        PlaceLog placeLog = placeLogRepository.findById(placeLogId)
                .orElseThrow(() -> new PlaceLogException(NOT_FOUND_PLACE_LOG));

        if(placeLog.getStatus().equals(PlaceLogStatus.PRIVATE)) {
            throw new PlaceLogException(PRIVATE_PLACE_LOG);
        }

        return new PlaceLogResult(placeLog, getTags(placeLog.getId()), getPlaces(placeLog.getId()));
    }

    @Override
    @Transactional
    public PlaceLogResult updatePlaceLog(Long userId, Long placeLogId, PlaceLogUpdateCommand command) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(NOT_FOUND_USER));

        PlaceLog placeLog = placeLogRepository.findById(placeLogId)
                .orElseThrow(() -> new PlaceLogException(NOT_FOUND_PLACE_LOG));

        if(user.isDeleted()) {
            throw new UserException(DELETED_USER);
        }

        if(!placeLog.getUser().getId().equals(userId)) {
            throw new PlaceLogException(NOT_PLACE_LOG_OWNER);
        }

    // S3 무관한 작업
        // 로그의 이름, 설명, 상태 업데이트
        updatePlaceLogDetails(placeLog, command.name(), command.description(), command.status());

        // 로그에 제목 있어야함
        if(placeLog.getName() == null || placeLog.getName().trim().isEmpty()) {
            throw new PlaceLogException(NO_PLACE_LOG_NAME);
        }
        // 태그 삭제
        deletePlaceLogTags(placeLog, command.deleteTags());
        // 태그 추가
        addPlaceLogTags(placeLog, command.addTags());
        // 장소 삭제
        deletePlaces(placeLog, command.deletePlaceIds());

    // S3 연관된 작업
        List<S3Image> rollbackFiles = new ArrayList<>();
        List<S3Image> deleteFiles = new ArrayList<>();

        try {
            // 로그 이미지 업데이트
            updatePlaceLogImage(placeLog, command.originalFile(), command.uuid(), rollbackFiles, deleteFiles);
            // 장소 업데이트
            updatePlaces(placeLog, command.updatePlaces(), rollbackFiles, deleteFiles);
            // 장소 추가
            addPlaces(placeLog, user, command.addPlaces(), rollbackFiles);
        } catch (ImageException | PlaceException | PlaceLogException e) {
            eventPublisher.publishEvent(new S3ImageRollbackEvent(rollbackFiles));
            throw e;
        }

        PlaceLog savedPlaceLog = placeLogRepository.save(placeLog);

        List<Place> places = getPlaces(savedPlaceLog.getId());

        List<Tag> tags = getTags(savedPlaceLog.getId());

        eventPublisher.publishEvent(new S3ImageAfterCommitEvent(deleteFiles));

        return PlaceLogResult.from(savedPlaceLog, places, tags);
    }

    @Override
    @Transactional
    public void removePlaceLog(Long userId, Long placeLogId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(NOT_FOUND_USER));

        PlaceLog placeLog = placeLogRepository.findById(placeLogId)
                .orElseThrow(() -> new PlaceLogException(NOT_FOUND_PLACE_LOG));

        if(!placeLog.getUser().getId().equals(userId)) {
            throw new PlaceLogException(NOT_PLACE_LOG_OWNER);
        }

        placeLogRepository.delete(placeLog);
    }

    @Override
    public List<PlaceLogBookmarkResponse> getPlaceBookmarkStatus(Long userId, Long placeLogId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(NOT_FOUND_USER));

        if(user.isDeleted()) {
            throw new UserException(DELETED_USER);
        }
        checkPlaceLog(placeLogId);

        List<Place> places = getPlaces(placeLogId);

        List<Long> placeIds = places.stream()
                .map(Place::getId)
                .toList();

        List<Long> bookmarkedPlaceIds = bookmarkRepository.findBookmarkedPlaceIdsByUserId(userId);

        return placeIds.stream()
                .map(placeId -> new PlaceLogBookmarkResponse(
                        placeId,
                        bookmarkedPlaceIds.contains(placeId)
                ))
                .toList();
    }

    private List<Place> getPlaces(Long placeLogId) {
        return placeLogPlaceMappingRepository.findByPlaceLogId(placeLogId).stream()
                .map(PlaceLogPlaceMapping::getPlace)
                .toList();
    }

    private List<Tag> getTags(Long placeLogId) {
        return placeLogTagMappingRepository.findByPlaceLogId(placeLogId).stream()
                .map(PlaceLogTagMapping::getTag)
                .toList();
    }

    private void updatePlaceLogDetails(PlaceLog placeLog, String name, String description, PlaceLogStatus status) {
        placeLog.update(name, description, status);
    }

    @Transactional
    public void addPlaceLogImage(PlaceLog placeLog, String originalFile, String uuid, List<S3Image> rollbackFiles) {
        if(originalFile == null || uuid == null) return;

        // 새로운 이미지 등록
        PlaceImageResponse placeLogImageResponse = imageService.uploadWithoutPlace(originalFile, uuid);
        rollbackFiles.add(S3Image.from(placeLogImageResponse, uuid));

        PlaceImage newPlaceLogImage = placeImageRepository.findById(placeLogImageResponse.imageId())
                .orElseThrow(() -> new ImageException(NOT_FOUND_IMAGE));

        placeLog.addImage(newPlaceLogImage);
    }

    @Transactional
    public void updatePlaceLogImage(PlaceLog placeLog, String originalFile, String uuid, List<S3Image> rollbackFiles, List<S3Image> deleteFiles) {
        if(originalFile == null || uuid == null) return;

        // 기존 이미지 삭제
        PlaceImage beforePlaceLogImage = placeLog.getPlaceLogImage();
        placeLog.deleteImage();

        placeImageRepository.delete(beforePlaceLogImage);
        deleteFiles.add(S3Image.from(beforePlaceLogImage));

        // 새로운 이미지 등록
        PlaceImageResponse placeLogImageResponse = imageService.uploadWithoutPlace(originalFile, uuid);
        rollbackFiles.add(S3Image.from(placeLogImageResponse, uuid));

        PlaceImage newPlaceLogImage = placeImageRepository.findById(placeLogImageResponse.imageId())
                .orElseThrow(() -> new ImageException(NOT_FOUND_IMAGE));

        placeLog.addImage(newPlaceLogImage);
    }

    @Transactional
    public void deletePlaceLogTags(PlaceLog placeLog, List<TagDto> deleteTagDtos) {
        if(deleteTagDtos.isEmpty()) return;

        List<String> deleteTagNames = deleteTagDtos.stream()
                .map(TagDto::name)
                .toList();

        List<Tag> deleteTags = tagRepository.findByNameIn(deleteTagNames);

        List<Long> deleteTagIds = deleteTags.stream()
                .map(Tag::getId)
                .toList();

        List<PlaceLogTagMapping> deleteMappings = placeLogTagMappingRepository.findByPlaceLogAndTagIn(placeLog.getId(), deleteTagIds);

        placeLogTagMappingRepository.deleteAll(deleteMappings);
    }

    @Transactional
    public void addPlaceLogTags(PlaceLog placeLog, List<TagDto> addTagDtos) {
        if(addTagDtos.isEmpty()) return;

        List<String> tagNames = addTagDtos.stream()
                .map(TagDto::name)
                .toList();

        List<Tag> addTags = tagRepository.findByNameIn(tagNames);

        List<Long> existTags = getTags(placeLog.getId()).stream()
                .map(Tag::getId)
                .toList();

        List<PlaceLogTagMapping> tagMappings = new ArrayList<>();

        for(Tag tag : addTags) {
            if(existTags.contains(tag.getId())) continue;

            PlaceLogTagMapping placeLogTagMapping = PlaceLogTagMapping.builder()
                    .placeLog(placeLog)
                    .tag(tag)
                    .build();
            tagMappings.add(placeLogTagMapping);
        }
        placeLogTagMappingRepository.saveAll(tagMappings);
    }

    @Transactional
    public void deletePlaces(PlaceLog placeLog, List<Long> placeIds) {
        if(placeIds.isEmpty()) return;

        List<PlaceLogPlaceMapping> deleteMappings = placeLogPlaceMappingRepository.findByPlaceLogAndPlaceIn(placeLog.getId(), placeIds);

        for(PlaceLogPlaceMapping mapping : deleteMappings) {
            mapping.getPlace().getPlaceLogPlaceMappings().remove(mapping);
            mapping.getPlaceLog().getPlaceLogPlaceMappings().remove(mapping);
        }

        placeLogPlaceMappingRepository.deleteAll(deleteMappings);
    }

    @Transactional
    public void updatePlaces(PlaceLog placeLog, List<PlaceLogPlaceUpdateCommand> commands, List<S3Image> rollbackFiles, List<S3Image> deleteFiles) {
        for(PlaceLogPlaceUpdateCommand command : commands) {
            Place place = placeRepository.findById(command.id())
                    .orElseThrow(() -> new PlaceException(NOT_FOUND_PLACE));

            // placeLog안에 있는 place여야함
            if (!placeLogPlaceMappingRepository.exists(placeLog.getId(), place.getId())) {
                throw new PlaceLogException(NOT_FOUND_PLACES);
            }

            // place 정보 수정
            place.updateDescription(command.description());

            // originalFile, uuid 개수 확인
            List<String> originalFiles = command.originalFiles();
            List<String> uuids = command.uuids();

            if (originalFiles.size() != uuids.size()) {
                throw new ImageException(IMAGE_UUID_MISMATCH);
            }

            // place 이미지 제거
            for (Long deleteImageId : command.deleteImageIds()) {
                PlaceImage placeImage = place.deletePlaceImage(deleteImageId);
                deleteFiles.add(S3Image.from(placeImage));
            }

            // place 이미지 추가
            for (int imageIndex = 0; imageIndex < originalFiles.size(); imageIndex++) {
                PlaceImageResponse imageResponse = imageService.upload(originalFiles.get(imageIndex), uuids.get(imageIndex), place.getId());
                rollbackFiles.add(S3Image.from(imageResponse, uuids.get(imageIndex)));
            }

            // place 이미지 개수 확인
            if (place.getPlaceImages().isEmpty()) {
                throw new PlaceException(IMAGE_MINIMUM_REQUIRED);
            }

            if (place.getPlaceImages().size() > 3) {
                throw new PlaceException(IMAGE_LIMIT_EXCEEDED);
            }

            // place 저장
            placeRepository.save(place);
        }
    }

    @Transactional
    public void addPlaces(PlaceLog placeLog, User user, List<PlaceLogPlaceRegisterRequest> requests, List<S3Image> rollbackFiles) {
        for (PlaceLogPlaceRegisterRequest request : requests) {
            List<String> originalFiles = request.originalFiles();
            List<String> uuids = request.uuids();

            if (originalFiles.size() != uuids.size()) {
                throw new ImageException(IMAGE_UUID_MISMATCH);
            }

            if (originalFiles.isEmpty()) {
                throw new PlaceException(IMAGE_MINIMUM_REQUIRED);
            }

            if (originalFiles.size() > 3) {
                throw new PlaceException(IMAGE_LIMIT_EXCEEDED);
            }

            PlaceRegisterRequest placeRegisterRequest = PlaceRegisterRequest.builder()
                    .name(request.name())
                    .description(request.description())
                    .originalFile(originalFiles.get(0))
                    .uuid(uuids.get(0))
                    .address(request.address())
                    .category(request.category())
                    .build();
            PlaceRegisterCommand placeRegisterCommand = placeRegisterRequest.from();

            Place place = placeRepository.save(placeRegisterCommand.toEntity(user));

            for (int imageIndex = 0; imageIndex < originalFiles.size(); imageIndex++) {
                PlaceImageResponse imageResponse = imageService.upload(originalFiles.get(imageIndex), uuids.get(imageIndex), place.getId());
                rollbackFiles.add(S3Image.from(imageResponse, uuids.get(imageIndex)));
            }

            PlaceLogPlaceMapping mapping = PlaceLogPlaceMapping.builder()
                    .placeLog(placeLog)
                    .place(place)
                    .build();

            placeLog.getPlaceLogPlaceMappings().add(mapping);
        }
    }

    private void checkPlaceLog(Long placeLogId) {
        if (placeLogRepository.findById(placeLogId).isEmpty()) {
            throw new PlaceLogException(NOT_FOUND_PLACE_LOG);
        }
    }
}