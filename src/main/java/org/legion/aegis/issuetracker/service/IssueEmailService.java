package org.legion.aegis.issuetracker.service;

import org.legion.aegis.admin.entity.UserAccount;
import org.legion.aegis.admin.service.UserAccountService;
import org.legion.aegis.general.entity.EmailEntity;
import org.legion.aegis.general.service.ExternalEmailService;
import org.legion.aegis.issuetracker.entity.Issue;
import org.legion.aegis.issuetracker.generator.NewIssueEmailGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IssueEmailService {

    private final ExternalEmailService emailService;
    private final UserAccountService userAccountService;
    private static final Logger log = LoggerFactory.getLogger(IssueEmailService.class);

    @Autowired
    public IssueEmailService(ExternalEmailService emailService, UserAccountService userAccountService) {
        this.emailService = emailService;
        this.userAccountService = userAccountService;
    }

    public void sendEmailNewIssueReported(Issue issue) {
        if (issue != null) {
            UserAccount sendTo = userAccountService.getUserById(issue.getAssignedTo());
            if (sendTo != null) {
                try {
                    NewIssueEmailGenerator generator = new NewIssueEmailGenerator(issue);
                    EmailEntity email = new EmailEntity();
                    email.setSentTo(sendTo.getEmail());
                    email.setSubject(generator.getSubject());
                    email.setContent(generator.generate());
                    emailService.sendEmail(email);
                } catch (Exception e) {
                    log.error("Email Sent Failed", e);
                }
            }
        }
    }
}
