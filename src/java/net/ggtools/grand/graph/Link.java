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

package net.ggtools.grand.graph;

import net.ggtools.grand.graph.visit.LinkVisitor;

/**
 * Interface for class representing links. A link is an object connecting
 * exactly two Nodes: the start node and the end node.
 *
 * @author Christophe Labouisse
 */
public interface Link extends GraphObject {

    /**
     * Attribute bit to be set on <i>weak </i> links. The definition of weak
     * depends on the graph source. For Ant weak links will be dependencies
     * underlying ant, antcall, subant, etc. task.
     */
    int ATTR_WEAK_LINK = 1 << 0;

    /**
     * Attribute bit to be set on link subject to a condition.
     */
    int ATTR_CONDITIONAL_LINK = 1 << 1;

    /**
     * Return the node located at the start of the link.
     *
     * @return start node
     */
    Node getStartNode();

    /**
     * Return the node located at the end of the link.
     *
     * @return end node
     */
    Node getEndNode();

    /**
     * Accepts a visitor. The implementation must call the appropriate
     * <code>visitLink</code> method.
     * @param visitor
     */
    void accept(LinkVisitor visitor);
}
