/*
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowledgement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowledgements normally appear.
 *
 * 4. The names "Ant" and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package org.apache.tools.ant;

import static org.junit.Assert.*;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URL;

/**
 * A BuildFileTest is a TestCase which executes targets from an Ant buildfile
 * for testing.
 *
 * This class provides a number of utility methods for particular build file
 * tests which extend this class.
 *
 * @author <a href="mailto:nico@seessle.de">Nico Seessle</a>
 * @author Conor MacNeill
 */
public abstract class BuildFileTest {

    /**
     * Field project.
     */
    protected Project project;

    /**
     * Field logBuffer.
     */
    private StringBuffer logBuffer;

    /**
     * Field fullLogBuffer.
     */
    private StringBuffer fullLogBuffer;

    /**
     * Field outBuffer.
     */
    private StringBuffer outBuffer;

    /**
     * Field errBuffer.
     */
    private StringBuffer errBuffer;

    /**
     * Field buildException.
     */
    private BuildException buildException;

    /**
     * Run a target, expect any build exception.
     * @param  target target to run
     * @param  cause  information string to reader of report
     */
    protected final void expectBuildException(final String target,
            final String cause) {
        expectSpecificBuildException(target, cause, null);
    }

    /**
     * Assert that only the given message has been logged with a
     * priority &gt;= INFO when running the given target.
     * @param target String
     * @param log String
     */
    protected final void expectLog(final String target, final String log) {
        executeTarget(target);
        final String realLog = getLog();
        assertEquals(log, realLog);
    }

    /**
     * Assert that the given substring is in the log messages.
     * @param substring String
     */
    protected final void assertLogContaining(final String substring) {
        final String realLog = getLog();
        assertTrue("expecting log to contain \"" + substring + "\" log was \""
                   + realLog + "\"", realLog.contains(substring));
    }

    /**
     * Assert that the given message has been logged with a priority
     * &gt;= INFO when running the given target.
     * @param target String
     * @param log String
     */
    protected final void expectLogContaining(final String target,
            final String log) {
        executeTarget(target);
        assertLogContaining(log);
    }

    /**
     * Gets the log the BuildFileTest object.
     * Only valid if configureProject() has been called.
     * @pre logBuffer!=null
     * @return    The log value
     */
    protected final String getLog() {
        return logBuffer.toString();
    }

    /**
     * Assert that the given message has been logged with a priority
     * &gt;= DEBUG when running the given target.
     * @param target String
     * @param log String
     */
    protected final void expectDebuglog(final String target,
            final String log) {
        executeTarget(target);
        final String realLog = getFullLog();
        assertEquals(log, realLog);
    }

    /**
     * Gets the log the BuildFileTest object.
     * Only valid if configureProject() has been called.
     * @pre fullLogBuffer!=null
     * @return    The log value
     */
    protected final String getFullLog() {
        return fullLogBuffer.toString();
    }

    /**
     * Execute the target, verify output matches expectations.
     * @param  target  target to execute
     * @param  output  output to look for
     */
    protected final void expectOutput(final String target,
            final String output) {
        executeTarget(target);
        final String realOutput = getOutput();
        assertEquals(output, realOutput.trim());
    }

    /**
     * Execute the target, verify output matches expectations
     * and that we got the named error at the end.
     * @param  target  target to execute
     * @param  output  output to look for
     * @param  error   Description of Parameter
     */
    protected final void expectOutputAndError(final String target,
            final String output, final String error) {
        executeTarget(target);
        final String realOutput = getOutput();
        assertEquals(output, realOutput);
        final String realError = getError();
        assertEquals(error, realError);
    }

    /**
     * Method getOutput.
     * @return String
     */
    protected final String getOutput() {
        return cleanBuffer(outBuffer);
    }

    /**
     * Method getError.
     * @return String
     */
    protected final String getError() {
        return cleanBuffer(errBuffer);
    }

    /**
     * Method getBuildException.
     * @return BuildException
     */
    protected final BuildException getBuildException() {
        return buildException;
    }

    /**
     * Method cleanBuffer.
     * @param buffer StringBuffer
     * @return String
     */
    private String cleanBuffer(final StringBuffer buffer) {
        final StringBuilder cleanedBuffer = new StringBuilder();
        for (int i = 0; i < buffer.length(); i++) {
            final char ch = buffer.charAt(i);
            if (ch == '\r') {
                continue;
            }
            cleanedBuffer.append(ch);
        }
        return cleanedBuffer.toString();
    }

