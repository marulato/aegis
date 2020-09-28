package org.legion.aegis.admin.entity;

import org.legion.aegis.common.base.BasePO;
import org.legion.aegis.common.jpa.annotation.Entity;
import org.legion.aegis.common.jpa.annotation.PrimaryKey;
import org.legion.aegis.common.validation.Length;

import java.util.Objects;

@Entity(tableName = "PJT_PROJECT_GROUP")
public class ProjectGroup extends BasePO {

    @PrimaryKey(autoIncrement = true)
    private Long id;
    @Length(min = 1, max = 100, message = "项目组名称为1~100个字符")
    private String name;
    private String description;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProjectGroup group = (ProjectGroup) o;
        return id.equals(group.id) &&
                name.equals(group.name) &&
                Objects.equals(description, group.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description);
    }

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
