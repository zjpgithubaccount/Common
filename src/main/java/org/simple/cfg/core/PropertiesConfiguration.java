package org.simple.cfg.core;

import org.apache.commons.configuration.ConfigurationException;
import org.nutz.lang.Streams;

/**
 * <h3>Class name</h3>
 * <h4>Description</h4>
 * <p/>
 * <h4>Special Notes</h4>
 *
 * @author Jay 12-5-29 下午9:36
 * @version 1.0
 */
public class PropertiesConfiguration extends org.apache.commons.configuration.PropertiesConfiguration implements Team4uConfiguration {
    public void loadContent(String content) throws ConfigurationException {
        super.load(Streams.wrap(content.getBytes()));
    }

}
