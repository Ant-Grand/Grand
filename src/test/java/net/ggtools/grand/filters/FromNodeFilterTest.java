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

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import net.ggtools.grand.ant.AntProject;
import net.ggtools.grand.exceptions.GrandException;
import net.ggtools.grand.exceptions.NonExistentNodeException;
import net.ggtools.grand.graph.Graph;
import net.ggtools.grand.graph.GraphProducer;
import net.ggtools.grand.graph.Node;
import net.ggtools.grand.utils.AbstractAntTester;

/**
 *
 *
 * @author Christophe Labouisse
 */
public class FromNodeFilterTest extends AbstractAntTester {
    /**
     * Field producer.
     */
    private GraphProducer producer;

    /**
     * Field NODES_AFTER_FILTERING.
     */
    private static final Set<String> NODES_AFTER_FILTERING =
            new HashSet<String>(Arrays.asList(new String[]{"build", "init",
                    "build.core", "build.examples", "build.xml", "jaxp",
                    "jaxpCheck", "build.javamail", "javamail", "javamailCheck",
                    "build.jms", "jms", "jmsCheck", "jndi", "jndiCheck",
                    "build.jmx", "jmx", "jmxCheck"}));

    /**
     * Method setUp.
     */
    @Before
    public final void setUp() {
        configureProject(getTestBuildFileName());
        project.setBasedir(TESTCASES_DIR);
        producer = new AntProject(project);
    }

    /**
     * Method getTestBuildFileName.
     * @return String
     */
    private String getTestBuildFileName() {
        return TESTCASES_DIR + "log4j-build.xml";
    }

    /**
     * Process log4j 1.2.8 build.xml and from the "build" node and check
     * if we get what we want.
     *
     * @throws GrandException
     */
    @Test
    public final void testConnectedStartNode() throws GrandException {
        final GraphFilter filter = new FromNodeFilter("build");
        filter.setProducer(producer);
        final Graph graph = filter.getGraph();

        int numNodes = 0;
        for (final Iterator<Node> iter = graph.getNodes(); iter.hasNext();) {
            numNodes++;
            final String nodeName = iter.next().getName();

            assertTrue("Node " + nodeName + " should have been filtered out",
                    NODES_AFTER_FILTERING.contains(nodeName));
        }

        assertEquals("Filtered graph does not have the right node count",
                NODES_AFTER_FILTERING.size(), numNodes);

        assertNull("Start node 'usage' should have been filtered out",
                graph.getStartNode());
    }

    /**
     * Process a modified version of log4j 1.2.8 build.xml featuring the "build"
     * target as default. Check if the project start node has not been filtered out.
     *
     * @throws GrandException
     */
    @Test
    public final void testNotFilteredStartNode() throws GrandException {
        final GraphFilter filter = new FromNodeFilter("build");
        filter.setProducer(producer);
        project.setDefault("build");
        final Graph graph = filter.getGraph();

        int numNodes = 0;
        for (final Iterator<Node> iter = graph.getNodes(); iter.hasNext();) {
            numNodes++;
            final String nodeName = iter.next().getName();

            assertTrue("Node " + nodeName + " should have been filtered out",
                    NODES_AFTER_FILTERING.contains(nodeName));
        }

        assertEquals("Filtered graph does not have the right node count",
                NODES_AFTER_FILTERING.size(), numNodes);

        assertNotNull("Start node 'build' should not have been filtered out",
                graph.getStartNode());
    }

    /**
     * Process the build file, trying to filter from an non existent node.
     *
     * @throws GrandException
     */
    @Test
    public final void testNonExistentNode() throws GrandException {
        final GraphFilter filter = new FromNodeFilter("gruik-gruik-you-won't-find-me");
        filter.setProducer(producer);
        try {
            filter.getGraph();
            fail("Should have raised a NonExistentNode exception");
        } catch (final NonExistentNodeException e) {
        }
    }

}
