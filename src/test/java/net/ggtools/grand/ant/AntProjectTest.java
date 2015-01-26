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

import static org.junit.Assert.*;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.junit.Before;
import org.junit.Test;

import net.ggtools.grand.exceptions.GrandException;
import net.ggtools.grand.graph.Graph;
import net.ggtools.grand.graph.Link;
import net.ggtools.grand.graph.Node;
import net.ggtools.grand.utils.AbstractAntTester;

/**
 * @author Christophe Labouisse
 */
public class AntProjectTest extends AbstractAntTester {

    /**
     * Field antProject.
     */
    protected AntProject antProject;

    /**
     * Field graph.
     */
    protected Graph graph;

    /**
     * Method testAnt.
     * @throws GrandException
     */
    @Test
    public final void testAnt() throws GrandException {
        // Test without antfile nor dir
        AntTargetNode node = (AntTargetNode) graph.getNode("ant-test");
        AntLink link = (AntLink) node.getLinks().iterator().next();
        assertNotNull("should have found a link", link);
        AntTargetNode endNode = (AntTargetNode) link.getEndNode();
        assertEquals("Target", "[gruik]", endNode.getName());
        final String buildFile = new File(project.getBaseDir(), "build.xml").getAbsolutePath();
        assertEquals("Build file should be build.xml in the current dir", buildFile,
                endNode.getBuildFile());

        // Test with antfile & dir set
        node = (AntTargetNode) graph.getNode("ant-with-file-test");
        link = (AntLink) node.getLinks().iterator().next();
        assertNotNull("should have found a link", link);
        endNode = (AntTargetNode) link.getEndNode();
        assertEquals("Target", "[gabuzo]", endNode.getName());
        // TODO is this a misfeature of Windows or Ant?
       File referenceFile = System.getProperty("os.version").startsWith("Windows")
               || System.getProperty("os.name").startsWith("Windows") ?
                       new File(project.getBaseDir(), "/gruik/gruik.xml") : new File("/gruik/gruik.xml");
        assertEquals("Build file", referenceFile.getAbsolutePath(),
                endNode.getBuildFile());
    }

    /**
     * Method testAntCall.
     * @throws GrandException
     */
    @Test
    public final void testAntCall() throws GrandException {
        final AntTargetNode node = (AntTargetNode) graph.getNode("antcall-test");
        AntLink link = null;
        try {
            link = (AntLink) node.getLinks().iterator().next();
        } catch (final NoSuchElementException e) {
            fail("Should have found a link");
        }
        assertNotNull("should have found a link", link);
        assertEquals("Target", "gruik", link.getEndNode().getName());
        final AntTargetNode endNode = (AntTargetNode) link.getEndNode();
        assertEquals("Target", "gruik", endNode.getName());
        assertNull("Build file", endNode.getBuildFile());
    }

    /**
     * Method testAntCallWithTargetElements.
     * @throws GrandException
     */
    @Test
    public final void testAntCallWithTargetElements() throws GrandException {
        final AntTargetNode node = (AntTargetNode) graph.getNode("antcall-with-target-elements-test");
        final Collection<Link> links = node.getLinks();
        assertNotNull("Links should not be null", links);

        final Iterator<Link> iterator = links.iterator();

        AntLink link = (AntLink) iterator.next();
        assertNotNull("should have found a link", link);
        AntTargetNode endNode = (AntTargetNode) link.getEndNode();
        assertEquals("Target", "gruik", endNode.getName());
        assertNull("Build file", endNode.getBuildFile());

        link = (AntLink) iterator.next();
        assertNotNull("should have found two links", link);
        endNode = (AntTargetNode) link.getEndNode();
        assertEquals("Target", "gabuzo", endNode.getName());
        assertNull("Build file", endNode.getBuildFile());

        assertFalse("There should be target after the second one", iterator.hasNext());
    }

