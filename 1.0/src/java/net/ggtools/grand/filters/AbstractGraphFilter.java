// $Id$
/* ====================================================================
 * Copyright (c) 2002-2003, Christophe Labouisse
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above
 *    copyright notice, this list of conditions and the following
 *    disclaimer in the documentation and/or other materials provided
 *    with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package net.ggtools.grand.filters;

import java.util.Collection;
import java.util.Iterator;

import net.ggtools.grand.Log;
import net.ggtools.grand.exceptions.GrandException;
import net.ggtools.grand.graph.Graph;
import net.ggtools.grand.graph.GraphProducer;
import net.ggtools.grand.graph.Node;

/**
 * 
 * 
 * @author Christophe Labouisse
 */
public abstract class AbstractGraphFilter implements GraphFilter {

    private GraphProducer graphProducer;

    private Graph producersGraph;

    /* (non-Javadoc)
     * @see net.ggtools.grand.graph.GraphProducer#getGraph()
     */
    public Graph getGraph() throws GrandException {
        Log.log("Triggering GraphWalkfilter", Log.MSG_VERBOSE);
        final Graph graph = getProducersGraph();
        final Collection nodeList = getFilteredNodes();

        for (Iterator iter = nodeList.iterator(); iter.hasNext();) {
            Node node = (Node) iter.next();
        }

        for (Iterator iter = graph.getNodes(); iter.hasNext();) {
            Node node = (Node) iter.next();

            if (!nodeList.contains(node)) {
                iter.remove();
            }
        }

        // The graph had been filtered so it must not be used if the filter is called
        // again.
        producersGraph = null;

        return graph;
    }

    /* (non-Javadoc)
     * @see net.ggtools.grand.GraphConsumer#setProducer(net.ggtools.grand.GraphProducer)
     */
    public void setProducer(final GraphProducer producer) {
        graphProducer = producer;
        producersGraph = null;
    }

    /**
     * Returns the current graph producer.
     * 
     * @return graph producer.
     */
    protected final GraphProducer getGraphProducer() {
        return graphProducer;
    }

    /**
     * Returns the graph from the current producer. This method is intended
     * to prevent calling several time the producer to get a graph.
     * 
     * @return current graph.
     * @throws GrandException if something goes wrong.
     */
    protected final Graph getProducersGraph() throws GrandException {
        if (producersGraph == null) {
            producersGraph = graphProducer.getGraph();
        }
        return producersGraph;
    }
}
