package com.spoteditor.backend.global.page;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;

import static org.springframework.data.domain.Sort.Direction;

@Getter @Setter
public class CustomPageRequest {

	private int page;
	private int size;
	private Direction direction;

	public void setPage(int page) {
		this.page = page <= 0 ? 1 : page;
	}

	public void setSize(int size) {
		int DEFAULT_SIZE = 20;
		int MAX_SIZE = 40;
		this.size = size > MAX_SIZE ? DEFAULT_SIZE : size;
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	public PageRequest of() {

		PageRequest pageRequest;
		try {
			pageRequest = PageRequest.of(page - 1, size, direction, "created_at");
		}catch (IllegalArgumentException e){	//	direction이 없는 경우
			pageRequest = PageRequest.of(0, 12, Direction.ASC, "created_at");
		}

		return pageRequest;
	}
}