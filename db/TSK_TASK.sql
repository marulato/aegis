CREATE TABLE TSK_TASK_GROUP
(
    ID          BIGINT PRIMARY KEY AUTO_INCREMENT,
    NAME        VARCHAR(100) NOT NULL,
    DESCRIPTION VARCHAR(500),
    CREATED_AT  DATETIME     NOT NULL,
    CREATED_BY  VARCHAR(32)  NOT NULL,
    UPDATED_AT  DATETIME     NOT NULL,
    UPDATED_BY  VARCHAR(32)  NOT NULL
);

CREATE TABLE TSK_TASK
(
    ID                    BIGINT PRIMARY KEY AUTO_INCREMENT,
    TASK_GROUP_ID         BIGINT       NOT NULL,
    NAME                  VARCHAR(100) NOT NULL,
    DESCRIPTION           VARCHAR(500),
    EST_DEV_START_DATE    DATETIME,
    EST_DEV_END_DATE      DATETIME,
    ACTUAL_DEV_START_DATE DATETIME,
    ACTUAL_DEV_END_DATE   DATETIME,
    ESTIMATE              INT          NOT NULL,
    STATUS                VARCHAR(16)  NOT NULL,
    CREATED_AT            DATETIME     NOT NULL,
    CREATED_BY            VARCHAR(32)  NOT NULL,
    UPDATED_AT            DATETIME     NOT NULL,
    UPDATED_BY            VARCHAR(32)  NOT NULL
);

CREATE TABLE TSK_TASK_ASSIGNMENT
(
    ID            BIGINT PRIMARY KEY AUTO_INCREMENT,
    TASK_ID       BIGINT      NOT NULL,
    TASK_GROUP_ID BIGINT      NOT NULL,
    CREATED_AT    DATETIME    NOT NULL,
    CREATED_BY    VARCHAR(32) NOT NULL,
    UPDATED_AT    DATETIME    NOT NULL,
    UPDATED_BY    VARCHAR(32) NOT NULL
);

CREATE TABLE TSK_TASK_EVENT /*Calendar Event*/
(
    ID            BIGINT PRIMARY KEY AUTO_INCREMENT,
    TASK_ID       BIGINT      NOT NULL,
    TASK_GROUP_ID BIGINT      NOT NULL,
    WORK_DATE     DATE        NOT NULL,
    WORK_HOURS    INT         NOT NULL,
    OT_HOURS      INT,
    REMARKS       VARCHAR(500),
    CREATED_AT    DATETIME    NOT NULL,
    CREATED_BY    VARCHAR(32) NOT NULL,
    UPDATED_AT    DATETIME    NOT NULL,
    UPDATED_BY    VARCHAR(32) NOT NULL
);