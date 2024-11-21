package com.artztall.user_service.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import java.time.LocalDateTime;

@Data
public abstract class BaseUser {
    @Id
    private String id;

    @Indexed(unique = true)
    private String email;

    private String password;
    private String name;
    private String phoneNumber;
    private String profilePictureUrl;
    private LocalDateTime joinDate;
    private UserType userType;
    private boolean isActive;
    private LocalDateTime lastLoginDate;
}