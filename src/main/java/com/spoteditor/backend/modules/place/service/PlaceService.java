package com.spoteditor.backend.modules.place.service;

import com.spoteditor.backend.modules.place.service.dto.PlaceRegisterCommand;
import com.spoteditor.backend.modules.place.service.dto.PlaceRegisterResult;
import org.springframework.stereotype.Service;

@Service
public interface PlaceService {

	PlaceRegisterResult addPlace(Long userId, PlaceRegisterCommand command);
}
