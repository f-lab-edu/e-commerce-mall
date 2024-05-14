package com.ecommerce.jwt.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReissueResponse {
    private String accessToken;
    private String refreshToke;
    private String message;
}
