package org.legion.aegis.general.dao;

import org.apache.ibatis.annotations.*;
import org.legion.aegis.common.base.SearchParam;
import org.legion.aegis.common.jpa.SimpleSQLGenerator;
import org.legion.aegis.general.entity.*;
import org.legion.aegis.general.vo.EmailAttachmentVO;
import org.legion.aegis.general.vo.EmailVO;

import java.util.List;

@Mapper
public interface EmailDAO {

    @InsertProvider(type = SimpleSQLGenerator.class, method = "insert")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "ID")
    void createEmail(Email email);

    @InsertProvider(type = SimpleSQLGenerator.class, method = "insert")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "ID")
    void createReply(EmailReply emailReply);

    List<EmailVO> searchMailInbox(@Param("sp") SearchParam searchParam);

    List<EmailVO> searchMailOutbox(@Param("sp") SearchParam searchParam);

    List<EmailVO> searchMailDraftBox(@Param("sp") SearchParam searchParam);

    List<EmailVO> searchMailRecycleBox(@Param("sp") SearchParam searchParam);

    Integer searchMailOutboxCount(@Param("sp") SearchParam searchParam);

    Integer searchMailInBoxCount(@Param("sp") SearchParam searchParam);

    Integer searchMailDraftBoxCount(@Param("sp") SearchParam searchParam);

    Integer searchMailRecycleBoxCount(@Param("sp") SearchParam searchParam);

    @Select("SELECT * FROM GNL_EMAIL_REPLY WHERE EMAIL_ID = #{emailId} ORDER BY CREATED_AT DESC")
    List<EmailReply> retrieveReplyByEmailId(Long emailId);

    @Select("SELECT * FROM GNL_EMAIL_REPLY_INBOX WHERE EMAIL_REPLY_ID IN " +
            "(SELECT ID FROM GNL_EMAIL_REPLY WHERE EMAIL_ID = #{emailId}) ORDER BY CREATED_AT DESC")
    List<EmailReplyInbox> retrieveReplyInboxByEmailId(Long emailId);

    @Select("SELECT * FROM GNL_EMAIL_REPLY_INBOX WHERE EMAIL_REPLY_ID = #{emailReplyId}")
    List<EmailReplyInbox> retrieveReplyInboxByReplyId(Long emailReplyId);

    @Select("SELECT * FROM GNL_EMAIL_REPLY WHERE ID = #{id}")
    EmailReply retrieveEmailReplyById(Long id);

    @Select("SELECT * FROM GNL_EMAIL_INBOX WHERE ID = #{id}")
    EmailInbox retrieveEmailInboxById(Long id);

    @Select("SELECT * FROM GNL_EMAIL_OUTBOX WHERE EMAIL_ID = #{emailId}")
    EmailOutbox retrieveEmailOutboxByEmailId(Long emailId);

    @Select("SELECT * FROM GNL_EMAIL_OUTBOX WHERE ID = #{outboxId}")
    EmailOutbox retrieveEmailOutboxById(Long outboxId);

    @Select("SELECT * FROM GNL_EMAIL_REPLY_OUTBOX WHERE EMAIL_REPLY_ID = #{emailReplyId}")
    EmailReplyOutbox retrieveReplyOutboxByReplyId(Long emailReplyId);

    @Select("SELECT * FROM GNL_EMAIL WHERE ID = #{id}")
    Email retrieveEmailById(Long id);

    @Select("SELECT * FROM GNL_EMAIL_INBOX WHERE EMAIL_ID = #{emailId}")
    List<EmailInbox> retrieveAllRecipientsInbox(Long emailId);

    @Select("SELECT * FROM GNL_EMAIL_REPLY_OUTBOX WHERE EMAIL_REPLY_ID = #{param1} AND SENDER = #{param2}")
    EmailReplyOutbox retrieveReplyOutboxByReplyIdAndSender(Long emailReplyId, String sender);

    List<EmailAttachmentVO> retrieveEmailAttachmentByEmailId(Long emailId);

    List<EmailAttachmentVO> retrieveReplyAttachmentByReplyId(Long emailReplyId);

    @Select("SELECT * FROM GNL_EMAIL_ATTACHMENT WHERE ID = #{id}")
    EmailAttachment getEmailAttachmentById(Long id);

}
