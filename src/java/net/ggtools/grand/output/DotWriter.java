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

package net.ggtools.grand.output;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Properties;

import net.ggtools.grand.Configuration;
import net.ggtools.grand.exceptions.GrandException;
import net.ggtools.grand.graph.Graph;
import net.ggtools.grand.graph.GraphProducer;
import net.ggtools.grand.graph.GraphWriter;
import net.ggtools.grand.graph.Node;
import net.ggtools.grand.log.LoggerManager;

import org.apache.commons.logging.Log;

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
 * @TODO The current configuration scheme sucks create something more generic.
 * 
 * @author Christophe Labouisse
 * @see <a href="http://www.research.att.com/sw/tools/graphviz/">Graphviz home page</a>
 * @see <a href="http://www.research.att.com/~erg/graphviz/info/attrs.html">Dot attributes</a>
 */
public class DotWriter implements GraphWriter {
    /**
     * @author Christophe Labouisse
     */
    private static class Output implements DotWriterOutput {
        /**
         * Escapes a string from special dot chars.
         * 
         * @param str string to escape.
         * @return the escaped string.
         */
        private static String escapeString(final String str) {
            if (str == null) { return null; }
            return str.replaceAll("(\\\"\\s)", "\\\\\\1");
        }


        private final PrintWriter writer;

        /**
         * 
         */
        private Output(final OutputStream stream) {
            this.writer = new PrintWriter(stream);
        }

        /* (non-Javadoc)
         * @see net.ggtools.grand.output.DotWriterOutput#append(java.lang.String)
         */
        public DotWriterOutput append(String strValue) {
            writer.print(strValue);
            return this;
        }

        /* (non-Javadoc)
         * @see net.ggtools.grand.output.DotWriterOutput#appendEscaped(java.lang.String)
         */
        public DotWriterOutput appendEscaped(String strValue) {
            writer.print(escapeString(strValue));
            return this;
        }

        /* (non-Javadoc)
         * @see net.ggtools.grand.output.DotWriterOutput#append(int)
         */
        public DotWriterOutput append(int intValue) {
            writer.print(intValue);
            return this;
        }

        /* (non-Javadoc)
         * @see net.ggtools.grand.output.DotWriterOutput#newLine()
         */
        public DotWriterOutput newLine() {
            writer.println();
            return this;
        }

        private void close() {
            writer.close();
        }
    }
    private static final Log log = LoggerManager.getLog(DotWriter.class);
    private static final String DOT_GRAPH_ATTRIBUTES = "dot.graph.attributes";

    private static final String DOT_LINK_ATTRIBUTES = "dot.link.attributes";


    private static final String DOT_NODE_ATTRIBUTES = "dot.node.attributes";
    private String graphAttributes;

    private String linkAttributes;

    private String nodeAttributes;

    private final Configuration config;

    private GraphProducer graphProducer;

    private boolean showGraphName;

    /**
     * Creates a new DotWriter using default configuration.
     * @throws IOException when the default configuration cannot be loaded.
     */
    public DotWriter() throws IOException {
        this(null);
    }

    /**
     * Creates a new DotWriter with custom properties. No
     * overriding will take place if override is null.
     * 
     * @param override
     *            custom configuration.
     * @throws IOException when the configuration cannot be loaded.
     */
    public DotWriter(final Properties override) throws IOException {
        config = Configuration.getConfiguration(override);
        graphAttributes = config.get(DOT_GRAPH_ATTRIBUTES);
        linkAttributes = config.get(DOT_LINK_ATTRIBUTES);
        nodeAttributes = config.get(DOT_NODE_ATTRIBUTES);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ggtools.dependgraph.GraphWriter#Write(java.io.File)
     */
    public void write(final File output) throws IOException, GrandException {
        log.info("Outputing to " + output);
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
    public void write(final OutputStream stream) throws GrandException {
        final Output output = new Output(stream);

        final Graph graph = graphProducer.getGraph();

        output.append("digraph \"").appendEscaped(graph.getName()).append("\" {")
                .newLine();
        output.append("graph [").append(graphAttributes);
        if (showGraphName) {
            output.append(",label=\"").append(graph.getName()).append("\"");
        }
        output.append("];").newLine();
        output.append("node [").append(nodeAttributes).append("];").newLine();
        output.append("edge [").append(linkAttributes).append("];").newLine();

        final DotWriterVisitor visitor = new DotWriterVisitor(output,config);
        final Node startNode = graph.getStartNode();

        if (startNode != null) {
            startNode.accept(visitor);
        }

        for (Iterator iter = graph.getNodes(); iter.hasNext();) {
            final Node node = (Node) iter.next();

            if (node.equals(startNode) || node.getName().equals("")) {
                continue;
            }
            node.accept(visitor);
        }
        
        output.append("}").newLine();
        
        output.close();
    }

    /* (non-Javadoc)
     * @see net.ggtools.grand.GraphConsumer#setProducer(net.ggtools.grand.GraphProducer)
     */
    public void setProducer(final GraphProducer producer) {
        graphProducer = producer;
    }

    /* (non-Javadoc)
     * @see net.ggtools.grand.graph.GraphWriter#setShowGraphName(boolean)
     */
    public void setShowGraphName(final boolean show) {
        showGraphName = show;
    }

}
