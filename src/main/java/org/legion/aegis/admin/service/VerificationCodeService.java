package org.legion.aegis.admin.service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.legion.aegis.admin.entity.UserAccount;
import org.legion.aegis.admin.generator.ResetEmailGenerator;
import org.legion.aegis.common.utils.MiscGenerator;
import org.legion.aegis.common.utils.StringUtils;
import org.legion.aegis.general.service.ExternalEmailService;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

@Service
public class VerificationCodeService {

    private final Cache<Long, String> resetEmailVerificationCode;
    private final UserAccountService userAccountService;
    private final ExternalEmailService emailService;

    public VerificationCodeService(UserAccountService userAccountService, ExternalEmailService emailService) {
        this.userAccountService = userAccountService;
        resetEmailVerificationCode = CacheBuilder.newBuilder().expireAfterAccess(5, TimeUnit.MINUTES).build();
        this.emailService = emailService;
    }

    public String generateForResetEmail(Long userId) {
        resetEmailVerificationCode.cleanUp();
        String code = MiscGenerator.generateVerificationCode();
        resetEmailVerificationCode.put(userId, code);
        return code;
    }

    public boolean isValidForResetEmail(Long userId, String code) {
        String correctCode = resetEmailVerificationCode.getIfPresent(userId);
        return StringUtils.isNotBlank(correctCode) && correctCode.equalsIgnoreCase(code);
    }

    public void sendConfirmEmailForResetEmail(Long userId, String newEmail) {
        UserAccount user = userAccountService.getUserById(userId);
        if (user != null && StringUtils.isNotBlank(newEmail)) {
            try {
                ResetEmailGenerator generator = new ResetEmailGenerator(generateForResetEmail(userId));
                emailService.sendEmail(new String[]{newEmail}, null, generator.getSubject(), generator.getEmailContent());
            } catch (Exception e) {
                resetEmailVerificationCode.invalidate(userId);
            }
        }
    }
}
