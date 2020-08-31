package org.legion.aegis.admin.dto;

import org.legion.aegis.common.base.BaseDto;
import org.legion.aegis.common.validation.ValidateWithRegex;

public class ProjectGroupDto extends BaseDto {

    private String id;
    @ValidateWithRegex(regex = ".{1,64}", message = "请输入名称，不超过个64字符")
    private String name;
    @ValidateWithRegex(regex = ".{0,500}", message = "描述不能超过500个字符")
    private String description;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
