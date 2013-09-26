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

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import net.ggtools.grand.graph.Graph;
import net.ggtools.grand.graph.Node;
import net.ggtools.grand.graph.visit.LinkVisitor;

/**
 * A link representing a call by a task such like <code>subant</code>.
 * Instances of this class may hold a list of directories. This list will be
 * used to create a link for the <i>genericantfile</i> version of
 * <code>subant</code>.
 *
 * @author Christophe Labouisse
 */
public class SubantTaskLink extends AntTaskLink {

    /**
     * List of the directories to apply the generic ant file to.
     */
    private final List<String> directories = new LinkedList<String>();

    /**
     * @param name
     * @param graph
     * @param startNode
     * @param endNode
     * @param taskName
     */
    public SubantTaskLink(final String name, final Graph graph, final Node startNode, final Node endNode, final String taskName) {
        super(name, graph, startNode, endNode, taskName);
    }

    /**
     * Method accept.
     * @param visitor LinkVisitor
     * @see net.ggtools.grand.graph.Link#accept(net.ggtools.grand.graph.visit.LinkVisitor)
     */
    @Override
    public void accept(final LinkVisitor visitor) {
        visitor.visitLink(this);
    }

    /**
     * Add a directory to the list of directory used when applying the generic
     * ant file.
     * @param newDir
     */
    public void addDirectory(final String newDir) {
        directories.add(newDir);
    }

    /**
     * Gets the list of directories to apply the generic ant file onto.
     *
     * @return a read-only list of directories.
     */
    public Collection<String> getDirectories() {
        return Collections.unmodifiableList(directories);
    }

}
