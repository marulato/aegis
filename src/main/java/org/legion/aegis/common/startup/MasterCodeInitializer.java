package org.legion.aegis.common.startup;

import org.legion.aegis.admin.entity.MasterCode;
import org.legion.aegis.common.cache.MasterCodeCache;
import org.legion.aegis.common.utils.SpringUtils;
import org.legion.aegis.general.dao.MasterCodeDAO;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MasterCodeInitializer implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) throws Exception {
        MasterCodeCache codeCache = new MasterCodeCache();
        MasterCodeDAO masterCodeDAO = SpringUtils.getBean(MasterCodeDAO.class);
        List<MasterCode> masterCodes = masterCodeDAO.getAllCacheNeededMasterCodes();
        for (MasterCode masterCode : masterCodes) {
            codeCache.set(masterCode);
        }
    }
}
