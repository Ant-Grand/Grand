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

package net.ggtools.grand.tasks;

import net.ggtools.grand.utils.*;

import org.apache.tools.ant.BuildException;

/**
 *
 *
 * @author Christophe Labouisse
 */
public class GraphFilterFactoryTest extends AbstractAntTester {

    /**
     * Creates an test case.
     * @param name String
     */
    public GraphFilterFactoryTest(final String name) {
        super(name);
    }

    /**
     * Field factory.
     */
    private GraphFilterFactory factory;

    /**
     * Method setUp.
     * @see TestCase#setUp()
     */
    @Override
    protected void setUp() {
        super.setUp();
        factory = new GraphFilterFactory();
    }

    /**
     * Method tearDown.
     * @see TestCase#tearDown()
     */
    @Override
    protected void tearDown() {
        super.tearDown();
    }

    /**
     * Method getTestBuildFileName.
     * @return String
     * @see net.ggtools.grand.tasks.AbstractTaskTester#getTestBuildFileName()
     */
    @Override
    protected String getTestBuildFileName() {
        return TESTCASES_DIR + "empty.xml";
    }

    /**
     * Method testTypeIsolatedNode.
     */
    public final void testTypeIsolatedNode() {
        final GraphFilterType filter = factory.getFilterType(project, "isolatednode");
        assertEquals("Wrong class for isolatednode",
                net.ggtools.grand.tasks.IsolatedNodeFilterType.class, filter.getClass());
    }

    /**
     * Method testTypeFromNode.
     */
    public final void testTypeFromNode() {
        final GraphFilterType filter = factory.getFilterType(project, "fromnode");
        assertEquals("Wrong class for fromnode",
                net.ggtools.grand.tasks.FromNodeFilterType.class, filter.getClass());
    }

    /**
     * Method testTypeToNode.
     */
    public final void testTypeToNode() {
        final GraphFilterType filter = factory.getFilterType(project, "tonode");
        assertEquals("Wrong class for tonode",
                net.ggtools.grand.tasks.ToNodeFilterType.class, filter.getClass());
    }

    /**
     * Method testTypeConnected.
     */
    public final void testTypeConnected() {
        final GraphFilterType filter = factory.getFilterType(project, "connected");
        assertEquals("Wrong class for connected",
                net.ggtools.grand.tasks.ConnectedFilterType.class, filter.getClass());
    }

    /**
     * Method testTypeMissingNode.
     */
    public final void testTypeMissingNode() {
        final GraphFilterType filter = factory.getFilterType(project, "missingnode");
        assertEquals("Wrong class for missingNode",
                net.ggtools.grand.tasks.MissingNodeFilterType.class, filter.getClass());
    }

    /**
     * Method testTypeNodeRemover.
     */
    public final void testTypeNodeRemover() {
        final GraphFilterType filter = factory.getFilterType(project, "removenode");
        assertEquals("Wrong class for missingNode",
                NodeRemoverFilterType.class, filter.getClass());
    }

   /**
    * Method testNotConfigurated.
    */
   public final void testNotConfigurated() {
        try {
            factory.getFilterType(project, "notconfigured");
        } catch (final BuildException e) {
            assertEquals("Filter notconfigured not configured", e.getMessage());
        }
    }

    /**
     * Method testClassNotFound.
     */
    public final void testClassNotFound() {
        try {
            factory.getFilterType(project, "gabuzotestfilter");
        } catch (final BuildException e) {
            assertEquals("Cannot find filter class", e.getMessage());
        }
    }
}
