// $Id$
/* ====================================================================
 * Copyright (c) 2002-2004, Christophe Labouisse
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

import net.ggtools.grand.graph.Graph;
import net.ggtools.grand.graph.LinkImpl;
import net.ggtools.grand.graph.Node;
import net.ggtools.grand.graph.visit.LinkVisitor;

/**
 * A class specialized in link within ant build files.
 * 
 * @author Christophe Labouisse
 */
public class AntLink extends LinkImpl {
    /**
     * Type for <em>standard</em> ant dependency.
     */
    public final static int LINK_DEPEND = 0;
    
    /**
     * Type for links resulting of a <code>ant</code> task.
     */
    public final static int LINK_ANT = 1;

    /**
     * Type for links resulting of a <code>antcall</code> task.
     */
    public final static int LINK_ANTCALL = 2;

    /**
     * Type for links resulting of a <code>foreach</code> task.
     */
    public final static int LINK_FOREACH = 3;

    /**
     * Type of links resulting of a any other task.
     */
    public final static int LINK_UNKNOWN_TASK = -1;
     
    private int type;
    
    /**
     * Creates a new link.
     * 
     * @param name
     * @param graph
     * @param startNode
     * @param endNode
     */
    public AntLink(String name, Graph graph, Node startNode, Node endNode) {
        super(name, graph, startNode, endNode);
        type = LINK_DEPEND;
    }
    
    /**
     * Gets the link's type.
     * @return Returns the type.
     */
    public final int getType() {
        return type;
    }
    
    /**
     * @param type The type to set.
     */
    final void setType(int type) {
        this.type = type;
    }
    
    /* (non-Javadoc)
     * @see net.ggtools.grand.graph.Link#accept(net.ggtools.grand.graph.visit.LinkVisitor)
     */
    public void accept(LinkVisitor visitor) {
        visitor.visitLink(this);
    }
}
