// $Id$
/*
 * ====================================================================
 * Copyright (c) 2002-2003, Christophe Labouisse All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package net.ggtools.grand;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * A singleton class for configuration.
 * 
 * @author Christophe Labouisse
 */
public class Configuration {

    private static Configuration defaultConfiguration;

    private static Properties defaultProperties;

    private static Object defaultPropertiesMonitor = new Object();

    /**
     * Get a configuration with the default values.
     * 
     * @return new configuration
     * @throws IOException
     *             if the default properties were not loadable.
     */
    public static Configuration getConfiguration() throws IOException {
        if (defaultConfiguration == null) {
            defaultConfiguration = getConfiguration((Properties) null);
        }

        return defaultConfiguration;
    }

    /**
     * Returns an new configuration overriding some parameters from a file.
     * 
     * @param propFile
     *            override file
     * @return new configuration
     * @throws IOException
     *             if the default properties were not loadable.
     */
    public static Configuration getConfiguration(final File propFile) throws IOException {
        Properties override = new Properties();
        override.load(new FileInputStream(propFile));
        return getConfiguration(override);
    }

    /**
     * Returns a new configuration overriding some parameters.
     * 
     * @param override
     *            the properties to override.
     * @return new configuration
     * @throws IOException
     *             if the default properties were not loadable.
     */
    public static Configuration getConfiguration(final Properties override) throws IOException {
        synchronized (defaultPropertiesMonitor) {
            if (defaultProperties == null) {
                defaultProperties = new Properties();
                defaultProperties.load(Configuration.class
                        .getResourceAsStream("Default.properties"));
            }
        }

        return new Configuration(override);
    }

    final private Properties buildProperties;

    private final Properties properties;

    final private String versionString;

    /**
     * Creates a new configuration. The new object's get methods will return
     * values from the supplied properties if the demanded key exists or from
     * the default properties in other case.
     * 
     * @param override
     *            properties to override in the default configuration.
     * @throws IOException
     */
    protected Configuration(final Properties override) throws IOException {
        if (override != null) {
            properties = new Properties(defaultProperties);
            properties.putAll(override);
        }
        else {
            properties = defaultProperties;
        }
        buildProperties = new Properties();
        buildProperties.load(getClass().getResourceAsStream("buildnum.properties"));
        versionString = "v" + buildProperties.getProperty("build.version.string") + " (build "
                + buildProperties.getProperty("build.number") + " "
                + buildProperties.getProperty("build.date") + ")";
    }

    /**
     * Get a parameter as a String.
     * 
     * @param key
     *            parameter to look for.
     * @return parameter value.
     */
    public final String get(final String key) {
        return properties.getProperty(key);
    }

    /**
     * @return Returns the buildProperties.
     */
    public final Properties getBuildProperties() {
        return buildProperties;
    }

    /**
     * @return Returns the versionString.
     */
    public final String getVersionString() {
        return versionString;
    }
}