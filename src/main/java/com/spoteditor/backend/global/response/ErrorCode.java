package com.spoteditor.backend.global.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

	// common
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C001", "internal server error"),
	INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C002", "invalid input type"),
	METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "C003", "method not allowed"),
	INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST, "C004", "invalid type value"),
	BAD_CREDENTIALS(HttpStatus.BAD_REQUEST, "C005", "bad credentials"),

	// token
	INVALID_ACCESS_TOKEN(HttpStatus.FORBIDDEN, "T003", "유효하지 않은 AccessToken"),
	INVALID_REFRESH_TOKEN(HttpStatus.FORBIDDEN, "T005", "유효하지 않은 RefreshToken"),
	REFRESH_TOKEN_NOT_IN_COOKIE(HttpStatus.FORBIDDEN, "T006", "쿠키에 RefreshToken 없음"),

	// user
	UNSUPPORTED_PROVIDER(HttpStatus.BAD_REQUEST, "U001", "지원하지 않는 Provider"),
	NOT_FOUND_USER(HttpStatus.NOT_FOUND, "U002", "해당 유저를 찾을 수 없습니다."),
	NO_AUTHENTICATED_USER(HttpStatus.UNAUTHORIZED, "U003", "현재 사용자의 권한이 없습니다."),
	DELETED_USER(HttpStatus.BAD_REQUEST, "U004", "삭제된 유저입니다."),
	NEED_ADMIN_ROLE(HttpStatus.FORBIDDEN, "U005", "관리자 권한이 필요합니다."),
	USER_ROLE_MISSING(HttpStatus.FORBIDDEN, "U006", "사용자의 역할이 없습니다."),
	NOT_OTHER_USER(HttpStatus.BAD_REQUEST, "U007", "타인정보 조회로 본인을 조회할 수 없습니다"),

	// follow
	DUPLICATED_FOLLOW(HttpStatus.BAD_REQUEST, "F001", "중복 팔로우는 할 수 없습니다."),
	SELF_FOLLOW_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "F002", "자기 자신을 팔로우할 수 없습니다."),

	// place
	NOT_FOUND_PLACE(HttpStatus.NOT_FOUND, "P001", "해당 장소를 찾을 수 없습니다."),
	IMAGE_MINIMUM_REQUIRED(HttpStatus.BAD_REQUEST, "P002", "장소에 이미지를 최소 1장 이상 등록해야합니다."),
	IMAGE_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "P003", "장소에 이미지를 최대 3장까지 등록 가능합니다."),
	NOT_PLACE_IMAGE(HttpStatus.NOT_FOUND, "P004", "장소의 이미지가 아닙니다"),

	// bookmark
	NOT_FOUND_PLACE_BOOKMARK(HttpStatus.NOT_FOUND, "B001", "해당 공간 북마크를 찾을 수 없습니다."),
	BOOKMARK_PROCESSING_FAILED(HttpStatus.CONFLICT, "B002", "북마크 처리에 실패했습니다. 잠시 후 다시 시도해주세요."),
	BOOKMARK_ALREADY_EXIST(HttpStatus.CONFLICT, "B003", "북마크가 이미 존재합니다."),
	BOOKMARK_ALREADY_REMOVED(HttpStatus.CONFLICT, "B004", "북마크가 존재하지 않습니다."),

	// image
	NOT_FOUND_IMAGE(HttpStatus.NOT_FOUND, "I001", "이미지를 찾을 수 없습니다."),
	IMAGE_UUID_MISMATCH(HttpStatus.BAD_REQUEST, "I002", "사진 개수와 UUID 개수가 일치해야 합니다."),

	// notification
	NOT_FOUND_NOTIFICATION(HttpStatus.NOT_FOUND, "N001", "알림을 찾을 수 없습니다."),

	// place log
	NOT_FOUND_PLACES(HttpStatus.NOT_FOUND, "PL001", "로그에 등록된 장소들을 찾을 수 없습니다."),
	PLACE_MINIMUM_REQUIRED(HttpStatus.BAD_REQUEST, "PL002", "로그에 장소는 최소 1개 이상 등록해야 합니다."),
	PLACE_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "PL003", "로그에 장소는 최대 10개까지 등록 가능합니다."),
	TEMP_PLACE_LOG_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "PL004", "임시저장은 1개까지 등록 가능합니다."),
	NOT_FOUND_PLACE_LOG(HttpStatus.BAD_REQUEST, "PL005", "로그를 찾을 수 없습니다."),
	NOT_PLACE_LOG_OWNER(HttpStatus.FORBIDDEN, "PL006", "로그 소유자가 아닙니다."),
	NOT_TEMP_PLACE_LOG(HttpStatus.BAD_REQUEST, "PL007", "해당 로그가 임시저장중이 아닙니다."),
	NOT_PUBLISHED_PLACE_LOG(HttpStatus.BAD_REQUEST, "PL008", "해당 로그가 퍼블리시중이 아닙니다"),
	NO_PLACE_LOG_NAME(HttpStatus.BAD_REQUEST, "PL009", "저장하려면 제목이 필요합니다"),
	STATUS_NOT_SUPPORTED(HttpStatus.BAD_REQUEST, "PL010", "지원하지 않는 status 입니다."),
	PRIVATE_PLACE_LOG(HttpStatus.FORBIDDEN, "PL011", "비공개 로그는 소유자만 조회 가능합니다."),

	// tag
	INVALID_TAG_CATEGORY(HttpStatus.BAD_REQUEST, "T001", "존재하지 않는 태그 카테고리 입니다."),
	TAG_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "T002", "태그는 최대 5개까지 등록 가능합니다."),
	INVALID_TAG(HttpStatus.BAD_REQUEST, "T003", "존재하지 않는 태그입니다.");

	private final HttpStatus status;
	private final String code;
	private final String message;
}