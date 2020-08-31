package org.legion.aegis.admin.entity;

import org.legion.aegis.common.base.BasePO;
import org.legion.aegis.common.jpa.annotation.Entity;
import org.legion.aegis.common.jpa.annotation.PrimaryKey;
import org.legion.aegis.common.validation.ValidateWithRegex;

@Entity(tableName = "PJT_PROJECT_GROUP")
public class ProjectGroup extends BasePO {

    @PrimaryKey(autoIncrement = true)
    private Long id;
    @ValidateWithRegex(regex = ".{1,64}", message = "请输入名称，不超过个64字符")
    private String name;
    @ValidateWithRegex(regex = ".{0,500}", message = "描述不能超过500个字符")
    private String description;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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
