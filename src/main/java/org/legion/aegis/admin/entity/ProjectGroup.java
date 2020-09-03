package org.legion.aegis.admin.entity;

import org.legion.aegis.common.base.BasePO;
import org.legion.aegis.common.jpa.annotation.Entity;
import org.legion.aegis.common.jpa.annotation.PrimaryKey;

@Entity(tableName = "PJT_PROJECT_GROUP")
public class ProjectGroup extends BasePO {

    @PrimaryKey(autoIncrement = true)
    private Long id;
    private String name;
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
