CREATE TABLE AC_USER_ACCT
(
    ID                    BIGINT PRIMARY KEY AUTO_INCREMENT,
    LOGIN_ID              VARCHAR(16) UNIQUE NOT NULL,
    DOMAIN                VARCHAR(12)        NOT NULL,
    NAME                  VARCHAR(24),
    DISPLAY_NAME          VARCHAR(32)        NOT NULL,
    PASSWORD              VARCHAR(256)       NOT NULL,
    EMAIL                 VARCHAR(64),
    PHONE_NO              VARCHAR(11),
    STATUS                CHAR(1)            NOT NULL,
    IS_FIRST_LOGIN        CHAR(1),
    IS_NEED_CHANGE_PWD    CHAR(1)            NOT NULL,
    LAST_LOGIN_SUCCESS_DT DATETIME,
    LAST_LOGIN_ATTEMPT_DT DATETIME,
    LAST_LOGIN_IP         VARCHAR(32),
    LOGIN_FAILED_TIMES    INT,
    ACTIVATED_AT          DATETIME           NOT NULL,
    DEACTIVATED_AT        DATETIME           NOT NULL,
    CREATED_AT            DATETIME           NOT NULL,
    CREATED_BY            VARCHAR(32)        NOT NULL,
    UPDATED_AT            DATETIME           NOT NULL,
    UPDATED_BY            VARCHAR(32)        NOT NULL

);



CREATE TABLE AC_USER_ACCT_H
(
    AUDIT_ID              BIGINT PRIMARY KEY AUTO_INCREMENT,
    AUDIT_TYPE            CHAR(2),
    AUDIT_TIME            DATETIME,
    ID                    BIGINT,
    LOGIN_ID              VARCHAR(16),
    DOMAIN                VARCHAR(12),
    NAME                  VARCHAR(24),
    DISPLAY_NAME          VARCHAR(32),
    TYPE                  VARCHAR(16),
    PASSWORD              VARCHAR(256),
    EMAIL                 VARCHAR(64),
    PHONE_NO              VARCHAR(11),
    STATUS                CHAR(1),
    IS_FIRST_LOGIN        CHAR(1),
    IS_NEED_CHANGE_PWD    CHAR(1),
    LAST_LOGIN_SUCCESS_DT DATETIME,
    LAST_LOGIN_ATTEMPT_DT DATETIME,
    LAST_LOGIN_IP         VARCHAR(32),
    LOGIN_FAILED_TIMES    INT,
    ACTIVATED_AT          DATETIME,
    DEACTIVATED_AT        DATETIME,
    CREATED_AT            DATETIME,
    CREATED_BY            VARCHAR(32),
    UPDATED_AT            DATETIME,
    UPDATED_BY            VARCHAR(32)
);


DELIMITER ##
CREATE TRIGGER AC_USER_ACCT_TRG_AI
    AFTER INSERT
    ON AC_USER_ACCT
    FOR EACH ROW
BEGIN
    INSERT INTO AC_USER_ACCT_H(AUDIT_ID, AUDIT_TYPE, AUDIT_TIME,
                               ID, LOGIN_ID, DOMAIN, NAME, DISPLAY_NAME, PASSWORD,
                               EMAIL, PHONE_NO, STATUS, IS_FIRST_LOGIN, IS_NEED_CHANGE_PWD,
                               LAST_LOGIN_SUCCESS_DT, LAST_LOGIN_ATTEMPT_DT, LAST_LOGIN_IP, LOGIN_FAILED_TIMES,
                               ACTIVATED_AT,
                               DEACTIVATED_AT, CREATED_AT, CREATED_BY, UPDATED_AT, UPDATED_BY)
    VALUES (NULL, 'AI', NOW(),
            NEW.ID, NEW.LOGIN_ID, NEW.DOMAIN, NEW.NAME, NEW.DISPLAY_NAME, NEW.PASSWORD,
            NEW.EMAIL, NEW.PHONE_NO, NEW.STATUS, NEW.IS_FIRST_LOGIN, NEW.IS_NEED_CHANGE_PWD,
            NEW.LAST_LOGIN_SUCCESS_DT, NEW.LAST_LOGIN_ATTEMPT_DT, NEW.LAST_LOGIN_IP, NEW.LOGIN_FAILED_TIMES,
            NEW.ACTIVATED_AT,
            NEW.DEACTIVATED_AT, NEW.CREATED_AT, NEW.CREATED_BY, NEW.UPDATED_AT, NEW.UPDATED_BY);
END ##
DELIMITER ;

DELIMITER ##
CREATE TRIGGER AC_USER_ACCT_TRG_BU
    BEFORE UPDATE
    ON AC_USER_ACCT
    FOR EACH ROW
