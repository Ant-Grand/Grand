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

package net.ggtools.grand.graph;

/**
 * A basic implementation of {@link net.ggtools.grand.graph.GraphElementFactory} creating
 * {@link net.ggtools.grand.graph.NodeImpl} & {@link net.ggtools.grand.graph.LinkImpl}.
 * Instances of this class are linked to a specific graph.
 * 
 * @author Christophe Labouisse
 */
class SimpleGraphElementFactory implements GraphElementFactory {
    
    final private Graph graph;
    
    /**
     * Creates a factory instance linked to a specific graph.
     * @param graph
     */
    SimpleGraphElementFactory(final Graph graph) {
        this.graph = graph;
    }

    /* (non-Javadoc)
     * @see net.ggtools.grand.graph.GraphElementFactory#createNode(java.lang.String)
     */
    public Node createNode(String nodeName) {
        return new NodeImpl(nodeName, graph);
    }

    /* (non-Javadoc)
     * @see net.ggtools.grand.graph.GraphElementFactory#createLink(java.lang.String, net.ggtools.grand.graph.Node, net.ggtools.grand.graph.Node)
     */
    public Link createLink(String linkName, Node startNode, Node endNode) {
        return new LinkImpl(linkName, graph, startNode, endNode);
    }

}
