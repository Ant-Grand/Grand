// $Id$
/*
 * ====================================================================
 * Copyright (c) 2002-2004, Christophe Labouisse All rights reserved.
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
package net.ggtools.grand.log;

import org.apache.commons.logging.Log;

/**
 * @author Christophe Labouisse
 */
public final class LoggerManager {

    /**
     * Field currentFactory.
     */
    private static LoggerFactory currentFactory;

    /**
     * Field defaultFactory.
     */
    private static final LoggerFactory DEFAULT_FACTORY = new LoggerFactory() {
        public Log getLog(final Class<?> clazz) {
            return new AntLog();
        }

        public Log getLog(final String name) {
            return new AntLog();
        }
    };

    /**
     * Method getLog.
     * @param clazz Class<?>
     * @return Log
     */
    public static Log getLog(final Class<?> clazz) {
        if (currentFactory != null) {
            return currentFactory.getLog(clazz);
        } else {
            return DEFAULT_FACTORY.getLog(clazz);
        }
    }

    /**
     * Method getLog.
     * @param name String
     * @return Log
     */
    public static Log getLog(final String name) {
        if (currentFactory != null) {
            return currentFactory.getLog(name);
        } else {
            return DEFAULT_FACTORY.getLog(name);
        }
    }

    /**
     * Method setFactory.
     * @param factory LoggerFactory
     */
    public static void setFactory(final LoggerFactory factory) {
        currentFactory = factory;
    }

    /**
     * No instantiation allowed.
     */
    private LoggerManager() {
    }

}