    /**
     * Set up to run the named project.
     *
     * @param  filename name of project file to run
     * @throws BuildException if project setup fails
     */
    protected final void configureProject(final String filename)
            throws BuildException {
        configureProject(filename, Project.MSG_DEBUG);
    }

    /**
     * Set up to run the named project.
     * @param  filename name of project file to run
     * @param  logLevel int
     * @throws BuildException if project setup fails
     */
    protected final void configureProject(final String filename,
            final int logLevel) throws BuildException {
        logBuffer = new StringBuffer();
        fullLogBuffer = new StringBuffer();
        project = new Project();
        project.init();
        project.setUserProperty("ant.file",
                new File(filename).getAbsolutePath());
        project.addBuildListener(new AntTestListener(logLevel));
        final ProjectHelper loader = ProjectHelper.getProjectHelper();
        project.addReference("ant.projectHelper", loader);
        loader.parse(project, new File(filename));
    }

    /**
     * Execute a target we have set up.
     * @pre configureProject has been called
     * @param  targetName  target to run
     */
    protected final void executeTarget(final String targetName) {
        final PrintStream sysOut = System.out;
        final PrintStream sysErr = System.err;
        try {
            sysOut.flush();
            sysErr.flush();
            outBuffer = new StringBuffer();
            final PrintStream out = new PrintStream(new AntOutputStream(outBuffer));
            System.setOut(out);
            errBuffer = new StringBuffer();
            final PrintStream err = new PrintStream(new AntOutputStream(errBuffer));
            System.setErr(err);
            logBuffer = new StringBuffer();
            fullLogBuffer = new StringBuffer();
            buildException = null;
            project.executeTarget(targetName);
        } finally {
            System.setOut(sysOut);
            System.setErr(sysErr);
        }

    }

    /**
     * Get the project which has been configured for a test.
     * @return the Project instance for this test.
     */
    protected final Project getProject() {
        return project;
    }

    /**
     * Get the directory of the project.
     * @return the base dir of the project
     */
    protected final File getProjectDir() {
        return project.getBaseDir();
    }

    /**
     * Run a target, wait for a build exception.
     * @param  target target to run
     * @param  cause  information string to reader of report
     * @param  msg    the message value of the build exception we are waiting for
     *         set to null for any build exception to be valid
     */
    protected final void expectSpecificBuildException(final String target,
            final String cause, final String msg) {
        try {
            executeTarget(target);
        } catch (final BuildException ex) {
            buildException = ex;
            if ((null != msg) && (!ex.getMessage().equals(msg))) {
                fail("Should throw BuildException because '" + cause
                        + "' with message '" + msg
                        + "' (actual message '" + ex.getMessage() + "' instead)");
            }
            return;
        }
        fail("Should throw BuildException because: " + cause);
    }

    /**
     * Run a target, expect an exception string,
     * containing the substring we look for (case sensitive match).
     * @param  target   target to run
     * @param  cause    information string to reader of report
     * @param  contains substring of the build exception to look for
     */
    protected final void expectBuildExceptionContaining(final String target,
            final String cause, final String contains) {
        try {
            executeTarget(target);
        } catch (final BuildException ex) {
            buildException = ex;
            if ((null != contains) && (!ex.getMessage().contains(contains))) {
                fail("Should throw BuildException because '" + cause
                        + "' with message containing '" + contains
                        + "' (actual message '" + ex.getMessage() + "' instead)");
            }
            return;
        }
        fail("Should throw BuildException because: " + cause);
    }


    /**
     * Call a target, verify property is as expected.
     * @param target build file target
     * @param property property name
     * @param value expected value
     */
    protected final void expectPropertySet(final String target,
            final String property, final String value) {
        executeTarget(target);
        assertPropertyEquals(property, value);
    }

    /**
     * Assert that a property equals a value; comparison is case sensitive.
     * @param property property name
     * @param value expected value
     */
    protected final void assertPropertyEquals(final String property,
            final String value) {
        final String result = project.getProperty(property);
        assertEquals("property " + property, value, result);
    }

