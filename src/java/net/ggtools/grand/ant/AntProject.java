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
import java.util.HashMap;
import java.util.Iterator;

import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.Target;
import net.ggtools.grand.Node;
import net.ggtools.grand.Project;

/**
 * 
 * 
 * @author Christophe Labouisse
 */
public class AntProject implements Project, AntTargetProxifier {

    private org.apache.tools.ant.Project antProject;
    private final HashMap proxifiedTargetCache = new HashMap();
    
    /**
     * Creates a new project from an ant build file.
     * 
     * The source object can be anything supported by {@link ProjectHelper} which is
     * at least a File.
     * 
     * @param source The source for XML configuration.
     * @see ProjectHelper#parse(org.apache.tools.ant.Project, java.lang.Object)
     */
    public AntProject(File source) {
        antProject = new org.apache.tools.ant.Project();
        antProject.setSystemProperties();
        antProject.init();
        
        ProjectHelper loader = ProjectHelper.getProjectHelper();
        // TODO figure out how to make this properly.
        antProject.addReference("ant.projectHelper",loader);
        loader.parse(antProject,source);
    }
    
    /**
     * Creates a new project from an existing ant project.
     * 
     * @param project
     */
    public AntProject(org.apache.tools.ant.Project project) {
        antProject = project;
    }


    /* (non-Javadoc)
     * @see org.ggtools.dependgraph.Project#getName()
     */
    public String getName() {
        return antProject.getName();
    }


    /**
     * Returns a node from its name.
     * 
     * @param depName the node name.
     * @return node
     */
    Node getNodeFromName(String depName) {
        return proxifyTarget((Target) antProject.getTargets().get(depName));
    }
    
    
    /* (non-Javadoc)
     * @see org.ggtools.dependgraph.Project#getNodes()
     */
    public Iterator getNodes() {
        return new TargetIteratorProxifier(this,antProject.getTargets().elements());
    }


    /* (non-Javadoc)
     * @see org.ggtools.dependgraph.Project#getStartNode()
     */
    public Node getStartNode() {
        final String defaultTarget = antProject.getDefaultTarget();
        if (defaultTarget == null) {
            return null;
        }
        else {
            return getNodeFromName(defaultTarget);
        }
    }

    /**
     * Return a wrapper to a target implementing the Node interface.
     * 
     * @param target
     * @return a wrapping node.
     * @see Target
     * @see Node
     */
    public Node proxifyTarget(Target target) {
        if (proxifiedTargetCache.containsKey(target)) {
            return (Node) proxifiedTargetCache.get(target);
        }
        else {
            Node result = new AntNode(this,target);
            proxifiedTargetCache.put(target,result);
            return result;
        }
    }

}
