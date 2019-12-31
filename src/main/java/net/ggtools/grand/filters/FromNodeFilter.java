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

import net.ggtools.grand.graph.ForwardLinkFinder;
import net.ggtools.grand.graph.LinkFinder;


/**
 * A graph filter returning all the nodes accessible from a specific node
 * using only forward links. For Ant, this will extract the sub graph of all
 * targets on which a specific node depend.
 *
 * @author Christophe Labouisse
 */
public class FromNodeFilter extends GraphWalkFilter implements GraphFilter {

    /**
     * Field linkFinder.
     */
    private final LinkFinder linkFinder = new ForwardLinkFinder();

    /**
     * Creates a new filter.
     * @param nodeName node to search from.
     */
    public FromNodeFilter(final String nodeName) {
        super(nodeName);
    }

    /**
     * Method getLinkFinder.
     * @return LinkFinder
     * @see net.ggtools.grand.filters.GraphWalkFilter#getLinkFinder()
     */
    @Override
    public final LinkFinder getLinkFinder() {
        return linkFinder;
    }
}
