package org.legion.aegis.admin.dto;

import com.google.common.base.Splitter;
import org.legion.aegis.admin.entity.IssueStatus;
import org.legion.aegis.admin.service.SystemMgrService;
import org.legion.aegis.common.base.BaseDto;
import org.legion.aegis.common.consts.AppConsts;
import org.legion.aegis.common.utils.SpringUtils;
import org.legion.aegis.common.utils.StringUtils;
import org.legion.aegis.common.validation.Length;
import org.legion.aegis.common.validation.MatchesPattern;
import org.legion.aegis.common.validation.MemberOf;
import org.legion.aegis.common.validation.ValidateWithMethod;

import java.util.List;

public class IssueStatusDto extends BaseDto {

    @MatchesPattern(pattern = "[A-Z_]{3,16}", message = "代码只能是包含3~16位长度的大写字母和下划线的字符", profile = {"add", "modify"})
    @ValidateWithMethod(methodName = "validateDuplicate", message = "此代码已存在", profile = {"add"})
    private String statusCode;
    @Length(min = 1, max = 64, message = "请输入名称，长度为1~64字符", profile = {"add", "modify"})
    private String displayName;
    @Length(max = 500, message = "描述的长度不得超过500个字符", profile = {"add", "modify"})
    private String description;
    @ValidateWithMethod(methodName = "validateColor", message = "请输入正确的16进制颜色代码（#000000 ~ #FFFFFF）", profile = {"add", "modify"})
    private String color;
    //@MemberOf(value = {AppConsts.YES, AppConsts.NO}, message = "", profile = {"add", "modify"})
    private String isSystem;
    @MemberOf(value = {AppConsts.YES, AppConsts.NO}, message = "请选择正确的启用/停用状态", profile = {"add", "modify"})
    private String isInuse;

    private boolean validateDuplicate(String statusCode) {
        SystemMgrService service = SpringUtils.getBean(SystemMgrService.class);
        IssueStatus issueStatus = service.getIssueStatusByCode(statusCode);
        return issueStatus == null;
    }

    private boolean validateColor(String color) {
        if (StringUtils.isNotBlank(color) && color.length() == 7) {
            String hexNumber = color.substring(1);
            List<String> rgb = Splitter.fixedLength(2).splitToList(hexNumber);
            for (String hex : rgb) {
                try {
                    int rgbColor = Integer.parseInt(hex, 16);
                    if (rgbColor < 0 || rgbColor > 255) {
                        return false;
                    }
                } catch (Exception e) {
                    return false;
                }

            }
            return true;
        }
        return false;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getIsSystem() {
        return isSystem;
    }

    public void setIsSystem(String isSystem) {
        this.isSystem = isSystem;
    }

    public String getIsInuse() {
        return isInuse;
    }

    public void setIsInuse(String isInuse) {
        this.isInuse = isInuse;
    }
}
