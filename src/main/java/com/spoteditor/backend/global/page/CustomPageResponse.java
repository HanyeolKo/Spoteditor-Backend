package com.spoteditor.backend.global.page;

import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.List;

@Getter
public class CustomPageResponse<T> {

	private final List<T> content;
	private final long totalCount;
	private final int pageNumber;
	private final int pageSize;
	private final int totalPages;
	private final Sort sort;

	public CustomPageResponse(Page<T> page) {
		this.content = page.getContent();
		this.totalCount = page.getTotalElements();
		this.pageNumber = page.getNumber() + 1;
		this.pageSize = page.getSize();
		this.totalPages = page.getTotalPages();
		this.sort = page.getSort();
	}
}
