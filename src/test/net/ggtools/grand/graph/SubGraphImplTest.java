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

import net.ggtools.grand.exceptions.DuplicateElementException;

import junit.framework.TestCase;

/**
 * @author Christophe Labouisse
 */
public class SubGraphImplTest extends TestCase {

    private static final String NODE_NAME_2 = "Node2";
    private static final String NODE_NAME_1 = "Node1";
    private static final String UNKNOWN_NODE_NAME = "Node3";
    private static final class TestIterator implements Iterator<Node> {
        private final Iterator<Node> underlying;

        private TestIterator(final Iterator<Node> iterator) {
            underlying = iterator;
        }

        public boolean hasNext() {
            return underlying.hasNext();
        }

        public Node next() {
            return underlying.next();
        }

        public void remove() {
            underlying.remove();
        }
    }

    private static final String SUBGRAPH_NAME = "myname";

    private SubGraphImpl sgi;

    /*
     * (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        sgi = new SubGraphImpl(SUBGRAPH_NAME, new SubGraphImpl.NodeIteratorFactory() {

            public Iterator<Node> createNodeIterator(final Iterator<Node> iterator) {
                return new TestIterator(iterator);
            }
        });
        sgi.addNode(new NodeImpl(NODE_NAME_1, null));
        sgi.addNode(new NodeImpl(NODE_NAME_2, null));
    }

    public final void testGetName() {
        assertEquals(SUBGRAPH_NAME, sgi.getName());
    }

    public final void testGetNode() throws DuplicateElementException {
        assertTrue(sgi.hasNode(NODE_NAME_1));
        assertTrue(sgi.hasNode(NODE_NAME_2));
        assertFalse(sgi.hasNode(UNKNOWN_NODE_NAME));
    }

    public final void testGetNodes() {
        final Iterator<Node> nodes = sgi.getNodes();
        assertEquals("Checking if getNodes return an iterator from the supplied factory.",
                TestIterator.class, nodes.getClass());
    }

    public final void testHasNode() throws DuplicateElementException {
        assertEquals(sgi.getNode(NODE_NAME_1),new NodeImpl(NODE_NAME_1,null));
        assertEquals(sgi.getNode(NODE_NAME_2),new NodeImpl(NODE_NAME_2,null));
        assertNull(sgi.getNode(UNKNOWN_NODE_NAME));
    }

    public final void testAddNode() throws DuplicateElementException {
        final Iterator<Node> nodes = sgi.getNodes();
        int nodeCount = 0;
        while (nodes.hasNext()) {
            nodes.next();
            nodeCount++;
        }
        assertEquals("Should have 2 nodes", 2, nodeCount);
    }

}
