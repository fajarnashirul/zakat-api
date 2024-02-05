package com.nashirul.zakat.service.implemetation;

import com.nashirul.zakat.dto.UserDto;
import com.nashirul.zakat.entity.Role;
import com.nashirul.zakat.entity.User;
import com.nashirul.zakat.repository.RoleRepo;
import com.nashirul.zakat.repository.UserRepo;
import com.nashirul.zakat.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserImpl implements UserService {
    private final UserRepo userRepo;
    private final RoleRepo roleRepo;
    private final PasswordEncoder passwordEncoder;

    public UserImpl(UserRepo userRepo, RoleRepo roleRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User findByEmail(String email) {
        return userRepo.findByEmail(email);
    }

    @Override
    public void saveAdmin(UserDto userDto) {
        User user = new User();
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        Role role = roleRepo.findByName("ROLE_USER");
        if(role == null){
            role = assignAdminRole();
        }

        user.setRoles(List.of(role));
        userRepo.save(user);
    }

    @Override
    public boolean existUser(String email) {
        return userRepo.existsByEmail(email);
    }

    @Override
    public void updateEmailAdmin(String email, String password, String newEmail) {
        User user = userRepo.findByEmail(email);
        if (user != null) {
            if (passwordEncoder.matches(password, user.getPassword())) {
                user.setEmail(newEmail);
                userRepo.save(user);
            } else {
                throw new BadCredentialsException("Invalid password");
            }
        } else {
            throw new EntityNotFoundException("User not found with email: " + email);
        }
    }

    private Role assignAdminRole() {
        Role role = new Role();
        role.setName("ROLE_ADMIN");
        return roleRepo.save(role);
    }
}
