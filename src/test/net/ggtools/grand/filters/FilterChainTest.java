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

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import net.ggtools.grand.exceptions.GrandException;
import net.ggtools.grand.graph.Graph;
import net.ggtools.grand.graph.GraphImpl;
import net.ggtools.grand.graph.GraphProducer;

/**
 * @author Christophe Labouisse
 */
public class FilterChainTest {

    /**
     * A dummy graph producer returning always the same empty graph.
     *
     *
     * @author Christophe Labouisse
     */
    private final class DummyProducer implements GraphProducer {
        /**
         * Field graph.
         */
        private final GraphImpl graph;

        /**
         * Constructor for DummyProducer.
         * @param name String
         */
        public DummyProducer(final String name) {
            graph = new GraphImpl(name);
        }

        /**
         * Method getGraph.
         * @return Graph
         * @throws GrandException
         * @see net.ggtools.grand.graph.GraphProducer#getGraph()
         */
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
        /**
         * Field name.
         */
        private final String name;

        /**
         * Field producer.
         */
        private GraphProducer producer;

        /**
         * Constructor for DummyFilter.
         * @param name String
         */
        public DummyFilter(final String name) {
            this.name = name;

        }

        /**
         * Method getGraph.
         * @return Graph
         * @throws GrandException
         * @see net.ggtools.grand.graph.GraphProducer#getGraph()
         */
        public Graph getGraph() throws GrandException {
            traceBuffer.append(name);
            return producer.getGraph();
        }

        /**
         * Method setProducer.
         * @param producer GraphProducer
         * @see net.ggtools.grand.graph.GraphConsumer#setProducer(GraphProducer)
         */
        public void setProducer(final GraphProducer producer) {
            this.producer = producer;
        }

        /**
         * Method getName.
         * @return String
         * @see net.ggtools.grand.filters.GraphFilter#getName()
         */
        public String getName() {
            return name;
        }
    }

    /**
     * Field filter1.
     */
    private GraphFilter filter1;

    /**
     * Field filter2.
     */
    private GraphFilter filter2;

    /**
     * Field filter3.
     */
    private GraphFilter filter3;

    /**
     * Field producer.
     */
    private GraphProducer producer;

    /**
     * Field traceBuffer.
     */
    private final StringBuffer traceBuffer = new StringBuffer();

    /**
     * Field filterChain.
     */
    private FilterChain filterChain;

    /**
     * Method testUnitializedChain.
     * @throws GrandException
     */
    @Test
    public final void testUnitializedChain() throws GrandException {
        assertNull(filterChain.getGraph());
    }

    /**
     * Method testEmptyChain.
     * @throws GrandException
     */
    @Test
    public final void testEmptyChain() throws GrandException {
        filterChain.setProducer(producer);
        assertSame("Both graphs should be the same",
                producer.getGraph(), filterChain.getGraph());
    }

    /**
     * Method testOneFilter.
     * @throws GrandException
     */
    @Test
    public final void testOneFilter() throws GrandException {
        filterChain.setProducer(producer);
        filterChain.addFilterFirst(filter1);
        assertSame("Both graphs should be the same",
                producer.getGraph(), filterChain.getGraph());
        assertEquals("1", traceBuffer.toString());
    }


    /**
     * Method testAddFilterFirst.
     * @throws GrandException
     */
    @Test
    public final void testAddFilterFirst() throws GrandException {
        filterChain.setProducer(producer);
        filterChain.addFilterFirst(filter1);
        filterChain.addFilterFirst(filter2);
        filterChain.addFilterFirst(filter3);
        assertSame("Both graphs should be the same",
                producer.getGraph(), filterChain.getGraph());
        assertEquals("123", traceBuffer.toString());
        assertSame("Both graphs should be the same",
                producer.getGraph(), filterChain.getGraph());
        assertEquals("123123", traceBuffer.toString());
    }

    /**
     * Method testAddFilterLast.
     * @throws GrandException
     */
    @Test
    public final void testAddFilterLast() throws GrandException {
        filterChain.setProducer(producer);
        filterChain.addFilterLast(filter1);
        filterChain.addFilterLast(filter2);
        filterChain.addFilterLast(filter3);
        assertSame("Both graphs should be the same",
                producer.getGraph(), filterChain.getGraph());
        assertEquals("321", traceBuffer.toString());
        assertSame("Both graphs should be the same",
                producer.getGraph(), filterChain.getGraph());
        assertEquals("321321", traceBuffer.toString());
    }

    /**
     * Method testClearFilters.
     * @throws GrandException
     */
    @Test
    public final void testClearFilters() throws GrandException {
        filterChain.setProducer(producer);
        filterChain.addFilterFirst(filter1);
        filterChain.addFilterFirst(filter2);
        filterChain.addFilterFirst(filter3);
        assertSame("Both graphs should be the same",
                producer.getGraph(), filterChain.getGraph());
        assertEquals("123", traceBuffer.toString());
        filterChain.clearFilters();
        assertSame("Both graphs should be the same",
                producer.getGraph(), filterChain.getGraph());
        assertEquals("Trace should not have changed",
                "123", traceBuffer.toString());
    }

    /**
     * Method setUp.
     */
    @Before
    public final void setUp() {
        filter1 = new DummyFilter("1");
        filter2 = new DummyFilter("2");
        filter3 = new DummyFilter("3");
        producer = new DummyProducer("dummy producer");
        filterChain = new FilterChain();
    }

}
