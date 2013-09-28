// $Id$
/*
 * ====================================================================
 * Copyright (c) 2002-2003, Christophe Labouisse All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer. 2. Redistributions in
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

import java.io.IOException;

import net.ggtools.grand.utils.AbstractAntTester;

import org.apache.tools.ant.BuildException;

/**
 * Tests for GrandTask.
 *
 * @author Christophe Labouisse
 */
public class GrandTaskTest extends AbstractAntTester {

    /**
     * Constructor for GrandTaskTest.
     * @param name String
     */
    public GrandTaskTest(final String name) {
        super(name);
    }

    /**
     * Method getTestBuildFileName.
     * @return String
     * @see net.ggtools.grand.tasks.AbstractTaskTester#getTestBuildFileName()
     */
    @Override
    protected final String getTestBuildFileName() {
        return TESTCASES_DIR + "grand-task.xml";
    }

    /**
     * Method testSuitability.
     */
    public final void testSuitability() {
        boolean suitable;

        try {
            project.checkTaskClass(GrandTask.class);
            suitable = true;
        } catch (final BuildException e) {
            suitable = false;
        }

        assertTrue("DependGraphTask suitability", suitable);
    }

    /**
     * Tests if the task.properties resource file creates the grand test.
     *
     */
    public final void testTaskDefinitionFile() {
        executeTarget("init-old");
        final Class<?> graphTaskClass = project.getTaskDefinitions().get("grand");
        assertNotNull("grand task class not found", graphTaskClass);
        assertEquals("Wrong class found for task", GrandTask.class, graphTaskClass);
    }

    /**
     * Test if the antlib.xml resource correctly initialize custom tasks and
     * types.
     *
     */
    public final void testAntLib() {
        executeTarget("init");
        final Class<?> graphTaskClass = project.getTaskDefinitions().get("grand");
        assertNotNull("grand task class not found", graphTaskClass);
        assertEquals("Wrong class found for task", GrandTask.class, graphTaskClass);
    }

    /**
     * Method testNoParam.
     */
    public final void testNoParam() {
        expectBuildException("test-no-param", "required attribute missing");
    }

    /**
     * Method testCurrentProject.
     */
    public final void testCurrentProject() {
        expectLogContaining("test-current-project", "Using current project");
        assertLogContaining("Writing output to ");
        assertNotNull("temp.file property", project.getProperty(TEMP_FILE_PROP));
    }

    /**
     * Method testOverride.
     * @throws IOException
     */
    public final void testOverride() throws IOException {
        expectLogContaining("output-config-file", "Overriding default properties from ");
        assertLogContaining("build-simple.xml");

        assertTempFileMatchExpected("src/etc/testcases/override.dot");
    }

    /**
     * Method testSimpleBuild.
     * @throws IOException
     */
    public final void testSimpleBuild() throws IOException {
        expectLogContaining("simple-build", "Loading project ");
        assertLogContaining("build-simple.xml");

        assertTempFileMatchExpected("src/etc/testcases/build-simple.dot");
    }

    /**
     * Method testSimpleBuildWithGraphName.
     * @throws IOException
     */
    public final void testSimpleBuildWithGraphName() throws IOException {
        expectLogContaining("simple-build-with-graph-name", "Loading project ");
        assertLogContaining("build-simple.xml");

        assertTempFileMatchExpected("src/etc/testcases/build-simple-with-graph-name.dot");
    }

    /**
     * Method testImport.
     * @throws IOException
     */
    public final void testImport() throws IOException {
        expectLogContaining("import", "Loading project ");
        assertLogContaining("build-import.xml");

        assertTempFileMatchExpected("src/etc/testcases/build-import.dot");
    }

    /**
     * Method testAntCall.
     * @throws IOException
     */
    public final void testAntCall() throws IOException {
        expectLogContaining("antcall", "Loading project ");
        assertLogContaining("build-complex.xml");

        assertTempFileMatchExpected("src/etc/testcases/build-complex.dot");
    }

    /**
     * Method testSubant.
     * @throws IOException
     */
    public final void testSubant() throws IOException {
        expectLogContaining("subant", "Loading project ");
        assertLogContaining("subant.xml");

        // This part of the test does not work from Maven so I disable it by default.
        if (Boolean.parseBoolean(System.getProperty("PerformSubantTest", "false"))) {
            assertTempFileMatchExpected("src/etc/testcases/subant.dot");
        } else {
            System.err.println("Subant test disabled by default, run with -DPerformSubantTest=true to enable it");
        }
    }

    /**
     * Method testNonExistentDefaultTarget.
     */
    public final void testNonExistentDefaultTarget() {
        expectLogContaining("non-existent-default-target", "Loading project ");
        assertLogContaining("non-existent-default-target.xml");
    }

}
