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
 * Simple log class logging to stdout.
 * 
 * @author Christophe Labouisse
 */
class SimpleLog implements Log {
    protected final static int LEVEL_NONE = 0;
    protected final static int LEVEL_FATAL = 1;
    protected final static int LEVEL_ERROR = 2;
    protected final static int LEVEL_WARN = 3;
    protected final static int LEVEL_INFO = 4;
    protected final static int LEVEL_DEBUG = 5;
    protected final static int LEVEL_TRACE = 6;
    protected final static int LEVEL_ALL = 7;

    protected static final String[] LEVEL_NAMES = {"NONE", "FATAL", "ERROR", "WARN", "INFO",
            "DEBUG", "TRACE", "ALL"};

    private static int logLevel = LEVEL_WARN;

    /**
     * Package only instanciation.
     */
    SimpleLog() {
    }

    /*
     * (non-Javadoc)
     * @see org.apache.commons.logging.Log#debug(java.lang.Object)
     */
    public void debug(Object message) {
        log(message, LEVEL_DEBUG);
    }

    /*
     * (non-Javadoc)
     * @see org.apache.commons.logging.Log#debug(java.lang.Object,
     *      java.lang.Throwable)
     */
    public void debug(Object message, Throwable t) {
        log(message, t, LEVEL_DEBUG);
    }

    /*
     * (non-Javadoc)
     * @see org.apache.commons.logging.Log#error(java.lang.Object)
     */
    public void error(Object message) {
        log(message, LEVEL_ERROR);
    }

    /*
     * (non-Javadoc)
     * @see org.apache.commons.logging.Log#error(java.lang.Object,
     *      java.lang.Throwable)
     */
    public void error(Object message, Throwable t) {
        log(message, t, LEVEL_ERROR);
    }

    /*
     * (non-Javadoc)
     * @see org.apache.commons.logging.Log#fatal(java.lang.Object)
     */
    public void fatal(Object message) {
        log(message, LEVEL_FATAL);
    }

    /*
     * (non-Javadoc)
     * @see org.apache.commons.logging.Log#fatal(java.lang.Object,
     *      java.lang.Throwable)
     */
    public void fatal(Object message, Throwable t) {
        log(message, t, LEVEL_FATAL);
    }

    /*
     * (non-Javadoc)
     * @see org.apache.commons.logging.Log#info(java.lang.Object)
     */
    public void info(Object message) {
        log(message, LEVEL_INFO);
    }

    /*
     * (non-Javadoc)
     * @see org.apache.commons.logging.Log#info(java.lang.Object,
     *      java.lang.Throwable)
     */
    public void info(Object message, Throwable t) {
        log(message, t, LEVEL_INFO);
    }

    /*
     * (non-Javadoc)
     * @see org.apache.commons.logging.Log#isDebugEnabled()
     */
    public boolean isDebugEnabled() {
        return logLevel >= LEVEL_DEBUG;
    }

    /*
     * (non-Javadoc)
     * @see org.apache.commons.logging.Log#isErrorEnabled()
     */
    public boolean isErrorEnabled() {
        return logLevel >= LEVEL_ERROR;
    }

    /*
     * (non-Javadoc)
     * @see org.apache.commons.logging.Log#isFatalEnabled()
     */
    public boolean isFatalEnabled() {
        return logLevel >= LEVEL_FATAL;
    }

    /*
     * (non-Javadoc)
     * @see org.apache.commons.logging.Log#isInfoEnabled()
     */
    public boolean isInfoEnabled() {
        return logLevel >= LEVEL_INFO;
    }

    /*
     * (non-Javadoc)
     * @see org.apache.commons.logging.Log#isTraceEnabled()
     */
    public boolean isTraceEnabled() {
        return logLevel >= LEVEL_TRACE;
    }

    /*
     * (non-Javadoc)
     * @see org.apache.commons.logging.Log#isWarnEnabled()
     */
    public boolean isWarnEnabled() {
        return logLevel >= LEVEL_WARN;
    }

    /*
     * (non-Javadoc)
     * @see org.apache.commons.logging.Log#trace(java.lang.Object)
     */
    public void trace(Object message) {
        log(message, LEVEL_TRACE);
    }

    /*
     * (non-Javadoc)
     * @see org.apache.commons.logging.Log#trace(java.lang.Object,
     *      java.lang.Throwable)
     */
    public void trace(Object message, Throwable t) {
        log(message, t, LEVEL_TRACE);
    }

    /*
     * (non-Javadoc)
     * @see org.apache.commons.logging.Log#warn(java.lang.Object)
     */
    public void warn(Object message) {
        log(message, LEVEL_WARN);
    }

    /*
     * (non-Javadoc)
     * @see org.apache.commons.logging.Log#warn(java.lang.Object,
     *      java.lang.Throwable)
     */
    public void warn(Object message, Throwable t) {
        log(message, t, LEVEL_WARN);
    }

    protected void log(final Object message, final int level) {
        log(message, null, level);
    }

    protected void log(final Object message, final Throwable t, final int level) {
        if (level <= logLevel) {
            System.out.println("[" + LEVEL_NAMES[level] + "] " + message);
            if (t != null) {
                t.printStackTrace(System.out);
            }
        }
    }

}
