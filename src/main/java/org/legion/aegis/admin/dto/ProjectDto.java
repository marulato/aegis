package org.legion.aegis.admin.dto;

import org.legion.aegis.admin.entity.Project;
import org.legion.aegis.admin.entity.ProjectGroup;
import org.legion.aegis.admin.service.ProjectService;
import org.legion.aegis.common.AppContext;
import org.legion.aegis.common.base.BaseDto;
import org.legion.aegis.common.consts.AppConsts;
import org.legion.aegis.common.utils.SpringUtils;
import org.legion.aegis.common.utils.StringUtils;
import org.legion.aegis.common.validation.*;
import java.util.List;

public class ProjectDto extends BaseDto {

    private String id;
    private String projectId;
    @MatchesPattern(pattern = ".{1,64}", message = "请输入项目名，不超过64字符")
    private String name;
    @NotBlank(message = "不能为空")
    @ValidateWithMethod.List({
            @ValidateWithMethod(methodName = "validatePathName", message = "名字只能包含数字，字母和减号"),
            @ValidateWithMethod(methodName = "validatePathAvailable", message = "该路径已被占用")
    })
    private String filePath;
    @MemberOf(value = {AppConsts.YES, AppConsts.NO}, message = "请选择正确的权限")
    private String isPublic;
    @MatchesPattern(pattern = ".{0,500}", message = "请输入描述，不超过500字符")
    private String description;
    private String status;

    @ValidateWithMethod(methodName = "validateModule", message = "")
    private String[] module;

    @ValidateWithMethod(methodName = "validateGroup", message = "")
    private String group;

    private String stage;


    public boolean validatePathAvailable(String filePath) {
        ProjectService service = SpringUtils.getBean(ProjectService.class);
        Project project = service.getProjectById(StringUtils.parseIfIsLong(projectId), false);
        if (project != null && project.getFilePath().equals(filePath)) {
            return true;
        }
        return service.isPathAvailable(filePath);
    }

    public boolean validateModule(String[] module) {
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

    public boolean validatePathName(String name) {
        if (StringUtils.isNotBlank(name)) {
            return name.matches("[\\w\\-]+");
        }
        return false;
    }

    public boolean validateGroup(String group) {
        AppContext context = AppContext.getFromWebThread();
        ProjectService service = SpringUtils.getBean(ProjectService.class);
        List<ProjectGroup> groupList = service.getProjectGroupUnderUser(context.getUserId(), context.getCurrentRole().getId());
        for (ProjectGroup projectGroup : groupList) {
            if (group.equals(String.valueOf(projectGroup.getId()))) {
                return true;
            }
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

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }
}
