// $Id$
/*
 * ====================================================================
 * Copyright (c) 2002-2004, Christophe Labouisse All rights reserved.
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
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import net.ggtools.grand.Log;
import net.ggtools.grand.exceptions.DuplicateNodeException;
import net.ggtools.grand.exceptions.GrandException;
import net.ggtools.grand.graph.Node;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.RuntimeConfigurable;

/**
 * A task visitor looking for links created by tasks like <code>ant</code>,
 * <code>antcall</code>, etc.
 * 
 * @author Christophe Labouisse
 */
public class LinkFinderVisitor extends ReflectTaskVisitorBase {

    private final static Map aliases = new HashMap();

    // Initialize the alias list
    static {
        aliases.put("runtarget", "antcall");
        aliases.put("foreach", "antcall");
    }

    private static final String ANT_FILE_PROPERTY = "ant.file";

    private static final String ATTR_ANTFILE = "antfile";

    private static final String ATTR_DIR = "dir";

    private static final String ATTR_NAME = "name";

    private static final String ATTR_TARGET = "target";

    private static final String ATTR_VALUE = "value";

    private static final String BUILD_XML = "build.xml";

    private static final String PARAM_ELEMENT = "param";

    private static final String PROPERTY_ELEMENT = "property";

    private AntGraph graph;

    private final AntProject project;

    private AntTargetNode startNode;

    public LinkFinderVisitor(final AntProject project) {
        this.project = project;
    }

    /**
     * Default action for unknown task. The default behavior is to recurse in
     * the children to find a possible task.
     * @param wrapper
     *            wrapper to check.
     * @see net.ggtools.grand.ant.ReflectTaskVisitorBase#defaultVisit(org.apache.tools.ant.RuntimeConfigurable)
     */
    public void defaultVisit(final RuntimeConfigurable wrapper) throws GrandException {
        final Enumeration children = wrapper.getChildren();
        while (children.hasMoreElements()) {
            visit((RuntimeConfigurable) children.nextElement());
        }
    }

    /*
     * (non-Javadoc)
     * @see net.ggtools.grand.ant.ReflectTaskVisitorBase#getAliasForTask(java.lang.String)
     */
    public String getAliasForTask(final String taskName) {
        String result = (String) aliases.get(taskName);
        if (result == null) {
            result = taskName;
        }
        return result;
    }

    /**
     * Process the <code>ant</code> task. This method will find or create the
     * destination node of the task, create an {@link AntTaskLink}and find the
     * nested <code>property</code> nodes to set the link properties. Only
     * <code>name</code>,<code>value</code> property nodes will be
     * processed: the <code>file</code> property nodes will be ignored.
     * 
     * The called node name will be either the plain <code>target</code>
     * attribute value if it is located in the current build file or
     * <code>[<em>target</em>]</code>.
     * 
     * @param wrapper
     *            the wrapper to process.
     * @throws DuplicateNodeException
     *             if a duplicate node is created, should not happen.
     */
    public void reflectVisit_ant(final RuntimeConfigurable wrapper) throws DuplicateNodeException {
        final Project antProject = project.getAntProject();
        Log.log("Processing ant target in " + startNode.getName(), Log.MSG_INFO);
        // Find the build file.
        final String targetBuildDirectoryName = (String) wrapper.getAttributeMap().get(ATTR_DIR);
        String antFile = (String) wrapper.getAttributeMap().get(ATTR_ANTFILE);
        if (antFile == null) {
            antFile = BUILD_XML;
        }

        final File targetBuildFile;
        if (targetBuildDirectoryName == null) {
            targetBuildFile = new File(antProject.getBaseDir(), antFile);
        }
        else {
            targetBuildFile = new File(targetBuildDirectoryName, antFile);
        }

        final File projectFile = new File(antProject.getProperty(ANT_FILE_PROPERTY));

        final boolean isSameBuildFile = projectFile.equals(targetBuildFile);

        final String endNodeName;

        if (isSameBuildFile) {
            endNodeName = antProject.replaceProperties((String) wrapper.getAttributeMap().get(
                    ATTR_TARGET));
        }
        else {
            endNodeName = "["
                    + antProject.replaceProperties((String) wrapper.getAttributeMap().get(
                            ATTR_TARGET)) + "]";
        }

        final AntTargetNode endNode = findOrCreateNode(endNodeName);

        Log.log("Creating link from " + startNode + " to " + endNodeName, Log.MSG_VERBOSE);
        final AntTaskLink link = graph.createTaskLink(null, startNode, endNode, wrapper
                .getElementTag());

        // TODO check that I'm not overriding a previously set file.
        if (!isSameBuildFile) endNode.setBuildFile(targetBuildFile.getAbsolutePath());

        // Look to params children.
        addNestPropertiesParameters(wrapper, link, PROPERTY_ELEMENT);
    }

