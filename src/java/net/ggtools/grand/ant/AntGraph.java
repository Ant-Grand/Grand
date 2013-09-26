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

import net.ggtools.grand.graph.GraphElementFactory;
import net.ggtools.grand.graph.GraphImpl;
import net.ggtools.grand.graph.Node;

import org.apache.tools.ant.Project;

/**
 * A Graph implementation specialized in Ant build files.
 *
 * @author Christophe Labouisse
 */
public class AntGraph extends GraphImpl {
    /**
     * Field elementFactory.
     */
    private AntGraphElementFactory elementFactory;

    /**
     * Field project.
     */
    private final Project project;

    /**
     * Creates a new graph.
     *
     * @param project
     *            the graph's project.
     */
    public AntGraph(final Project project) {
        super(project.getName());
        this.project = project;
    }

    /**
     * Creates a link representing a call by a task like <code>ant</code>.
     * @param linkName
     * @param startNode
     * @param endNode
     * @param taskName
     * @return AntTaskLink
     */
    public AntTaskLink createTaskLink(final String linkName, final Node startNode,
            final Node endNode, final String taskName) {
        final AntTaskLink link = getFactoryInternal().createTaskLink(linkName, startNode, endNode,
                taskName);
        startNode.addLink(link);
        endNode.addBackLink(link);
        return link;
    }

    /**
     * Creates a link representing a call by a <code>subant</code> task.
     *
     * @param linkName
     * @param startNode
     * @param endNode
     * @param taskName
     * @return SubantTaskLink
     */
    public SubantTaskLink createSubantTaskLink(final String linkName, final Node startNode,
            final Node endNode, final String taskName) {
        final SubantTaskLink link = getFactoryInternal().createSubantTaskLink(linkName, startNode, endNode,
                taskName);
        startNode.addLink(link);
        endNode.addBackLink(link);
        return link;
    }

    /**
     * Returns the project.
     * @return Project
     */
    public Project getProject() {
        return project;
    }

    /**
     * Getter/Instanciator to {@link #elementFactory}. Used internally to keep
     * the {@link AntGraphElementFactory}type.
     * @return AntGraphElementFactory
     */
    private final AntGraphElementFactory getFactoryInternal() {
        if (elementFactory == null) {
            elementFactory = new AntGraphElementFactory(this);
        }
        return elementFactory;
    }

    /**
     * Method getFactory.
     * @return GraphElementFactory
     * @see net.ggtools.grand.graph.GraphImpl#getFactory()
     */
    @Override
    protected GraphElementFactory getFactory() {
        return getFactoryInternal();
    }
}
