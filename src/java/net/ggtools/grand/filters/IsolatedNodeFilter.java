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
import java.util.LinkedHashSet;
import java.util.Set;

import net.ggtools.grand.exceptions.GrandException;
import net.ggtools.grand.graph.Graph;
import net.ggtools.grand.graph.Node;

/**
 * A filter to remove isolated nodes in a graph.
 *
 * @author Christophe Labouisse
 */
public class IsolatedNodeFilter extends AbstractGraphFilter implements GraphFilter {

    /**
     * Method getFilteredNodes.
     * @return Collection<Node>
     * @throws GrandException
     * @see net.ggtools.grand.filters.AbstractGraphFilter#getFilteredNodes()
     */
    @Override
    protected Collection<Node> getFilteredNodes() throws GrandException {
        final Graph graph = getProducersGraph();
        final Set<Node> result = new LinkedHashSet<Node>();

        for (final Iterator<Node> iter = graph.getNodes(); iter.hasNext();) {
            final Node node = iter.next();

            if ((node.getLinks().size() > 0) || (node.getBackLinks().size() > 0)) {
                result.add(node);
            }
        }

        return result;
    }

}
