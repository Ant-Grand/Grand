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

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

import net.ggtools.grand.Log;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

/**
 * A class to instanciante the right filter type classes based
 * on the name parameter.
 * 
 * TODO implement unittest for this class
 * 
 * @author Christophe Labouisse
 */
final class GraphFilterFactory {
    static final Properties CONFIGURATION = new Properties();

    static {
        try {
            CONFIGURATION.load(GraphFilterFactory.class
                    .getResourceAsStream("GraphFilterFactory.properties"));
        } catch (IOException e) {
            Log.log("Cannot read properties: " + e.getMessage(), Log.MSG_ERR);
            throw new RuntimeException(e);
        }
    }

    /**
     * Get a filter object for a name.
     * 
     * @param project owner project
     * @param name value of the name attribute in the filter element
     * @return a GraphFilterType object.
     * @throws BuildException
     */
    GraphFilterType getFilterType(Project project, String name) throws BuildException {
        project.log("Creating filter for name " + name, Project.MSG_DEBUG);

        String filterClassName = CONFIGURATION.getProperty(name);

        if (filterClassName == null) { throw new BuildException("Filter " + name
                + " not configured"); }

        project.log("Using " + filterClassName, Project.MSG_DEBUG);

        Class filterClass;
        try {
            filterClass = Class.forName(filterClassName);
        } catch (ClassNotFoundException e) {
            throw new BuildException("Cannot find filter class", e);
        }

        Constructor constructor;
        try {
            constructor = filterClass.getConstructor(new Class[]{Project.class});
        } catch (SecurityException e) {
            final String message = "Cannot access constructor for class " + filterClassName;
            project.log(message, Project.MSG_ERR);
            throw new BuildException(message, e);
        } catch (NoSuchMethodException e) {
            final String message = "Cannot find constructor for class " + filterClassName;
            project.log(message, Project.MSG_ERR);
            throw new BuildException(message, e);
        }

        GraphFilterType filter;
        try {
            filter = (GraphFilterType) constructor.newInstance(new Object[]{project});
        } catch (IllegalArgumentException e) {
            throw new BuildException("Cannot instanciate filter", e);
        } catch (InstantiationException e) {
            throw new BuildException("Cannot instanciate filter", e);
        } catch (IllegalAccessException e) {
            throw new BuildException("Cannot instanciate filter", e);
        } catch (InvocationTargetException e) {
            throw new BuildException("Cannot instanciate filter", e);
        }

        return filter;
    }
}
