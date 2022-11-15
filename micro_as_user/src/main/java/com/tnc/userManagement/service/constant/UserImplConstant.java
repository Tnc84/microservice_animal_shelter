package com.tnc.userManagement.service.constant;


import lombok.Getter;
////Must be renamed -> UserMessageEnum
public enum UserImplConstant {
    USERNAME_ALREADY_EXIST("Username already exist"),
    EMAIL_ALREADY_EXIST("Email already exist"),
    NO_USER_FOUND_BY_USERNAME("User not found by username "),
    FOUND_USER_BY_USERNAME("Returning found user by username "),
    NO_USER_FOUND_BY_EMAIL("No user found by email. ");

    @Getter
    private final String text;

    UserImplConstant(String text) {
        this.text = text;
    }
}
