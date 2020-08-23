package org.legion.aegis.common.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.apache.commons.io.FilenameUtils;
import org.legion.aegis.admin.entity.Config;
import org.legion.aegis.common.consts.SystemConsts;
import org.legion.aegis.common.utils.LogUtils;
import org.legion.aegis.common.utils.SpringUtils;
import org.legion.aegis.common.utils.StringUtils;
import org.legion.aegis.general.dao.MasterCodeDAO;
import org.legion.aegis.general.ex.InitializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.FileInputStream;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class ConfigCache implements ICache<String, String> {

    private static final LoadingCache<String, String> loadingCache;
    private static final Cache<String, String> commonCache;
    public static final String KEY = "org.legion.aegis.common.cache.ConfigCache";
    private static final Map<String, Properties> propertyList = new HashMap<>();
    private static final List<String> propertyFiles = List.of("dev", "prd", "uat", "aegis", "settings");
    private static final Logger log = LoggerFactory.getLogger(ConfigCache.class);

    static {
        commonCache = CacheBuilder.newBuilder().build();
        loadingCache = CacheBuilder.newBuilder().build(new CacheLoader<String, String>() {
            @Override
            public String load(String key) throws Exception {
                if (propertyList.isEmpty()) {
                    String classPath = SystemConsts.CLASSPATH;
                    File file = new File(classPath + "config");
                    File[] files = file.listFiles();
                    for (File configFile : files) {
                        String baseName = FilenameUtils.getBaseName(configFile.getName());
                        if (configFile.isFile() && "properties".
                                equalsIgnoreCase(FilenameUtils.getExtension(configFile.getName()))
                                && propertyFiles.contains(baseName)) {
                            Properties properties = new Properties();
                            FileInputStream inputStream = new FileInputStream(configFile);
                            properties.load(inputStream);
                            inputStream.close();
                            propertyList.put(baseName, properties);
                        }
                    }
                    if (propertyList.get("aegis") != null) {
                        String mode = propertyList.get("aegis").getProperty("server.mode");
                        if (SystemConsts.MODE_DEV.equalsIgnoreCase(mode) && propertyList.containsKey("dev")) {
                            propertyList.remove("prd");
                            propertyList.remove("uat");
                        } else if (SystemConsts.MODE_PRD.equalsIgnoreCase(mode) && propertyList.containsKey("prd")) {
                            propertyList.remove("dev");
                            propertyList.remove("uat");
                        } else if (SystemConsts.MODE_UAT.equalsIgnoreCase(mode) && propertyList.containsKey("uat")) {
                            propertyList.remove("prd");
                            propertyList.remove("dev");
                        } else {
                            throw new InitializationException(LogUtils.print("Initialization FAILED: server mode setting NOT found"));
                        }
                    } else {
                        throw new InitializationException("Initialization FAILED: [aegis.properties] NOT found");
                    }
                }
                String value = null;
                Set<String> keys = propertyList.keySet();
                for (String name : keys) {
                    Properties currentProp = propertyList.get(name);
                    value = currentProp.getProperty(key);
                    if (value != null) {
                        currentProp.remove(key);
                        if (currentProp.isEmpty()) {
                            propertyList.remove(name);
                            log.info(LogUtils.around("Properties Removed: " + name));
                        }
                        break;
                    }
                }
                if (value == null) {
                    log.info(LogUtils.around("Properties missed, try database"));
                    MasterCodeDAO configDAO = SpringUtils.getBean(MasterCodeDAO.class);
                    Config config= configDAO.getConfig(key);
                    if (config != null && StringUtils.parseBoolean(config.getIsNeedRestart())) {
                        value = config.getConfigValue();
                    } else if (config != null) {
                        commonCache.put(key, config.getConfigValue());
                        value = "";
                    }
                }
                return value;
            }
        });
        ConfigCache configCache = new ConfigCache();
        CachePool.setCache(KEY, configCache);
    }

    @Override
    public String get(String key) {
        String value = null;
        try {
            value = loadingCache.get(key);
        } catch (ExecutionException e) {
            log.error("", e);
        }
        if (value == null || value.isEmpty()) {
            value = commonCache.getIfPresent(key);
        }
        return value;
    }

    @Override
    public void set(String key, String value) {

    }
}