    /**
     * Run a graph on a file including an antcall whose target is based on a
     * property.
     */
    @Test
    public final void testAntCallWithUndefinedProperty() {
        expectLogContaining("ant-call-with-property", "Outputting to ");
        assertLogContaining("Target antcall-props-1 has dependency to non existent target ${antcall.target}, creating a dummy node");
        assertLogContaining("Target antcall-props-2 has dependency to non existent target do-${antcall.target}, creating a dummy node");
    }

    /**
     * Run a graph on a file including an undefined task.
     */
    @Test
    public final void testAntCallWithUndefinedTask() {
        // TODO check if this test is useful.
        expectLogContaining("undefined-task", "Outputting to ");
    }

    /**
     * Method testAntNoTargetDifferentFile.
     * @throws GrandException
     */
    @Test
    public final void testAntNoTargetDifferentFile() throws GrandException {
        // Test without target different file
        final AntTargetNode node = (AntTargetNode) graph.getNode("ant-without-target-with-file-test");
        final AntLink link = (AntLink) node.getLinks().iterator().next();
        assertNotNull("should have found a link", link);
        final AntTargetNode endNode = (AntTargetNode) link.getEndNode();
        assertEquals("Target", "['default']", endNode.getName());
        // TODO is this a misfeature of Windows or Ant?
        File referenceFile = System.getProperty("os.version").startsWith("Windows")
                || System.getProperty("os.name").startsWith("Windows") ?
                        new File(project.getBaseDir(), "/gruik/gruik.xml") : new File("/gruik/gruik.xml");
        assertEquals("Build file", referenceFile.getAbsolutePath(),
                endNode.getBuildFile());
    }

    /**
     * Method testAntNoTargetSameFile.
     * @throws GrandException
     */
    @Test
    public final void testAntNoTargetSameFile() throws GrandException {
        // Test without target same file
        final AntTargetNode node = (AntTargetNode) graph.getNode("ant-without-target-test");
        final AntLink link = (AntLink) node.getLinks().iterator().next();
        assertNotNull("should have found a link", link);
        final AntTargetNode endNode = (AntTargetNode) link.getEndNode();
        assertEquals("Should be the default target", "init", endNode.getName());
        assertNull("Build file should be the currentFile", endNode.getBuildFile());
    }

    /**
     * Method testAntWithTargetElements.
     * @throws GrandException
     */
    @Test
    public final void testAntWithTargetElements() throws GrandException {
        final AntTargetNode node = (AntTargetNode) graph.getNode("ant-with-target-elements-test");
        final Collection<Link> links = node.getLinks();
        assertNotNull("Links should not be null", links);

        final Iterator<Link> iterator = links.iterator();

        AntLink link = (AntLink) iterator.next();
        assertNotNull("should have found a link", link);
        AntTargetNode endNode = (AntTargetNode) link.getEndNode();
        assertEquals("Target", "[gruik]", endNode.getName());
        String buildFile = new File(project.getBaseDir(), "build.xml").getAbsolutePath();
        assertEquals("Build file should be build.xml in the current dir", buildFile, endNode
                .getBuildFile());

        link = (AntLink) iterator.next();
        assertNotNull("should have found two links", link);
        endNode = (AntTargetNode) link.getEndNode();
        assertEquals("Target", "[gabuzo (2)]", endNode.getName());
        buildFile = new File(project.getBaseDir(), "build.xml").getAbsolutePath();
        assertEquals("Build file should be build.xml in the current dir", buildFile, endNode
                .getBuildFile());

        assertFalse("There should be target after the second one", iterator.hasNext());
    }

    /**
     * Method testIfCondition.
     * @throws GrandException
     */
    @Test
    public final void testIfCondition() throws GrandException {
        final AntTargetNode node = (AntTargetNode) graph.getNode("if-cond-test");
        assertNotNull("if-cond-test Node", node);
        assertEquals("If condition for target if-cond-test", "test-if-condition", node
                .getIfCondition());
    }

    /**
     * Test if the nested tasks are found (ticket #29).
     *
     * @throws GrandException
     */
    @Test
    public final void testNestedAnt() throws GrandException {
        final AntTargetNode node = (AntTargetNode) graph.getNode("[nested-missing-node]");
        assertNotNull("nested-missing-node not found", node);
        assertTrue("nested-missing-node has MISSING_NODE_ATTR set", node
                .hasAttributes(Node.ATTR_MISSING_NODE));
    }

