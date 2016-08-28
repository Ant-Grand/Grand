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
    private class NodeIterator implements Iterator<Node> {

        /**
         * Field lastNode.
         */
        private Node lastNode;

        /**
         * Logger for this class.
         */
        @SuppressWarnings("unused")
        private final Log log = LoggerManager.getLog(NodeIterator.class);

        /**
         * Field underlying.
         */
        private final Iterator<Node> underlying;

        /**
         * @param iterator
         *            underlying iterator.
         */
        public NodeIterator(final Iterator<Node> iterator) {
            underlying = iterator;
        }

        /**
         * @return true if the iterator still has elements
         * @see java.util.Iterator#hasNext()
         */
        public boolean hasNext() {
            return underlying.hasNext();
        }

        /**
         * @return the next element.
         * @see java.util.Iterator#next()
         */
        public Node next() {
            lastNode = underlying.next();
            return lastNode;
        }

        /**
         *
         * @see java.util.Iterator#remove()
         */
        public void remove() {
            underlying.remove();
            // lastNode should not be null here since remove succeed.
            unlinkNode(lastNode);
        }
    }

    /**
     * Field log.
     */
    private static final Log LOG = LoggerManager.getLog(GraphImpl.class);

    /**
     * Field elementFactory.
     */
    private GraphElementFactory elementFactory;

    /**
     * Field graphStartNode.
     */
    private Node graphStartNode;

    /**
     * The node storage for the main (sub)graph.
     */
    private final SubGraph mainSubGraph;

    /**
     * Field name.
     */
    private final String name;

    /**
     * Field subGraphList.
     */
    private final Map<String, SubGraph> subGraphList =
            new LinkedHashMap<String, SubGraph>();

    /**
     * Creates a new named graph.
     *
     * @param graphName
     *            name for the new graph.
     */
    public GraphImpl(final String graphName) {
        name = graphName;
        mainSubGraph = new SubGraphImpl(graphName, new SubGraphImpl.NodeIteratorFactory() {
            public final Iterator<Node> createNodeIterator(final Iterator<Node> iterator) {
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
     * @param startNode
     *            start node
     * @param endNode
     *            end node
     * @return new link
     * @see net.ggtools.grand.graph.Graph#createLink(String, Node, Node)
     */
    public final Link createLink(final String linkName, final Node startNode,
            final Node endNode) {
        final Link link = getFactory().createLink(linkName, startNode, endNode);
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
     * @see net.ggtools.grand.graph.Graph#createNode(String)
     */
    public final Node createNode(final String nodeName)
            throws DuplicateElementException {
        return createNode(mainSubGraph, nodeName);
    }

    /**
     * Method createNode.
     * @param subGraph SubGraph
     * @param nodeName String
     * @return Node
     * @throws DuplicateElementException
     *             if there is already a node with the same name.
     * @see net.ggtools.grand.graph.Graph#createNode(net.ggtools.grand.graph.SubGraph,
     *      java.lang.String)
     */
    public final Node createNode(final SubGraph subGraph, final String nodeName)
            throws DuplicateElementException {
        // We don't want to create a node if it's not gonna be inserted.
        if (subGraph.hasNode(nodeName)) {
            throw new DuplicateElementException("Creating two nodes named " + nodeName);
        }
        final Node node = getFactory().createNode(nodeName);
        subGraph.addNode(node);
        return node;
    }

    /**
     * Method createSubGraph.
     * @param subGraphName String
     * @return SubGraph
     * @throws DuplicateElementException
     *             if there is already a subgraph with the same name.
     * @see net.ggtools.grand.graph.Graph#createSubGraph(java.lang.String)
     */
    public final SubGraph createSubGraph(final String subGraphName)
            throws DuplicateElementException {
        if (subGraphList.containsKey(subGraphName)) {
            LOG.error("createSubGraph(subGraphName = " + subGraphName
                    + ") - Cannot create two subgraphs with the same name", null);
            throw new DuplicateElementException("A subgraph called "
                    + subGraphName + " already exists");
        }
        final SubGraph newSubGraph = new SubGraphImpl(subGraphName);
        subGraphList.put(subGraphName, newSubGraph);
        return newSubGraph;
    }

    /**
     * Returns the graph's name.
     *
     * @return graph's name.
     * @see net.ggtools.grand.graph.Graph#getName()
     */
    public final String getName() {
        return name;
    }

    /**
     * Method getNode.
     * @param nodeName String
     * @return Node
     * @see net.ggtools.grand.graph.NodeContainer#getNode(java.lang.String)
     */
    public final Node getNode(final String nodeName) {
        return mainSubGraph.getNode(nodeName);
    }

    /**
     * Method getNodes.
     * @return Iterator of <code>Node</code>
     * @see net.ggtools.grand.graph.NodeContainer#getNodes()
     */
    public final Iterator<Node> getNodes() {
        return mainSubGraph.getNodes();
    }

    /**
     * Returns the start node of the graph. If no such node is defined,
     * <code>null</code> will be returned.
     *
     * @return start node
     * @see net.ggtools.grand.graph.Graph#getStartNode()
     */
    public final Node getStartNode() {
        return graphStartNode;
    }

    /**
     * Method getSubGraph.
     * @param subGraphName String
     * @return SubGraph
     * @see net.ggtools.grand.graph.Graph#getSubGraph(java.lang.String)
     */
    public final SubGraph getSubGraph(final String subGraphName) {
        return subGraphList.get(subGraphName);
    }

    /**
     * Method getSubgraphs.
     * @return Iterator of <code>SubGraph</code>
     * @see net.ggtools.grand.graph.Graph#getSubgraphs()
     */
    public final Iterator<SubGraph> getSubgraphs() {
        return subGraphList.values().iterator();
    }

    /**
     * Method hasNode.
     * @param nodeName String
     * @return boolean
     * @see net.ggtools.grand.graph.NodeContainer#hasNode(java.lang.String)
     */
    public final boolean hasNode(final String nodeName) {
        return mainSubGraph.hasNode(nodeName);
    }

    /**
     * Method hasSubGraph.
     * @param subGraphName String
     * @return boolean
     * @see net.ggtools.grand.graph.Graph#hasSubGraph(java.lang.String)
     */
    public final boolean hasSubGraph(final String subGraphName) {
        return subGraphList.containsKey(subGraphName);
    }

    /**
     * Sets the graph starting node.
     *
     * @param node
     *            to be marked as the starting node of the graph.
     * @see net.ggtools.grand.graph.Graph#setStartNode(Node)
     */
    public final void setStartNode(final Node node) {
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
     * method can be overridden to use a custom factory.
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
    protected final void unlinkNode(final Node node) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("Unlinking node " + node);
        }

        for (final Iterator<Link> iter = node.getLinks().iterator(); iter.hasNext();) {
            final Link link = iter.next();
            iter.remove();
            final Node endNode = link.getEndNode();
            endNode.removeBackLink(link);
        }

        for (final Iterator<Link> iter = node.getBackLinks().iterator(); iter.hasNext();) {
            final Link link = iter.next();
            iter.remove();
            final Node startNode = link.getStartNode();
            startNode.removeLink(link);
        }

        if (node == graphStartNode) {
            graphStartNode = null;
        }
    }
}
