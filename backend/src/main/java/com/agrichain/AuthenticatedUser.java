package com.agrichain.security;

import com.agrichain.common.enums.UserRole;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

/**
 * Authenticated user principal
 */
@Getter
@Builder
public class AuthenticatedUser {

    private UUID userId;
    private UUID sessionId;
    private UUID organizationId;
    private UserRole role;

    public boolean hasRole(UserRole... roles) {
        for (UserRole r : roles) {
            if (this.role == r) {
                return true;
            }
        }
        return false;
    }

    public boolean isAdmin() {
        return role == UserRole.SYSTEM_ADMIN || role == UserRole.ORG_ADMIN;
    }

    public boolean isFarmer() {
        return role == UserRole.FARMER;
    }

    public boolean belongsToOrganization(UUID orgId) {
        return organizationId != null && organizationId.equals(orgId);
    }
}
