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

/**
 * Interface to be implemented by graphs. A Graph is both a container and a
 * factory/manager. As a container a Graph can contain both Nodes and SubGraphs
 * and as a factory it is responsible for the creation and ownership of the
 * contained Nodes or Subgraphs. Although some nodes may be stored in subgraphs,
 * the graph is still the factory/manager for them.
 * 
 * @author Christophe Labouisse
 */
public interface Graph extends NodeContainer {

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
     */
    Link createLink(final String linkName, final Node startNode, final Node endNode);

    /**
     * Creates a new Node in the top level graph. The object's name must not be
     * <code>null</code> and must be unique within the graph.
     * 
     * @param nodeName
     *            new node's name
     * @return a new Node.
     * @throws DuplicateElementException
     *             if there is already a node with the same name.
     */
    Node createNode(final String nodeName) throws DuplicateElementException;

    /**
     * Creates a new Node in a specific graph. The object's name must not be
     * <code>null</code> and must be unique within the subgraph.
     * 
     * @param subGraph
     *            the subgraph to place the node in.
     * @param nodeName
     *            new node's name
     * @return a new Node.
     * @throws DuplicateElementException
     *             if there is already a node with the same name.
     */
    Node createNode(final SubGraph subGraph, final String nodeName)
            throws DuplicateElementException;

    /**
     * Creates a new subgraph in the graph. The subgraph name must not be
     * <code>null</code> and must be unique within the graph.
     * 
     * @param subGraphName
     *            new subgraph name.
     * @return a new SubGraph.
     * @throws DuplicateElementException
     *             if a sub graph with the same name already exists in the
     *             graph.
     */
    SubGraph createSubGraph(final String subGraphName) throws DuplicateElementException;

    /**
     * Returns the graph's name.
     * 
     * @return graph's name.
     */
    String getName();

    /**
     * Returns the start node of the graph. If no such node is defined,
     * <code>null</code> will be returned.
     * 
     * @return start node
     */
    Node getStartNode();

    /**
     * Find a subgraph from its name.
     * 
     * @param subGraphName
     *            name of the subgraph to find.
     * @return the subgraph or null if not found.
     */
    SubGraph getSubGraph(final String subGraphName);

    /**
     * Get the nodes contained in the graph. The implementing class should
     * garantee that the Iterator will only returns object implementing the Node
     * interface. The returned iterator should implement the optional
     * {@link Iterator#remove()}method in order to allow the filters to remove
     * nodes.
     * 
     * @return an iterator to the graph's nodes.
     */
    Iterator getSubgraphs();

    /**
     * Checks if the graph has a subgraph with a specific name.
     * 
     * @param subGraphName
     *            subgraph to search.
     * @return true if the graph contains a subgraph called
     *         <code>nodeName</code>.
     */
    boolean hasSubGraph(final String subGraphName);

    /**
     * Sets the graph starting node.
     * 
     * @param node
     *            the node to declare a the start of the graph.
     */
    void setStartNode(final Node node);
}