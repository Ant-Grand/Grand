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


/**
 * 
 * 
 * @author Christophe Labouisse
 */
public class LinkImpl extends AttributeManager implements Link {

    private Node startNode;
    private Node endNode;
    private Graph graph;
    private String name;
    
    /**
     * Creates a new Link.
     * 
     * @param name link's name, may be <code>null</code>.
     * @param graph owning graph.
     */
    public LinkImpl(String name, Graph graph, Node startNode, Node endNode) {
        this.name = name;
        this.graph = graph;
        this.startNode = startNode;
        this.endNode = endNode;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return (name == null ? "" : name)+" ("+startNode+" -> "+endNode+")";
    }
    
    /* (non-Javadoc)
     * @see net.ggtools.grand.Link#getStartNode()
     */
    public Node getStartNode() {
        return startNode;
    }

    /* (non-Javadoc)
     * @see net.ggtools.grand.Link#getEndNode()
     */
    public Node getEndNode() {
        return endNode;
    }

    /* (non-Javadoc)
     * @see net.ggtools.grand.GraphObject#getGraph()
     */
    public Graph getGraph() {
        return graph;
    }

    /* (non-Javadoc)
     * @see net.ggtools.grand.GraphObject#getName()
     */
    public String getName() {
        return name;
    }

}
