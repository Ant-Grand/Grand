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

package net.ggtools.grand.utils;

import java.io.File;
import java.io.IOException;

import org.apache.tools.ant.BuildFileTest;

/**
 * An abstract class for Ant test featuring a standard project setup and a
 * tearDown method removing temporary file after running a test.
 *
 * This clean up is disabled is the test fails. However this behaviour can be
 * overridden by setting the <code>CleanupOnError</code> system property to
 * <code>true</code>.
 *
 * @author Christophe Labouisse
 */
public abstract class AbstractAntTester extends BuildFileTest {
    /**
     * Field JUNIT_TEST_NAME.
     * (value is ""junit.test.name"")
     */
    protected static final String JUNIT_TEST_NAME = "junit.test.name";

    /**
     * Field TEMP_FILE_PROP.
     * (value is ""temp.file"")
     */
    protected static final String TEMP_FILE_PROP = "temp.file";

    /**
     * Field TESTCASES_DIR.
     * (value is ""src/etc/testcases/"")
     */
    protected static final String TESTCASES_DIR = "src/etc/testcases/";

    /**
     * Field testOk.
     */
    private boolean testOk = true;

    /**
     * Constructor for AbstractAntTester.
     * @param name String
     */
    public AbstractAntTester(final String name) {
        super(name);
    }

    /**
     * Method setUp.
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() {
        configureProject(getTestBuildFileName());
        project.setBasedir(TESTCASES_DIR);
        project.setProperty(JUNIT_TEST_NAME, getName());
    }

    /**
     * Method tearDown.
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() {
        final String tempFile = project.getProperty(TEMP_FILE_PROP);

        if (tempFile != null) {
            if (testOk || Boolean.parseBoolean(System.getProperty("CleanupOnError", "false"))) {
                final File file = new File(tempFile);
                file.delete();
            }
        }
    }

    /**
     * Returns this test case's ant build file.
     *
     * @return the full or relative path to the build file.
     */
    protected abstract String getTestBuildFileName();

    /**
     * Compares the temporary file with a reference file.
     *
     * @param reference String
     * @throws IOException
     */
    protected final void assertTempFileMatchExpected(final String reference) throws IOException {
        final String tempFileProp = project.getProperty(TEMP_FILE_PROP);
        assertNotNull("temp.file property", tempFileProp);
        final File tempFile = new File(tempFileProp);

        final File referenceFile = new File(reference);

        final FileComparator comparator = new FileComparator(referenceFile, tempFile);
        comparator.assertLinesMatch();
    }

    /**
     * Method assertFullLogContaining.
     * @param substring String
     */
    protected final void assertFullLogContaining(final String substring) {
        final String realLog = getFullLog();
        assertTrue("expecting full log to contain \"" + substring + "\" full log was \"" + realLog
                + "\"", realLog.indexOf(substring) >= 0);
    }

    /**
     * Assert that the given message has been logged when running the given
     * target.
     * @param target String
     * @param log String
     */
    protected final void expectFullLogContaining(final String target, final String log) {
        executeTarget(target);
        assertFullLogContaining(log);
    }

    /**
     * Method runTest.
     * @throws Throwable
     * @see junit.framework.TestCase#runTest()
     */
    @Override
    protected final void runTest() throws Throwable {
        try {
            super.runTest();
        } catch (final Throwable t) {
            System.err.println("Ooops test failed: " + getName() + " "
                    + project.getProperty(TEMP_FILE_PROP));
            System.err.println("Log was: " + getLog());
            testOk = false;
            throw t;
        }
    }
}
