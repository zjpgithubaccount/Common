package org.simple.cfg.core;

import org.apache.commons.configuration.AbstractFileConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.LogFactory;
import org.nutz.json.Json;
import org.nutz.lang.Streams;

import java.io.File;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

@SuppressWarnings("unchecked")
public class JsonConfiguration extends AbstractFileConfiguration implements Team4uConfiguration {

    protected Map<String, Object> jsonMap = new LinkedHashMap<String, Object>();

    /**
     * Creates a new instance of <code>JsonConfiguration</code>.
     */
    public JsonConfiguration() {
        super();
        setLogger(LogFactory.getLog(JsonConfiguration.class));
    }

    /**
     * Creates a new instance of <code>JsonConfiguration</code>. The
     * configuration is loaded from the specified file
     *
     * @param fileName the name of the file to loadContent
     * @throws org.apache.commons.configuration.ConfigurationException
     *          if the file cannot be loaded
     */
    public JsonConfiguration(String fileName) throws ConfigurationException {
        super(fileName);
        setLogger(LogFactory.getLog(JsonConfiguration.class));
    }

    /**
     * Creates a new instance of <code>JsonConfiguration</code>. The
     * configuration is loaded from the specified file.
     *
     * @param file the file
     * @throws org.apache.commons.configuration.ConfigurationException
     *          if an error occurs while loading the file
     */
    public JsonConfiguration(File file) throws ConfigurationException {
        super(file);
        setLogger(LogFactory.getLog(JsonConfiguration.class));
    }

    /**
     * Creates a new instance of <code>JsonConfiguration</code>. The
     * configuration is loaded from the specified URL.
     *
     * @param url the URL
     * @throws org.apache.commons.configuration.ConfigurationException
     *          if loading causes an error
     */
    public JsonConfiguration(URL url) throws ConfigurationException {
        super(url);
        setLogger(LogFactory.getLog(JsonConfiguration.class));
    }

    protected void parse(String prefix, Map<String, Object> map) {
        if (map == null) {
            return;
        }

        jsonMap.putAll(map);

        if (prefix == null) {
            prefix = "";
        } else {
            if (!prefix.endsWith(".")) {
                prefix += ".";
            }
        }

        for (Map.Entry<?, ?> entry : map.entrySet()) {
            String k = prefix + entry.getKey();
            addPropertyDirect(k, entry.getValue());

            if (entry.getValue() instanceof Map) {
                parse(k, (Map<String, Object>) entry.getValue());
            }
        }
    }

    protected <T> T get(String key) {
        Object node = null;

        Map<String, Object> temp = jsonMap;
        for (String k : key.split("\\.")) {
            node = temp.get(k);
            if (node instanceof Map) {
                temp = (Map<String, Object>) node;
            }
        }

        return (T) node;
    }

    protected void set(String key, Object value) {
        Map<String, Object> map;

        if (key.contains(".")) {
            String pKey = key.substring(0, key.lastIndexOf("."));
            map = get(pKey);

            key = key.substring(key.lastIndexOf(".") + 1);
        } else {
            map = jsonMap;
        }

        map.put(key, value);
    }

    @Override
    public void setProperty(String key, Object value) {
        set(key, value);
        super.setProperty(key, value);
    }

    @Override
    public void load(Reader in) throws ConfigurationException {
        parse(null, Json.fromJson(LinkedHashMap.class, Streams.readAndClose(in)));
    }

    public void loadContent(String content) throws ConfigurationException {
        super.load(Streams.wrap(content.getBytes()));
    }

    @Override
    public void save(Writer out) throws ConfigurationException {
        try {
            Streams.write(out, Json.toJson(jsonMap));
        } catch (Exception e) {
            throw new ConfigurationException(e.getMessage(), e);
        }
    }
}