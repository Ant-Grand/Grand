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

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.logging.Log;

import net.ggtools.grand.graph.visit.NodeVisitor;
import net.ggtools.grand.log.LoggerManager;

/**
 * 
 * 
 * @author Christophe Labouisse
 */
public class NodeImpl extends AttributeManager implements Node {
    private static final Log log = LoggerManager.getLog(NodeImpl.class);

    private final String name;

    private final Graph graph;

    private String description;

    private final Set<Link> links;

    private final Set<Link> backLinks;

    private String source;

    /**
     * Creates an new NodeImpl.
     * 
     * @param name
     *            node's name
     * @param graph
     *            owner graph.
     */
    public NodeImpl(final String name, final Graph graph) {
        this.name = name;
        this.graph = graph;
        links = new LinkedHashSet<Link>();
        backLinks = new LinkedHashSet<Link>();
    }

    /**
     * Returns true of the current object and <code>obj</code> are equals. Two
     * Nodes are equals when they belong to the same graph and they have the
     * same name.
     * 
     * @param obj
     *            object to compare the node to.
     * @return true if this is equal to obj.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof Node) {
            final Node otherNode = (Node) obj;
            return (graph == otherNode.getGraph()) && (name.equals(otherNode.getName()));
        }
        return false;
    }

    /**
     * Compute a hash code for the current node. A Node's hash will be his
     * name's hashcode.
     * 
     * @return hash code for the current node.
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return name.hashCode();
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return name;
    }

    /*
     * (non-Javadoc)
     * @see net.ggtools.grand.Node#getLinks()
     */
    public Collection<Link> getLinks() {
        return links;
    }

    /*
     * (non-Javadoc)
     * @see net.ggtools.grand.Node#getBackLinks()
     */
    public Collection<Link> getBackLinks() {
        return backLinks;
    }

    /*
     * (non-Javadoc)
     * @see net.ggtools.grand.Node#getDescription()
     */
    public String getDescription() {
        return description;
    }

    /*
     * (non-Javadoc)
     * @see net.ggtools.grand.Node#setDescription(java.lang.String)
     */
    public void setDescription(final String desc) {
        description = desc;
    }

    /*
     * (non-Javadoc)
     * @see net.ggtools.grand.GraphObject#getGraph()
     */
    public Graph getGraph() {
        return graph;
    }

    /*
     * (non-Javadoc)
     * @see net.ggtools.grand.GraphObject#getName()
     */
    public String getName() {
        return name;
    }

    /*
     * (non-Javadoc)
     * @see net.ggtools.grand.Node#addLink(net.ggtools.grand.Link)
     */
    public void addLink(final Link link) {
        links.add(link);
    }

    /*
     * (non-Javadoc)
     * @see net.ggtools.grand.Node#addLink(net.ggtools.grand.Link)
     */
    public void addBackLink(final Link link) {
        backLinks.add(link);
    }

    /*
     * (non-Javadoc)
     * @see net.ggtools.grand.Node#removeLink(net.ggtools.grand.Link)
     */
    public void removeLink(final Link link) {
        if (log.isTraceEnabled()) {
            log.trace(name + ": removing link " + link);
        }
        links.remove(link);
    }

    /*
     * (non-Javadoc)
     * @see net.ggtools.grand.Node#removeBackLink(net.ggtools.grand.Link)
     */
    public void removeBackLink(final Link link) {
        if (log.isTraceEnabled()) {
            log.trace(name + ": removing back link " + link);
        }
        backLinks.remove(link);
    }

    /*
     * (non-Javadoc)
     * @see net.ggtools.grand.graph.Node#accept(net.ggtools.grand.graph.visit.NodeVisitor)
     */
    public void accept(final NodeVisitor visitor) {
        visitor.visitNode(this);
    }

    /*
     * (non-Javadoc)
     * @see net.ggtools.grand.graph.Node#getSource()
     */
    public String getSource() {
        return source;
    }

    /**
     * Sets the source snippet for the node.
     * 
     * @param newSource
     */
    public void setSource(final String newSource) {
        source = newSource;
    }
}
