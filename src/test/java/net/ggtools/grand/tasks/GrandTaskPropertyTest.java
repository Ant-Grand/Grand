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

package net.ggtools.grand.tasks;

import org.junit.Before;
import org.junit.Test;

import net.ggtools.grand.utils.AbstractAntTester;

/**
 * Tests for GrandTask focused on properties.
 *
 * @author Christophe Labouisse
 */
public class GrandTaskPropertyTest extends AbstractAntTester {

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
        return TESTCASES_DIR + "grand-task-property.xml";
    }

    /**
     * Run the test with an undefined property.
     */
    @Test
    public final void testUndefinedProperty() {
        project.setProperty("antcall.target", "init");
        expectLogContaining("test-noprops", "Outputting to ");
        assertFullLogContaining("Creating link from antcall-props-1 to ${antcall.target}");
        assertFullLogContaining("Creating link from antcall-props-2 to do-${antcall.target}");
    }

    /**
     * Set the property in the calling project and create a graph with inheritall set.
     */
    @Test
    public final void testInheritAll() {
        project.setProperty("antcall.target", "init");
        expectLogContaining("test-inheritall", "Outputting to ");
        assertFullLogContaining("Creating link from antcall-props-1 to init");
        assertFullLogContaining("Creating link from antcall-props-2 to do-init");
    }

    /**
     * Use the nested element "property" to set antcall.target.
     */
    @Test
    public final void testPropertyElement() {
        expectLogContaining("test-property", "Outputting to ");
        assertFullLogContaining("Creating link from antcall-props-1 to init");
        assertFullLogContaining("Creating link from antcall-props-2 to do-init");
    }

    /**
     * Use the nested element "propertyset" to set antcall.target.
     */
    @Test
    public final void testPropertySet() {
        project.setProperty("antcall.target", "init");
        expectLogContaining("test-propertyset", "Outputting to ");
        assertFullLogContaining("Creating link from antcall-props-1 to init");
        assertFullLogContaining("Creating link from antcall-props-2 to do-init");
    }


    /**
     * Set the property in the calling build script, sets inheritall to true
     * and use a property element with a different value. The inheritall property
     * should prevail.
     */
    @Test
    public final void testInheritPrevailsOverProperty() {
        project.setProperty("antcall.target", "init");
        expectLogContaining("test-property", "Outputting to ");
        assertFullLogContaining("Creating link from antcall-props-1 to init");
        assertFullLogContaining("Creating link from antcall-props-2 to do-init");
    }

}
