CREATE TABLE CM_MASTER_CODE
(
    ID            INT PRIMARY KEY AUTO_INCREMENT,
    TYPE          VARCHAR(64) NOT NULL,
    CODE          VARCHAR(16) NOT NULL,
    VALUE         VARCHAR(64) NOT NULL,
    DESCRIPTION   VARCHAR(500),
    IS_CACHED     CHAR(1)     NOT NULL,
    IS_SYSTEM     CHAR(1),
    DISPLAY_ORDER INT,
    IS_EDITABLE   CHAR(1)     NOT NULL,
    CREATED_AT    DATETIME    NOT NULL,
    CREATED_BY    VARCHAR(32) NOT NULL,
    UPDATED_AT    DATETIME    NOT NULL,
    UPDATED_BY    VARCHAR(32) NOT NULL,
    UNIQUE MASTER_TYPE_CODE_UK (TYPE, CODE)
);

INSERT INTO CM_MASTER_CODE (TYPE, CODE, VALUE, DESCRIPTION, IS_CACHED, IS_SYSTEM, DISPLAY_ORDER, IS_EDITABLE,
                            CREATED_AT, CREATED_BY, UPDATED_AT, UPDATED_BY)
VALUES ('issue.status.default', 'OPEN', 'Open', '新问题（未分配）', 'Y', 'Y', 1, 'N', NOW(), 'SYSTEM', NOW(), 'SYSTEM');

INSERT INTO CM_MASTER_CODE (TYPE, CODE, VALUE, DESCRIPTION, IS_CACHED, IS_SYSTEM, DISPLAY_ORDER, IS_EDITABLE,
                            CREATED_AT, CREATED_BY, UPDATED_AT, UPDATED_BY)
VALUES ('issue.status.default', 'REOPEN', 'Reopen', '复现（未修复）', 'Y', 'Y', 1, 'N', NOW(), 'SYSTEM', NOW(), 'SYSTEM');

INSERT INTO CM_MASTER_CODE (TYPE, CODE, VALUE, DESCRIPTION, IS_CACHED, IS_SYSTEM, DISPLAY_ORDER, IS_EDITABLE,
                            CREATED_AT, CREATED_BY, UPDATED_AT, UPDATED_BY)
VALUES ('issue.status.default', 'INVEST', 'Investigation', '正在查明原因', 'Y', 'Y', 1, 'N', NOW(), 'SYSTEM', NOW(), 'SYSTEM');

INSERT INTO CM_MASTER_CODE (TYPE, CODE, VALUE, DESCRIPTION, IS_CACHED, IS_SYSTEM, DISPLAY_ORDER, IS_EDITABLE,
                            CREATED_AT, CREATED_BY, UPDATED_AT, UPDATED_BY)
VALUES ('issue.status.default', 'ACK', 'Acknowledged', '问题已确认', 'Y', 'Y', 1, 'N', NOW(), 'SYSTEM', NOW(), 'SYSTEM');

INSERT INTO CM_MASTER_CODE (TYPE, CODE, VALUE, DESCRIPTION, IS_CACHED, IS_SYSTEM, DISPLAY_ORDER, IS_EDITABLE,
                            CREATED_AT, CREATED_BY, UPDATED_AT, UPDATED_BY)
VALUES ('issue.status.default', 'COMMITTED', 'Committed', '已提交仓库', 'Y', 'Y', 1, 'N', NOW(), 'SYSTEM', NOW(), 'SYSTEM');

INSERT INTO CM_MASTER_CODE (TYPE, CODE, VALUE, DESCRIPTION, IS_CACHED, IS_SYSTEM, DISPLAY_ORDER, IS_EDITABLE,
                            CREATED_AT, CREATED_BY, UPDATED_AT, UPDATED_BY)
VALUES ('issue.status.default', 'P_DEPLOY', 'Pending Deployment', '等待部署', 'Y', 'Y', 1, 'N', NOW(), 'SYSTEM', NOW(), 'SYSTEM');

INSERT INTO CM_MASTER_CODE (TYPE, CODE, VALUE, DESCRIPTION, IS_CACHED, IS_SYSTEM, DISPLAY_ORDER, IS_EDITABLE,
                            CREATED_AT, CREATED_BY, UPDATED_AT, UPDATED_BY)
VALUES ('issue.status.default', 'DEPLOYED', 'Deployed', '已部署', 'Y', 'Y', 1, 'N', NOW(), 'SYSTEM', NOW(), 'SYSTEM');

