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
 * overriden by setting the <code>CleanupOnError</code> system property to
 * <code>true</code>.
 * 
 * @author Christophe Labouisse
 */
public abstract class AbstractAntTester extends BuildFileTest {
    protected static final String JUNIT_TEST_NAME = "junit.test.name";

    protected static final String TEMP_FILE_PROP = "temp.file";

    protected static final String TESTCASES_DIR = "src/etc/testcases/";

    private boolean testOk = true;

    public AbstractAntTester(String name) {
        super(name);
    }

    /*
     * (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() {
        configureProject(getTestBuildFileName());
        project.setBasedir(TESTCASES_DIR);
        project.setProperty(JUNIT_TEST_NAME, getName());
    }

    /*
     * (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() {
        final String tempFile = project.getProperty(TEMP_FILE_PROP);

        if (tempFile != null) {
            if (testOk || Boolean.parseBoolean(System.getProperty("CleanupOnError", "false"))) {
                final File f = new File(tempFile);
                f.delete();
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
     * @param reference
     * @throws IOException
     */
    protected void assertTempFileMatchExpected(String reference) throws IOException {
        final String tempFileProp = project.getProperty(TEMP_FILE_PROP);
        assertNotNull("temp.file property", tempFileProp);
        File tempFile = new File(tempFileProp);

        File referenceFile = new File(reference);

        FileComparator comparator = new FileComparator(referenceFile, tempFile);
        comparator.assertLinesMatch();
    }

    protected void assertFullLogContaining(String substring) {
        String realLog = getFullLog();
        assertTrue("expecting full log to contain \"" + substring + "\" full log was \"" + realLog
                + "\"", realLog.indexOf(substring) >= 0);
    }

    /**
     * Assert that the given message has been logged when running the given
     * target.
     */
    protected void expectFullLogContaining(String target, String log) {
        executeTarget(target);
        assertFullLogContaining(log);
    }

    /*
     * (non-Javadoc)
     * @see junit.framework.TestCase#runTest()
     */
    protected void runTest() throws Throwable {
        try {
            super.runTest();
        } catch (Throwable t) {
            System.err.println("Ooops test failed: " + getName() + " "
                    + project.getProperty(TEMP_FILE_PROP));
            System.err.println("Log was: "+getLog());
            testOk = false;
            throw t;
        }
    }
}
