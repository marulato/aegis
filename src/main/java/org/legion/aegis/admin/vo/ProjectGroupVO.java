package org.legion.aegis.admin.vo;

import org.legion.aegis.admin.entity.ProjectGroup;
import org.legion.aegis.common.base.BaseVO;

public class ProjectGroupVO extends BaseVO {

    public ProjectGroupVO(ProjectGroup po) {
        super(po);
    }

    private String id;
    private String name;
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
