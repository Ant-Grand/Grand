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
package net.ggtools.grand.graph;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import net.ggtools.grand.exceptions.DuplicateElementException;
import net.ggtools.grand.log.LoggerManager;

import org.apache.commons.logging.Log;

/**
 * @author Christophe Labouisse
 */
class SubGraphImpl implements SubGraph {
    /**
     * @author Christophe Labouisse
     */
    interface NodeIteratorFactory {
        /**
         * Create a new iterator from an existing one. A trivial implementation
         * may merely return <code>iterator</code>.
         *
         * @param iterator Iterator<Node>
         * @return Iterator<Node>
         */
        Iterator<Node> createNodeIterator(final Iterator<Node> iterator);
    }

    /**
     * Field log.
     */
    private static final Log LOG = LoggerManager.getLog(SubGraphImpl.class);

    /**
     * Field name.
     */
    private final String name;

    /**
     * Field nodeIteratorFactory.
     */
    private final NodeIteratorFactory nodeIteratorFactory;

    /**
     * Field nodeList.
     */
    private final Map<String, Node> nodeList = new LinkedHashMap<String, Node>();

    /**
     * Creates a new instance using a trivial
     * {@link SubGraphImpl.NodeIteratorFactory}.
     * @param name
     *            sub graph name.
     */
    SubGraphImpl(final String name) {
        this(name, new NodeIteratorFactory() {

            public final Iterator<Node> createNodeIterator(final Iterator<Node> iterator) {
                return iterator;
            }
        });
    }

    /**
     * Creates a new instance using a specific
     * {@link SubGraphImpl.NodeIteratorFactory}.
     * @param name
     *            sub graph name.
     * @param nodeIteratorFactory
     *            factory to be used for {@link #getNodes()}.
     */
    SubGraphImpl(final String name, final NodeIteratorFactory nodeIteratorFactory) {
        this.name = name;
        this.nodeIteratorFactory = nodeIteratorFactory;
    }

    /**
     * Method addNode.
     * @param node Node
     * @throws DuplicateElementException
     * @see net.ggtools.grand.graph.SubGraph#addNode(Node)
     */
    public void addNode(final Node node) throws DuplicateElementException {
        final String nodeName = node.getName();
        if (nodeList.containsKey(nodeName)) {
            LOG.error("addNode() - Cannot add two nodes with the same name : nodeName = "
                    + nodeName, null);
            throw new DuplicateElementException("Creating two nodes named " + nodeName);
        }
        nodeList.put(nodeName, node);
    }

    /**
     * Method getName.
     * @return String
     * @see net.ggtools.grand.graph.SubGraph#getName()
     */
    public String getName() {
        return name;
    }

    /**
     * Method getNode.
     * @param nodeName String
     * @return Node
     * @see net.ggtools.grand.graph.NodeContainer#getNode(java.lang.String)
     */
    public Node getNode(final String nodeName) {
        return nodeList.get(nodeName);
    }

    /**
     * Method getNodes.
     * @return Iterator<Node>
     * @see net.ggtools.grand.graph.NodeContainer#getNodes()
     */
    public Iterator<Node> getNodes() {
        return nodeIteratorFactory.createNodeIterator(nodeList.values().iterator());
    }

    /**
     * Method hasNode.
     * @param nodeName String
     * @return boolean
     * @see net.ggtools.grand.graph.NodeContainer#hasNode(java.lang.String)
     */
    public boolean hasNode(final String nodeName) {
        return nodeList.containsKey(nodeName);
    }
}
