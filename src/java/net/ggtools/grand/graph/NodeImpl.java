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

import net.ggtools.grand.Log;

/**
 * 
 * 
 * @author Christophe Labouisse
 */
public class NodeImpl extends AttributeManager implements Node {
    
    private String name;
    private Graph graph;
    private String description;
    private LinkedHashSet links;
    private LinkedHashSet backLinks;
    
    public NodeImpl(String name, Graph graph) {
        this.name = name;
        this.graph = graph;
        links = new LinkedHashSet();
        backLinks = new LinkedHashSet();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return name;
    }

    /* (non-Javadoc)
     * @see net.ggtools.grand.Node#getLinks()
     */
    public Collection getLinks() {
        return links;
    }

    /* (non-Javadoc)
     * @see net.ggtools.grand.Node#getBackLinks()
     */
    public Collection getBackLinks() {
        return backLinks;
    }

    /* (non-Javadoc)
     * @see net.ggtools.grand.Node#getDescription()
     */
    public String getDescription() {
        return description;
    }

    /* (non-Javadoc)
     * @see net.ggtools.grand.Node#setDescription(java.lang.String)
     */
    public void setDescription(String desc) {
        description = desc;
    }

    /* (non-Javadoc)
     * @see net.ggtools.grand.GraphObject#getGraph()
     */
    public Graph getGraph() {
        return graph;
    }

    /* (non-Javadoc)
     * @see net.ggtools.grand.GraphObject#getName()
     */
    public String getName() {
        return name;
    }

    /* (non-Javadoc)
     * @see net.ggtools.grand.Node#addLink(net.ggtools.grand.Link)
     */
    public void addLink(Link link) {
        links.add(link);
    }

    /* (non-Javadoc)
     * @see net.ggtools.grand.Node#addLink(net.ggtools.grand.Link)
     */
    public void addBackLink(Link link) {
        backLinks.add(link);
    }

    /* (non-Javadoc)
     * @see net.ggtools.grand.Node#removeLink(net.ggtools.grand.Link)
     */
    public void removeLink(Link link) {
        Log.log(name+": removing link "+link,Log.MSG_DEBUG);
        links.remove(link);
    }

    /* (non-Javadoc)
     * @see net.ggtools.grand.Node#removeBackLink(net.ggtools.grand.Link)
     */
    public void removeBackLink(Link link) {
        Log.log(name+": removing back link "+link,Log.MSG_DEBUG);
        backLinks.remove(link);
    }
}
