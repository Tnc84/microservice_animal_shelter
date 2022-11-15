package com.tnc.userManagement.service.constant;

public enum AuthorityEnum {
    USER_AUTHORITIES("user:read"),
    ADMIN_AUTHORITIES("user:read", "user:write"),
    MANAGER_AUTHORITIES("user:read", "user:write", "user:update"),
    OWNER_AUTHORITIES("user:read", "user:write", "user:update", "user:delete");

    AuthorityEnum(String authority) {
    }

    AuthorityEnum(String authority, String authority2) {
    }

    AuthorityEnum(String authority, String authority2, String authority3) {
    }

    AuthorityEnum(String authority, String authority2, String authority3, String authority4) {
    }
}
