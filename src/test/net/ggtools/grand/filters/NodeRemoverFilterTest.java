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

package net.ggtools.grand.filters;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.ggtools.grand.ant.AntProject;
import net.ggtools.grand.exceptions.GrandException;
import net.ggtools.grand.graph.Graph;
import net.ggtools.grand.graph.GraphProducer;
import net.ggtools.grand.graph.Node;
import net.ggtools.grand.utils.AbstractAntTester;

/**
 * 
 * 
 * @author Christophe Labouisse
 */
public class NodeRemoverFilterTest extends AbstractAntTester {
    private GraphProducer producer;

    /**
     * Constructor for NodeRemoverFilterTest.
     * @param name
     */
    public NodeRemoverFilterTest(String name) {
        super(name);
    }

    /**
     * Process the build file, trying to removed a non existent node.
     * 
     */
    public void testNonExistentNode() throws GrandException {
        final Set toRemove = new HashSet();
        toRemove.add("gruik-gruik-you-won't-find-me");
        final GraphFilter filter = new NodeRemoverFilter(toRemove);
        filter.setProducer(producer);
        Graph graph = producer.getGraph();
        int numNode = countNodes(graph);
        graph = filter.getGraph();
        int numNodeAfterFiltering = countNodes(graph);
        assertEquals("Graph should have the same node count before and after filtering", numNode,
                numNodeAfterFiltering);
    }

    /**
     * Process log4j 1.2.8 build.xml and remove the "init" node.
     * 
     */
    public void testOneNode() throws GrandException {
        final Set toRemove = new HashSet();
        toRemove.add("init");
        GraphFilter filter = new NodeRemoverFilter(toRemove);
        filter.setProducer(producer);
        Graph graph = filter.getGraph();

        for (Iterator iter = graph.getNodes(); iter.hasNext();) {
            final String nodeName = ((Node) iter.next()).getName();

            assertFalse("Node " + nodeName + " should have been filtered out", toRemove
                    .contains(nodeName));
        }
    }

    /**
     * Process log4j 1.2.8 build.xml and remove the "init", "build" and jar
     * nodes.
     * 
     */
    public void testSeveralNodes() throws GrandException {
        final Set toRemove = new HashSet();
        toRemove.add("init");
        toRemove.add("build");
        toRemove.add("jar");
        final GraphFilter filter = new NodeRemoverFilter(toRemove);
        filter.setProducer(producer);
        final Graph graph = filter.getGraph();

        for (Iterator iter = graph.getNodes(); iter.hasNext();) {
            final String nodeName = ((Node) iter.next()).getName();

            assertFalse("Node " + nodeName + " should have been filtered out", toRemove
                    .contains(nodeName));
        }
    }

    private int countNodes(final Graph graph) {
        int numNode = 0;
        for (final Iterator iter = graph.getNodes(); iter.hasNext();) {
            iter.next();
            numNode++;
        }
        return numNode;
    }

    /*
     * (non-Javadoc)
     * @see net.ggtools.grand.utils.AbstractTaskTester#getTestBuildFileName()
     */
    protected String getTestBuildFileName() {
        return TESTCASES_DIR + "log4j-build.xml";
    }

    /*
     * (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() {
        super.setUp();
        producer = new AntProject(project);
    }

}
