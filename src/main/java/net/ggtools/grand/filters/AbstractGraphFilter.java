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

package net.ggtools.grand.filters;

import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.logging.Log;

import net.ggtools.grand.exceptions.GrandException;
import net.ggtools.grand.graph.Graph;
import net.ggtools.grand.graph.GraphProducer;
import net.ggtools.grand.graph.Node;
import net.ggtools.grand.log.LoggerManager;

/**
 * @author Christophe Labouisse
 */
public abstract class AbstractGraphFilter implements GraphFilter {
    /**
     * Field log.
     */
    private static final Log LOG =
            LoggerManager.getLog(AbstractGraphFilter.class);

    /**
     * Field graphProducer.
     */
    private GraphProducer graphProducer;

    /**
     * Field producersGraph.
     */
    private Graph producersGraph;

    /**
     * Field name.
     */
    protected String name;

    /**
     * Creates an anonymous filter.
     *
     */
    protected AbstractGraphFilter() {
        this("Anonymous");
    }

    /**
     * Creates a named filter.
     *
     * @param name String
     */
    protected AbstractGraphFilter(final String name) {
        this.name = name;
    }

    /**
     * Method getGraph.
     * @return Graph
     * @throws GrandException if an error occurs in getNodes()/getFilteredNodes()
     * @see net.ggtools.grand.graph.GraphProducer#getGraph()
     */
    public final Graph getGraph() throws GrandException {
        LOG.debug("Triggering AbstractGraphFilter");
        final Graph graph = getProducersGraph();

        if (graph != null) {
            final Collection<Node> nodeList = getFilteredNodes();

            for (final Iterator<Node> iter = graph.getNodes(); iter.hasNext();) {
                final Node node = iter.next();

                if (!nodeList.contains(node)) {
                    iter.remove();
                }
            }
        }

        // The graph had been filtered so it must not be used
        // if the filter is called again.
        producersGraph = null;

        return graph;
    }

    /**
     * Method setProducer.
     * @param producer GraphProducer
     * @see net.ggtools.grand.graph.GraphConsumer#setProducer(net.ggtools.grand.graph.GraphProducer)
     */
    public final void setProducer(final GraphProducer producer) {
        graphProducer = producer;
        producersGraph = null;
    }

    /**
     * Method getName.
     * @return String
     * @see net.ggtools.grand.filters.GraphFilter#getName()
     */
    public final String getName() {
        return name;
    }

    /**
     * Get the nodes from the graph that pass the filter. This method should not
     * alter the input graph. The returned collection may be read only and
     * return {@link UnsupportedOperationException}on modification methods.
     *
     * @return a collection of nodes.
     * @throws GrandException
     *             if the filtering cannot be done
     */
    protected abstract Collection<Node> getFilteredNodes()
            throws GrandException;

    /**
     * Returns the current graph producer.
     *
     * @return graph producer.
     */
    protected final GraphProducer getGraphProducer() {
        return graphProducer;
    }

    /**
     * Returns the graph from the current producer. This method is intended to
     * prevent calling several time the producer to get a graph.
     *
     * @return current graph.
     * @throws GrandException
     *             if something goes wrong.
     */
    protected final Graph getProducersGraph() throws GrandException {
        if ((producersGraph == null) && (graphProducer != null)) {
            producersGraph = graphProducer.getGraph();
        }
        return producersGraph;
    }
}
