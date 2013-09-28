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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import net.ggtools.grand.exceptions.GrandException;
import net.ggtools.grand.graph.Graph;
import net.ggtools.grand.graph.GraphProducer;

/**
 * A <i>metafilter</i> made by chaining together several filters. The behaviour
 * of an empty chain is to behave like an identity filter.
 *
 * @author Christophe Labouisse
 */
public class FilterChain implements GraphFilter {

    /**
     * Field filterList.
     */
    private final LinkedList<GraphFilter> filterList = new LinkedList<GraphFilter>();

    /**
     * Field lastFilter.
     */
    private GraphProducer lastFilter = null;

    /**
     * Field name.
     */
    private String name;

    /**
     * Field producer.
     */
    private GraphProducer producer = null;

    /**
     * Creates an <i>anonymous</i> filter chain.
     *
     */
    public FilterChain() {
        this("Anonymous");
    }

    /**
     * Creates a named filter chain.
     *
     * @param name String
     */
    public FilterChain(final String name) {
        this.name = name;
    }

    /**
     * Add a new filter at the beginning of the chain.
     *
     * @param newFilter GraphFilter
     */
    public void addFilterFirst(final GraphFilter newFilter) {
        if (filterList.isEmpty()) {
            lastFilter = newFilter;
        } else {
            final GraphFilter oldFirstFilter = filterList.getFirst();
            oldFirstFilter.setProducer(newFilter);
        }

        filterList.addFirst(newFilter);

        if (producer != null) {
            newFilter.setProducer(producer);
        }

    }

    /**
     * Adds a new filter at the end of the chain.
     *
     * @param newFilter GraphFilter
     */
    public void addFilterLast(final GraphFilter newFilter) {
        filterList.addLast(newFilter);
        newFilter.setProducer(lastFilter);
        lastFilter = newFilter;
    }

    /**
     * Removes all the filters in the chain.
     *
     */
    public void clearFilters() {
        filterList.clear();
        lastFilter = producer;
    }

    /**
     * Returns a list of the filter in the chain.
     *
     * @return a readonly list of the filters.
     */
    public List<GraphFilter> getFilterList() {
        return Collections.unmodifiableList(filterList);
    }

    /**
     * Method getGraph.
     * @return Graph
     * @throws GrandException
     * @see net.ggtools.grand.graph.GraphProducer#getGraph()
     */
    public Graph getGraph() throws GrandException {
        final Graph filteredGraph;

        if (lastFilter == null) {
            filteredGraph = null;
        } else {
            filteredGraph = lastFilter.getGraph();
        }

        return filteredGraph;
    }

    /**
     * Method getName.
     * @return String
     * @see net.ggtools.grand.filters.GraphFilter#getName()
     */
    public String getName() {
        return name;
    }

    /**
     * Method setProducer.
     * @param newProducer GraphProducer
     * @see net.ggtools.grand.graph.GraphConsumer#setProducer(net.ggtools.grand.graph.GraphProducer)
     */
    public void setProducer(final GraphProducer newProducer) {
        producer = newProducer;
        if (filterList.isEmpty()) {
            lastFilter = newProducer;
        } else {
            final GraphFilter firstFilter = filterList.getFirst();
            firstFilter.setProducer(producer);
        }
    }

}
