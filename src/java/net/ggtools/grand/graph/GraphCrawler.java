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

package net.ggtools.grand.graph;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;

/**
 * Walks a graph starting from a specific node to find all the nodes
 * accessible from it.
 * 
 * @author Christophe Labouisse
 */
public class GraphCrawler {
    
    @SuppressWarnings("unused")
    private final Graph graph;
    private final LinkFinder finder;

    /**
     * Creates a new crawler.
     * 
     * @param graph graph to inspect
     * @param finder finder to use.
     */
    public GraphCrawler(final Graph graph, final LinkFinder finder) {
        this.graph = graph;
        this.finder = finder;
    }

    /**
     * Walks the graph from <code>startNode</code> and returns the traversed
     * nodes.
     * 
     * The method maintain a list of nodes to visit initialized with
     * <code>startNode</code> and a  LinkedHashSet of results.
     * 
     * The startNode is popped from the list, added to the result set,
     * and the finder is used to findout all the accessible nodes from it.
     * The nodes founds are added to the list of nodes to visit.
     * 
     * This continue until there is no node to visit.
     * 
     * @param startNode node to start the crawl from.
     * @return a collection containing the traversed nodes.
     */
    public Collection<Node> crawl(final Node startNode) {
        final Set<Node> result = new LinkedHashSet<Node>();
        final LinkedList<Node> nodesToVisit = new LinkedList<Node>();
        nodesToVisit.add(startNode);
        
        while (!nodesToVisit.isEmpty()) {
            final Node current = nodesToVisit.removeFirst();
            
            if (!result.contains(current)) {
                result.add(current);
                nodesToVisit.addAll(finder.getLinks(current));
            }
        }
        
        return result;
    }
}
