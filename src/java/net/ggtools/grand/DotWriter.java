// $Id$
/*
 * ====================================================================
 * Copyright (c) 2002-2003, Christophe Labouisse All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: 1.
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution.
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

package net.ggtools.grand;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.Properties;

/**
 * A class to write dependency graph in dot format.
 * 
 * The rendering can be customized either by properties at object creation or
 * at runtime usign various setters.
 * 
 * The property names use the following scheme: <code>dot.<i>objecttype</i>.attributes</code>.
 * Where <i>objectype</i> can be:
 * <ul>
 * <li><code>node</code> for "common" nodes,</li>
 * <li><code>link</code> for dependency links,</li>
 * <li><code>mainnode</code> for nodes with a special importance (i.e.: when
 * node.isMainNode() is true),</li>
 * <li><code>startnode</code> for the start node,</li>
 * <li>and <code>graph</code> for the graph itself.</li>
 * </ul>
 * 
 * The property values are sets of valid dot attributes without the surrounding
 * bracket.
 * 
 * @author Christophe Labouisse
 * @see <a href="http://www.research.att.com/sw/tools/graphviz/">Graphviz home page</a>
 * @see <a href="http://www.research.att.com/~erg/graphviz/info/attrs.html">Dot attributes</a>
 */
public class DotWriter implements GraphWriter {

    private static final String LINE_SEPARATOR = System
            .getProperty("line.separator");

    private static final String DOT_GRAPH_ATTRIBUTES = "dot.graph.attributes";

    private static final String DOT_LINK_ATTRIBUTES = "dot.link.attributes";

    private static final String DOT_MAINNODE_ATTRIBUTES = "dot.mainnode.attributes";

    private static final String DOT_NODE_ATTRIBUTES = "dot.node.attributes";

    private static final String DOT_STARTNODE_ATTRIBUTES = "dot.startnode.attributes";

    /**
     * Escapes a string from special dot chars.
     * 
     * @param str
     * @return
     */
    private static String escapeString(String str) {
        if (str == null) { return null; }
        return str.replaceAll("(\\\"\\s)", "\\\\\\1");
    }

    private Project project;

    private String graphAttributes;

    private String linkAttributes;

    private String mainNodeAttributes;

    private String nodeAttributes;

    private String startNodeAttributes;

    private final Configuration config;

    /**
     * Creates a new DotWriter using default configuration.
     * 
     * @param project
     *            project to convert write in dot format.
     */
    public DotWriter(Project project) throws IOException {
        this(project, null);
    }

    /**
     * Creates a new DotWriter with custom properties. No
     * overriding will take place if override is null.
     * 
     * @param project
     *            to convert
     * @param override
     *            custom configuration.
     */
    public DotWriter(Project project, Properties override) throws IOException {
        this.project = project;

        config = Configuration.getConfiguration(override);
        graphAttributes = config.get(DOT_GRAPH_ATTRIBUTES);
        linkAttributes = config.get(DOT_LINK_ATTRIBUTES);
        mainNodeAttributes = config.get(DOT_MAINNODE_ATTRIBUTES);
        nodeAttributes = config.get(DOT_NODE_ATTRIBUTES);
        startNodeAttributes = config.get(DOT_STARTNODE_ATTRIBUTES);
    }

    /**
     * Generates the dot code for a node an its dependencies. This method
     * automatically find out the node style depending on the node category and
     * the writer properties.
     * 
     * @param node
     *            the node to be processed.
     * @return a string buffer holding the node information in dot language.
     */
    private StringBuffer getNodeAsDot(Node node) {
        String nodeAttributes = null;

        if (node.isMainNode()) {
            final String mainNodeProps = mainNodeAttributes;

            if (mainNodeProps != null) {
                nodeAttributes = mainNodeProps;
            } else {
                nodeAttributes = "";
            }

            nodeAttributes += ",comment=\""
                    + escapeString(node.getDescription()) + "\"";
        }

        return getNodeAsDot(node, nodeAttributes);
    }

    /**
     * Generates the dot code for a node an its dependencies.
     * 
     * @param node
     *            the node to be processed.
     * @param attributes
     *            attributes for the processed node.
     * @return a string buffer holding the node information in dot language.
     */
    private StringBuffer getNodeAsDot(Node node, String attributes) {
        StringBuffer strBuf = new StringBuffer();
        strBuf.append("\"").append(escapeString(node.getName())).append("\"");
        String nodeInfo = strBuf.toString();

        if (attributes != null) {
            strBuf.append(" [").append(attributes).append("];");
        }

        strBuf.append(LINE_SEPARATOR);

        Node[] depArray = node.getDependenciesArray();
        for (int i = 0; i < depArray.length; i++) {
            Node depNode = depArray[i];

            strBuf.append(nodeInfo).append(" -> \"").append(
                    escapeString(depNode.getName())).append("\"");

            if (depArray.length > 1) {
                strBuf.append(" [label=\"").append(i + 1).append("\"]");
            }
            strBuf.append(";").append(LINE_SEPARATOR);
        }

        return strBuf;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ggtools.dependgraph.GraphWriter#Write(java.io.File)
     */
    public void write(File output) throws IOException {
        FileOutputStream oStream = new FileOutputStream(output);
        write(oStream);
        oStream.flush();
        oStream.close();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ggtools.dependgraph.GraphWriter#Write(java.io.OutputStream)
     */
    public void write(OutputStream stream) {
        PrintStream output = new PrintStream(stream);

        StringBuffer header = new StringBuffer();
        header.append("digraph \"").append(escapeString(project.getName()))
                .append("\" {").append(LINE_SEPARATOR);
        header.append("graph [").append(graphAttributes).append("];").append(
                LINE_SEPARATOR);
        header.append("node [").append(nodeAttributes).append("];").append(
                LINE_SEPARATOR);
        header.append("edge [").append(linkAttributes).append("];");
        output.println(header);

        final Node startNode = project.getStartNode();

        if (startNode != null) {
            // TODO: Check if the default target actually exists.
            output.println(getNodeAsDot(startNode, startNodeAttributes));
        }

        for (Iterator iter = project.getNodes(); iter.hasNext(); ) {
            Node node = (Node) iter.next();

            if (node.equals(startNode) || node.getName().equals("")) {
                continue;
            }
            output.println(getNodeAsDot(node));
        }
        output.println("}");
    }

    /**
     * @return Returns the graphAttributes.
     */
    public String getGraphAttributes() {
        return graphAttributes;
    }

    /**
     * @param graphAttributes
     *            The graphAttributes to set.
     */
    public void setGraphAttributes(String graphAttributes) {
        this.graphAttributes = graphAttributes;
    }

    /**
     * @return Returns the linkAttributes.
     */
    public String getLinkAttributes() {
        return linkAttributes;
    }

    /**
     * @param linkAttributes
     *            The linkAttributes to set.
     */
    public void setLinkAttributes(String linkAttributes) {
        this.linkAttributes = linkAttributes;
    }

    /**
     * @return Returns the mainNodeAttributes.
     */
    public String getMainNodeAttributes() {
        return mainNodeAttributes;
    }

    /**
     * @param mainNodeAttributes
     *            The mainNodeAttributes to set.
     */
    public void setMainNodeAttributes(String mainNodeAttributes) {
        this.mainNodeAttributes = mainNodeAttributes;
    }

    /**
     * @return Returns the nodeAttributes.
     */
    public String getNodeAttributes() {
        return nodeAttributes;
    }

    /**
     * @param nodeAttributes
     *            The nodeAttributes to set.
     */
    public void setNodeAttributes(String nodeAttributes) {
        this.nodeAttributes = nodeAttributes;
    }
}
