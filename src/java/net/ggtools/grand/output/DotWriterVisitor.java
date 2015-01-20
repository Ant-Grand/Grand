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
 * Visitor class creating a graph description in dot format.
 *
 * @author Christophe Labouisse
 */
class DotWriterVisitor implements NodeVisitor {
    /**
     * A visitor dedicated to the links of a specific node.
     *
     * @author Christophe Labouisse
     */
    private final class NodeLinksVisitor implements LinkVisitor {

        /**
         * Field node.
         */
        @SuppressWarnings("unused")
        private final Node node;

        /**
         * Field nodeInfo.
         */
        private final String nodeInfo;

        /**
         * Field numDeps.
         */
        private final int numDeps;

        /**
         * Field visitedLinks.
         */
        private int visitedLinks;

        /**
         * Constructor for NodeLinksVisitor.
         * @param node Node
         */
        private NodeLinksVisitor(final Node node) {
            this.node = node;

            nodeInfo = node.getName();
            numDeps = node.getLinks().size();
            visitedLinks = 0;
        }

        /**
         * Method visitLink.
         * @param link AntLink
         * @see net.ggtools.grand.graph.visit.LinkVisitor#visitLink(net.ggtools.grand.ant.AntLink)
         */
        public void visitLink(final AntLink link) {
            visitLink((Link) link);
        }

        /**
         * Method visitLink.
         * @param link AntTaskLink
         * @see net.ggtools.grand.graph.visit.LinkVisitor#visitLink(net.ggtools.grand.ant.AntTaskLink)
         */
        public void visitLink(final AntTaskLink link) {
            visitLink((Link) link);
        }

        /**
         * Method visitLink.
         * @param link Link
         * @see net.ggtools.grand.graph.visit.LinkVisitor#visitLink(net.ggtools.grand.graph.Link)
         */
        public void visitLink(final Link link) {
            String attributes = null;
            if (link.hasAttributes(Link.ATTR_WEAK_LINK)) {
                attributes = weakLinkAttributes;
            }
            outputOneLink(link, attributes);
        }

        /**
         * Method visitLink.
         * @param link SubantTaskLink
         * @see net.ggtools.grand.graph.visit.LinkVisitor#visitLink(net.ggtools.grand.ant.SubantTaskLink)
         */
        public void visitLink(final SubantTaskLink link) {
            outputOneLink(link, subantLinkAttributes,
                    link.getDirectories().size());
        }

        /**
         * Method outputOneLink.
         * @param link Link
         * @param attributes String
         */
        private void outputOneLink(final Link link, final String attributes) {
            outputOneLink(link, attributes, 1);
        }

        /**
         * Method outputOneLink.
         * @param link Link
         * @param attributes String
         * @param visits int
         */
        private void outputOneLink(final Link link, final String attributes, final int visits) {
            visitedLinks++;
            final Node depNode = link.getEndNode();

            output.append("\"").appendEscaped(nodeInfo).append("\"").append(" -> \"")
                    .appendEscaped(depNode.getName()).append("\"");

            // TODO create a proper attribute manager.
            if ((numDeps > 1) || (visits > 1) || (attributes != null)) {
                output.append(" [");
                if ((numDeps > 1) || (visits > 1)) {
                    output.append("label=\"").append(visitedLinks);
                    if (visits > 1) {
                        visitedLinks += visits - 1;
                        output.append("-").append(visitedLinks);
                    }
                    output.append("\"");
                }
                if (attributes != null) {
                    if ((numDeps > 1) || (visits > 1)) {
                        output.append(", ");
                    }

                    output.append(attributes);
                }
                output.append("]");
            }
            output.append(";").newLine();
        }

    }

    /**
     * Field DOT_GRAPH_ATTRIBUTES.
     * (value is ""dot.graph.attributes"")
     */
    private static final String DOT_GRAPH_ATTRIBUTES =
            "dot.graph.attributes";

    /**
     * Field DOT_LINK_ATTRIBUTES.
     * (value is ""dot.link.attributes"")
     */
    private static final String DOT_LINK_ATTRIBUTES =
            "dot.link.attributes";

    /**
     * Field DOT_MAINNODE_ATTRIBUTES.
     * (value is ""dot.mainnode.attributes"")
     */
    private static final String DOT_MAINNODE_ATTRIBUTES =
            "dot.mainnode.attributes";

    /**
     * Field DOT_MISSINGNODE_ATTRIBUTES.
     * (value is ""dot.missingnode.attributes"")
     */
    private static final String DOT_MISSINGNODE_ATTRIBUTES =
            "dot.missingnode.attributes";

