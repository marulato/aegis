CREATE TABLE ISU_ISSUE
(
    ID              BIGINT PRIMARY KEY AUTO_INCREMENT,
    MODULE_ID       BIGINT       NOT NULL,
    GROUP_ID        BIGINT       NOT NULL,
    PROJECT_ID      BIGINT       NOT NULL,
    TITLE           VARCHAR(100) NOT NULL,
    DESCRIPTION     MEDIUMBLOB   NOT NULL,
    REPRODUCIBILITY VARCHAR(16)  NOT NULL,
    STATUS          VARCHAR(16)  NOT NULL,
    RESOLUTION      VARCHAR(16)  NOT NULL,
    PRIORITY        VARCHAR(16)  NOT NULL,
    SEVERITY        VARCHAR(16)  NOT NULL,
    ASSIGNED_TO     BIGINT,
    ROOT_CAUSE      VARCHAR(4000),
    FIXED_AT        DATETIME,
    REPORTED_BY     BIGINT       NOT NULL,
    REPORTED_AT     DATETIME     NOT NULL,
    TAG             VARCHAR(4000),
    CREATED_AT      DATETIME     NOT NULL,
    CREATED_BY      VARCHAR(32)  NOT NULL,
    UPDATED_AT      DATETIME     NOT NULL,
    UPDATED_BY      VARCHAR(32)  NOT NULL,
    INDEX PROJECT_ID_IDX (PROJECT_ID),
    INDEX GROUP_ID_IDX (GROUP_ID)

);

CREATE TABLE ISU_ISSUE_HISTORY
(
    ID         BIGINT PRIMARY KEY AUTO_INCREMENT,
    ISSUE_ID   BIGINT,
    FIELD_NAME VARCHAR(64)  NOT NULL,
    OLD_VALUE  VARCHAR(128) NOT NULL,
    NEW_VALUE  VARCHAR(128) NOT NULL,
    CREATED_AT DATETIME     NOT NULL,
    CREATED_BY VARCHAR(32)  NOT NULL,
    UPDATED_AT DATETIME     NOT NULL,
    UPDATED_BY VARCHAR(32)  NOT NULL
);
ALTER TABLE ISU_ISSUE_HISTORY
    ADD CONSTRAINT ISU_ISSUE_HISTORY_FK FOREIGN KEY (ISSUE_ID) REFERENCES ISU_ISSUE (ID);

CREATE TABLE ISU_ATTACHMENT
(
    ID          BIGINT PRIMARY KEY AUTO_INCREMENT,
    ISSUE_ID    BIGINT       NOT NULL,
    FILE_NAME   VARCHAR(128) NOT NULL,
    FILE_NET_ID BIGINT       NOT NULL,
    CREATED_AT  DATETIME     NOT NULL,
    CREATED_BY  VARCHAR(32)  NOT NULL,
    UPDATED_AT  DATETIME     NOT NULL,
    UPDATED_BY  VARCHAR(32)  NOT NULL
);
ALTER TABLE ISU_ATTACHMENT
    ADD CONSTRAINT ISU_ATTACHMENT_FK FOREIGN KEY (ISSUE_ID) REFERENCES ISU_ISSUE (ID);

CREATE TABLE ISU_NOTE
(
    ID         BIGINT PRIMARY KEY AUTO_INCREMENT,
    ISSUE_ID   BIGINT        NOT NULL,
    CONTENT    VARCHAR(4000) NOT NULL,
    TYPE       VARCHAR(8),
    CREATED_AT DATETIME      NOT NULL,
    CREATED_BY VARCHAR(32)   NOT NULL,
    UPDATED_AT DATETIME      NOT NULL,
    UPDATED_BY VARCHAR(32)   NOT NULL
);
ALTER TABLE ISU_NOTE
    ADD CONSTRAINT ISU_NOTE_FK FOREIGN KEY (ISSUE_ID) REFERENCES ISU_ISSUE (ID);

