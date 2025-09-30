package com.tnt.StayNest.Utils;

import com.tnt.StayNest.exception.UnauthorizedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class SecurityUtils {
    public  String getLoggedInUserId() {
        log.info("Attempting to get logged-in user id from security context");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
            String userId = jwt.getClaimAsString("sub");
            log.info("Logged-in user id found: {}", userId);
            return userId;
        }
        else {
            throw new UnauthorizedException("No authentication or JWT principal found in security context");
        }
    }
}