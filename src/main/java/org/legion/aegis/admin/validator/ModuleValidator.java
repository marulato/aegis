package org.legion.aegis.admin.validator;

import org.legion.aegis.admin.controller.ProjectMgrController;
import org.legion.aegis.admin.entity.Module;
import org.legion.aegis.admin.entity.Project;
import org.legion.aegis.admin.service.ProjectService;
import org.legion.aegis.common.SessionManager;
import org.legion.aegis.common.base.BaseValidator;
import org.legion.aegis.common.utils.SpringUtils;
import org.legion.aegis.common.utils.StringUtils;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public class ModuleValidator implements BaseValidator {


    @Override
    public Map<String, String> doValidate(Map<String, String> params) {
        Map<String, String> errors = new HashMap<>();
        String moduleName = params.get("moduleName");
        String moduleDesc = params.get("moduleDesc");
        String id = params.get("moduleId");
        String projectId = params.get("projectId");
        String action = params.get("action");
        ProjectService service = SpringUtils.getBean(ProjectService.class);
        if (StringUtils.isBlank(moduleName) || moduleName.length() > 64) {
            errors.put("moduleName", "请输入正确的名称，不超过64字符");
        }
        if (StringUtils.isNotBlank(moduleDesc) && moduleDesc.length() > 500) {
            errors.put("moduleDesc", "请输入正确的描述，不超过500字符");
        }
        Project requested = (Project) SessionManager.getAttribute(ProjectMgrController.SESSION_PROJECT_KEY);
        Project current = service.getProjectById(StringUtils.parseIfIsLong(projectId), true);
        if (current == null || !current.equals(requested)) {
            errors.put("projectId", "拒绝访问，请求参数与预期不一致");
        } else if (!action.equals("add")){
            Module module = service.getModuleById(StringUtils.parseIfIsLong(id));
            if (module == null || !current.getModules().contains(module)) {
                errors.put("id", "拒绝访问，请求参数与预期不一致");
            }
        }
        return errors;
    }
}
