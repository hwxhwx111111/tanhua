package com.itheima.tanhua.enums;

public enum UserStatus {
    UNFREEZE("1"), FREEZE("2");

    String type;

    UserStatus(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
