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

package net.ggtools.grand;

import java.util.Iterator;

/**
 * Interface implementated by nodes populating the graph.
 * 
 * @author Christophe Labouisse
 */
public interface Node {
    
    /**
     * Return the nodes upon which the current node depends.
     * 
     * The returned iterator won't have to support the <code>remove</code>
     * method.
     * 
     * @return iterator to the nodes.
     */
    Iterator getDependencies();
    
    /**
     * Returns node dependencies as an array.
     * 
     * @return
     */
    Node [] getDependenciesArray();

    /**
     * Returns the node name.
     * 
     * @return name 
     */
    String getName();
    
    /**
     * Returns a short description (one line of less) of the node.
     * 
     * @return description.
     */
    String getDescription();
    
    /**
     * Tells wether or not the node is a main node. The definition of a main
     * node depends on the underlying system but it is generaly a node of special
     * importance (not necessary the start node). In Ant, for example, special
     * nodes will be main targets (targets with a description attribute, meaned to 
     * be user visible).
     * 
     * When a system has no target of special importance, this method should always
     * return <code>true</code>. 
     * 
     * @return true if the node is a main node.
     */
    boolean isMainNode();
}
