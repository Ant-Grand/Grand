// $Id$
/* ====================================================================
 * Copyright (c) 2002-2003, Christophe Labouisse
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above
 *    copyright notice, this list of conditions and the following
 *    disclaimer in the documentation and/or other materials provided
 *    with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package net.ggtools.grand;

import net.ggtools.grand.log.Logger;
import net.ggtools.grand.log.SimpleLogger;

import org.apache.tools.ant.Project;

/**
 * Poor man's Apache commons logging. This class provides a generic logging
 * facility implementation working with or without an Ant project set up.
 * 
 * @author Christophe Labouisse
 * TODO Add a task reference for cleaner logging.
 */
public final class Log {
    /** Message priority of "error". */
    public static final int MSG_ERR = Project.MSG_ERR;

    /** Message priority of "warning". */
    public static final int MSG_WARN = Project.MSG_WARN;

    /** Message priority of "information". */
    public static final int MSG_INFO = Project.MSG_INFO;

    /** Message priority of "verbose". */
    public static final int MSG_VERBOSE = Project.MSG_VERBOSE;

    /** Message priority of "debug". */
    public static final int MSG_DEBUG = Project.MSG_DEBUG;
    
    private static Logger logger = new SimpleLogger();

    /**
     * Private constructor since this class should not but
     * instanciated.
     *
     */
    private Log() {
    }

    /**
     * Writes a message to the log with the default log level
     * of MSG_INFO
     * @param message The text to log. Should not be <code>null</code>.
     */

    public static void log(final String message) {
        log(message, MSG_INFO);
    }

    /**
     * Writes a project level message to the log with the given log level.
     * @param message The text to log. Should not be <code>null</code>.
     * @param msgLevel The priority level to log at.
     */
    public static void log(final String message, final int msgLevel) {
        logger.log(message,msgLevel);
    }

    /**
     * @param level The logLevel to set.
     */
    public static void setLogLevel(final int level) {
        logger.setLogLevel(level);
    }
    
    public static void setLogger(final Logger newLogger) {
        logger = newLogger;
    }
}
