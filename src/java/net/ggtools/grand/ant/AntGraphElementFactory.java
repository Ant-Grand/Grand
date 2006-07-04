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

import net.ggtools.grand.graph.GraphElementFactory;
import net.ggtools.grand.graph.Link;
import net.ggtools.grand.graph.Node;

/**
 * An element factory specialized in ant graph.
 * 
 * @author Christophe Labouisse
 */
class AntGraphElementFactory implements GraphElementFactory {
    private final AntGraph graph;

    /**
     * Creates a new factory linked to a specific graph.
     * 
     * @param graph
     */
    public AntGraphElementFactory(final AntGraph graph) {
        this.graph = graph;
    }

    /*
     * (non-Javadoc)
     * @see net.ggtools.grand.graph.GraphElementFactory#createNode(java.lang.String)
     */
    public Node createNode(final String nodeName) {
        return new AntTargetNode(nodeName, graph);
    }

    /*
     * (non-Javadoc)
     * @see net.ggtools.grand.graph.GraphElementFactory#createLink(java.lang.String,
     *      net.ggtools.grand.graph.Node, net.ggtools.grand.graph.Node)
     */
    public Link createLink(final String linkName, final Node startNode, final Node endNode) {
        return new AntLink(linkName, graph, startNode, endNode);
    }

    /**
     * Creates a link for a task call.
     * 
     * @param linkName
     * @param startNode
     * @param endNode
     * @param taskName
     * @return
     */
    public AntTaskLink createTaskLink(final String linkName, final Node startNode,
            final Node endNode, final String taskName) {
        return new AntTaskLink(linkName, graph, startNode, endNode, taskName);
    }

    /**
     * Creates a link for a subant task call. Although it can be used for any
     * kind of link resulting from a subant task, this method is intented to be
     * called for the <em>genericantfile</em> version of the task.
     * 
     * @param linkName
     * @param startNode
     * @param endNode
     * @param taskName
     * @return
     */
    public SubantTaskLink createSubantTaskLink(final String linkName, final Node startNode,
            final Node endNode, final String taskName) {
        return new SubantTaskLink(linkName, graph, startNode, endNode, taskName);
    }

}