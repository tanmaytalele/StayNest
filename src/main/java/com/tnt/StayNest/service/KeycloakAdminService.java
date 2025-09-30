package com.tnt.StayNest.service;


import com.tnt.StayNest.config.SNConfiguration;
import com.tnt.StayNest.dto.RegisterRequest;
import com.tnt.StayNest.exception.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class KeycloakAdminService {

    private final SNConfiguration configuration;

    public KeycloakAdminService(SNConfiguration configuration){
        this.configuration = configuration;
    }

    private final RestTemplate restTemplate = new RestTemplate();

    public String getAdminAccessToken() {
        log.info("Requesting admin access token from Keycloak");
        String url = configuration.getKeycloakServerUrl() + "/realms/master/protocol/openid-connect/token";

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

        params.add("client_id", "admin-cli");
        params.add("username", configuration.getAdminUsername());
        params.add("password", configuration.getAdminPassword());
        params.add("grant_type", "password");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, new HttpEntity<>(params, headers), Map.class);
            log.info("Admin access token received successfully");
            return (String) response.getBody().get("access_token");
        } catch (Exception e) {
            log.error("Failed to get admin access token: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to get admin access token", e);
        }
    }

    public String createUserInKeycloak(RegisterRequest request) {

        String accessToken = getAdminAccessToken();

        Map<String, Object> user = new HashMap<>();
        user.put("username", request.getUsername());
        user.put("email", request.getEmail());
        user.put("enabled", true);
        user.put("credentials", List.of(Map.of("type", "password", "value", request.getPassword(), "temporary", false)));

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        try {
            log.debug("Sending request to Keycloak to create user: username={}", request.getUsername());
            ResponseEntity<String> response = restTemplate.postForEntity(
                    configuration.getKeycloakServerUrl() + "/admin/realms/" + configuration.getRealm() + "/users",
                    new HttpEntity<>(user, headers),
                    String.class
            );
            String location = response.getHeaders().getFirst("Location");
            log.info("User created in Keycloak: username={}, location={}", request.getUsername(), location);
            return location.substring(location.lastIndexOf("/") + 1);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().value() == 409) {
                log.warn("User already exists");
               throw new BadRequestException("User already exists");
            } else if (e.getStatusCode().value() == 400) {
                log.warn("User creation failed due to password policy: username={}", request.getUsername());
            throw new BadRequestException("Password must meet complexity requirements (minimum 8 characters, at least one " +
                    "uppercase, one lowercase, one digit, one special character)");
            }
            log.error("Failed to create user in Keycloak: {}", e.getResponseBodyAsString(), e);
            throw new RuntimeException("Failed to create user in Keycloak: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            log.error("Unexpected error while creating user in Keycloak: {}", e.getMessage(), e);
            throw new RuntimeException("Unexpected error while creating user in Keycloak", e);
        }
    }

    public void assignRoleToUser(String keycloakUserId, String roleName) {
        log.info("Assigning role '{}' to Keycloak userId={}", roleName, keycloakUserId);
        String accessToken = getAdminAccessToken();

        ResponseEntity<Map> roleResp = restTemplate.exchange(
                configuration.getKeycloakServerUrl() + "/admin/realms/" + configuration.getRealm() + "/roles/" + roleName,
                HttpMethod.GET,
                new HttpEntity<>(new HttpHeaders() {{
                    setBearerAuth(accessToken);
                }}),
                Map.class
        );

        Map<String, Object> role = roleResp.getBody();

        restTemplate.postForEntity(
                configuration.getKeycloakServerUrl() + "/admin/realms/" + configuration.getRealm() + "/users/" + keycloakUserId + "/role-mappings/realm",
                new HttpEntity<>(List.of(role), new HttpHeaders() {{
                    setBearerAuth(accessToken);
                }}),
                void.class
        );
        log.info("Role '{}' assigned to userId={}", roleName, keycloakUserId);
    }



}