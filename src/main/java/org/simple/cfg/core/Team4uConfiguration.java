package org.simple.cfg.core;

import org.apache.commons.configuration.ConfigurationException;

/**
 * <h3>Class name</h3>
 * <h4>Description</h4>
 * <p/>
 * <h4>Special Notes</h4>
 *
 * @author Jay 12-5-29 下午9:51
 * @version 1.0
 */
public interface Team4uConfiguration {
    public abstract void loadContent(String content) throws ConfigurationException;
}
