package com.nashirul.zakat.dto;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public record UserLoginResponseDto(String username, Collection<? extends GrantedAuthority> authorities, String jwt) {
}
