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

package net.ggtools.grand.ant;

import net.ggtools.grand.graph.Graph;
import net.ggtools.grand.graph.NodeImpl;
import net.ggtools.grand.graph.visit.NodeVisitor;

/**
 * A node implementation specialized for ant target.
 * 
 * @author Christophe Labouisse
 */
public class AntTargetNode extends NodeImpl {

    public static class SourceElement {

        private int style;

        private String text;

        public SourceElement(final String text, final int style) {
            this.text = text;
            this.style = style;
        }

        /**
         * @return Returns the style.
         */
        public final int getStyle() {
            return style;
        }

        /**
         * @return Returns the text.
         */
        public final String getText() {
            return text;
        }

        /**
         * @param style
         *            The style to set.
         */
        final void setStyle(int style) {
            this.style = style;
        }

        /**
         * @param text
         *            The text to set.
         */
        final void setText(String text) {
            this.text = text;
        }
    }

    public final static int SOURCE_ATTRIBUTE = 2;

    public final static int SOURCE_INKNOWN = 0;

    public final static int SOURCE_MARKUP = 1;

    public final static int SOURCE_TEXT = 3;

    private String ifCondition;

    private SourceElement[] richSource;

    private String unlessCondition;

    /**
     * @param name
     * @param graph
     */
    public AntTargetNode(String name, Graph graph) {
        super(name, graph);
    }

    /*
     * (non-Javadoc)
     * @see net.ggtools.grand.graph.Node#accept(net.ggtools.grand.graph.visit.NodeVisitor)
     */
    public void accept(NodeVisitor visitor) {
        visitor.visitNode(this);
    }

    /**
     * Returns the <em>if condition</em> for the target or <code>null</code>
     * if none defined.
     * 
     * @return Returns the <em>if condition</em>.
     */
    public final String getIfCondition() {
        return ifCondition;
    }

    /**
     * @return Returns the richSource.
     */
    public final SourceElement[] getRichSource() {
        return richSource;
    }

    /**
     * Returns the <em>unless condition</em> for the target or
     * <code>null</code> if none defined.
     * 
     * @return Returns the <em>unless condition</em>.
     */
    public final String getUnlessCondition() {
        return unlessCondition;
    }

    /**
     * @param richSource
     *            The richSource to set.
     */
    public final void setRichSource(SourceElement[] richSource) {
        this.richSource = richSource;
    }

    /**
     * @param ifCondition
     *            The ifCondition to set.
     */
    final void setIfCondition(String ifCondition) {
        this.ifCondition = ifCondition;
    }

    /**
     * @param unlessCondition
     *            The unlessCondition to set.
     */
    final void setUnlessCondition(String unlessCondition) {
        this.unlessCondition = unlessCondition;
    }

}