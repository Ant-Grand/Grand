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

import net.ggtools.grand.exceptions.GrandException;
import net.ggtools.grand.exceptions.NonExistentNodeException;
import net.ggtools.grand.graph.Graph;
import net.ggtools.grand.graph.GraphCrawler;
import net.ggtools.grand.graph.LinkFinder;
import net.ggtools.grand.graph.Node;

/**
 * An abstract class implementing generic filtering from a graph walk.
 * Derived class need to implemented the getLinkFinder method.
 *
 * @author Christophe Labouisse
 */
public abstract class GraphWalkFilter extends AbstractGraphFilter implements GraphFilter {

    /**
     * Field startNodeName.
     */
    private final String startNodeName;

    /**
     * Creates a new filter.
     * @param nodeName node to search from.
     */
    public GraphWalkFilter(final String nodeName) {
        startNodeName = nodeName;
    }

    /**
     * Method getFilteredNodes.
     * @return Collection<Node>
     * @throws GrandException
     * @see net.ggtools.grand.filters.AbstractGraphFilter#getFilteredNodes()
     */
    @Override
    protected final Collection<Node> getFilteredNodes() throws GrandException {
        final Graph graph = getProducersGraph();
        final Node fromNode = graph.getNode(startNodeName);

        if (fromNode == null) {
            throw new NonExistentNodeException("Node " + startNodeName + " does not exist");
        }

        final GraphCrawler crawler = new GraphCrawler(graph, getLinkFinder());

        return crawler.crawl(fromNode);
    }

    /**
     * Returns an object finding the links to follow from a specific node.
     * This object will be used when walking the graph in {@link #getFilteredNodes()}
     * to find the filtered nodes.
     *
     * @return the <code>LinkFinder</code> object used for filtering.
     * @see LinkFinder
     */
    public abstract LinkFinder getLinkFinder();
}
