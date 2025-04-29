package com.spoteditor.backend.global.exception;

import com.spoteditor.backend.global.response.ErrorCode;
import lombok.Getter;

@Getter
public class PlaceBookmarkException extends BusinessException {

	public PlaceBookmarkException(ErrorCode errorCode) {
		super(errorCode);
	}
}
