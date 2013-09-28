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

    /**
     * Inner class SourceElement.
     * @author Christophe Labouisse
     */
    public static class SourceElement {

        /**
         * Field style.
         */
        private int style;

        /**
         * Field text.
         */
        private String text;

        /**
         * Constructor for SourceElement.
         * @param text String
         * @param style int
         */
        public SourceElement(final String text, final int style) {
            this.text = text;
            this.style = style;
        }

        /**
         * Method getStyle.
         * @return Returns the style.
         */
        public final int getStyle() {
            return style;
        }

        /**
         * Method getText.
         * @return Returns the text.
         */
        public final String getText() {
            return text;
        }

        /**
         * Method setStyle.
         * @param style
         *            The style to set.
         */
        final void setStyle(final int style) {
            this.style = style;
        }

        /**
         * Method setText.
         * @param text
         *            The text to set.
         */
        final void setText(final String text) {
            this.text = text;
        }
    }

    /**
     * Field SOURCE_ATTRIBUTE.
     * (value is 2)
     */
    public final static int SOURCE_ATTRIBUTE = 2;

    /**
     * Field SOURCE_INKNOWN.
     * (value is 0)
     */
    public final static int SOURCE_INKNOWN = 0;

    /**
     * Field SOURCE_MARKUP.
     * (value is 1)
     */
    public final static int SOURCE_MARKUP = 1;

    /**
     * Field SOURCE_TEXT.
     * (value is 3)
     */
    public final static int SOURCE_TEXT = 3;

    /**
     * Field buildFile.
     */
    private String buildFile;

    /**
     * Field ifCondition.
     */
    private String ifCondition;

    /**
     * Field richSource.
     */
    private SourceElement[] richSource;

    /**
     * Field unlessCondition.
     */
    private String unlessCondition;

    /**
     * Constructor.
     * @param name String
     * @param graph Graph
     */
    public AntTargetNode(final String name, final Graph graph) {
        super(name, graph);
    }

    /**
     * Method accept.
     * @param visitor NodeVisitor
     * @see net.ggtools.grand.graph.Node#accept(net.ggtools.grand.graph.visit.NodeVisitor)
     */
    @Override
    public void accept(final NodeVisitor visitor) {
        visitor.visitNode(this);
    }

    /**
     * Returns this node build file or <code>null</code> if the build file is
     * the <em>current</em> project.
     *
     * @return Returns the buildFile.
     */
    public final String getBuildFile() {
        return buildFile;
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
    public final void setRichSource(final SourceElement[] richSource) {
        this.richSource = richSource;
    }

    /**
     * @param buildFile
     *            The buildFile to set.
     */
    final void setBuildFile(final String buildFile) {
        this.buildFile = buildFile;
    }

    /**
     * @param ifCondition
     *            The ifCondition to set.
     */
    final void setIfCondition(final String ifCondition) {
        this.ifCondition = ifCondition;
    }

    /**
     * @param unlessCondition
     *            The unlessCondition to set.
     */
    final void setUnlessCondition(final String unlessCondition) {
        this.unlessCondition = unlessCondition;
    }

}
