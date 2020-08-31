package org.legion.aegis.admin.vo;

import org.legion.aegis.admin.entity.ProjectGroup;
import org.legion.aegis.common.base.BaseVO;

public class ProjectGroupVO extends BaseVO {

    public ProjectGroupVO() {}

    public ProjectGroupVO(ProjectGroup po) {
        super(po);
    }

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
