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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import net.ggtools.grand.ant.AntProject;
import net.ggtools.grand.exceptions.GrandException;
import net.ggtools.grand.filters.GraphFilter;
import net.ggtools.grand.graph.GraphProducer;
import net.ggtools.grand.graph.GraphWriter;
import net.ggtools.grand.log.AntLog;
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

    /**
     * Field buildFile.
     */
    private File buildFile;

    /**
     * Field output.
     */
    private File output;

    /**
     * Field outputConfigurationFile.
     */
    private File outputConfigurationFile;

    /**
     * The sets of properties to pass to the graphed project.
     */
    private final List<PropertySet> propertySets = new ArrayList<PropertySet>();

    /**
     * Field filters.
     */
    private final List<FilterType> filters = new LinkedList<FilterType>();

    /**
     * Field showGraphName.
     */
    private boolean showGraphName = false;

    /**
     * Field inheritAll.
     */
    private boolean inheritAll = false;

    /**
     * Field properties.
     */
    private final List<Property> properties = new LinkedList<Property>();

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

        for (FilterType filter : filters) {
            filter.checkParameters();
        }
    }

    /**
     * Method execute.
     * @throws BuildException
     * @see org.apache.tools.ant.Task#execute()
     */
    @Override
    public void execute() throws BuildException {
        checkParams();

        final GraphProducer graphProject = initAntProject();

        log("Done loading project ", Project.MSG_VERBOSE);

        log("Setting up filter chain");
        GraphProducer producer = graphProject;
        int numFilters = 0;

        for (FilterType f : filters) {
            log("Adding filter " + f.getFilterName(), Project.MSG_VERBOSE);
            final GraphFilter filter = f.getFilter();
            filter.setProducer(producer);
            producer = filter;
            numFilters++;
        }

        if (numFilters > 0) {
            log("Loaded " + numFilters + ((numFilters > 1) ? " filters" : " filter"));
        }

        try {
            Properties override = null;
            if (outputConfigurationFile != null) {
                log("Overriding default properties from " + outputConfigurationFile);
                override = new Properties();
                override.load(new FileInputStream(outputConfigurationFile));
            }

            final GraphWriter writer = new DotWriter(override);
            writer.setProducer(producer);
            writer.setShowGraphName(showGraphName);

            log("Writing output to " + output);
            writer.write(output);
        } catch (final IOException e) {
            log("Cannot write graph", Project.MSG_ERR);
            throw new BuildException("Cannot write graph", e);
        } catch (final GrandException e) {
            log("Cannot process graph", Project.MSG_ERR);
            throw new BuildException("Cannot write graph", e);
        }
    }

    /**
     * Create and initialize a GraphProducer according to the
     * task parameters.
     *
     * @return an initialized GraphProducer.
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

        for (PropertySet ps : propertySets) {
            addAlmostAll(antProject, ps.getProperties());
        }

        if (properties.size() > 0) {
            for (Property prop : properties) {
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
        final Project antProject = new Project();

        // Set the current project listeners to the graphed project
        // in order to get some trace if we need to execute some tasks
        // in the graphed project.
        for (final BuildListener listener : getProject().getBuildListeners()) {
            antProject.addBuildListener(listener);
        }

        antProject.init();

        if (!inheritAll) {
            // set Java built-in properties separately,
            // b/c we won't inherit them.
            antProject.setSystemProperties();

        } else {
            // set all properties from calling project
            final Properties props = new Properties();
            for (Map.Entry<String,Object> entry : getProject().getProperties().entrySet()) {
                props.put(entry.getKey(), entry.getValue());
            }
            addAlmostAll(antProject, props);
        }

        antProject.setUserProperty("ant.file", buildFile.getAbsolutePath());
        final ProjectHelper loader = ProjectHelper.getProjectHelper();
        antProject.addReference("ant.projectHelper", loader);
        loader.parse(antProject, buildFile);

        getProject().copyUserProperties(antProject);

        return antProject;
    }

    /**
     * Method setProject.
     * @param project Project
     * @see org.apache.tools.ant.ProjectComponent#setProject(org.apache.tools.ant.Project)
     */
    @Override
    public void setProject(final Project project) {
        super.setProject(project);
        AntLog.setCurrentProject(project);
        AntLog.setCurrentTask(this);
    }

    /**
     * Sets the buildFile.
     *
     * @param file File
     */
    public void setBuildFile(final File file) {
        buildFile = file;
    }

    /**
     * Sets the output file.
     *
     * @param file File
     */
    public void setOutput(final File file) {
        output = file;
    }

    /**
     * Set a property file to override the output default configuration.
     * @deprecated use {@link #setOutputConfigFile(File)}.
     * @param propertyFile File
     */
    @Deprecated
    public void setPropertyFile(final File propertyFile) {
        log(
"Using of deprecated \"propertyfile\" attribute, use \"outputconfigfile\" from now on",
                Project.MSG_WARN);
        outputConfigurationFile = propertyFile;
    }

    /**
     * Set a property file to override the output default configuration.
     * @param propertyFile File
     */
    public void setOutputConfigFile(final File propertyFile) {
        outputConfigurationFile = propertyFile;
    }

    /**
     * Method setShowGraphName.
     * @param show boolean
     */
    public void setShowGraphName(final boolean show) {
        showGraphName = show;
    }

    /**
     * If true, pass all properties to the new Ant project.
     * Defaults to true.
     * @param value if true pass all properties to the new Ant project.
     */
    public void setInheritAll(final boolean value) {
        inheritAll = value;
    }

    /**
     * Add a filter to the task.
     * @param filter FilterType
     */
    public void addFilter(final FilterType filter) {
        filters.add(filter);
    }

    /**
     * Add a new property to be passed to the graphed project.
     *
     * @param p the property to set.
     */
    public void addProperty(final Property p) {
        properties.add(p);
    }

    /**
     * Set of properties to pass to the graphed project.
     *
     * @param ps property set to add
     */
    public void addPropertyset(final PropertySet ps) {
        propertySets.add(ps);
    }

    /**
     * Copies all properties from the given table to the destination project -
     * omitting those that have already been set in the destination project as
     * well as properties named basedir or ant.file.
     * @param props properties to copy to the new project
     * @since Ant 1.6
     * @param destProject Project
     */
    private void addAlmostAll(final Project destProject, final Properties props) {
        final Enumeration<Object> e = props.keys();
        while (e.hasMoreElements()) {
            final String key = e.nextElement().toString();
            if ("basedir".equals(key) || "ant.file".equals(key)) {
                // basedir and ant.file should not be altered.
                continue;
            }

            final String value = props.get(key).toString();
            // don't re-set user properties, avoid the warning message
            if (destProject.getProperty(key) == null) {
                // no user property
                destProject.setNewProperty(key, value);
            }
        }
    }

}
