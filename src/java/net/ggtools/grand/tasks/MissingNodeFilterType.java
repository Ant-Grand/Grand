// $Id: IsolatedNodeFilterType.java 252 2004-02-05 22:56:30Z moi $
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

package net.ggtools.grand.tasks;

import net.ggtools.grand.filters.GraphFilter;
import net.ggtools.grand.filters.MissingNodeFilter;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

/**
 * Ant frontend to {@link net.ggtools.grand.filters.MissingNodeFilter}.
 * 
 * @author Christophe Labouisse
 */
class MissingNodeFilterType implements GraphFilterType {

    private Project project;

    /**
     * Creates a new object.
     * 
     * @param antProject project within which the filter will run.
     */
    public MissingNodeFilterType(Project antProject) {
        project = antProject;
    }
    
    /* (non-Javadoc)
     * @see net.ggtools.grand.tasks.GraphFilterType#checkParameters()
     */
    public void checkParameters() throws BuildException {
        // Do nothing.
    }

    /* (non-Javadoc)
     * @see net.ggtools.grand.tasks.GraphFilterType#getFilter()
     */
    public GraphFilter getFilter() {
        return new MissingNodeFilter();
    }

    /* (non-Javadoc)
     * @see net.ggtools.grand.tasks.GraphFilterType#setNodeName(java.lang.String)
     */
    public void setNodeName(String name) {
        project.log("Node parameter useless for isolatednode",Project.MSG_WARN);
    }

}