INSERT INTO CM_MASTER_CODE (TYPE, CODE, VALUE, DESCRIPTION, IS_CACHED, IS_SYSTEM, DISPLAY_ORDER, IS_EDITABLE,
                            CREATED_AT, CREATED_BY, UPDATED_AT, UPDATED_BY)
VALUES ('issue.status.default', 'MONITOR', 'Monitoring', '监控中', 'Y', 'Y', 1, 'N', NOW(), 'SYSTEM', NOW(), 'SYSTEM');

INSERT INTO CM_MASTER_CODE (TYPE, CODE, VALUE, DESCRIPTION, IS_CACHED, IS_SYSTEM, DISPLAY_ORDER, IS_EDITABLE,
                            CREATED_AT, CREATED_BY, UPDATED_AT, UPDATED_BY)
VALUES ('issue.status.default', 'FIX_VERIFIED', 'Fixed and Verified', '修复已验证', 'Y', 'Y', 1, 'N', NOW(), 'SYSTEM', NOW(), 'SYSTEM');

INSERT INTO CM_MASTER_CODE (TYPE, CODE, VALUE, DESCRIPTION, IS_CACHED, IS_SYSTEM, DISPLAY_ORDER, IS_EDITABLE,
                            CREATED_AT, CREATED_BY, UPDATED_AT, UPDATED_BY)
VALUES ('issue.status.default', 'P_EXT_PARTY', 'Pending External Party', '等待外部确认', 'Y', 'Y', 1, 'N', NOW(), 'SYSTEM', NOW(), 'SYSTEM');

INSERT INTO CM_MASTER_CODE (TYPE, CODE, VALUE, DESCRIPTION, IS_CACHED, IS_SYSTEM, DISPLAY_ORDER, IS_EDITABLE,
                            CREATED_AT, CREATED_BY, UPDATED_AT, UPDATED_BY)
VALUES ('issue.status.default', 'P_CONFIRM', 'Pending Confirmation', '等待确认', 'Y', 'Y', 1, 'N', NOW(), 'SYSTEM', NOW(), 'SYSTEM');

INSERT INTO CM_MASTER_CODE (TYPE, CODE, VALUE, DESCRIPTION, IS_CACHED, IS_SYSTEM, DISPLAY_ORDER, IS_EDITABLE,
                            CREATED_AT, CREATED_BY, UPDATED_AT, UPDATED_BY)
VALUES ('issue.status.default', 'CLOSED', 'Closed', '已关闭', 'Y', 'Y', 1, 'N', NOW(), 'SYSTEM', NOW(), 'SYSTEM');


-- fix status
INSERT INTO CM_MASTER_CODE (TYPE, CODE, VALUE, DESCRIPTION, IS_CACHED, IS_SYSTEM, DISPLAY_ORDER, IS_EDITABLE,
                            CREATED_AT, CREATED_BY, UPDATED_AT, UPDATED_BY)
VALUES ('issue.resolution.default', 'RESOLVED', 'Resolved', '已解决', 'Y', 'Y', 1, 'N', NOW(), 'SYSTEM', NOW(), 'SYSTEM');

INSERT INTO CM_MASTER_CODE (TYPE, CODE, VALUE, DESCRIPTION, IS_CACHED, IS_SYSTEM, DISPLAY_ORDER, IS_EDITABLE,
                            CREATED_AT, CREATED_BY, UPDATED_AT, UPDATED_BY)
VALUES ('issue.resolution.default', 'RESOLVING', 'Resolving', '正在解决', 'Y', 'Y', 2, 'N', NOW(), 'SYSTEM', NOW(), 'SYSTEM');

INSERT INTO CM_MASTER_CODE (TYPE, CODE, VALUE, DESCRIPTION, IS_CACHED, IS_SYSTEM, DISPLAY_ORDER, IS_EDITABLE,
                            CREATED_AT, CREATED_BY, UPDATED_AT, UPDATED_BY)
VALUES ('issue.resolution.default', 'POSTPONE', 'Postpone', '推迟处理', 'Y', 'Y', 3, 'N', NOW(), 'SYSTEM', NOW(), 'SYSTEM');

INSERT INTO CM_MASTER_CODE (TYPE, CODE, VALUE, DESCRIPTION, IS_CACHED, IS_SYSTEM, DISPLAY_ORDER, IS_EDITABLE,
                            CREATED_AT, CREATED_BY, UPDATED_AT, UPDATED_BY)
