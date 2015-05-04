package org.simple.cfg.api;

import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.commons.configuration.CombinedConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.FileConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.apache.commons.lang.StringUtils;
import org.nutz.lang.Files;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.simple.cfg.core.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 获取公共配置属性
 *
 * @author Jay Wu
 */

public class SysCfg {

    private static final Log log = Logs.get();

    private static CombinedConfiguration config;

    public static String GS = "gs";

    public static String XML = "xml";

    public static String JSON = "js";

    public static String PROPERTIES = "properties";

    static {
        init();
    }

    private static void init() {
        config = new CombinedConfiguration();
        config.setForceReloadCheck(true);
    }

    public static CombinedConfiguration get() {
        return config;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Configuration> T get(Object name) {
        if (name == null) {
            return null;
        }

        return (T) config.getConfiguration(name.toString());
    }

    @SuppressWarnings("unchecked")
    public static <T> T getValue(String key) {
        return (T) get().getProperty(key);
    }

    /**
     * 根据配置文件内容装载，支持js/xml/properties/groovy
     *
     * @param content 配置文件内容
     */
    public static synchronized void loadContent(String key, String content, String suffix) {
        if (StringUtils.isBlank(content)) {
            return;
        }

        Team4uConfiguration configuration = (Team4uConfiguration) get(key);

        try {
            if (configuration == null) {
                if (XML.equalsIgnoreCase(suffix)) {
                    configuration = new XMLConfiguration();
                } else if (PROPERTIES.equalsIgnoreCase(suffix)) {
                    configuration = new PropertiesConfiguration();
                } else if (JSON.equalsIgnoreCase(suffix)) {
                    configuration = new JsonConfiguration();
                } else if (GS.equalsIgnoreCase(suffix)) {
                    configuration = new GroovyConfiguration();
                }

                if (configuration == null) {
                    return;
                }

                config.addConfiguration((AbstractConfiguration) configuration, key);
            }

            log.info("load content: " + key);
            configuration.loadContent(content);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }


    /**
     * 根据文件路径装载文件或文件夹下的所有配置文件，支持js/xml/properties/groovy
     *
     * @param paths 文件/文件夹路径集
     */
    public static synchronized void load(String... paths) {
        if (paths == null || paths.length == 0) {
            return;
        }
        for (String path : paths) {
            if (StringUtils.isBlank(path)) {
                return;
            }

            File f = Files.findFile(path.trim());

            if (f == null) {
                return;
            }

            List<File> list = new ArrayList<File>();

            if (f.isDirectory()) {
                list = scanFiles(path, null);
            } else {
                list.add(f);
            }

            for (File file : list) {
                String suffix = Files.getSuffixName(file);
                if (XML.equalsIgnoreCase(suffix)) {
                    addConfiguration(new XMLConfiguration(), file);
                } else if (PROPERTIES.equalsIgnoreCase(suffix)) {
                    addConfiguration(new PropertiesConfiguration(), file);
                } else if (JSON.equalsIgnoreCase(suffix)) {
                    addConfiguration(new JsonConfiguration(), file);
                } else if (GS.equalsIgnoreCase(suffix)) {
                    addConfiguration(new GroovyConfiguration(), file);
                }
            }
        }
    }

    private static void addConfiguration(FileConfiguration configuration, File file) {
        try {
            String configName = Files.getMajorName(file);

            /**
             * 防止重复添加
             */
            if (config.getConfigurationNames().contains(configName)) {
                return;
            }

            configuration.setFile(file);
            configuration.load();
            FileChangedReloadingStrategy strategy = new FileChangedReloadingStrategy();
            strategy.setRefreshDelay(3000l);
            configuration.setReloadingStrategy(strategy);
            config.addConfiguration((AbstractConfiguration) configuration, configName);
            log.info("load file: " + file.getName());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 递归获取一个目录下所有的文件。隐藏文件会被忽略。
     *
     * @param dir    目录
     * @param suffix 文件后缀名。如果为 null，则获取全部文件
     * @return 文件数组
     */
    public static List<File> scanFiles(String dir, final String suffix) {
        List<File> list = new ArrayList<File>();

        File dirFile = Files.findFile(dir);
        for (File file : Files.scanDirs(dirFile)) {
            list.addAll(Arrays.asList(Files.files(file, suffix)));
        }

        return list;
    }

    public void setPaths(String... paths) {
        load(paths);
    }
}

