package com.spoteditor.backend.modules.user.service;

import com.spoteditor.backend.modules.user.service.dto.OtherUserResult;
import com.spoteditor.backend.modules.user.service.dto.UserResult;
import com.spoteditor.backend.modules.user.service.dto.UserUpdateCommand;
import com.spoteditor.backend.modules.user.service.dto.UserUpdateResult;

public interface UserService {

    UserResult getUser(Long userId);

    OtherUserResult getOtherUser(Long userId, Long otherUserId);

    UserUpdateResult updateUser(Long userId, UserUpdateCommand command);

    void deleteUser(Long userId);
}
