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
package net.ggtools.grand.ant;

import java.io.File;
import java.util.Enumeration;
import java.util.Iterator;

import net.ggtools.grand.Log;
import net.ggtools.grand.exceptions.GrandException;
import net.ggtools.grand.graph.Graph;
import net.ggtools.grand.graph.GraphImpl;
import net.ggtools.grand.graph.GraphProducer;
import net.ggtools.grand.graph.Link;
import net.ggtools.grand.graph.Node;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.RuntimeConfigurable;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.Task;

/**
 * @author Christophe Labouisse
 */
public class AntProject implements GraphProducer {

    private static final String ANTCALL_TASK_NAME = "antcall";

    private org.apache.tools.ant.Project antProject;

    /**
     * Creates a new project from an ant build file.
     * 
     * The source object can be anything supported by {@link ProjectHelper}
     * which is at least a File.
     * 
     * @param source
     *            The source for XML configuration.
     * @see ProjectHelper#parse(org.apache.tools.ant.Project, java.lang.Object)
     */
    public AntProject(final File source) {
        Log.log("Parsing from " + source);
        antProject = new Project();
        antProject.setSystemProperties();
        antProject.init();
        antProject.setUserProperty("ant.file", source.getAbsolutePath());
        
        ProjectHelper loader = ProjectHelper.getProjectHelper();
        antProject.addReference("ant.projectHelper", loader);
        loader.parse(antProject, source);
        Log.log("Done parsing", Log.MSG_VERBOSE);
    }

    /**
     * Creates a new project from an existing ant project.
     * 
     * @param project
     */
    public AntProject(final Project project) {
        antProject = project;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ggtools.dependgraph.Project#getName()
     */
    public String getName() {
        return antProject.getName();
    }

    /**
     * Convert an Ant project to a Grand Graph.
     * 
     * The conversion is done in several steps:
     * 
     * <ol>
     * <li>Each ant target will be converted to a Grand Node using both the
     * target's name and description the the node's ones. Targets with a
     * non empty description will be converted to Nodes with the
     * {@link Node#ATTR_MAIN_NODE} attribute set. If the project element
     * has a valid default target, the corresponding node will be the graph
     * start target.</li>
     * <li><code>depends</code> attributes on targets will be translated into
     * links. <code>antcall</code>s will be translated in links with the
     * {@link net.ggtools.grand.graph.Link#ATTR_WEAK_LINK} set.</li>
     * </ol> 
     * 
     * @return a graph representing the dependency of the ant project.
     * @throws GrandException if the project cannot be converted to a graph.
     * @see net.ggtools.grand.GraphProducer#getGraph(net.ggtools.grand.GraphFactory)
     */
    public Graph getGraph() throws GrandException {
        Log.log("Triggering AntProject", Log.MSG_VERBOSE);

        final Graph graph = new GraphImpl(antProject.getName());

        // First pass, create the nodes.
        for (Iterator iter = antProject.getTargets().values().iterator(); iter.hasNext(); ) {
            final Target target = (Target) iter.next();
            if (target.getName().equals("")) {
                continue;
            }

            final String targetName = target.getName();
            final Node node = graph.createNode(targetName);

            final String targetDescription = target.getDescription();
            if ((targetDescription != null) && (!targetDescription.equals(""))) {
                node.setAttributes(Node.ATTR_MAIN_NODE);
                node.setDescription(targetDescription);
            }
        }

        // Sets the start node if needed.
        final String defaultTarget = antProject.getDefaultTarget();
        if (defaultTarget != null) {
            final Node startNode = graph.getNode(defaultTarget);

            if (startNode != null) {
                graph.setStartNode(startNode);
            }
        }

        // Second pass, create the links
        for (Iterator iter = antProject.getTargets().values().iterator(); iter.hasNext(); ) {
            final Target target = (Target) iter.next();
            if (target.getName().equals("")) {
                continue;
            }

            final Enumeration deps = target.getDependencies();
            final String startNodeName = target.getName();
            final Node startNode = graph.getNode(startNodeName);

            while (deps.hasMoreElements()) {
                final String depName = (String) deps.nextElement();
                final Node endNode = graph.getNode(depName);

                if (endNode == null) {
                    Log.log("Node " + startNodeName + " has dependency to non existent node "
                            + depName, Log.MSG_WARN);
                } else {
                    graph.createLink(null, startNode, endNode);
                }
            }

            final Task[] tasks = target.getTasks();
            for (int i = 0; i < tasks.length; i++) {
                final Task task = tasks[i];
                if (ANTCALL_TASK_NAME.equals(task.getTaskType())) {
                    //if (task.getTaskType().equals(ANTCALL_TASK_NAME)) {
                    final RuntimeConfigurable wrapper = task.getRuntimeConfigurableWrapper();
                    final String called = antProject.replaceProperties((String) wrapper
                            .getAttributeMap().get("target"));

                    // Ant call can call targets which won't exists at the moment
                    // so we call a dummy target if none is available.
                    Node calledNode = graph.getNode(called);
                    if (calledNode == null) {
                        Log.log("Creating dummy node for missing antcall target " + called);
                        calledNode = graph.createNode(called);
                    }
                    final Link link = graph
                            .createLink(ANTCALL_TASK_NAME, startNode, calledNode);
                    link.setAttributes(Link.ATTR_WEAK_LINK);
                }
            }
        }

        return graph;
    }
}