VALUES ('issue.resolution.default', 'NOT_PROCEED', 'Not Proceed', '暂不处理', 'Y', 'Y', 4, 'N', NOW(), 'SYSTEM', NOW(), 'SYSTEM');

INSERT INTO CM_MASTER_CODE (TYPE, CODE, VALUE, DESCRIPTION, IS_CACHED, IS_SYSTEM, DISPLAY_ORDER, IS_EDITABLE,
                            CREATED_AT, CREATED_BY, UPDATED_AT, UPDATED_BY)
VALUES ('issue.resolution.default', 'UNSOLVABLE', 'Unsolvable', '无法处理', 'Y', 'Y', 5, 'N', NOW(), 'SYSTEM', NOW(), 'SYSTEM');

--
INSERT INTO CM_MASTER_CODE (TYPE, CODE, VALUE, DESCRIPTION, IS_CACHED, IS_SYSTEM, DISPLAY_ORDER, IS_EDITABLE,
                            CREATED_AT, CREATED_BY, UPDATED_AT, UPDATED_BY)
VALUES ('project.stage.default', 'DEV', 'Developing', '开发阶段', 'Y', 'Y', 1, 'N', NOW(), 'SYSTEM', NOW(), 'SYSTEM');

INSERT INTO CM_MASTER_CODE (TYPE, CODE, VALUE, DESCRIPTION, IS_CACHED, IS_SYSTEM, DISPLAY_ORDER, IS_EDITABLE,
                            CREATED_AT, CREATED_BY, UPDATED_AT, UPDATED_BY)
VALUES ('project.stage.default', 'SIT', 'Self Internal Test', '系统集成测试', 'Y', 'Y', 1, 'N', NOW(), 'SYSTEM', NOW(), 'SYSTEM');

INSERT INTO CM_MASTER_CODE (TYPE, CODE, VALUE, DESCRIPTION, IS_CACHED, IS_SYSTEM, DISPLAY_ORDER, IS_EDITABLE,
                            CREATED_AT, CREATED_BY, UPDATED_AT, UPDATED_BY)
VALUES ('project.stage.default', 'UAT', 'User Acceptance Testing', '用户验收测试', 'Y', 'Y', 1, 'N', NOW(), 'SYSTEM', NOW(), 'SYSTEM');

INSERT INTO CM_MASTER_CODE (TYPE, CODE, VALUE, DESCRIPTION, IS_CACHED, IS_SYSTEM, DISPLAY_ORDER, IS_EDITABLE,
                            CREATED_AT, CREATED_BY, UPDATED_AT, UPDATED_BY)
VALUES ('project.stage.default', 'PRD', 'Production', '线上维护', 'Y', 'Y', 1, 'N', NOW(), 'SYSTEM', NOW(), 'SYSTEM');





--
INSERT INTO CM_MASTER_CODE (TYPE, CODE, VALUE, DESCRIPTION, IS_CACHED, IS_SYSTEM, DISPLAY_ORDER, IS_EDITABLE,
                            CREATED_AT, CREATED_BY, UPDATED_AT, UPDATED_BY)
VALUES ('issue.reproducibility', 'ALWAYS', '始终', NULL, 'Y', 'Y', 1, 'N', NOW(), 'SYSTEM', NOW(), 'SYSTEM');

INSERT INTO CM_MASTER_CODE (TYPE, CODE, VALUE, DESCRIPTION, IS_CACHED, IS_SYSTEM, DISPLAY_ORDER, IS_EDITABLE,
                            CREATED_AT, CREATED_BY, UPDATED_AT, UPDATED_BY)
VALUES ('issue.reproducibility', 'SOMETIMES', '偶尔', NULL, 'Y', 'Y', 2, 'N', NOW(), 'SYSTEM', NOW(), 'SYSTEM');

INSERT INTO CM_MASTER_CODE (TYPE, CODE, VALUE, DESCRIPTION, IS_CACHED, IS_SYSTEM, DISPLAY_ORDER, IS_EDITABLE,
                            CREATED_AT, CREATED_BY, UPDATED_AT, UPDATED_BY)
VALUES ('issue.reproducibility', 'RANDOM', '随机', NULL, 'Y', 'Y', 3, 'N', NOW(), 'SYSTEM', NOW(), 'SYSTEM');

