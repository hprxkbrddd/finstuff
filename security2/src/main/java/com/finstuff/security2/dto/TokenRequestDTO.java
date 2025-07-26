package com.finstuff.security2.dto;

public record TokenRequestDTO(
        String grant_type,
        String client_id,
        String username,
        String password,
        String scope) {
}