CREATE TABLE ISU_RELATIONSHIP
(

    ID                BIGINT PRIMARY KEY AUTO_INCREMENT,
    SRC_ISSUE_ID      BIGINT      NOT NULL,
    DEST_ISSUE_ID     BIGINT      NOT NULL,
    RELATIONSHIP_TYPE VARCHAR(16) NOT NULL,
    CREATED_AT        DATETIME    NOT NULL,
    CREATED_BY        VARCHAR(32) NOT NULL,
    UPDATED_AT        DATETIME    NOT NULL,
    UPDATED_BY        VARCHAR(32) NOT NULL,
    INDEX SRC_ISSUE_ID_IDX (SRC_ISSUE_ID),
    INDEX DEST_ISSUE_ID_IDX (DEST_ISSUE_ID)
);


CREATE TABLE ISU_FOLLOWER
(
    ISSUE_ID                BIGINT      NOT NULL,
    USER_ACCT_ID            BIGINT      NOT NULL,
    IS_NOTIFICATION_ENABLED CHAR(1),
    CREATED_AT              DATETIME    NOT NULL,
    CREATED_BY              VARCHAR(32) NOT NULL,
    UPDATED_AT              DATETIME    NOT NULL,
    UPDATED_BY              VARCHAR(32) NOT NULL,
    UNIQUE ISSUE_FOLLOWER_UX (ISSUE_ID, USER_ACCT_ID)
);

CREATE TABLE ISU_P_CONFIRMATION
(
    ISSUE_ID     BIGINT      NOT NULL,
    REQUEST_FROM BIGINT      NOT NULL,
    REQUEST_TO   BIGINT      NOT NULL,
    IS_CONFIRMED CHAR(1)     NOT NULL DEFAULT 'N',
    CREATED_AT   DATETIME    NOT NULL,
    CREATED_BY   VARCHAR(32) NOT NULL,
    UPDATED_AT   DATETIME    NOT NULL,
    UPDATED_BY   VARCHAR(32) NOT NULL
);
ALTER TABLE ISU_P_CONFIRMATION
    ADD CONSTRAINT ISU_P_CONFIRMATION_FK FOREIGN KEY (ISSUE_ID) REFERENCES ISU_ISSUE (ID);

CREATE TABLE ISU_VCS_TRACKER
(
    ID             BIGINT PRIMARY KEY AUTO_INCREMENT,
    ISSUE_ID       BIGINT        NOT NULL,
    PROJECT_ID     BIGINT        NOT NULL,
    FILE_FULL_PATH VARCHAR(4000) NOT NULL,
    MASTER_VERSION VARCHAR(64),
    BRANCH         VARCHAR(256),
    BRANCH_VERSION VARCHAR(64),
    TAG            VARCHAR(4000),
    CREATED_AT     DATETIME      NOT NULL,
    CREATED_BY     VARCHAR(32)   NOT NULL,
    UPDATED_AT     DATETIME      NOT NULL,
    UPDATED_BY     VARCHAR(32)   NOT NULL
);
ALTER TABLE ISU_VCS_TRACKER
    ADD CONSTRAINT ISU_VCS_TRACKER_FK FOREIGN KEY (ISSUE_ID) REFERENCES ISU_ISSUE (ID);


CREATE TABLE ISU_SEARCH_FILTER
(
    USER_ACCT_ID BIGINT UNIQUE NOT NULL,
    GROUP_ID     BIGINT,
    PROJECT_ID   BIGINT,
    REPORTER     BIGINT,
    DEVELOPER    BIGINT,
    MODULE_ID    BIGINT,
    SEVERITY     VARCHAR(16),
    STATUS       VARCHAR(16),
    RESOLUTION   VARCHAR(16),
    CREATED_AT   DATETIME      NOT NULL,
    CREATED_BY   VARCHAR(32)   NOT NULL,
    UPDATED_AT   DATETIME      NOT NULL,
    UPDATED_BY   VARCHAR(32)   NOT NULL
);