INSERT INTO CM_MASTER_CODE (TYPE, CODE, VALUE, DESCRIPTION, IS_CACHED, IS_SYSTEM, DISPLAY_ORDER, IS_EDITABLE,
                            CREATED_AT, CREATED_BY, UPDATED_AT, UPDATED_BY)
VALUES ('issue.reproducibility', 'NEVER', '从不', NULL, 'Y', 'Y', 4, 'N', NOW(), 'SYSTEM', NOW(), 'SYSTEM');

INSERT INTO CM_MASTER_CODE (TYPE, CODE, VALUE, DESCRIPTION, IS_CACHED, IS_SYSTEM, DISPLAY_ORDER, IS_EDITABLE,
                            CREATED_AT, CREATED_BY, UPDATED_AT, UPDATED_BY)
VALUES ('issue.reproducibility', 'NA', '未知', NULL, 'Y', 'Y', 5, 'N', NOW(), 'SYSTEM', NOW(), 'SYSTEM');




--
INSERT INTO CM_MASTER_CODE (TYPE, CODE, VALUE, DESCRIPTION, IS_CACHED, IS_SYSTEM, DISPLAY_ORDER, IS_EDITABLE,
                            CREATED_AT, CREATED_BY, UPDATED_AT, UPDATED_BY)
VALUES ('issue.priority', 'NORMAL', '普通', NULL, 'Y', 'Y', 1, 'N', NOW(), 'SYSTEM', NOW(), 'SYSTEM');

INSERT INTO CM_MASTER_CODE (TYPE, CODE, VALUE, DESCRIPTION, IS_CACHED, IS_SYSTEM, DISPLAY_ORDER, IS_EDITABLE,
                            CREATED_AT, CREATED_BY, UPDATED_AT, UPDATED_BY)
VALUES ('issue.priority', 'LOW', '低', NULL, 'Y', 'Y', 2, 'N', NOW(), 'SYSTEM', NOW(), 'SYSTEM');

INSERT INTO CM_MASTER_CODE (TYPE, CODE, VALUE, DESCRIPTION, IS_CACHED, IS_SYSTEM, DISPLAY_ORDER, IS_EDITABLE,
                            CREATED_AT, CREATED_BY, UPDATED_AT, UPDATED_BY)
VALUES ('issue.priority', 'MEDIUM', '中等', NULL, 'Y', 'Y', 3, 'N', NOW(), 'SYSTEM', NOW(), 'SYSTEM');

INSERT INTO CM_MASTER_CODE (TYPE, CODE, VALUE, DESCRIPTION, IS_CACHED, IS_SYSTEM, DISPLAY_ORDER, IS_EDITABLE,
                            CREATED_AT, CREATED_BY, UPDATED_AT, UPDATED_BY)
VALUES ('issue.priority', 'URGENT', '紧急', NULL, 'Y', 'Y', 4, 'N', NOW(), 'SYSTEM', NOW(), 'SYSTEM');

INSERT INTO CM_MASTER_CODE (TYPE, CODE, VALUE, DESCRIPTION, IS_CACHED, IS_SYSTEM, DISPLAY_ORDER, IS_EDITABLE,
                            CREATED_AT, CREATED_BY, UPDATED_AT, UPDATED_BY)
VALUES ('issue.priority', 'V_URGENT', '十分紧急', NULL, 'Y', 'Y', 5, 'N', NOW(), 'SYSTEM', NOW(), 'SYSTEM');



--
INSERT INTO CM_MASTER_CODE (TYPE, CODE, VALUE, DESCRIPTION, IS_CACHED, IS_SYSTEM, DISPLAY_ORDER, IS_EDITABLE,
                            CREATED_AT, CREATED_BY, UPDATED_AT, UPDATED_BY)
VALUES ('issue.severity', 'S0', '高危', NULL, 'Y', 'Y', 5, 'N', NOW(), 'SYSTEM', NOW(), 'SYSTEM');

INSERT INTO CM_MASTER_CODE (TYPE, CODE, VALUE, DESCRIPTION, IS_CACHED, IS_SYSTEM, DISPLAY_ORDER, IS_EDITABLE,
                            CREATED_AT, CREATED_BY, UPDATED_AT, UPDATED_BY)
VALUES ('issue.severity', 'S1', '严重', NULL, 'Y', 'Y', 4, 'N', NOW(), 'SYSTEM', NOW(), 'SYSTEM');

