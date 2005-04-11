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

package net.ggtools.grand.ant;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

import net.ggtools.grand.exceptions.GrandException;
import net.ggtools.grand.graph.Graph;
import net.ggtools.grand.graph.Node;
import net.ggtools.grand.utils.AbstractAntTester;

/**
 * @author Christophe Labouisse
 */
public class AntProjectTest extends AbstractAntTester {

    /**
     * Constructor for AntProjectTest.
     * 
     * @param arg0
     */
    public AntProjectTest(String arg0) {
        super(arg0);
    }

    public void testAnt() throws GrandException {
        Graph graph = getProjectGraph();

        // Test without antfile nor dir
        AntTargetNode node = (AntTargetNode) graph.getNode("ant-test");
        AntLink link = (AntLink) node.getLinks().iterator().next();
        assertNotNull("shoud have found a link", link);
        AntTargetNode endNode = (AntTargetNode) link.getEndNode();
        assertEquals("Target", "[gruik]", endNode.getName());
        String buildFile = new File(project.getBaseDir(), "build.xml").getAbsolutePath();
        assertEquals("Build file should be build.xml in the current dir", buildFile, endNode
                .getBuildFile());

        // Test with antfile & dir set
        node = (AntTargetNode) graph.getNode("ant-with-file-test");
        link = (AntLink) node.getLinks().iterator().next();
        assertNotNull("shoud have found a link", link);
        endNode = (AntTargetNode) link.getEndNode();
        assertEquals("Target", "[gabuzo]", endNode.getName());
        assertEquals("Build file", new File("/gruik/gruik.xml").getAbsolutePath(), endNode
                .getBuildFile());
    }

    public void testAntCall() throws GrandException {
        Graph graph = getProjectGraph();
        AntTargetNode node = (AntTargetNode) graph.getNode("antcall-test");
        AntLink link = null;
        try {
            link = (AntLink) node.getLinks().iterator().next();
        } catch (NoSuchElementException e) {
            fail("Should have found a link");
        }
        assertNotNull("should have found a link", link);
        assertEquals("Target", "gruik", link.getEndNode().getName());
        AntTargetNode endNode = (AntTargetNode) link.getEndNode();
        assertEquals("Target", "gruik", endNode.getName());
        assertNull("Build file", endNode.getBuildFile());
    }

    public void testAntCallWithTargetElements() throws GrandException {
        Graph graph = getProjectGraph();

        AntTargetNode node = (AntTargetNode) graph.getNode("antcall-with-target-elements-test");
        final Collection links = node.getLinks();
        assertNotNull("Links should not be null", links);

        final Iterator iterator = links.iterator();

        AntLink link = (AntLink) iterator.next();
        assertNotNull("shoud have found a link", link);
        AntTargetNode endNode = (AntTargetNode) link.getEndNode();
        assertEquals("Target", "gruik", endNode.getName());
        assertNull("Build file", endNode.getBuildFile());

        link = (AntLink) iterator.next();
        assertNotNull("shoud have found twos links", link);
        endNode = (AntTargetNode) link.getEndNode();
        assertEquals("Target", "gabuzo", endNode.getName());
        assertNull("Build file", endNode.getBuildFile());

        assertFalse("There should be target after the second one", iterator.hasNext());
    }

    /**
     * Run a graph on a file including an antcall whose target is based on a
     * property.
     * 
     * @throws IOException
     */
    public void testAntCallWithUndefinedProperty() {
        expectLogContaining("ant-call-with-property", "Outputing to ");
        assertLogContaining("Target antcall-props-1 has dependency to non existent target ${antcall.target}, creating a dummy node");
        assertLogContaining("Target antcall-props-2 has dependency to non existent target do-${antcall.target}, creating a dummy node");
    }

    /**
     * Run a graph on a file including an undefined task.
     * 
     * @throws IOException
     */
    public void testAntCallWithUndefinedTask() {
        // TODO Check if this test is useful.
        expectLogContaining("undefined-task", "Outputing to ");
    }

    public void testAntNoTargetDifferentFile() throws GrandException {
        Graph graph = getProjectGraph();

        // Test without target different file
        AntTargetNode node = (AntTargetNode) graph.getNode("ant-without-target-with-file-test");
        AntLink link = (AntLink) node.getLinks().iterator().next();
        assertNotNull("shoud have found a link", link);
        AntTargetNode endNode = (AntTargetNode) link.getEndNode();
        assertEquals("Target", "['default']", endNode.getName());
        assertEquals("Build file", new File("/gruik/gruik.xml").getAbsolutePath(), endNode
                .getBuildFile());
    }

    public void testAntNoTargetSameFile() throws GrandException {
        Graph graph = getProjectGraph();

        // Test without target same file
        AntTargetNode node = (AntTargetNode) graph.getNode("ant-without-target-test");
        AntLink link = (AntLink) node.getLinks().iterator().next();
        assertNotNull("shoud have found a link", link);
        AntTargetNode endNode = (AntTargetNode) link.getEndNode();
        assertEquals("Should be the default target", "init", endNode.getName());
        assertNull("Build file should be the currentFile", endNode.getBuildFile());
    }

