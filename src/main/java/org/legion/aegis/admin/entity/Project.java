package org.legion.aegis.admin.entity;

import com.google.common.base.Objects;
import org.legion.aegis.common.base.BasePO;
import org.legion.aegis.common.jpa.annotation.Entity;
import org.legion.aegis.common.jpa.annotation.NotColumn;
import org.legion.aegis.common.jpa.annotation.PrimaryKey;
import java.util.Date;
import java.util.List;

@Entity(tableName = "PJT_PROJECT")
public class Project extends BasePO {

    @PrimaryKey(autoIncrement = true)
    private Long id;
    private String name;
    private Long groupId;
    private String filePath;
    private String isPublic;
    private String description;
    private String stage;
    private String status;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Project)) return false;
        Project project = (Project) o;
        return Objects.equal(id, project.id) &&
                Objects.equal(name, project.name) &&
                Objects.equal(groupId, project.groupId) &&
                Objects.equal(filePath, project.filePath) &&
                Objects.equal(isPublic, project.isPublic) &&
                Objects.equal(description, project.description) &&
                Objects.equal(stage, project.stage) &&
                Objects.equal(status, project.status);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, name, groupId, filePath, isPublic, description, stage, status);
    }

    @NotColumn
    private List<Module> modules;
    @NotColumn
    private String groupName;

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

    public List<Module> getModules() {
        return modules;
    }

    public void setModules(List<Module> modules) {
        this.modules = modules;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
