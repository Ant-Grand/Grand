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

import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.tools.ant.Target;
import net.ggtools.grand.Node;

/**
 * A Node wrapper for ant targets.
 * 
 * This class provides a implementation of the {@link org.ggtools.grand.Node}
 * target wrapping a {@link org.apache.tools.ant.Target} object. 
 * 
 * @author Christophe Labouisse
 */
class AntNode implements Node {
    private final Target target;
    private final AntProject ownerProject;
    private List dependencies;

    /**
     * Creates a new target wrapper.
     * 
     * @param project owner project
     * @param underlying target to be proxified
     */
    AntNode(AntProject project, Target underlying) {
        target = underlying;
        ownerProject = project;
    }

    /* (non-Javadoc)
     * @see org.ggtools.dependgraph.Node#getDependencies()
     */
    public Iterator getDependencies() {
        checkDepList();
        
        return dependencies.iterator();
    }

    /**
     * Check if the dependency list have be created.
     */
    private void checkDepList() {
        if (dependencies == null) {
            final Enumeration antDeps = target.getDependencies();
            final List tmpDeps = new LinkedList();
            
            while (antDeps.hasMoreElements()) {
                final String depName = (String) antDeps.nextElement();
                tmpDeps.add(ownerProject.getNodeFromName(depName));
            }

            dependencies = Collections.unmodifiableList(tmpDeps);
        }
    }

    /* (non-Javadoc)
     * @see org.ggtools.dependgraph.Node#getDependenciesArray()
     */
    public Node[] getDependenciesArray() {
        checkDepList();
        
        return (Node[]) dependencies.toArray(new Node [0]);
    }

    /* (non-Javadoc)
     * @see org.ggtools.dependgraph.Node#getName()
     */
    public String getName() {
        return target.getName();
    }

    /* (non-Javadoc)
     * @see org.ggtools.dependgraph.Node#getDescription()
     */
    public String getDescription() {
        return target.getDescription();
    }

    /* (non-Javadoc)
     * @see org.ggtools.dependgraph.Node#isMainNode()
     */
    public boolean isMainNode() {
        return target.getDescription()!= null;
    }

}
