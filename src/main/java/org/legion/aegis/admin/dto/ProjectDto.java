package org.legion.aegis.admin.dto;


import org.apache.commons.io.FilenameUtils;
import org.legion.aegis.admin.entity.Project;
import org.legion.aegis.admin.service.ProjectService;
import org.legion.aegis.common.base.BaseDto;
import org.legion.aegis.common.consts.AppConsts;
import org.legion.aegis.common.utils.SpringUtils;
import org.legion.aegis.common.utils.StringUtils;
import org.legion.aegis.common.validation.IsIn;
import org.legion.aegis.common.validation.ValidateWithMethod;
import org.legion.aegis.common.validation.ValidateWithMethodList;
import org.legion.aegis.common.validation.ValidateWithRegex;

import java.io.File;

public class ProjectDto extends BaseDto {

    private String id;
    private String projectId;
    @ValidateWithRegex(regex = ".{1,64}", message = "请输入项目名，不超过64字符")
    private String name;
    @ValidateWithMethodList(methodList = {
            @ValidateWithMethod(method = "validatePathAvailable", message = "文件夹已被其他项目使用"),
            @ValidateWithMethod(method = "validatePathName", message = "命名只能包含数字，字母，下划线和减号"),
    })
    private String filePath;
    @IsIn(value = {AppConsts.YES, AppConsts.NO}, message = "请选择正确的权限")
    private String isPublic;
    @ValidateWithRegex(regex = ".{0,500}", message = "请输入描述，不超过500字符")
    private String description;
    private String status;

    @ValidateWithMethod(method = "validateModule", message = "")
    private String[] module;

    private String statusCode;

    private boolean validatePathAvailable(String filePath) {
        ProjectService service = SpringUtils.getBean(ProjectService.class);
        Project project = service.getProjectById(StringUtils.parseIfIsLong(projectId), false);
        if (project != null && project.getFilePath().equals(filePath)) {
            return true;
        }
        return service.isPathAvailable(filePath);
    }

    private boolean validateModule(String[] module) {
        if (module != null) {
            for (String s : module) {
                String[] details = s.split("&&");
                if (StringUtils.isBlank(details[0]) || details[0].length() > 64) {
                    return false;
                }
                if (details.length == 2 && details[1].length() > 500) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean validatePathName(String name) {
        if (StringUtils.isNotBlank(name)) {
            return name.matches("[\\w\\-]+");
        }
        return false;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(String isPublic) {
        this.isPublic = isPublic;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String[] getModule() {
        return module;
    }

    public void setModule(String[] module) {
        this.module = module;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }
}
