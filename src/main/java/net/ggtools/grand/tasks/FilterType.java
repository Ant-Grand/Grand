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

package net.ggtools.grand.tasks;

import net.ggtools.grand.filters.GraphFilter;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

/**
 * A meta class for graph filters. This class will be instantiated
 * and configure by Ant and passed to graph task. On execution, the
 * enclosing task need to call the {@link #checkParameters()} method
 * to check if the filter is properly configured and {@link #getFilter()}
 * to get the actual graph filter.
 *
 * @author Christophe Labouisse
 */
public class FilterType {
    /**
     * Field filterFactory.
     */
    private static final GraphFilterFactory FILTER_FACTORY =
            new GraphFilterFactory();

    /**
     * Field filter.
     */
    private GraphFilterType filter;

    /**
     * Field filterName.
     */
    private String filterName;

    /**
     * Field nodeName.
     */
    private String nodeName;

    /**
     * Field project.
     */
    private final Project project;

    /**
     * Creates a new filter type.
     *
     * @param prj the owner's project.
     */
    public FilterType(final Project prj) {
        project = prj;
    }

    /**
     * Create the actual filter if it does not yet exit.
     */
    private void checkFilter() {
        if (filter == null) {
            filter = FILTER_FACTORY.getFilterType(project, filterName);
            if (nodeName != null) {
                filter.setNodeName(nodeName);
            }
            // Use an HashMap for parameters?
            // Pb: type conversion check ant's utility classes
        }
    }

    /**
     * Checks if the filter parameters are correct.
     */
    final void checkParameters() {
        if (filterName == null) {
            final String message = "required attribute missing";
            project.log(message, Project.MSG_ERR);
            throw new BuildException(message);
        }
        checkFilter();
        filter.checkParameters();
    }

    /**
     * Return the actual filter after creating it if needed.
     *
     * @return the actual filter.
     */
    public final GraphFilter getFilter() {
        checkFilter();
        return filter.getFilter();
    }

    /**
     * Sets the filter's name.
     *
     * @param name filter's name
     */
    public final void setName(final String name) {
        filterName = name;
    }

    /**
     * Sets the filter's node name.
     *
     * @param node node's name.
     */
    public final void setNode(final String node) {
        nodeName = node;
    }

    /**
     * @return Returns the filterName.
     */
    public final String getFilterName() {
        return filterName;
    }
}