    public void testAntWithTargetElements() throws GrandException {
        Graph graph = getProjectGraph();

        AntTargetNode node = (AntTargetNode) graph.getNode("ant-with-target-elements-test");
        final Collection links = node.getLinks();
        assertNotNull("Links should not be null", links);

        final Iterator iterator = links.iterator();

        AntLink link = (AntLink) iterator.next();
        assertNotNull("shoud have found a link", link);
        AntTargetNode endNode = (AntTargetNode) link.getEndNode();
        assertEquals("Target", "[gruik]", endNode.getName());
        String buildFile = new File(project.getBaseDir(), "build.xml").getAbsolutePath();
        assertEquals("Build file should be build.xml in the current dir", buildFile, endNode
                .getBuildFile());

        link = (AntLink) iterator.next();
        assertNotNull("shoud have found two links", link);
        endNode = (AntTargetNode) link.getEndNode();
        assertEquals("Target", "[gabuzo (2)]", endNode.getName());
        buildFile = new File(project.getBaseDir(), "build.xml").getAbsolutePath();
        assertEquals("Build file should be build.xml in the current dir", buildFile, endNode
                .getBuildFile());

        assertFalse("There should be target after the second one", iterator.hasNext());
    }

    public void testIfCondition() throws GrandException {
        Graph graph = getProjectGraph();
        AntTargetNode node = (AntTargetNode) graph.getNode("if-cond-test");
        assertNotNull("if-cond-test Node", node);
        assertEquals("If condition for target if-cond-test", "test-if-condition", node
                .getIfCondition());
    }

    /**
     * Test if the nested task are found (ticket #29).
     * @throws GrandException
     */
    public void testNestedAnt() throws GrandException {
        Graph graph = getProjectGraph();
        AntTargetNode node = (AntTargetNode) graph.getNode("[nested-missing-node]");
        assertNotNull("nested-missing-node not found", node);
        assertTrue("nested-missing-node has MISSING_NODE_ATTR set", node
                .hasAttributes(Node.ATTR_MISSING_NODE));
    }

    public void testRunTarget() throws GrandException {
        Graph graph = getProjectGraph();
        AntTargetNode node = (AntTargetNode) graph.getNode("runtarget-test");
        AntLink link = null;
        try {
            link = (AntLink) node.getLinks().iterator().next();
        } catch (NoSuchElementException e) {
            fail("Should have found a link");
        }
        assertNotNull("should have found a link", link);
        assertEquals("Target", "gruik", link.getEndNode().getName());
        AntTargetNode endNode = (AntTargetNode) link.getEndNode();
        assertEquals("Target", "gruik", endNode.getName());
        assertNull("Build file", endNode.getBuildFile());
    }

    /**
     * Test the subant task.
     * @throws GrandException
     */
    public void testSubant() throws GrandException {
        Graph graph = getProjectGraph();

        // Test for genericantfile.
        AntTargetNode node = (AntTargetNode) graph.getNode("subant-generic");
        Collection links = node.getLinks();
        assertEquals("Should have found 1 link", 1, links.size());
        AntLink link = (AntLink) links.iterator().next();
        assertNotNull("shoud have found a link", link);
        AntTargetNode endNode = (AntTargetNode) link.getEndNode();
        assertEquals("Target", "subant-generic-target", endNode.getName());
        assertNull("Build file should be the currentFile", endNode.getBuildFile());

        // Test with antfile & dir set
        node = (AntTargetNode) graph.getNode("subant-antfile");
        links = node.getLinks();
        assertEquals("Should have found 2 links", 2, links.size());
        final Iterator iterator = links.iterator();
        link = (AntLink) iterator.next();
        assertNotNull("shoud have found a link", link);
        endNode = (AntTargetNode) link.getEndNode();
        assertEquals("Target", "[init]", endNode.getName());
        String buildFile = new File(project.getBaseDir(), "subant-1/build.xml").getAbsolutePath();
        assertEquals("Build file should be build.xml in the subant-1 dir", buildFile, endNode
                .getBuildFile());

        link = (AntLink) iterator.next();
        assertNotNull("shoud have found a link", link);
        endNode = (AntTargetNode) link.getEndNode();
        assertEquals("Target", "[target]", endNode.getName());
        buildFile = new File(project.getBaseDir(), "subant-2/build.xml").getAbsolutePath();
        assertEquals("Build file should be build.xml in the subant-2 dir", buildFile, endNode
                .getBuildFile());

    }

    protected Graph getProjectGraph() throws GrandException {
        AntProject antProject = new AntProject(project);
        Graph graph = antProject.getGraph();
        return graph;
    }

    public void testUnlessCondition() throws GrandException {
        Graph graph = getProjectGraph();
        AntTargetNode node = (AntTargetNode) graph.getNode("unless-cond-test");
        assertNotNull("unless-cond-test Node", node);
        assertEquals("Unless condition for target unless-cond-test", "test-unless-condition", node
                .getUnlessCondition());
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.ggtools.grand.utils.AbstractAntTester#getTestBuildFileName()
     */
    protected String getTestBuildFileName() {
        return TESTCASES_DIR + "ant-project-test.xml";
    }

}
