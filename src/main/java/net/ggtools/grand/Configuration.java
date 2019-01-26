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
import java.io.InputStream;
import java.util.Properties;

/**
 * A singleton class for configuration.
 *
 * @author Christophe Labouisse
 */
public class Configuration {

    /**
     * Field ANT_VERSION_TXT.
     * (value is ""/org/apache/tools/ant/version.txt"")
     */
    private static final String ANT_VERSION_TXT =
            "/org/apache/tools/ant/version.txt";

    /**
     * Field ANT_MAIN_CLASS.
     * (value is ""org.apache.tools.ant.Main"")
     */
    private static final String ANT_MAIN_CLASS =
            "org.apache.tools.ant.Main";

    /**
     * Field defaultConfiguration.
     */
    private static Configuration defaultConfiguration;

    /**
     * Field defaultProperties.
     */
    private static volatile Properties defaultProperties;

    /**
     * Field defaultPropertiesMonitor.
     */
    private static final Object DEFAULT_PROPERTIES_MONITOR =
            new Object();

    /**
     * Get a configuration with the default values.
     *
     * @return new configuration
     * @throws IOException
     *             if the default properties were not loadable.
     */
    public static Configuration getConfiguration() throws IOException {
        synchronized (Configuration.class) {
            if (defaultConfiguration == null) {
                defaultConfiguration = getConfiguration((Properties) null);
            }
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
    public static Configuration getConfiguration(final File propFile)
            throws IOException {
        final Properties override = new Properties();
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
    public static Configuration getConfiguration(final Properties override)
            throws IOException {
        synchronized (DEFAULT_PROPERTIES_MONITOR) {
            if (defaultProperties == null) {
                defaultProperties = new Properties();
                defaultProperties.load(Configuration.class
                        .getResourceAsStream("Default.properties"));
            }
        }

        return new Configuration(override);
    }

    /**
     * Field antVersionString.
     */
    private final String antVersionString;

    /**
     * Field buildProperties.
     */
    private final Properties buildProperties;

    /**
     * Field properties.
     */
    private final Properties properties;

    /**
     * Field versionString.
     */
    private final String versionString;

    /**
     * Creates a new configuration. The new object's get methods will return
     * values from the supplied properties if the demanded key exists or from
     * the default properties in other case.
     *
     * @param override
     *            properties to override in the default configuration.
     * @throws IOException if an error occurs in load()
     */
    protected Configuration(final Properties override) throws IOException {
        if (override != null) {
            properties = new Properties(defaultProperties);
            properties.putAll(override);
        } else {
            properties = defaultProperties;
        }
        buildProperties = new Properties();
        buildProperties.load(getClass().getResourceAsStream("buildnum.properties"));
        versionString = "v" + buildProperties.getProperty("build.version.string")
                + " (build " + buildProperties.getProperty("build.number") + " "
                + buildProperties.getProperty("build.date") + ")";

        final Properties antProperties = new Properties();
        InputStream antVersionStream =
                getClass().getResourceAsStream(ANT_VERSION_TXT);
        // when on module path, try harder
        if (antVersionStream == null) {
            try {
                antVersionStream = Class.forName(ANT_MAIN_CLASS)
                        .getResourceAsStream(ANT_VERSION_TXT);
            } catch (ClassNotFoundException e) {
                // ignore
            }
        }

        if (antVersionStream != null) {
            antProperties.load(antVersionStream);
            antVersionString = antProperties.getProperty("VERSION")
                    + " (" + antProperties.getProperty("DATE", "Unknown") + ")";
        } else {
            antVersionString = null;
        }
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
     * Method getAntVersionString.
     * @return String
     */
    public final String getAntVersionString() {
        return antVersionString;
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
