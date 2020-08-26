package org.legion.aegis.admin.service;

import org.apache.commons.codec.digest.DigestUtils;
import org.legion.aegis.admin.dao.UserAccountDAO;
import org.legion.aegis.admin.dto.UserDto;
import org.legion.aegis.admin.entity.*;
import org.legion.aegis.admin.generator.NewUserEmailGenerator;
import org.legion.aegis.common.AppContext;
import org.legion.aegis.common.consts.AppConsts;
import org.legion.aegis.common.jpa.exec.JPAExecutor;
import org.legion.aegis.common.utils.CollectionUtils;
import org.legion.aegis.common.utils.DateUtils;
import org.legion.aegis.common.utils.MiscGenerator;
import org.legion.aegis.common.utils.StringUtils;
import org.legion.aegis.general.service.ExternalEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

@Service
public class UserAccountService {

    private final UserAccountDAO userAccountDAO;
    private final ExternalEmailService emailService;

    @Autowired
    public UserAccountService(UserAccountDAO userAccountDAO, ExternalEmailService emailService) {
        this.userAccountDAO = userAccountDAO;
        this.emailService = emailService;
    }

    public String encryptPassword(String pwd) {
        if (StringUtils.isNotBlank(pwd)) {
            String hash = pwd;
            for (int i = 0; i < 5; i++) {
                hash = DigestUtils.sha256Hex(hash);
            }
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            return encoder.encode(hash);
        }
        return pwd;
    }

    public boolean isPasswordMatch(String plaintext, String ciphertext) {
        if (StringUtils.isNotEmpty(plaintext) && StringUtils.isNotEmpty(ciphertext)) {
            String hash = plaintext;
            for (int i = 0; i < 5; i++) {
                hash = DigestUtils.sha256Hex(hash);
            }
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            return encoder.matches(hash, ciphertext);
        }
        return false;
    }

    public boolean isActive(UserAccount user) {
        if (user != null) {
            long now = DateUtils.now().getTime();
            return user.getActivatedAt().getTime() <= now && user.getDeactivatedAt().getTime() >= now;
        }
        return false;
    }

    public UserAccount getUserByLoginId(String loginId) {
        if (StringUtils.isNotBlank(loginId)) {
            return userAccountDAO.getUserByLoginId(loginId);
        }
        return null;
    }

    public List<UserRoleAssign> getActiveRoleAssignById(Long userAcctId) {
        if (userAcctId != null && userAcctId > 0) {
            return userAccountDAO.getActiveUserRoleAssignment(userAcctId);
        }
        return null;
    }

    public UserRole getRoleById(String roleId) {
        if (StringUtils.isNotBlank(roleId)) {
            return userAccountDAO.getRoleById(roleId);
        }
        return null;
    }

    public List<UserRole> getAllRoles() {
        return userAccountDAO.getAllRoles();
    }

    public UserAccount createDefaultUser(UserAccount account) {
        if (account != null) {
            String pwd = null;
            if (account.getActivatedAt() == null) {
                account.setActivatedAt(DateUtils.today());
            }
            if (account.getDeactivatedAt() == null) {
                account.setDeactivatedAt(DateUtils.addYear(DateUtils.today(), 3));
            }
            if (StringUtils.isBlank(account.getPassword())) {
                pwd = MiscGenerator.generateInitialPassword();
                account.setPassword(encryptPassword(pwd));
            }
            account.setIsFirstLogin(AppConsts.YES);
            account.setIsNeedChangePwd(AppConsts.YES);
            account.setStatus(AppConsts.ACCOUNT_STATUS_ACTIVE);
            account.setLoginFailedTimes(0);
            JPAExecutor.save(account);
            UserAccount newUser = getUserByLoginId(account.getLoginId());
            newUser.setOriginalPwd(pwd);
            return newUser;
        }
        return null;
    }

    public void assignRoles(UserAccount account, List<UserRole> roles) {
        if (account != null && !CollectionUtils.isEmpty(roles)) {
            UserAccount user = userAccountDAO.getUserById(account.getId());
            if (user == null) {
                user = getUserByLoginId(account.getLoginId());
            }
            if (user != null) {
                for (UserRole role : roles) {
                    UserRoleAssign assign = new UserRoleAssign();
                    assign.setRoleId(role.getId());
                    assign.setUserAcctId(account.getId());
                    assign.setApprovedBy(AppContext.getFromWebThread().getLoginId());
                    assign.setActivatedAt(account.getActivatedAt());
                    assign.setDeactivatedAt(account.getDeactivatedAt());
                    JPAExecutor.save(assign);
                }
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public UserAccount createUser(UserAccount userAccount, List<UserRole> roles, List<Project> projects) {
        if (userAccount != null && !CollectionUtils.isEmpty(roles) && !CollectionUtils.isEmpty(projects)) {
            UserAccount newUser = createDefaultUser(userAccount);
            if (newUser != null) {
                assignRoles(userAccount, roles);
                for (Project project : projects) {
                    UserProjectAssign projectAssign = new UserProjectAssign();
                    projectAssign.setProjectId(project.getId());
                    projectAssign.setUserAcctId(newUser.getId());
                    projectAssign.setAssignReason("New user created");
                    JPAExecutor.save(projectAssign);
                }
                return newUser;
            }
        }
        return null;
    }

    public void sendAcknowledgementEmail(UserAccount account) throws Exception {
        if (account != null) {
            NewUserEmailGenerator generator = new NewUserEmailGenerator(account);
            emailService.sendEmail(new String[]{account.getEmail()}, null, generator.getSubject(), generator.getEmailContent());
        }
    }


}
