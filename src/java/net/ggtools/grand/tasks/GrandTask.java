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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import net.ggtools.grand.Log;
import net.ggtools.grand.ant.AntProject;
import net.ggtools.grand.exceptions.GrandException;
import net.ggtools.grand.filters.GraphFilter;
import net.ggtools.grand.graph.GraphProducer;
import net.ggtools.grand.graph.GraphWriter;
import net.ggtools.grand.output.DotWriter;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.BuildListener;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Property;
import org.apache.tools.ant.types.PropertySet;

/**
 * A task to create graphs. 
 * 
 * @author Christophe Labouisse
 */
public class GrandTask extends Task {

    private File buildFile;

    private File output;

    private File outputConfigurationFile;

    /** the sets of properties to pass to the graphed project */
    private ArrayList propertySets = new ArrayList();

    private LinkedList filters = new LinkedList();

    private boolean showGraphName = false;

    private boolean inheritAll = false;

    private List properties = new LinkedList();

    /**
     * Check the parameters validity before execution.
     * 
     */
    private void checkParams() {
        if (output == null) {
            final String message = "required attribute missing";
            log(message, Project.MSG_ERR);
            throw new BuildException(message);
        }

        for (Iterator iter = filters.iterator(); iter.hasNext(); ) {
            FilterType filter = (FilterType) iter.next();
            filter.checkParameters();
        }
    }

    /* (non-Javadoc)
     * @see org.apache.tools.ant.Task#execute()
     */
    public void execute() throws BuildException {
        checkParams();

        final GraphProducer graphProject = initAntProject();

        log("Done loading project ", Project.MSG_VERBOSE);

        log("Setting up filter chain");
        GraphProducer producer = graphProject;
        int numFilters = 0;

        for (Iterator iter = filters.iterator(); iter.hasNext(); ) {
            FilterType f = (FilterType) iter.next();
            log("Adding filter " + f.getFilterName(), Project.MSG_VERBOSE);
            GraphFilter filter = f.getFilter();
            filter.setProducer(producer);
            producer = filter;
            numFilters++;
        }

        if (numFilters > 0) {
            log("Loaded " + numFilters + (numFilters > 1 ? " filters" : " filter"));
        }

        try {
            Properties override = null;
            if (outputConfigurationFile != null) {
                log("Overriding default properties from " + outputConfigurationFile);
                override = new Properties();
                override.load(new FileInputStream(outputConfigurationFile));
            }

            GraphWriter writer = new DotWriter(override);
            writer.setProducer(producer);
            writer.setShowGraphName(showGraphName);

            log("Writing output to " + output);
            writer.write(output);
        } catch (IOException e) {
            log("Cannot write graph", Project.MSG_ERR);
            throw new BuildException("Cannot write graph", e);
        } catch (GrandException e) {
            log("Cannot process graph", Project.MSG_ERR);
            throw new BuildException("Cannot write graph", e);
        }
    }

    /**
     * Create and initialize a GraphProducer according to the
     * task parameters.
     * 
     * @return an intialized GraphProducer.
     */
    private GraphProducer initAntProject() {

        Project antProject;

        if (buildFile == null) {
            // Working with current project
            log("Using current project");
            antProject = getProject();
        } else {
            // Open a new project.
            log("Loading project " + buildFile);

            antProject = loadNewProject();
        }

        for (Iterator iter = propertySets.iterator(); iter.hasNext(); ) {
            PropertySet ps = (PropertySet) iter.next();
            addAlmostAll(antProject, ps.getProperties());
        }

        if (properties.size() > 0) {
            for (Iterator iter = properties.iterator(); iter.hasNext(); ) {
                Property prop = (Property) iter.next();
                prop.setProject(antProject);
                prop.setTaskName("property");
                prop.execute();
            }
        }

        return new AntProject(antProject);
    }

    /**
     * Load an initialize a new project from a ant build file.
     * 
     * @return a new initialized ant project.
     */
    private Project loadNewProject() {
        Project antProject = new Project();

        // Set the current project listeners to the graphed project
        // in order to get some trace if we need to execute some tasks
        // in the graphed project.
        final Vector listeners = getProject().getBuildListeners();
        for (Iterator iter = listeners.iterator(); iter.hasNext(); ) {
            antProject.addBuildListener((BuildListener) iter.next());

        }

        antProject.init();

        if (!inheritAll) {
            // set Java built-in properties separately,
            // b/c we won't inherit them.
            antProject.setSystemProperties();

        } else {
            // set all properties from calling project
            addAlmostAll(antProject, getProject().getProperties());
        }

        antProject.setUserProperty("ant.file", buildFile.getAbsolutePath());
        ProjectHelper loader = ProjectHelper.getProjectHelper();
        antProject.addReference("ant.projectHelper", loader);
        loader.parse(antProject, buildFile);

        getProject().copyUserProperties(antProject);

        return antProject;
    }

    /* (non-Javadoc)
     * @see org.apache.tools.ant.ProjectComponent#setProject(org.apache.tools.ant.Project)
     */
    public void setProject(Project project) {
        super.setProject(project);

        Log.setProject(project);
    }

    /**
     * Sets the buildFile.
     * 
     * @param file
     */
    public void setBuildFile(File file) {
        buildFile = file;
    }

    /**
     * Sets the output file.
     * 
     * @param file
     */
    public void setOutput(File file) {
        output = file;
    }

    /**
     * Set a property file to override the output default configuration.
     * @param outputConfigurationFile The outputConfigurationFile to set.
     * @deprecated use {@link #setOutputConfigFile(File)}.
     */
    public void setPropertyFile(File propertyFile) {
        log(
                "Using of deprecated \"propertyfile\" attribute, use \"outputconfigfile\" from now on",
                Project.MSG_WARN);
        this.outputConfigurationFile = propertyFile;
    }

    /**
     * Set a property file to override the output default configuration.
     * @param outputConfigurationFile The outputConfigurationFile to set.
     */
    public void setOutputConfigFile(File propertyFile) {
        this.outputConfigurationFile = propertyFile;
    }

    public void setShowGraphName(boolean show) {
        showGraphName = show;
    }

    /**
     * If true, pass all properties to the new Ant project.
     * Defaults to true.
     * @param value if true pass all properties to the new Ant project.
     */
    public void setInheritAll(boolean value) {
        inheritAll = value;
    }

    /**
     * Add a filter to the task.
     * @param filter
     */
    public void addFilter(FilterType filter) {
        filters.add(filter);
    }

    /**
     * Add a new property to be passed to the graphed project.
     * 
     * @param p the property to set.
     */
    public void addProperty(Property p) {
        properties.add(p);
    }

    /**
     * Set of properties to pass to the graphed project.
     *
     * @param ps property set to add
     */
    public void addPropertyset(PropertySet ps) {
        propertySets.add(ps);
    }

    /**
     * Copies all properties from the given table to the destination project -
     * omitting those that have already been set in the destination project as
     * well as properties named basedir or ant.file.
     * @param props properties to copy to the new project
     * @since Ant 1.6
     */
    private void addAlmostAll(Project destProject, Hashtable props) {
        Enumeration e = props.keys();
        while (e.hasMoreElements()) {
            String key = e.nextElement().toString();
            if ("basedir".equals(key) || "ant.file".equals(key)) {
                // basedir and ant.file should not be altered.
                continue;
            }

            String value = props.get(key).toString();
            // don't re-set user properties, avoid the warning message
            if (destProject.getProperty(key) == null) {
                // no user property
                destProject.setNewProperty(key, value);
            }
        }
    }

}