    /**
     * Method testRunTarget.
     * @throws GrandException
     */
    @Test
    public final void testRunTarget() throws GrandException {
        final AntTargetNode node = (AntTargetNode) graph.getNode("runtarget-test");
        AntLink link = null;
        try {
            link = (AntLink) node.getLinks().iterator().next();
        } catch (final NoSuchElementException e) {
            fail("Should have found a link");
        }
        assertNotNull("should have found a link", link);
        assertEquals("Target", "gruik", link.getEndNode().getName());
        final AntTargetNode endNode = (AntTargetNode) link.getEndNode();
        assertEquals("Target", "gruik", endNode.getName());
        assertNull("Build file", endNode.getBuildFile());
    }

    /**
     * Test the subant task.
     *
     * @throws GrandException
     */
    @Test
    public final void testSubant() throws GrandException {
        // Test for genericantfile.
        AntTargetNode node = (AntTargetNode) graph.getNode("subant-generic");
        Collection<Link> links = node.getLinks();
        assertEquals("Should have found 1 link", 1, links.size());
        AntLink link = (AntLink) links.iterator().next();
        assertNotNull("should have found a link", link);
        AntTargetNode endNode = (AntTargetNode) link.getEndNode();
        assertEquals("Target", "subant-generic-target", endNode.getName());
        assertNull("Build file should be the currentFile", endNode.getBuildFile());

        // Test with antfile & dir set
        node = (AntTargetNode) graph.getNode("subant-antfile");
        links = node.getLinks();
        assertEquals("Should have found 2 links", 2, links.size());
        final Iterator<Link> iterator = links.iterator();
        link = (AntLink) iterator.next();
        assertNotNull("should have found a link", link);
        endNode = (AntTargetNode) link.getEndNode();
        assertEquals("Target", "[init]", endNode.getName());
        String buildFile = new File(project.getBaseDir(), "subant-1/build.xml").getAbsolutePath();
        assertEquals("Build file should be build.xml in the subant-1 dir", buildFile, endNode
                .getBuildFile());

        link = (AntLink) iterator.next();
        assertNotNull("should have found a link", link);
        endNode = (AntTargetNode) link.getEndNode();
        assertEquals("Target", "[target]", endNode.getName());
        buildFile = new File(project.getBaseDir(), "subant-2/build.xml").getAbsolutePath();
        assertEquals("Build file should be build.xml in the subant-2 dir", buildFile, endNode
                .getBuildFile());

        // Test with property nested elements.
        node = (AntTargetNode) graph.getNode("subant-withproperties");
        links = node.getLinks();
        assertEquals("Should have found 2 links", 2, links.size());
        for (Link link2 : links) {
            final AntTaskLink subLink = (AntTaskLink) link2;
            assertEquals("A parameter call ga=meu should be defined",
                    "bu", subLink.getParameter("ga"));
        }
    }

    /**
     * Method testUnlessCondition.
     * @throws GrandException
     */
    @Test
    public final void testUnlessCondition() throws GrandException {
        final AntTargetNode node = (AntTargetNode) graph.getNode("unless-cond-test");
        assertNotNull("unless-cond-test Node", node);
        assertEquals("Unless condition for target unless-cond-test",
                "test-unless-condition", node.getUnlessCondition());
    }

    /**
     * Method getTestBuildFileName.
     * @return String
     */
    protected final String getTestBuildFileName() {
        return TESTCASES_DIR + "ant-project-test.xml";
    }

    /**
     * Method setUp.
     */
    @Before
    public final void setUp() {
        configureProject(getTestBuildFileName());
        project.setBasedir(TESTCASES_DIR);
        createGraph();
    }

    /**
     * Method createGraph.
     */
    protected void createGraph() {
        antProject = new AntProject(project);
        try {
            graph = antProject.getGraph();
        } catch (final GrandException e) {
            fail("Got exception while creating graph: " + e.getMessage());
        }
    }

}
