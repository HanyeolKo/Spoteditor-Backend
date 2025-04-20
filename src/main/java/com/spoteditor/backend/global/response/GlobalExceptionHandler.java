package com.spoteditor.backend.global.response;

import com.spoteditor.backend.global.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import static com.spoteditor.backend.global.response.ErrorCode.BAD_CREDENTIALS;
import static com.spoteditor.backend.global.response.ErrorCode.INVALID_TYPE_VALUE;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler
	protected ResponseEntity<ErrorResponse> handleBadCredentialException(BadCredentialsException e) {
		log.error("Bad credentials exception: {}", e.getMessage());
		final ErrorResponse response = ErrorResponse.of(BAD_CREDENTIALS);
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler
	protected ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
		log.error("Method argument type mismatch exception: {}", e.getMessage());
		final ErrorResponse response = ErrorResponse.of(INVALID_TYPE_VALUE);
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(UserException.class)
	public ResponseEntity<ErrorResponse> handleUserException(UserException e) {
		ErrorResponse apiErrorResponse = ErrorResponse.of(
				e.getErrorCode()
		);
    return ResponseEntity
				.status(apiErrorResponse.getStatus())
				.body(apiErrorResponse);
    }

	@ExceptionHandler(BookmarkException.class)
	public ResponseEntity<ErrorResponse> handleBookmarkException(BookmarkException e) {
		ErrorResponse apiErrorResponse = ErrorResponse.of(
				e.getErrorCode()
		);

		return ResponseEntity
				.status(apiErrorResponse.getStatus())
				.body(apiErrorResponse);
	}

	@ExceptionHandler(FollowException.class)
	public ResponseEntity<ErrorResponse> handleFollowException(FollowException e) {
		ErrorResponse apiErrorResponse = ErrorResponse.of(
				e.getErrorCode()
		);

		return ResponseEntity
				.status(apiErrorResponse.getStatus())
				.body(apiErrorResponse);
	}

	@ExceptionHandler(ImageException.class)
	public ResponseEntity<ErrorResponse> handleImageException(ImageException e) {
		ErrorResponse apiErrorResponse = ErrorResponse.of(
				e.getErrorCode()
		);

		return ResponseEntity
				.status(apiErrorResponse.getStatus())
				.body(apiErrorResponse);
	}

	@ExceptionHandler(PlaceException.class)
	public ResponseEntity<ErrorResponse> handlePlaceException(PlaceException e) {
		ErrorResponse apiErrorResponse = ErrorResponse.of(
				e.getErrorCode()
		);

		return ResponseEntity
				.status(apiErrorResponse.getStatus())
				.body(apiErrorResponse);
	}

	@ExceptionHandler(TokenException.class)
	public ResponseEntity<ErrorResponse> handleTokenException(TokenException e) {
		ErrorResponse apiErrorResponse = ErrorResponse.of(
				e.getErrorCode()
		);

		return ResponseEntity
				.status(apiErrorResponse.getStatus())
				.body(apiErrorResponse);
	}

	@ExceptionHandler(PlaceLogException.class)
	public ResponseEntity<ErrorResponse> handlePlaceLogException(PlaceLogException e) {
		ErrorResponse apiErrorResponse = ErrorResponse.of(
				e.getErrorCode()
		);

		return ResponseEntity
				.status(apiErrorResponse.getStatus())
				.body(apiErrorResponse);
	}
}