INSERT INTO CM_MASTER_CODE (TYPE, CODE, VALUE, DESCRIPTION, IS_CACHED, IS_SYSTEM, DISPLAY_ORDER, IS_EDITABLE,
                            CREATED_AT, CREATED_BY, UPDATED_AT, UPDATED_BY)
VALUES ('issue.severity', 'S2', '重要', NULL, 'Y', 'Y', 3, 'N', NOW(), 'SYSTEM', NOW(), 'SYSTEM');

INSERT INTO CM_MASTER_CODE (TYPE, CODE, VALUE, DESCRIPTION, IS_CACHED, IS_SYSTEM, DISPLAY_ORDER, IS_EDITABLE,
                            CREATED_AT, CREATED_BY, UPDATED_AT, UPDATED_BY)
VALUES ('issue.severity', 'S3', '普通', NULL, 'Y', 'Y', 1, 'N', NOW(), 'SYSTEM', NOW(), 'SYSTEM');

INSERT INTO CM_MASTER_CODE (TYPE, CODE, VALUE, DESCRIPTION, IS_CACHED, IS_SYSTEM, DISPLAY_ORDER, IS_EDITABLE,
                            CREATED_AT, CREATED_BY, UPDATED_AT, UPDATED_BY)
VALUES ('issue.severity', 'S4', '建议', NULL, 'Y', 'Y', 2, 'N', NOW(), 'SYSTEM', NOW(), 'SYSTEM');


INSERT INTO CM_MASTER_CODE (TYPE, CODE, VALUE, DESCRIPTION, IS_CACHED, IS_SYSTEM, DISPLAY_ORDER, IS_EDITABLE,
                            CREATED_AT, CREATED_BY, UPDATED_AT, UPDATED_BY)
    VALUES ('issue.relationship', 'RESEMBLE', '原因相似/相同', NULL, 'Y', 'Y', 1, 'N', NOW(), 'SYSTEM', NOW(), 'SYSTEM');

INSERT INTO CM_MASTER_CODE (TYPE, CODE, VALUE, DESCRIPTION, IS_CACHED, IS_SYSTEM, DISPLAY_ORDER, IS_EDITABLE,
                            CREATED_AT, CREATED_BY, UPDATED_AT, UPDATED_BY)
VALUES ('issue.relationship', 'DUPLICATED', '重复', NULL, 'Y', 'Y', 1, 'N', NOW(), 'SYSTEM', NOW(), 'SYSTEM');

INSERT INTO CM_MASTER_CODE (TYPE, CODE, VALUE, DESCRIPTION, IS_CACHED, IS_SYSTEM, DISPLAY_ORDER, IS_EDITABLE,
                            CREATED_AT, CREATED_BY, UPDATED_AT, UPDATED_BY)
VALUES ('issue.relationship', 'CONFLICTED', '冲突', NULL, 'Y', 'Y', 1, 'N', NOW(), 'SYSTEM', NOW(), 'SYSTEM');



INSERT INTO CM_MASTER_CODE (TYPE, CODE, VALUE, DESCRIPTION, IS_CACHED, IS_SYSTEM, DISPLAY_ORDER, IS_EDITABLE,
                            CREATED_AT, CREATED_BY, UPDATED_AT, UPDATED_BY)
VALUES ('timesheet.event.type', 'COMMON', '普通', NULL, 'Y', 'Y', 1, 'N', NOW(), 'SYSTEM', NOW(), 'SYSTEM');

INSERT INTO CM_MASTER_CODE (TYPE, CODE, VALUE, DESCRIPTION, IS_CACHED, IS_SYSTEM, DISPLAY_ORDER, IS_EDITABLE,
                            CREATED_AT, CREATED_BY, UPDATED_AT, UPDATED_BY)
VALUES ('timesheet.event.type', 'TASK', '任务', NULL, 'Y', 'Y', 2, 'N', NOW(), 'SYSTEM', NOW(), 'SYSTEM');

INSERT INTO CM_MASTER_CODE (TYPE, CODE, VALUE, DESCRIPTION, IS_CACHED, IS_SYSTEM, DISPLAY_ORDER, IS_EDITABLE,
                            CREATED_AT, CREATED_BY, UPDATED_AT, UPDATED_BY)
VALUES ('timesheet.event.type', 'PUBLIC', '公共', NULL, 'Y', 'Y', 3, 'N', NOW(), 'SYSTEM', NOW(), 'SYSTEM');

