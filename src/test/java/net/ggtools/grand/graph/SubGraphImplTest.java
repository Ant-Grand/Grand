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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import net.ggtools.grand.exceptions.DuplicateElementException;

/**
 * @author Christophe Labouisse
 */
public class SubGraphImplTest {

    /**
     * Field NODE_NAME_2.
     * (value is {@value #NODE_NAME_2})
     */
    private static final String NODE_NAME_2 = "Node2";

    /**
     * Field NODE_NAME_1.
     * (value is {@value #NODE_NAME_1})
     */
    private static final String NODE_NAME_1 = "Node1";

    /**
     * Field UNKNOWN_NODE_NAME.
     * (value is {@value #UNKNOWN_NODE_NAME})
     */
    private static final String UNKNOWN_NODE_NAME = "Node3";

    /**
     * @author Christophe Labouisse
     */
    private static final class TestIterator implements Iterator<Node> {
        /**
         * Field underlying.
         */
        private final Iterator<Node> underlying;

        /**
         * Constructor for TestIterator.
         * @param iterator Iterator&lt;Node&gt;
         */
        private TestIterator(final Iterator<Node> iterator) {
            underlying = iterator;
        }

        /**
         * Method hasNext.
         * @return boolean
         * @see java.util.Iterator#hasNext()
         */
        public boolean hasNext() {
            return underlying.hasNext();
        }

        /**
         * Method next.
         * @return Node
         * @see java.util.Iterator#next()
         */
        public Node next() {
            return underlying.next();
        }

        /**
         * Method remove.
         * @see java.util.Iterator#remove()
         */
        public void remove() {
            underlying.remove();
        }
    }

    /**
     * Field SUBGRAPH_NAME.
     * (value is {@value #SUBGRAPH_NAME}")
     */
    private static final String SUBGRAPH_NAME = "myname";

    /**
     * Field sgi.
     */
    private SubGraphImpl sgi;

    /**
     * Method setUp.
     * @throws DuplicateElementException if any of test nodes is present
     */
    @Before
    public final void setUp() throws DuplicateElementException {
        sgi = new SubGraphImpl(SUBGRAPH_NAME, new SubGraphImpl.NodeIteratorFactory() {

            public Iterator<Node> createNodeIterator(final Iterator<Node> iterator) {
                return new TestIterator(iterator);
            }
        });
        sgi.addNode(new NodeImpl(NODE_NAME_1, null));
        sgi.addNode(new NodeImpl(NODE_NAME_2, null));
    }

    /**
     * Method testGetName.
     */
    @Test
    public final void testGetName() {
        assertEquals(SUBGRAPH_NAME, sgi.getName());
    }

    /**
     * Method testGetNode.
     */
    @Test
    public final void testGetNode() {
        assertTrue(sgi.hasNode(NODE_NAME_1));
        assertTrue(sgi.hasNode(NODE_NAME_2));
        assertFalse(sgi.hasNode(UNKNOWN_NODE_NAME));
    }

    /**
     * Method testGetNodes.
     */
    @Test
    public final void testGetNodes() {
        final Iterator<Node> nodes = sgi.getNodes();
        assertEquals("Checking if getNodes return an iterator from the supplied factory.",
                TestIterator.class, nodes.getClass());
    }

    /**
     * Method testHasNode.
     */
    @Test
    public final void testHasNode() {
        assertEquals(sgi.getNode(NODE_NAME_1),
                new NodeImpl(NODE_NAME_1, null));
        assertEquals(sgi.getNode(NODE_NAME_2),
                new NodeImpl(NODE_NAME_2, null));
        assertNull(sgi.getNode(UNKNOWN_NODE_NAME));
    }

    /**
     * Method testAddNode.
     */
    @Test
    public final void testAddNode() {
        final Iterator<Node> nodes = sgi.getNodes();
        int nodeCount = 0;
        while (nodes.hasNext()) {
            nodes.next();
            nodeCount++;
        }
        assertEquals("Should have 2 nodes", 2, nodeCount);
    }

}
