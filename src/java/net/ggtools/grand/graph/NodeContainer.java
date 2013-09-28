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

/**
 * Internal interface for node containers.
 *
 * @author Christophe Labouisse
 */
interface NodeContainer {
    /**
     * Find a node from its name.
     *
     * @param nodeName
     *            name of the node to find.
     * @return the node or null if not found.
     */
    Node getNode(final String nodeName);

    /**
     * Get the nodes contained in the graph. The implementing class should
     * guarantee that the Iterator will only returns object implementing the Node
     * interface. The returned iterator should implement the optional
     * {@link Iterator#remove()}method in order to allow the filters to remove
     * nodes.
     *
     * @return an iterator to the graph's nodes.
     */
    Iterator<Node> getNodes();

    /**
     * Checks if the graph has a node with a specific name.
     *
     * @param nodeName
     *            node to search.
     * @return true if the graph contains a node called <code>nodeName</code>.
     */
    boolean hasNode(final String nodeName);

}
