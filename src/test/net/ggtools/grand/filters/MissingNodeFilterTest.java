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

package net.ggtools.grand.filters;

import net.ggtools.grand.ant.AntProject;
import net.ggtools.grand.exceptions.GrandException;
import net.ggtools.grand.graph.Graph;
import net.ggtools.grand.graph.GraphProducer;
import net.ggtools.grand.utils.AbstractAntTester;

/**
 * 
 * 
 * @author Christophe Labouisse
 */
public class MissingNodeFilterTest extends AbstractAntTester {
    /**
     * Field producer.
     */
    private GraphProducer producer;
    
    /**
     * Constructor for IsolatedNodeFilterTest.
     * @param name
     */
    public MissingNodeFilterTest(final String name) {
        super(name);
    }

    /**
     * Method setUp.
     * @see TestCase#setUp()
     */
    @Override
    protected void setUp() {
        super.setUp();
        producer = new AntProject(project);
    }

    /**
     * Method getTestBuildFileName.
     * @return String
     * @see net.ggtools.grand.utils.AbstractTaskTester#getTestBuildFileName()
     */
    @Override
    protected String getTestBuildFileName() {
        return TESTCASES_DIR + "missing-node-filter.xml";
    }

    /**
     * Check the full graph completeness.
     *
     * @throws GrandException
     */
    public void testFullGraph() throws GrandException {
        final Graph graph = producer.getGraph();
        
        assertNotNull("Target not found",graph.getNode("init"));
        assertNotNull("Target not found",graph.getNode("depend-1"));
        assertNotNull("Target not found",graph.getNode("depend-2"));
        assertNotNull("Target not found",graph.getNode("depend-3"));
        assertNotNull("Target not found",graph.getNode("missing"));
    }

    
    /**
     * Process the full graph through an MissingNodeFilter and check the
     * remaining nodes. This test includes removing the project's start node.
     *
     * @throws GrandException
     */
    public void testFilter() throws GrandException {
        final GraphFilter filter = new MissingNodeFilter();
        filter.setProducer(producer);
        final Graph graph = filter.getGraph();
        
        assertNotNull("Normal node should not have been removed",graph.getNode("init"));
        assertNotNull("Normal node should not have been removed",graph.getNode("depend-1"));
        assertNotNull("Normal node should not have been removed",graph.getNode("depend-2"));
        assertNotNull("Normal node should not have been removed",graph.getNode("depend-3"));
        assertNull("Missing node should have been found",graph.getNode("missing"));
    }
}
