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
import java.util.HashMap;
import java.util.Iterator;

import net.ggtools.grand.Graph;
import net.ggtools.grand.GraphProducer;
import net.ggtools.grand.Log;
import net.ggtools.grand.Node;
import net.ggtools.grand.exceptions.GrandException;
import net.ggtools.grand.impl.GraphImpl;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.Target;

/**
 * @author Christophe Labouisse
 */
public class AntProject implements GraphProducer {

    private org.apache.tools.ant.Project antProject;

    private final HashMap proxifiedTargetCache = new HashMap();

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
        antProject = new Project();
        antProject.setSystemProperties();
        antProject.init();
        antProject.setUserProperty("ant.file", source.getAbsolutePath());

        ProjectHelper loader = ProjectHelper.getProjectHelper();
        antProject.addReference("ant.projectHelper", loader);
        loader.parse(antProject, source);
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

    /*
     * (non-Javadoc)
     * 
     * @see net.ggtools.grand.GraphProducer#getGraph(net.ggtools.grand.GraphFactory)
     */
    public Graph getGraph() throws GrandException {
        Log.log("Triggering AntProject",Log.MSG_VERBOSE);
        
        final String defaultTarget = antProject.getDefaultTarget();
        final Graph graph = new GraphImpl(antProject.getName());

        for (Iterator iter = antProject.getTargets().values().iterator(); iter.hasNext(); ) {
            final Target target = (Target) iter.next();

            final String targetName = target.getName();
            final Node node = graph.createNode(targetName);

            if ((defaultTarget != null) && (defaultTarget.equals(targetName))) {
                graph.setStartNode(node);
            }

            final String targetDescription = target.getDescription();
            if ((targetDescription != null) && (!targetDescription.equals(""))) {
                node.setAttributes(Node.ATTR_MAIN_NODE);
                node.setDescription(targetDescription);
            }
        }

        for (Iterator iter = antProject.getTargets().values().iterator(); iter.hasNext(); ) {
            final Target target = (Target) iter.next();
            final Enumeration deps = target.getDependencies();
            final String startNodeName = target.getName();
            final Node startNode = graph.getNode(startNodeName);

            while (deps.hasMoreElements()) {
                final String depName = (String) deps.nextElement();
                Node endNode = graph.getNode(depName);

                if (endNode == null) {
                    Log.log("Node " + startNodeName + " has dependency to non existent node "
                            + depName);
                } else {
                    graph.createLink(null, startNode, endNode);
                }
            }
        }

        return graph;
    }
}
