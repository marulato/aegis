package org.legion.aegis.admin.dao;

import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.legion.aegis.admin.entity.UserAccount;
import org.legion.aegis.admin.entity.UserRole;
import org.legion.aegis.admin.entity.UserRoleAssign;
import org.legion.aegis.common.jpa.SimpleSQLGenerator;

import java.util.List;

@Mapper
public interface UserAccountDAO {

    @Select("SELECT * FROM AC_USER_ACCT WHERE LOGIN_ID = #{loginId}")
    UserAccount getUserByLoginId(String loginId);

    @Select("SELECT * FROM AC_USER_ACCT WHERE ID = #{id}")
    UserAccount getUserById(Long id);

    @Select("SELECT * FROM AC_USER_ROLE_ASSIGN WHERE USER_ACCT_ID = #{userAcctId}")
    List<UserRoleAssign> getUserRoleAssignment(Long userAcctId);

    @Select("SELECT * FROM AC_USER_ROLE_ASSIGN WHERE USER_ACCT_ID = #{userAcctId} AND ACTIVATED_AT <= NOW() AND DEACTIVATED_AT > NOW()")
    List<UserRoleAssign> getActiveUserRoleAssignment(Long userAcctId);

    @Select("SELECT * FROM AC_ROLE WHERE ID = #{roleId}")
    UserRole getRoleById(String roleId);

    @Select("SELECT * FROM AC_ROLE ORDER BY ID ASC")
    List<UserRole> getAllRoles();

    @InsertProvider(type = SimpleSQLGenerator.class, method = "insert")
    void createUserAccount(UserAccount userAccount);
}