BEGIN
    INSERT INTO AC_USER_ACCT_H(AUDIT_ID, AUDIT_TYPE, AUDIT_TIME,
                               ID, LOGIN_ID, DOMAIN, NAME, DISPLAY_NAME, PASSWORD,
                               EMAIL, PHONE_NO, STATUS, IS_FIRST_LOGIN, IS_NEED_CHANGE_PWD,
                               LAST_LOGIN_SUCCESS_DT, LAST_LOGIN_ATTEMPT_DT, LAST_LOGIN_IP, LOGIN_FAILED_TIMES,
                               ACTIVATED_AT,
                               DEACTIVATED_AT, CREATED_AT, CREATED_BY, UPDATED_AT, UPDATED_BY)
    VALUES (NULL, 'BU', NOW(),
            OLD.ID, OLD.LOGIN_ID, OLD.DOMAIN, OLD.NAME, OLD.DISPLAY_NAME, OLD.PASSWORD,
            OLD.EMAIL, OLD.PHONE_NO, OLD.STATUS, OLD.IS_FIRST_LOGIN, OLD.IS_NEED_CHANGE_PWD,
            OLD.LAST_LOGIN_SUCCESS_DT, OLD.LAST_LOGIN_ATTEMPT_DT, OLD.LAST_LOGIN_IP, OLD.LOGIN_FAILED_TIMES,
            OLD.ACTIVATED_AT,
            OLD.DEACTIVATED_AT, OLD.CREATED_AT, OLD.CREATED_BY, OLD.UPDATED_AT, OLD.UPDATED_BY);
END ##
DELIMITER ;

DELIMITER ##
CREATE TRIGGER AC_USER_ACCT_TRG_AU
    AFTER UPDATE
    ON AC_USER_ACCT
    FOR EACH ROW
BEGIN
    INSERT INTO AC_USER_ACCT_H(AUDIT_ID, AUDIT_TYPE, AUDIT_TIME,
                               ID, LOGIN_ID, DOMAIN, NAME, DISPLAY_NAME, PASSWORD,
                               EMAIL, PHONE_NO, STATUS, IS_FIRST_LOGIN, IS_NEED_CHANGE_PWD,
                               LAST_LOGIN_SUCCESS_DT, LAST_LOGIN_ATTEMPT_DT, LAST_LOGIN_IP, LOGIN_FAILED_TIMES,
                               ACTIVATED_AT,
                               DEACTIVATED_AT, CREATED_AT, CREATED_BY, UPDATED_AT, UPDATED_BY)
    VALUES (NULL, 'AU', NOW(),
            NEW.ID, NEW.LOGIN_ID, NEW.DOMAIN, NEW.NAME, NEW.DISPLAY_NAME, NEW.PASSWORD,
            NEW.EMAIL, NEW.PHONE_NO, NEW.STATUS, NEW.IS_FIRST_LOGIN, NEW.IS_NEED_CHANGE_PWD,
            NEW.LAST_LOGIN_SUCCESS_DT, NEW.LAST_LOGIN_ATTEMPT_DT, NEW.LAST_LOGIN_IP, NEW.LOGIN_FAILED_TIMES,
            NEW.ACTIVATED_AT,
            NEW.DEACTIVATED_AT, NEW.CREATED_AT, NEW.CREATED_BY, NEW.UPDATED_AT, NEW.UPDATED_BY);
END ##
DELIMITER ;

DELIMITER ##
CREATE TRIGGER AC_USER_ACCT_TRG_BD
    BEFORE DELETE
    ON AC_USER_ACCT
    FOR EACH ROW
BEGIN
    INSERT INTO AC_USER_ACCT_H(AUDIT_ID, AUDIT_TYPE, AUDIT_TIME,
                               ID, LOGIN_ID, DOMAIN, NAME, DISPLAY_NAME, PASSWORD,
                               EMAIL, PHONE_NO, STATUS, IS_FIRST_LOGIN, IS_NEED_CHANGE_PWD,
                               LAST_LOGIN_SUCCESS_DT, LAST_LOGIN_ATTEMPT_DT, LAST_LOGIN_IP, LOGIN_FAILED_TIMES,
                               ACTIVATED_AT,
                               DEACTIVATED_AT, CREATED_AT, CREATED_BY, UPDATED_AT, UPDATED_BY)
    VALUES (NULL, 'BD', NOW(),
            OLD.ID, OLD.LOGIN_ID, OLD.DOMAIN, OLD.NAME, OLD.DISPLAY_NAME, OLD.PASSWORD,
            OLD.EMAIL, OLD.PHONE_NO, OLD.STATUS, OLD.IS_FIRST_LOGIN, OLD.IS_NEED_CHANGE_PWD,
            OLD.LAST_LOGIN_SUCCESS_DT, OLD.LAST_LOGIN_ATTEMPT_DT, OLD.LAST_LOGIN_IP, OLD.LOGIN_FAILED_TIMES,
            OLD.ACTIVATED_AT,
            OLD.DEACTIVATED_AT, OLD.CREATED_AT, OLD.CREATED_BY, OLD.UPDATED_AT, OLD.UPDATED_BY);
END ##
DELIMITER ;