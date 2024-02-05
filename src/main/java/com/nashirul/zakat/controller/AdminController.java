package com.nashirul.zakat.controller;

import com.nashirul.zakat.dto.UserDto;
import com.nashirul.zakat.dto.UserLoginResponseDto;
import com.nashirul.zakat.entity.User;
import com.nashirul.zakat.security.JwtUtils;
import com.nashirul.zakat.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private final UserService userService;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private AuthenticationManager authenticationManager;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(path = "/register", consumes = "application/json")
    public ResponseEntity<?> registerAdmin(@RequestBody UserDto userDto){
        if (userService.existUser(userDto.getEmail())){
            return new ResponseEntity<>("User with the same email already exists", HttpStatus.CONFLICT);
        }
        userService.saveAdmin(userDto);
        return new ResponseEntity<>("User registered successfully!",HttpStatus.CREATED);
    }

    @PostMapping(path = "/login", consumes = "application/json")
    public ResponseEntity<?> loginAdmin(@RequestBody UserDto userDto){
        try {
            // Perform authentication
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userDto.getEmail(), userDto.getPassword())
            );

            // Set authentication in the SecurityContextHolder
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Generate JWT
            String jwt = jwtUtils.generateToken(userDto.getEmail());

            // Get user details
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            // Create a custom response object with user details and JWT
            UserLoginResponseDto userLoginResponseDto = new UserLoginResponseDto(
                    userDetails.getUsername(),
                    userDetails.getAuthorities(),
                    jwt
            );

            // Return ResponseEntity with custom response object and OK status
            return ResponseEntity.ok(userLoginResponseDto);
        } catch (AuthenticationException e) {
            // Handle authentication failure
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }
    @GetMapping(path = "/current")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> currentUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String username = userDetails.getUsername();
            User user = userService.findByEmail(username);
            // Now 'username' contains the subject (username) from the JWT token
            return ResponseEntity.ok(user.getEmail());
        } else {
            // Handle the case where the authentication information is not available
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Authentication information not available");
        }
    }
    @PutMapping(path = "/edit", consumes = "application/json")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> changeEmail(@RequestParam String newEmail,
                                              @RequestBody String password){
        if (userService.existUser(newEmail)){
            return new ResponseEntity<>("User with the same email already exists", HttpStatus.CONFLICT);
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String email = userDetails.getUsername();
            try{
                userService.updateEmailAdmin(email, password, newEmail);
                return ResponseEntity.ok("email already updated, please re login");
            } catch (EntityNotFoundException e) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Entity not found");
            } catch (BadCredentialsException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid password");
            }
        }else {
        // Handle the case where the authentication information is not available
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Authentication information not available");
        }
    }
}
