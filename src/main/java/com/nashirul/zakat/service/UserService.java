package com.nashirul.zakat.service;

import com.nashirul.zakat.dto.UserDto;
import com.nashirul.zakat.entity.User;

public interface UserService {
    User findByEmail(String email);
    void saveAdmin(UserDto userDto);
    boolean existUser(String email);
    void updateEmailAdmin(String email, String password, String newEmail);
}
