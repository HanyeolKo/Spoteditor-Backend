package com.spoteditor.backend.modules.user.service.dto;

import com.spoteditor.backend.modules.user.entity.User;

public record UserResult(
        User user,
        Long follower,
        Long following
) {
    public static UserResult from(
            User user,
            Long follower,
            Long following
    ){
        return new UserResult(
                user,
                follower,
                following
        );
    }
}
