package org.legion.aegis.admin.consts;

public enum LoginStatus {

    SUCCESS(0),
    INVALID_PASS(-1),
    ACCOUNT_NOT_EXIST(-2),
    ACCOUNT_INACTIVE(1),
    ACCOUNT_EXPIRED(2),
    ACCOUNT_LOCKED(3),
    ACCOUNT_FROZEN(4),
    VOIDED(5);

    private int value;

    private LoginStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