    /**
     * Field DOT_NODE_ATTRIBUTES.
     * (value is ""dot.node.attributes"")
     */
    private static final String DOT_NODE_ATTRIBUTES =
            "dot.node.attributes";

    /**
     * Field DOT_STARTNODE_ATTRIBUTES.
     * (value is ""dot.startnode.attributes"")
     */
    private static final String DOT_STARTNODE_ATTRIBUTES =
            "dot.startnode.attributes";

    /**
     * Field DOT_SUBANTLINK_ATTRIBUTES.
     * (value is ""dot.subantlink.attributes"")
     */
    private static final String DOT_SUBANTLINK_ATTRIBUTES =
            "dot.subantlink.attributes";

    /**
     * Field DOT_WEAK_LINK_ATTRIBUTES.
     * (value is ""dot.weaklink.attributes"")
     */
    private static final String DOT_WEAK_LINK_ATTRIBUTES =
            "dot.weaklink.attributes";

    /**
     * Field log.
     */
    @SuppressWarnings("unused")
    private static final Log LOG = LoggerManager.getLog(DotWriterVisitor.class);

    /**
     * Field config.
     */
    @SuppressWarnings("unused")
    private final Configuration config;

    /**
     * Field graphAttributes.
     */
    @SuppressWarnings("unused")
    private final String graphAttributes;

    /**
     * Field linkAttributes.
     */
    @SuppressWarnings("unused")
    private final String linkAttributes;

    /**
     * Field mainNodeAttributes.
     */
    private final String mainNodeAttributes;

    /**
     * Field missingNodeAttributes.
     */
    private final String missingNodeAttributes;

    /**
     * Field nodeAttributes.
     */
    @SuppressWarnings("unused")
    private final String nodeAttributes;

    /**
     * Field output.
     */
    private final DotWriterOutput output;

    /**
     * Field startNodeAttributes.
     */
    private final String startNodeAttributes;

    /**
     * Field subantLinkAttributes.
     */
    private final String subantLinkAttributes;

    /**
     * Field weakLinkAttributes.
     */
    private final String weakLinkAttributes;

    /**
     * Creates a new instance outputting to the supplied PrintWriter.
     * @param output DotWriterOutput
     * @param config Configuration
     */
    DotWriterVisitor(final DotWriterOutput output, final Configuration config) {
        this.output = output;
        this.config = config;
        graphAttributes = config.get(DOT_GRAPH_ATTRIBUTES);
        linkAttributes = config.get(DOT_LINK_ATTRIBUTES);
        weakLinkAttributes = config.get(DOT_WEAK_LINK_ATTRIBUTES);
        subantLinkAttributes = config.get(DOT_SUBANTLINK_ATTRIBUTES);
        mainNodeAttributes = config.get(DOT_MAINNODE_ATTRIBUTES);
        missingNodeAttributes = config.get(DOT_MISSINGNODE_ATTRIBUTES);
        nodeAttributes = config.get(DOT_NODE_ATTRIBUTES);
        startNodeAttributes = config.get(DOT_STARTNODE_ATTRIBUTES);
    }

    /**
     * Method visitNode.
     * @param node AntTargetNode
     * @see net.ggtools.grand.graph.visit.NodeVisitor#visitNode(net.ggtools.grand.ant.AntTargetNode)
     */
    public void visitNode(final AntTargetNode node) {
        visitNode((Node) node);
    }

    /**
     * Method visitNode.
     * @param node Node
     * @see net.ggtools.grand.graph.visit.NodeVisitor#visitNode(net.ggtools.grand.graph.Node)
     */
    public void visitNode(final Node node) {

        output.append("\"").appendEscaped(node.getName()).append("\"");

        String attributes = null;

        if (node.hasAttributes(Node.ATTR_START_NODE)) {
            attributes = startNodeAttributes;
        } else if (node.hasAttributes(Node.ATTR_MAIN_NODE)) {
            attributes = mainNodeAttributes;
        } else if (node.hasAttributes(Node.ATTR_MISSING_NODE)) {
            attributes = missingNodeAttributes;
        }

        final String description = node.getDescription();
        if ((attributes != null) || (description != null)) {
            output.append(" [");

            if (attributes != null) {
                output.append(attributes);
            }

            if (description != null) {
                if (attributes != null) {
                    output.append(",");
                }
                output.append("comment=\"").appendEscaped(description).append("\"");
            }

            output.append("];");
        }

        output.newLine();

        final Collection<Link> deps = node.getLinks();
        final NodeLinksVisitor linkVisitor = new NodeLinksVisitor(node);

        for (Link link : deps) {
            link.accept(linkVisitor);
        }

        output.newLine();
    }

}
