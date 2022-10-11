package com.itheima.tanhua.enums;

public enum FreezingRange {
    LONGIN(1), COMMENT(2), ALL(3);

    int type;

    FreezingRange(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
