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
         * Create a new iterator from an existing one. A trival implementation
         * may merely return <code>iterator</code>.
         * 
         * @param iterator
         * @return
         */
        Iterator createNodeIterator(final Iterator iterator);
    }

    private static final Log log = LoggerManager.getLog(SubGraphImpl.class);

    private final String name;

    private final NodeIteratorFactory nodeIteratorFactory;

    private final Map nodeList = new LinkedHashMap();

    /**
     * Creates a new instance using a trival
     * {@link SubGraphImpl.NodeIteratorFactory}.
     * @param name
     *            sub graph name.
     */
    SubGraphImpl(final String name) {
        this(name, new NodeIteratorFactory() {

            final public Iterator createNodeIterator(final Iterator iterator) {
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

    public void addNode(final Node node) throws DuplicateElementException {
        final String nodeName = node.getName();
        if (nodeList.containsKey(nodeName)) {
            log.error("addNode() - Cannot add two nodes with the same name : nodeName = "
                    + nodeName, null);
            throw new DuplicateElementException("Creating two nodes named " + nodeName);
        }
        nodeList.put(nodeName, node);
    }

    /*
     * (non-Javadoc)
     * @see net.ggtools.grand.graph.SubGraph#getName()
     */
    public String getName() {
        return name;
    }

    /*
     * (non-Javadoc)
     * @see net.ggtools.grand.graph.NodeContainer#getNode(java.lang.String)
     */
    public Node getNode(final String nodeName) {
        return (Node) nodeList.get(nodeName);
    }

    /*
     * (non-Javadoc)
     * @see net.ggtools.grand.graph.NodeContainer#getNodes()
     */
    public Iterator getNodes() {
        return nodeIteratorFactory.createNodeIterator(nodeList.values().iterator());
    }

    /*
     * (non-Javadoc)
     * @see net.ggtools.grand.graph.NodeContainer#hasNode(java.lang.String)
     */
    public boolean hasNode(final String nodeName) {
        return nodeList.containsKey(nodeName);
    }
}
