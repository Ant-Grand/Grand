// $Id$
/*
 * ====================================================================
 * Copyright (c) 2002-2003, Christophe Labouisse All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: 1.
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution.
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

package net.ggtools.grand.tasks;

import java.io.File;
import java.io.IOException;

import net.ggtools.grand.utils.*;
import net.ggtools.grand.utils.FileComparator;

import org.apache.tools.ant.BuildException;

/**
 * Tests for GrandTask.
 * 
 * @author Christophe Labouisse
 */
public class GrandTaskTest extends AbstractAntTester {

    private static final String BUILD_SIMPLE_SHA1 = "4fd05ee05abf302f597e925bcff29bc8f1da5031";

    private static final String BUILD_OVERRIDE_SHA1 = "fbeac4ca0c4e9a7c01883d260f3b6731a12dc2e6";

    private static final String BUILD_IMPORT_SHA1 = "413f951e5a6db291d751afb32841269ea06a6c07";

    public GrandTaskTest(String name) {
        super(name);
    }

    /**
     * @param expectedDigest
     * @throws NoSuchAlgorithmException
     * @throws FileNotFoundException
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
    
    
    /* (non-Javadoc)
     * @see net.ggtools.grand.tasks.AbstractTaskTester#getTestBuildFileName()
     */
    protected String getTestBuildFileName() {
        return TESTCASES_DIR+"grand-task.xml";
    }
    
    public void testSuitability() {
        boolean suitable;

        try {
            project.checkTaskClass(GrandTask.class);
            suitable = true;
        } catch (BuildException e) {
            suitable = false;
        }

        assertTrue("DependGraphTask suitability", suitable);
    }

    /**
     * Tests if the task.properties ressource file creates the
     * grand test.
     *
     */
    public void testTaskDefinitionFile() {
        executeTarget("init-old");
        Class graphTaskClass = (Class) project.getTaskDefinitions().get("grand");
        assertNotNull("grand task class not found", graphTaskClass);
        assertEquals("Wrong class found for task", GrandTask.class, graphTaskClass);
    }

    /**
     * Test if the antlib.xml resource correctly initialize custom
     * tasks and types.
     *
     */
    public void testAntLib() {
        executeTarget("init");
        Class graphTaskClass = (Class) project.getTaskDefinitions().get("grand");
        assertNotNull("grand task class not found", graphTaskClass);
        assertEquals("Wrong class found for task", GrandTask.class, graphTaskClass);
    }

    public void testNoParam() {
        expectBuildException("test-no-param", "required attribute missing");
    }

    public void testCurrentProject() {
        expectLogContaining("test-current-project", "Using current project");
        assertLogContaining("Writing output to ");
        assertNotNull("temp.file property", project.getProperty(TEMP_FILE_PROP));
    }

    public void testOverride() throws IOException {
        expectLogContaining("property-file", "Overriding default properties from ");
        assertLogContaining("src/etc/testcases/build-simple.xml");

        assertTempFileMatchExpected("src/etc/testcases/override.dot");
    }

    public void testSimpleBuild() throws IOException {
        expectLogContaining("simple-build", "Loading project ");
        assertLogContaining("src/etc/testcases/build-simple.xml");

        assertTempFileMatchExpected("src/etc/testcases/build-simple.dot");
    }

    public void testImport() throws IOException {
        expectLogContaining("import", "Loading project ");
        assertLogContaining("src/etc/testcases/build-import.xml");

        assertTempFileMatchExpected("src/etc/testcases/build-import.dot");
    }

    public void testAntCall() throws IOException {
        expectLogContaining("antcall", "Loading project ");
        assertLogContaining("src/etc/testcases/build-complex.xml");

        assertTempFileMatchExpected("src/etc/testcases/build-complex.dot");
    }

    public void testNonExistentDefaultTarget() {
        expectLogContaining("non-existent-default-target", "Loading project ");
        assertLogContaining("src/etc/testcases/non-existent-default-target.xml");
    }

}