    /**
     * Process <code>antcall</code> and similar tasks. The method will create
     * a link between the current start node and the node referenced by the
     * <code>target</code> attribute creating it with the
     * {@link Node#ATTR_MISSING_NODE}if no such node exists. It will then
     * create an {@link AntTaskLink}link and look for nested <code>param</code>
     * elements to set parameters to newly created link.
     * 
     * @param wrapper
     *            wrapper to process.
     * @throws DuplicateNodeException
     *             if a duplicate node is created (should not happen).
     */
    public void reflectVisit_antcall(final RuntimeConfigurable wrapper)
            throws DuplicateNodeException {
        Log.log("Processing antcall target in " + startNode.getName(), Log.MSG_INFO);
        final Project antProject = project.getAntProject();
        final String endNodeName = antProject.replaceProperties((String) wrapper.getAttributeMap()
                .get(ATTR_TARGET));

        final AntTargetNode endNode = findOrCreateNode(endNodeName);

        Log.log("Creating link from " + startNode + " to " + endNodeName, Log.MSG_VERBOSE);
        final AntTaskLink link = graph.createTaskLink(null, startNode, endNode, wrapper
                .getElementTag());

        // Look to params children.
        addNestPropertiesParameters(wrapper, link, PARAM_ELEMENT);
    }

    /**
     * @param graph
     *            The graph to set.
     */
    public void setGraph(AntGraph graph) {
        this.graph = graph;
    }

    /**
     * @param startNode
     *            The startNode to set.
     */
    public void setStartNode(AntTargetNode startNode) {
        this.startNode = startNode;
    }

    /**
     * @param wrapper
     * @param link
     * @param elementName
     */
    private void addNestPropertiesParameters(final RuntimeConfigurable wrapper,
            final AntTaskLink link, final String elementName) {
        final Enumeration children = wrapper.getChildren();
        while (children.hasMoreElements()) {
            final RuntimeConfigurable child = (RuntimeConfigurable) children.nextElement();
            if (elementName.equals(child.getElementTag())) {
                final Hashtable childAttributeMap = child.getAttributeMap();
                final String name = (String) childAttributeMap.get(ATTR_NAME);
                if (name != null) {
                    link.setParameter(name, (String) childAttributeMap.get(ATTR_VALUE));
                }
            }
        }
    }

    /**
     * @param endNodeName
     * @return @throws
     *         DuplicateNodeException
     */
    private AntTargetNode findOrCreateNode(final String endNodeName) throws DuplicateNodeException {
        AntTargetNode endNode = (AntTargetNode) graph.getNode(endNodeName);

        if (endNode == null) {
            Log.log("Target " + startNode + " has dependency to non existent target " + endNodeName
                    + ", creating a dummy node", Log.MSG_INFO);
            endNode = (AntTargetNode) graph.createNode(endNodeName);
            endNode.setAttributes(Node.ATTR_MISSING_NODE);
        }
        return endNode;
    }
}