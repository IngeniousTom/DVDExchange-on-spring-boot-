package com.dvdexchange.model;

public enum UserRoles {
    ADMIN("ADMIN"),
    USER("USER"),
    ANONYMOUS("ANONYMOUS");

    private final String text;

    UserRoles(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
