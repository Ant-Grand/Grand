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

package net.ggtools.grand.filters;

import net.ggtools.grand.exceptions.GrandException;
import net.ggtools.grand.graph.Graph;
import net.ggtools.grand.graph.GraphImpl;
import net.ggtools.grand.graph.GraphProducer;
import junit.framework.TestCase;

/**
 * @author Christophe Labouisse
 */
public class FilterChainTest extends TestCase {

    /**
     * A dummy graph producer returning always the same empty graph.
     * 
     * 
     * @author Christophe Labouisse
     */
    private final class DummyProducer implements GraphProducer {
        private final GraphImpl graph;

        public DummyProducer(String name) {
            graph = new GraphImpl(name);
        }

        public Graph getGraph() throws GrandException {
            return graph;
        }
    }

    /**
     * A Dummy filter class for test purpose. It basically does nothing except
     * adding a trace in the FilterChainTest class.
     * 
     * @author Christophe Labouisse
     */
    private final class DummyFilter implements GraphFilter {
        private final String name;

        private GraphProducer producer;

        public DummyFilter(String name) {
            this.name = name;

        }

        public Graph getGraph() throws GrandException {
            traceBuffer.append(name);
            return producer.getGraph();
        }

        public void setProducer(GraphProducer producer) {
            this.producer = producer;
        }

        public String getName() {
            return name;
        }
    }

    private GraphFilter filter1;

    private GraphFilter filter2;

    private GraphFilter filter3;

    private GraphProducer producer;

    private final StringBuffer traceBuffer = new StringBuffer();

    private FilterChain filterChain;

    public final void testUnitializedChain() throws GrandException {
        assertNull(filterChain.getGraph());
    }

    public final void testEmptyChain() throws GrandException {
        filterChain.setProducer(producer);
        assertSame("Both graph should be the same",producer.getGraph(),filterChain.getGraph());
    }
    
    public final void testOneFilter() throws GrandException {
        filterChain.setProducer(producer);
        filterChain.addFilterFirst(filter1);
        assertSame("Both graph should be the same",producer.getGraph(),filterChain.getGraph());
        assertEquals("1",traceBuffer.toString());
    }


    public final void testAddFilterFirst() throws GrandException {
        filterChain.setProducer(producer);
        filterChain.addFilterFirst(filter1);
        filterChain.addFilterFirst(filter2);
        filterChain.addFilterFirst(filter3);
        assertSame("Both graph should be the same",producer.getGraph(),filterChain.getGraph());
        assertEquals("123",traceBuffer.toString());
        assertSame("Both graph should be the same",producer.getGraph(),filterChain.getGraph());
        assertEquals("123123",traceBuffer.toString());
    }

    public final void testAddFilterLast() throws GrandException {
        filterChain.setProducer(producer);
        filterChain.addFilterLast(filter1);
        filterChain.addFilterLast(filter2);
        filterChain.addFilterLast(filter3);
        assertSame("Both graph should be the same",producer.getGraph(),filterChain.getGraph());
        assertEquals("321",traceBuffer.toString());
        assertSame("Both graph should be the same",producer.getGraph(),filterChain.getGraph());
        assertEquals("321321",traceBuffer.toString());
    }

    public final void testClearFilters() throws GrandException {
        filterChain.setProducer(producer);
        filterChain.addFilterFirst(filter1);
        filterChain.addFilterFirst(filter2);
        filterChain.addFilterFirst(filter3);
        assertSame("Both graph should be the same",producer.getGraph(),filterChain.getGraph());
        assertEquals("123",traceBuffer.toString());
        filterChain.clearFilters();
        assertSame("Both graph should be the same",producer.getGraph(),filterChain.getGraph());
        assertEquals("Trace should not have changed","123",traceBuffer.toString());
    }

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() {
        filter1 = new DummyFilter("1");
        filter2 = new DummyFilter("2");
        filter3 = new DummyFilter("3");
        producer = new DummyProducer("dummy producer");
        filterChain = new FilterChain();
    }

}