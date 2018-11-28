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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.apache.tools.ant.BuildException;
import org.junit.Before;
import org.junit.Test;

import net.ggtools.grand.utils.AbstractAntTester;

/**
 * Tests for GrandTask.
 *
 * @author Christophe Labouisse
 */
public class GrandTaskTest extends AbstractAntTester {

    /**
     * Method setUp.
     */
    @Before
    public final void setUp() {
        configureProject(getTestBuildFileName());
        project.setBasedir(TESTCASES_DIR);
    }

    /**
     * Method getTestBuildFileName.
     * @return String
     */
    private String getTestBuildFileName() {
        return TESTCASES_DIR + "grand-task.xml";
    }

    /**
     * Method testSuitability.
     */
    @Test
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
    @Test
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
    @Test
    public final void testAntLib() {
        executeTarget("init");
        final Class<?> graphTaskClass = project.getTaskDefinitions().get("grand");
        assertNotNull("grand task class not found", graphTaskClass);
        assertEquals("Wrong class found for task", GrandTask.class, graphTaskClass);
    }

    /**
     * Method testNoParam.
     */
    @Test
    public final void testNoParam() {
        expectBuildException("test-no-param", "required attribute missing");
    }

    /**
     * Method testCurrentProject.
     */
    @Test
    public final void testCurrentProject() {
        expectLogContaining("test-current-project", "Using current project");
        assertLogContaining("Writing output to ");
        assertNotNull("temp.file property", project.getProperty(TEMP_FILE_PROP));
    }

    /**
     * Method testFileOverride.
     * @throws IOException if file comparator fails
     */
    @Test
    public final void testFileOverride() throws IOException {
        expectLogContaining("output-config-file", "Overriding default output configuration from ");
        assertLogContaining("build-simple.xml");

        assertTempFileMatchExpected(TESTCASES_DIR + "override.dot");
    }

    /**
     * Method testImpossibleOverride.
     */
    @Test
    public final void testImpossibleOverride() {
        expectBuildException("output-config-file-and-prefix", "cannot specify both outputconfigfile and outputconfigprefix");
   }

    /**
     * Method testPrefixOverride.
     * @throws IOException if file comparator fails
     */
    @Test
    public final void testPrefixOverride() throws IOException {
        expectLogContaining("output-config-prefix", "Overriding default output configuration from project properties grand");
        assertLogContaining("build-simple.xml");

        assertTempFileMatchExpected(TESTCASES_DIR + "override-prefix.dot");
    }

    /**
     * Method testSimpleBuild.
     * @throws IOException if file comparator fails
     */
    @Test
    public final void testSimpleBuild() throws IOException {
        expectLogContaining("simple-build", "Loading project ");
        assertLogContaining("build-simple.xml");

        assertTempFileMatchExpected(TESTCASES_DIR + "build-simple.dot");
    }

    /**
     * Method testSimpleBuildWithGraphName.
     * @throws IOException if file comparator fails
     */
    @Test
    public final void testSimpleBuildWithGraphName() throws IOException {
        expectLogContaining("simple-build-with-graph-name", "Loading project ");
        assertLogContaining("build-simple.xml");

        assertTempFileMatchExpected(TESTCASES_DIR + "build-simple-with-graph-name.dot");
    }

    /**
     * Method testImport.
     * @throws IOException if file comparator fails
     */
    @Test
    public final void testImport() throws IOException {
        expectLogContaining("import", "Loading project ");
        assertLogContaining("build-import.xml");

        assertTempFileMatchExpected(TESTCASES_DIR + "build-import.dot");
    }

    /**
     * Method testAntCall.
     * @throws IOException if file comparator fails
     */
    @Test
    public final void testAntCall() throws IOException {
        expectLogContaining("antcall", "Loading project ");
        assertLogContaining("build-complex.xml");

        assertTempFileMatchExpected(TESTCASES_DIR + "build-complex.dot");
    }

    /**
     * Method testSubant.
     * @throws IOException if file comparator fails
     */
    @Test
    public final void testSubant() throws IOException {
        expectLogContaining("subant", "Loading project ");
        assertLogContaining("subant.xml");
        // This part of the test does not work from test suite so I disable it by default.
        if (Boolean.parseBoolean(System.getProperty("PerformSubantTest", "false"))) {
            assertTempFileMatchExpected(TESTCASES_DIR + "subant.dot");
        } else {
            System.err.println("Subant test disabled by default, run with -DPerformSubantTest=true to enable it");
        }
    }

    /**
     * Method testNonExistentDefaultTarget.
     */
    @Test
    public final void testNonExistentDefaultTarget() {
        expectLogContaining("non-existent-default-target", "Loading project ");
        assertLogContaining("non-existent-default-target.xml");
    }

}
