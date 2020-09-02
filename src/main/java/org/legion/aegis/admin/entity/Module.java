package org.legion.aegis.admin.entity;

import com.google.common.base.Objects;
import org.legion.aegis.common.base.BasePO;
import org.legion.aegis.common.jpa.annotation.Entity;
import org.legion.aegis.common.jpa.annotation.PrimaryKey;

@Entity(tableName = "PJT_MODULE")
public class Module extends BasePO {

    @PrimaryKey(autoIncrement = true)
    private Long id;
    private Long projectId;
    private String name;
    private String description;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Module)) return false;
        Module module = (Module) o;
        return Objects.equal(id, module.id) &&
                Objects.equal(projectId, module.projectId) &&
                Objects.equal(name, module.name) &&
                Objects.equal(description, module.description);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, projectId, name, description);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
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
