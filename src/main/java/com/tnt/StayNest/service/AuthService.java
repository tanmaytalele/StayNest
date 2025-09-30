package com.tnt.StayNest.service;


import com.tnt.StayNest.config.SNConfiguration;
import com.tnt.StayNest.dto.LoginRequest;
import com.tnt.StayNest.dto.LoginResponse;
import com.tnt.StayNest.dto.RegisterRequest;
import com.tnt.StayNest.exception.UnauthorizedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Service
public class AuthService {

    private final SNConfiguration configuration;

    private final KeycloakAdminService keycloakAdminService;

    public AuthService(SNConfiguration configuration,KeycloakAdminService keycloakAdminService){
        this.configuration = configuration;
        this.keycloakAdminService = keycloakAdminService;
    }


    public void register(RegisterRequest request, String role) {
        log.info("Registering user: username={}, email={}", request.getUsername(), request.getEmail());

        String keycloakUserId = keycloakAdminService.createUserInKeycloak(request);
        keycloakAdminService.assignRoleToUser(keycloakUserId, role.toUpperCase());

        log.info("User registered successfully: username={}", request.getUsername());
    }


    public LoginResponse login(LoginRequest request) {
        log.info("Login attempt for username={}", request.getUsername());

        // Request body
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("client_id", configuration.getClientId());
        formData.add("username", request.getUsername());
        formData.add("password", request.getPassword());
        formData.add("grant_type", "password");
        formData.add("client_secret", configuration.getClientSecret());

        // Headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // Call Keycloak
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> tokenResponse;

        try {
            log.debug("Requesting token from Keycloak for username={}", request.getUsername());
            tokenResponse = restTemplate.postForEntity(
                    configuration.getRegistrationUrl(),
                    new HttpEntity<>(formData, headers),
                    Map.class
            );
            log.debug("Received token response from Keycloak for username={}", request.getUsername());
        } catch (HttpClientErrorException e) {
            log.warn("Invalid credentials for username={}", request.getUsername());
            throw new UnauthorizedException("Invalid Credentials");
        } catch (Exception e) {
            log.error("Error during Keycloak authentication for username={}: {}", request.getUsername(), e.getMessage(), e);
            throw new UnauthorizedException("Authentication failed");
        }

        Map<String, Object> tokenData = tokenResponse.getBody();
        if (tokenData == null || !tokenData.containsKey("access_token")) {
            log.error("Invalid token response from Keycloak for username={}", request.getUsername());
            throw new UnauthorizedException("Invalid token response from Keycloak");
        }
        return new LoginResponse(tokenData.get("access_token").toString());
    }
}