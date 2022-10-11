package com.itheima.tanhua.enums;

public enum FreezingTime {
    ONE_DAY(1), ONE_WEEK(2), PERPETUAL(3);

    int type;

    FreezingTime(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