    /**
     * Assert that a property equals &quot;true&quot;.
     * @param property property name
     */
    protected final void assertPropertySet(final String property) {
        assertPropertyEquals(property, "true");
    }

    /**
     * Assert that a property is null.
     * @param property property name
     */
    protected final void assertPropertyUnset(final String property) {
        assertPropertyEquals(property, null);
    }

    /**
     * Call a target, verify named property is "true".
     * @param target build file target
     * @param property property name
     */
    protected final void expectPropertySet(final String target,
            final String property) {
        expectPropertySet(target, property, "true");
    }

    /**
     * Call a target, verify property is null.
     * @param target build file target
     * @param property property name
     */
    protected final void expectPropertyUnset(final String target,
            final String property) {
        expectPropertySet(target, property, null);
    }

    /**
     * Retrieve a resource from the caller classloader to avoid
     * assuming a vm working directory. The resource path must be
     * relative to the package name or absolute from the root path.
     * @param resource the resource to retrieve its url.
     * @return URL
     */
    protected final URL getResource(final String resource) {
        final URL url = getClass().getResource(resource);
        assertNotNull("Could not find resource :" + resource, url);
        return url;
    }

    /**
     * an output stream which saves stuff to our buffer.
     */
    private static class AntOutputStream extends OutputStream {
        /**
         * Field buffer.
         */
        private final StringBuffer buffer;

        /**
         * Constructor for AntOutputStream.
         * @param buffer StringBuffer
         */
        public AntOutputStream(final StringBuffer buffer) {
            this.buffer = buffer;
        }

        /**
         * Method write.
         * @param b int
         */
        @Override
        public void write(final int b) {
            buffer.append((char) b);
        }
    }

    /**
     * Our own personal build listener.
     */
    private class AntTestListener implements BuildListener {
        /**
         * Field logLevel.
         */
        private final int logLevel;

        /**
         * Constructs a test listener which will ignore log events
         * above the given level.
         * @param logLevel int
         */
        public AntTestListener(final int logLevel) {
            this.logLevel = logLevel;
        }

        /**
         * Fired before any targets are started.
         * @param event BuildEvent
         * @see org.apache.tools.ant.BuildListener#buildStarted(BuildEvent)
         */
        public void buildStarted(final BuildEvent event) {
        }

        /**
         * Fired after the last target has finished. This event
         * will still be thrown if an error occurred during the build.
         *
         * @param event BuildEvent
         * @see BuildEvent#getException()
         */
        public void buildFinished(final BuildEvent event) {
        }

        /**
         * Fired when a target is started.
         *
         * @param event BuildEvent
         * @see BuildEvent#getTarget()
         */
        public void targetStarted(final BuildEvent event) {
            //System.out.println("targetStarted " + event.getTarget().getName());
        }

        /**
         * Fired when a target has finished. This event will
         * still be thrown if an error occurred during the build.
         *
         * @param event BuildEvent
         * @see BuildEvent#getException()
         */
        public void targetFinished(final BuildEvent event) {
            //System.out.println("targetFinished " + event.getTarget().getName());
        }

        /**
         * Fired when a task is started.
         *
         * @param event BuildEvent
         * @see BuildEvent#getTask()
         */
        public void taskStarted(final BuildEvent event) {
            //System.out.println("taskStarted " + event.getTask().getTaskName());
        }

        /**
         * Fired when a task has finished. This event will still
         * be throw if an error occurred during the build.
         *
         * @param event BuildEvent
         * @see BuildEvent#getException()
         */
        public void taskFinished(final BuildEvent event) {
            //System.out.println("taskFinished " + event.getTask().getTaskName());
        }

        /**
         * Fired whenever a message is logged.
         *
         * @param event BuildEvent
         * @see BuildEvent#getMessage()
         * @see BuildEvent#getPriority()
         */
        public void messageLogged(final BuildEvent event) {
            if (event.getPriority() > logLevel) {
                // ignore event
                return;
            }

            if ((event.getPriority() == Project.MSG_INFO)
                    || (event.getPriority() == Project.MSG_WARN)
                    || (event.getPriority() == Project.MSG_ERR)) {
                logBuffer.append(event.getMessage()).append(System.getProperty("line.separator"));
            }
            fullLogBuffer.append(event.getMessage());

        }
    }

}
