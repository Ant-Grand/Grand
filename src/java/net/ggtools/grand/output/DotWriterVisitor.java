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
package net.ggtools.grand.output;

import java.util.Collection;
import java.util.Iterator;

import net.ggtools.grand.Configuration;
import net.ggtools.grand.ant.AntLink;
import net.ggtools.grand.ant.AntTargetNode;
import net.ggtools.grand.ant.AntTaskLink;
import net.ggtools.grand.ant.SubantTaskLink;
import net.ggtools.grand.graph.Link;
import net.ggtools.grand.graph.Node;
import net.ggtools.grand.graph.visit.LinkVisitor;
import net.ggtools.grand.graph.visit.NodeVisitor;
import net.ggtools.grand.log.LoggerManager;

import org.apache.commons.logging.Log;

/**
 * Visitor class creating a Dot graph description.
 * 
 * @author Christophe Labouisse
 */
class DotWriterVisitor implements NodeVisitor {
    /**
     * A visitor dedicated to the links of a specific node.
     * 
     * @author Christophe Labouisse
     */
    private class NodeLinksVisitor implements LinkVisitor {

        private final Node node;

        private final String nodeInfo;

        private int visitedLinks;

        private final int numDeps;

        private NodeLinksVisitor(final Node node) {
            this.node = node;

            nodeInfo = node.getName();
            numDeps = node.getLinks().size();
            visitedLinks = 0;
        }

        /*
         * (non-Javadoc)
         * @see net.ggtools.grand.graph.visit.LinkVisitor#visitLink(net.ggtools.grand.graph.Link)
         */
        public void visitLink(Link link) {
            visitedLinks++;
            final Node depNode = link.getEndNode();

            final StringBuffer strBuf = new StringBuffer();
            output.append("\"").appendEscaped(nodeInfo).append("\"").append(" -> \"")
                    .appendEscaped(depNode.getName()).append("\"");

            // TODO create a proper attribute manager.
            if (numDeps > 1 || link.hasAttributes(Link.ATTR_WEAK_LINK)) {
                output.append(" [");
                if (numDeps > 1) {
                    output.append("label=\"").append(visitedLinks).append("\"");
                }
                if (link.hasAttributes(Link.ATTR_WEAK_LINK)) {
                    if (numDeps > 1) {
                        output.append(", ");
                    }

                    output.append(weakLinkAttributes);
                }
                output.append("]");
            }
            output.append(";").newLine();
        }

        /*
         * (non-Javadoc)
         * @see net.ggtools.grand.graph.visit.LinkVisitor#visitLink(net.ggtools.grand.ant.AntLink)
         */
        public void visitLink(AntLink link) {
            visitLink((Link) link);
        }

        /*
         * (non-Javadoc)
         * @see net.ggtools.grand.graph.visit.LinkVisitor#visitLink(net.ggtools.grand.ant.AntTaskLink)
         */
        public void visitLink(AntTaskLink link) {
            visitLink((Link) link);
        }

        /*
         * (non-Javadoc)
         * @see net.ggtools.grand.graph.visit.LinkVisitor#visitLink(net.ggtools.grand.ant.SubantTaskLink)
         */
        public void visitLink(SubantTaskLink link) {
            visitLink((Link) link);
        }

    }

    private static final Log log = LoggerManager.getLog(DotWriterVisitor.class);

    private static final String DOT_GRAPH_ATTRIBUTES = "dot.graph.attributes";

    private static final String DOT_LINK_ATTRIBUTES = "dot.link.attributes";

    private static final String DOT_WEAK_LINK_ATTRIBUTES = "dot.weaklink.attributes";

    private static final String DOT_MAINNODE_ATTRIBUTES = "dot.mainnode.attributes";

    private static final String DOT_MISSINGNODE_ATTRIBUTES = "dot.missingnode.attributes";

    private static final String DOT_NODE_ATTRIBUTES = "dot.node.attributes";

    private static final String DOT_STARTNODE_ATTRIBUTES = "dot.startnode.attributes";

    private String graphAttributes;

    private String linkAttributes;

    private String weakLinkAttributes;

    private String mainNodeAttributes;

    private String missingNodeAttributes;

    private String nodeAttributes;

    private String startNodeAttributes;

    private final Configuration config;

    private final DotWriterOutput output;

    /**
     * Creates a new instance outputing to the supplied PrintWriter.
     */
    DotWriterVisitor(final DotWriterOutput output, final Configuration config) {
        this.output = output;
        this.config = config;
        graphAttributes = config.get(DOT_GRAPH_ATTRIBUTES);
        linkAttributes = config.get(DOT_LINK_ATTRIBUTES);
        weakLinkAttributes = config.get(DOT_WEAK_LINK_ATTRIBUTES);
        mainNodeAttributes = config.get(DOT_MAINNODE_ATTRIBUTES);
        missingNodeAttributes = config.get(DOT_MISSINGNODE_ATTRIBUTES);
        nodeAttributes = config.get(DOT_NODE_ATTRIBUTES);
        startNodeAttributes = config.get(DOT_STARTNODE_ATTRIBUTES);
    }

    /*
     * (non-Javadoc)
     * @see net.ggtools.grand.graph.visit.NodeVisitor#visitNode(net.ggtools.grand.graph.Node)
     */
    public void visitNode(final Node node) {

        output.append("\"").appendEscaped(node.getName()).append("\"");

        String attributes = null;

        if (node.hasAttributes(Node.ATTR_START_NODE)) {
            attributes = startNodeAttributes;
        }
        else if (node.hasAttributes(Node.ATTR_MAIN_NODE)) {
            attributes = mainNodeAttributes;
        }
        else if (node.hasAttributes(Node.ATTR_MISSING_NODE)) {
            attributes = missingNodeAttributes;
        }

        final String description = node.getDescription();
        if ((attributes != null) || (description != null)) {
            output.append(" [");

            if (attributes != null) output.append(attributes);

            if (description != null) {
                if (attributes != null) output.append(",");
                output.append("comment=\"").appendEscaped(description).append("\"");
            }

            output.append("];");
        }

        output.newLine();

        final Collection deps = node.getLinks();
        final NodeLinksVisitor linkVisitor = new NodeLinksVisitor(node);

        for (Iterator iter = deps.iterator(); iter.hasNext();) {
            final Link link = (Link) iter.next();
            link.accept(linkVisitor);
        }

        output.newLine();
    }

    /*
     * (non-Javadoc)
     * @see net.ggtools.grand.graph.visit.NodeVisitor#visitNode(net.ggtools.grand.ant.AntTargetNode)
     */
    public void visitNode(final AntTargetNode node) {
        visitNode((Node) node);
    }

}
