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

/**
 * Interface implementated by nodes populating the graph.
 * 
 * @author Christophe Labouisse
 */
public interface Node extends GraphObject {
    /**
     * Attribute bit to be set on <i>main</i> nodes. The definition
     * of a main node depends on the graph's source. For Ant a main
     * node will be a target with a description attribute.
     */
    int ATTR_MAIN_NODE = 1 << 0;
    
    /**
     * Attribute bit to be set on missing nodes, that is nodes created
     * by the graph producer even if no such node exists in the original
     * data source. The cause of such creations depends on the graph's
     * source. For ant projects, <i>missing</i> nodes may be created when
     * a target (or an antcall) refers a non existing node.
     */
    int ATTR_MISSING_NODE = 1 << 1;
    
    /**
     * Returns links originating from the node. The implementing class should
     * insure that the returned list only contains objects implementing the
     * Link interface.
     * 
     * The returned collection should allow modification operations.
     * 
     * @return list of links.
     */
    Collection getLinks();

    /**
     * Returns links coming to the node. The implementing class should
     * insure that the returned list only contains objects implementing the
     * Link interface.
     * 
     * The returned collection should allow modification operations.
     * 
     * @return list of links.
     */
    Collection getBackLinks();

    /**
     * Add a link to the node. This method should be called when the link
     * starts from the node. The implementations should try to preserve
     * the order in which the nodes were added.
     * 
     * @param link link to add
     */
    void addLink(Link link);

    /**
     * Add a link to the node. This method should be called when the link
     * ends at the node. The implementations should try to preserve
     * the order in which the nodes were added.
     * 
     * @param link link to add
     */
    void addBackLink(Link link);

    /**
     * Remove a link from the node. This method should be called when the link
     * starts from the node.
     * 
     * @param link link to remove
     */
    void removeLink(Link link);

    /**
     * Remove a link from the node. This method should be called when the link
     * ends at the node.
     * 
     * @param link link to remove
     */
    void removeBackLink(Link link);

    /**
     * Returns a short description (one line of less) of the node.
     * 
     * @return description.
     */
    String getDescription();
    
    /**
     * Sets the node's description.
     * 
     * @param description node's description
     */
    void setDescription(String description);
}
