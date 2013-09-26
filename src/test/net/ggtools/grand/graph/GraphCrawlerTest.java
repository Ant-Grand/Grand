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

package net.ggtools.grand.graph;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import net.ggtools.grand.ant.AntProject;
import net.ggtools.grand.exceptions.GrandException;
import net.ggtools.grand.graph.ForwardLinkFinder;
import net.ggtools.grand.graph.Graph;
import net.ggtools.grand.graph.GraphCrawler;
import net.ggtools.grand.graph.GraphProducer;
import net.ggtools.grand.graph.Node;
import net.ggtools.grand.utils.AbstractAntTester;

/**
 * 
 * 
 * @author Christophe Labouisse
 */
public class GraphCrawlerTest extends AbstractAntTester {
    /**
     * Field producer.
     */
    private GraphProducer producer;

    /**
     * Field NODES_AFTER_FILTERING.
     */
    private static final Set<String> NODES_AFTER_FILTERING = new HashSet<String>(Arrays
            .asList(new String[]{"build", "init", "build.core", "build.examples", "build.xml",
                    "jaxp", "jaxpCheck", "build.javamail", "javamail", "javamailCheck",
                    "build.jms", "jms", "jmsCheck", "jndi", "jndiCheck", "build.jmx", "jmx",
                    "jmxCheck"}));

    /**
     * Constructor for GraphCrawlerTest.
     * @param arg0
     */
    public GraphCrawlerTest(final String arg0) {
        super(arg0);
    }

    /**
     * Method getTestBuildFileName.
     * @return String
     * @see net.ggtools.grand.utils.AbstractTaskTester#getTestBuildFileName()
     */
    @Override
    protected String getTestBuildFileName() {
        return TESTCASES_DIR + "log4j-build.xml";
    }

    /**
     * Method setUp.
     * @see AbstractTaskTester#setUp()
     */
    @Override
    protected void setUp() {
        super.setUp();
        producer = new AntProject(project);
    }

    /**
     * Method tearDown.
     * @see AbstractTaskTester#tearDown()
     */
    @Override
    protected void tearDown() {
        super.tearDown();
    }

    /**
     * Method testCrawl.
     * @throws GrandException
     */
    public final void testCrawl() throws GrandException {
        final Graph graph = producer.getGraph();
        final GraphCrawler crawler = new GraphCrawler(graph, new ForwardLinkFinder());
        final Collection<Node> result = crawler.crawl(graph.getNode("build"));

        assertEquals("Result and reference do not have the same size", NODES_AFTER_FILTERING
                .size(), result.size());
        
        for (Node node : result) {
            final String nodeName = node.getName();
            assertTrue("Node " + nodeName + " should have been filtered out",
                    NODES_AFTER_FILTERING.contains(nodeName));
        }

    }
}
