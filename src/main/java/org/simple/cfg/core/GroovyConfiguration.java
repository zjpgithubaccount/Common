package org.simple.cfg.core;

import groovy.util.ConfigObject;
import groovy.util.ConfigSlurper;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.LogFactory;
import org.nutz.lang.Streams;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.util.Map;

public class GroovyConfiguration extends JsonConfiguration {

    ConfigSlurper configSlurper = new ConfigSlurper();

    ConfigObject configObject;

    /**
     * Creates a new instance of <code>GroovyConfiguration</code>.
     */
    public GroovyConfiguration() {
        super();
        setLogger(LogFactory.getLog(GroovyConfiguration.class));
    }

    /**
     * Creates a new instance of <code>GroovyConfiguration</code>. The
     * configuration is loaded from the specified file
     *
     * @param fileName the name of the file to loadContent
     * @throws org.apache.commons.configuration.ConfigurationException if the file cannot be loaded
     */
    public GroovyConfiguration(String fileName) throws ConfigurationException {
        super(fileName);
        setLogger(LogFactory.getLog(GroovyConfiguration.class));
    }

    /**
     * Creates a new instance of <code>GroovyConfiguration</code>. The
     * configuration is loaded from the specified file.
     *
     * @param file the file
     * @throws org.apache.commons.configuration.ConfigurationException if an error occurs while loading the file
     */
    public GroovyConfiguration(File file) throws ConfigurationException {
        super(file);
        setLogger(LogFactory.getLog(GroovyConfiguration.class));
    }

    /**
     * Creates a new instance of <code>GroovyConfiguration</code>. The
     * configuration is loaded from the specified URL.
     *
     * @param url the URL
     * @throws org.apache.commons.configuration.ConfigurationException if loading causes an error
     */
    public GroovyConfiguration(URL url) throws ConfigurationException {
        super(url);
        setLogger(LogFactory.getLog(GroovyConfiguration.class));
    }

    public ConfigObject config() {
        return configObject;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void set(String key, Object value) {
        super.set(key, value);

        Map<String, Object> map = null;

        if (key.contains(".")) {
            String pKey = key.substring(0, key.lastIndexOf("."));
            map = get(pKey);

            key = key.substring(key.lastIndexOf(".") + 1);
        } else {
            map = configObject;
        }

        map.put(key, value);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void load(Reader in) throws ConfigurationException {
        configObject = configSlurper.parse(Streams.readAndClose(in));
        parse(null, configObject);
    }

    @Override
    public void save(Writer out) throws ConfigurationException {
        try {
            configObject.writeTo(out);
        } catch (IOException e) {
            throw new ConfigurationException(e);
        }
    }
}