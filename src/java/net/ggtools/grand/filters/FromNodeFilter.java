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

package net.ggtools.grand.filters;

import java.util.Iterator;

import net.ggtools.grand.Graph;
import net.ggtools.grand.GraphFilter;
import net.ggtools.grand.GraphProducer;
import net.ggtools.grand.Link;
import net.ggtools.grand.Log;
import net.ggtools.grand.Node;
import net.ggtools.grand.exceptions.DuplicateNodeException;
import net.ggtools.grand.exceptions.GrandException;
import net.ggtools.grand.graph.GraphImpl;

/**
 * 
 * 
 * @author Christophe Labouisse
 */
public class FromNodeFilter implements GraphFilter {

    GraphProducer graphProducer;

    private final String fromNodeName;
    
    public FromNodeFilter(String nodeName) {
        fromNodeName = nodeName;
    }

    /* (non-Javadoc)
     * @see net.ggtools.grand.GraphProducer#getGraph()
     */
    public Graph getGraph() throws GrandException {
        Log.log("Triggering IsolatedNodeFilter", Log.MSG_VERBOSE);
        final Graph graph = graphProducer.getGraph();
        final Node fromNode = graph.getNode(fromNodeName);
        
        final Graph filteredGraph = new GraphImpl(graph.getName());
        
        copyNodeAndChildren(fromNode,filteredGraph);
        
        final Node startNode = filteredGraph.getNode(graph.getStartNode().getName());
        if (startNode != null) {
            filteredGraph.setStartNode(startNode);
        }
        
        return filteredGraph;
    }

    /* (non-Javadoc)
     * @see net.ggtools.grand.GraphConsumer#setProducer(net.ggtools.grand.GraphProducer)
     */
    public void setProducer(GraphProducer producer) {
        graphProducer = producer;
    }

    private Node copyNodeAndChildren(Node node, Graph toGraph) throws DuplicateNodeException {
        Log.log("FromNodeFilter: copying node "+node,Log.MSG_DEBUG);
        final Graph graph = node.getGraph();
        Node copiedNode = toGraph.createNode(node.getName());
        copiedNode.setDescription(node.getDescription());
        copiedNode.setAttributes(node.getAttributes());
        
        for (Iterator iter = node.getLinks().iterator(); iter.hasNext(); ) {
            Link link = (Link) iter.next();
            
            Node endNode = link.getEndNode();
            Node copiedEndNode = toGraph.getNode(endNode.getName());
            
            if (copiedEndNode == null) {
                copiedEndNode = copyNodeAndChildren(endNode,toGraph);
            }
            
            Link copiedLink = toGraph.createLink(link.getName(),copiedNode,copiedEndNode);
            copiedLink.setAttributes(link.getAttributes());
        }
        
        return copiedNode;
    }
}
