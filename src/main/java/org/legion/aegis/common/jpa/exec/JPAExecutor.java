package org.legion.aegis.common.jpa.exec;



import org.legion.aegis.common.AppContext;
import org.legion.aegis.common.base.BasePO;
import org.legion.aegis.common.utils.SpringUtils;

import java.util.Date;

public class JPAExecutor {

    private static IExecutor executor = SpringUtils.getBean(IExecutor.class);

    public static void save(BasePO entity) {
        if (entity != null) {
            Date now = new Date();
            AppContext appContext = AppContext.getFromWebThread();
            if (appContext == null) {
                appContext = new AppContext();
                appContext.setLoginId("TEST");
            }
            entity.setCreatedAt(now);
            entity.setUpdatedAt(now);
            entity.setCreatedBy(appContext.getLoginId());
            entity.setUpdatedBy(appContext.getLoginId());
            executor.insert(entity);
        }
    }

    public static void update(BasePO entity) {
        if (entity != null) {
            Date now = new Date();
            AppContext appContext = AppContext.getFromWebThread();
            entity.setUpdatedAt(now);
            entity.setUpdatedBy(appContext.getLoginId());
            executor.update(entity);
        }
    }
}
