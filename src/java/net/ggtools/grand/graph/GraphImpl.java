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
import java.util.LinkedHashMap;
import java.util.Map;

import net.ggtools.grand.exceptions.DuplicateNodeException;
import net.ggtools.grand.log.LoggerManager;

import org.apache.commons.logging.Log;

/**
 * Simple GraphImpl implementation.
 * 
 * @author Christophe Labouisse
 */
public class GraphImpl implements Graph {
    private static final Log log = LoggerManager.getLog(GraphImpl.class);

    /**
     * An proxified iterator used for getNodes.
     * 
     * @author Christophe Labouisse
     */
    private class NodeIterator implements Iterator {
        private Iterator underlying;

        private Object lastNode;

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

    private final String name;

    private final Map nodeList = new LinkedHashMap();

    private Node graphStartNode;

    private GraphElementFactory elementFactory;

    /**
     * Creates a new named graph.
     * 
     * @param graphName
     *            name for the new graph.
     */
    public GraphImpl(final String graphName) {
        name = graphName;
    }

    /**
     * Returns the graph's name.
     * 
     * @return graph's name.
     */
    public final String getName() {
        return name;
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

    /**
     * Sets the graph starting node.
     * 
     * @param node
     *            to be marked as the starting node of the graph.
     */
    public void setStartNode(final Node node) {
        graphStartNode = node;
    }

    /**
     * Creates a new Node. The object's name must not be <code>null</code> and
     * must be unique within the graph.
     * 
     * @param nodeName
     *            new node's name
     * @return a new Node.
     * @throws DuplicateNodeException
     *             if there is already a node with the same name.
     */
    public Node createNode(final String nodeName) throws DuplicateNodeException {
        if (nodeList.containsKey(nodeName)) { throw new DuplicateNodeException(
                "Creating two nodes named " + nodeName); }
        Node node = getFactory().createNode(nodeName);
        nodeList.put(nodeName, node);
        return node;
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
     * Find a node from its name.
     * 
     * @param nodeName
     *            name of the node to find.
     * @return the node or null if not found.
     */
    public Node getNode(final String nodeName) {
        return (Node) nodeList.get(nodeName);
    }

    /*
     * (non-Javadoc)
     * @see net.ggtools.grand.Graph#hasNode(java.lang.String)
     */
    public boolean hasNode(final String nodeName) {
        return nodeList.containsKey(nodeName);
    }

    /**
     * Get the nodes contained in the graph. The implementing class should
     * garantee that the Iterator will only returns object implementing the Node
     * interface. The returned iterator should implement the optional
     * {@link Iterator#remove()}method in order to allow the filters to remove
     * nodes.
     * 
     * @return an iterator to the graph's nodes.
     */
    public Iterator getNodes() {
        return new NodeIterator(nodeList.values().iterator());
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
}
