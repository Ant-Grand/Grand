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

import java.io.File;

import net.ggtools.grand.ant.AntProject;
import net.ggtools.grand.graph.Graph;
import net.ggtools.grand.graph.GraphFilter;
import net.ggtools.grand.graph.GraphProducer;

import org.apache.tools.ant.BuildFileTest;

/**
 * 
 * 
 * @author Christophe Labouisse
 */
public class IsolatedNodeFilterTest extends BuildFileTest {
    private static final String TEMP_FILE_PROP = "temp.file";
    private GraphProducer producer;
    
    /**
     * Constructor for IsolatedNodeFilterTest.
     * @param name
     */
    public IsolatedNodeFilterTest(String name) {
        super(name);
    }

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        configureProject("src/etc/testcases/isolated-node-filter.xml");
        project.setBasedir("src/etc/testcases");
        producer = new AntProject(project);
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        String tempFile = project.getProperty(TEMP_FILE_PROP);

        if (tempFile != null) {
            File f = new File(tempFile);
            f.delete();
        }
    }

    /**
     * Check the full graph completness.
     *
     */
    public void testFullGraph() throws Exception {
        Graph graph = producer.getGraph();
        
        assertNotNull("Target not found",graph.getNode("init"));
        assertNotNull("Target not found",graph.getNode("depend-1"));
        assertNotNull("Target not found",graph.getNode("depend-2"));
        assertNotNull("Target not found",graph.getNode("isolated"));
        assertNotNull("Start node not found",graph.getStartNode());
    }

    
    /**
     * Process the full graph through an IsolatedNodeFilter and check the
     * remaining nodes. This test includes removing the project's start node.
     *
     */
    public void testFilter() throws Exception {
        GraphFilter filter = new IsolatedNodeFilter();
        filter.setProducer(producer);
        Graph graph = filter.getGraph();
        
        assertNotNull("Connected node should not have been removed",graph.getNode("init"));
        assertNotNull("Connected node should not have been removed",graph.getNode("depend-1"));
        assertNotNull("Connected node should not have been removed",graph.getNode("depend-2"));
        assertNull("Isolated node should not have been found",graph.getNode("isolated"));
        assertNull("Isolated start node should have been removed",graph.getStartNode());
    }
    
    
    /**
     * Process the full graph through an IsolatedNodeFilter and check that a connected
     * node as start node is not removed.
     *
     */
    public void testConnectedStartNode() throws Exception {
        GraphFilter filter = new IsolatedNodeFilter();
        filter.setProducer(producer);
        project.setDefault("depend-1");
        Graph graph = filter.getGraph();
        
        assertNotNull("Connected start node should not have been removed",graph.getStartNode());
    }

    
}
