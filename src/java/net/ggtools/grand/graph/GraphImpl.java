// $Id$
/*
 * ====================================================================
 * Copyright (c) 2002-2003, Christophe Labouisse All rights reserved.
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

package net.ggtools.grand.graph;

import java.util.Iterator;

import net.ggtools.grand.exceptions.DuplicateElementException;
import net.ggtools.grand.log.LoggerManager;

import org.apache.commons.logging.Log;

/**
 * Simple GraphImpl implementation.
 * 
 * @author Christophe Labouisse
 */
public class GraphImpl implements Graph {

    /**
     * An proxified iterator used for getNodes. This class ensure that on
     * deletion the node's links are also removed.
     * 
     * @author Christophe Labouisse
     */
    private class NodeIterator implements Iterator {

        private Object lastNode;
        private Iterator underlying;

        /**
         * @param iterator
         *            underlying iterator.
         */
        public NodeIterator(final Iterator iterator) {
            this.underlying = iterator;
        }

        /**
         * @return true if the iterator still has elements
         */
        public boolean hasNext() {
            return underlying.hasNext();
        }

        /**
         * @return the next element.
         */
        public Object next() {
            lastNode = underlying.next();
            return lastNode;
        }

        /**
         * 
         */
        public void remove() {
            underlying.remove();
            // lastNode should not be null here since remove succeed.
            unlinkNode((Node) lastNode);
        }
    }
    private static final Log log = LoggerManager.getLog(GraphImpl.class);

    private GraphElementFactory elementFactory;

    private Node graphStartNode;

    /**
     * The node storage for the main (sub)graph.
     */
    private final SubGraph mainSubGraph;

    private final String name;

    /**
     * Creates a new named graph.
     * 
     * @param graphName
     *            name for the new graph.
     */
    public GraphImpl(final String graphName) {
        name = graphName;
        mainSubGraph = new SubGraphImpl(graphName, new SubGraphImpl.NodeIteratorFactory() {

            final public Iterator createNodeIterator(final Iterator iterator) {
                return new NodeIterator(iterator);
            }
        });
    }

    /**
     * Creates a new link between two nodes. Unlike {@link #createNode(String)},
     * this method do not require the link's name to be unique or not null. Both
     * nodes should be not null.
     * 
     * @param linkName
     *            the new link name, can be <code>null</code>
     * @param graphStartNode
     *            start node
     * @param endNode
     *            end node
     * @return new link
     */
    public Link createLink(final String linkName, final Node startNode, final Node endNode) {
        Link link = getFactory().createLink(linkName, startNode, endNode);
        startNode.addLink(link);
        endNode.addBackLink(link);
        return link;
    }

    /**
     * Creates a new Node. The object's name must not be <code>null</code> and
     * must be unique within the graph.
     * 
     * @param nodeName
     *            new node's name
     * @return a new Node.
     * @throws DuplicateElementException
     *             if there is already a node with the same name.
     */
    public Node createNode(final String nodeName) throws DuplicateElementException {
        return createNode(mainSubGraph,nodeName);
    }

    /*
     * (non-Javadoc)
     * @see net.ggtools.grand.graph.Graph#createNode(net.ggtools.grand.graph.SubGraph,
     *      java.lang.String)
     */
    public Node createNode(final SubGraph subGraph, final String nodeName) throws DuplicateElementException {
        // We don't want to create a node if it's not gonna be inserted.
        if (subGraph.hasNode(nodeName)) { throw new DuplicateElementException(
                "Creating two nodes named " + nodeName); }
        final Node node = getFactory().createNode(nodeName);
        subGraph.addNode(node);
        return node;
    }

    /*
     * (non-Javadoc)
     * @see net.ggtools.grand.graph.Graph#createSubGraph(java.lang.String)
     */
    public SubGraph createSubGraph(String subGraphName) throws DuplicateElementException {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Returns the graph's name.
     * 
     * @return graph's name.
     */
    public final String getName() {
        return name;
    }

    /*
     * (non-Javadoc)
     * @see net.ggtools.grand.graph.NodeContainer#getNode(java.lang.String)
     */
    public Node getNode(final String nodeName) {
        return mainSubGraph.getNode(nodeName);
    }

    /*
     * (non-Javadoc)
     * @see net.ggtools.grand.graph.NodeContainer#getNodes()
     */
    public Iterator getNodes() {
        return mainSubGraph.getNodes();
    }

    /**
     * Returns the start node of the graph. If no such node is defined,
     * <code>null</code> will be returned.
     * 
     * @return start node
     */
    public Node getStartNode() {
        return graphStartNode;
    }

    /*
     * (non-Javadoc)
     * @see net.ggtools.grand.graph.Graph#getSubGraph(java.lang.String)
     */
    public SubGraph getSubGraph(String subGraphName) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * @see net.ggtools.grand.graph.Graph#getSubgraphs()
     */
    public Iterator getSubgraphs() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * @see net.ggtools.grand.graph.NodeContainer#hasNode(java.lang.String)
     */
    public boolean hasNode(String nodeName) {
        return mainSubGraph.hasNode(nodeName);
    }

    /*
     * (non-Javadoc)
     * @see net.ggtools.grand.graph.Graph#hasSubGraph(java.lang.String)
     */
    public boolean hasSubGraph(String subGraphName) {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * Sets the graph starting node.
     * 
     * @param node
     *            to be marked as the starting node of the graph.
     */
    public void setStartNode(final Node node) {
        if (graphStartNode != null) {
            graphStartNode.clearAttributes(Node.ATTR_START_NODE);
        }
        graphStartNode = node;
        if (graphStartNode != null) {
            graphStartNode.setAttributes(Node.ATTR_START_NODE);
        }
    }

    /**
     * Returns the current element factory creating one if none exists yet. This
     * method can be overriden to use a custom factory.
     * 
     * @return the element factory.
     */
    protected GraphElementFactory getFactory() {
        if (elementFactory == null) {
            elementFactory = new SimpleGraphElementFactory(this);
        }
        return elementFactory;
    }

    /**
     * Remove all links starting from or ending to the node. This method do not
     * remove the node from nodeList.
     * 
     * @param node
     *            node to remove from the links.
     */
    protected void unlinkNode(final Node node) {
        if (log.isTraceEnabled()) log.trace("Unlinking node " + node);

        for (Iterator iter = node.getLinks().iterator(); iter.hasNext();) {
            Link link = (Link) iter.next();
            iter.remove();
            Node endNode = link.getEndNode();
            endNode.removeBackLink(link);
        }

        for (Iterator iter = node.getBackLinks().iterator(); iter.hasNext();) {
            Link link = (Link) iter.next();
            iter.remove();
            Node startNode = link.getStartNode();
            startNode.removeLink(link);
        }

        if (node == graphStartNode) {
            graphStartNode = null;
        }
    }
}
