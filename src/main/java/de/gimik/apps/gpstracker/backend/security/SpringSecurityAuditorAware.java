package de.gimik.apps.gpstracker.backend.security;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Created by trung on 07.09.2015.
 */
public class SpringSecurityAuditorAware implements AuditorAware<String> {
    public String getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        try {
            return ((DefaultUserDetails) authentication.getPrincipal()).getUsername();
        } catch (Exception e) {
        }
        return null;
    }
}
