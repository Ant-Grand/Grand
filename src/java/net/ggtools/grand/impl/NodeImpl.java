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

package net.ggtools.grand.impl;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import net.ggtools.grand.Graph;
import net.ggtools.grand.Link;
import net.ggtools.grand.Node;

/**
 * 
 * 
 * @author Christophe Labouisse
 */
public class NodeImpl implements Node {
    
    private String name;
    private Graph graph;
    private String description;
    private List links;
    private int attributes;
    
    public NodeImpl(String name, Graph graph) {
        this.name = name;
        this.graph = graph;
        links = new LinkedList();
        attributes = 0;
    }

    /* (non-Javadoc)
     * @see net.ggtools.grand.Node#getLinks()
     */
    public List getLinks() {
        return Collections.unmodifiableList(links);
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
     * @see net.ggtools.grand.Node#setAttribute(int)
     */
    public void setAttribute(int attribute) {
        attributes |= attribute;
    }

    /* (non-Javadoc)
     * @see net.ggtools.grand.Node#setAttribute(int)
     */
    public void unsetAttribute(int attribute) {
        attributes &= -1 ^ attribute;
    }

    /* (non-Javadoc)
     * @see net.ggtools.grand.Node#setAttributes(int)
     */
    public void setAttributes(int attributes) {
        this.attributes = attributes;
    }

    /* (non-Javadoc)
     * @see net.ggtools.grand.Node#hasAttribute(int)
     */
    public boolean hasAttribute(int attribute) {
        return (attributes & attribute) == attribute;
    }

    /* (non-Javadoc)
     * @see net.ggtools.grand.Node#getAttributes()
     */
    public int getAttributes() {
        return attributes;
    }

}
