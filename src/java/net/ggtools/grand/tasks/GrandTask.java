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
import java.util.Properties;

import net.ggtools.grand.GraphProducer;
import net.ggtools.grand.GraphWriter;
import net.ggtools.grand.Log;
import net.ggtools.grand.ant.AntProject;
import net.ggtools.grand.exceptions.GrandException;
import net.ggtools.grand.output.DotWriter;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;

/**
 * A task to create graphs. 
 * 
 * @author Christophe Labouisse
 * @todo add attributes elements
 * @todo add property file override
 */
public class GrandTask extends Task {

    private File buildFile;

    private File output;

    private File propertyFile;

    /**
     * Check the parameters validity before execution.
     * 
     */
    private void checkParams() {
        if (output == null) {
            final String message = "Required attribute missing";
            log(message, Project.MSG_ERR);
            throw new BuildException(message);
        }
    }

    /* (non-Javadoc)
     * @see org.apache.tools.ant.Task#execute()
     */
    public void execute() throws BuildException {
        checkParams();

        GraphProducer graphProject;

        if (buildFile == null) {
            // Working with current project
            log("Using current project");
            graphProject = new AntProject(getProject());
        } else {
            // Open a new project
            log("Loading project " + buildFile);
            graphProject = new AntProject(buildFile);
        }

        try {
            Properties override = null;
            if (propertyFile != null) {
                log("Overriding default properties from " + propertyFile);
                override = new Properties();
                override.load(new FileInputStream(propertyFile));
            }
            GraphWriter writer = new DotWriter(override);
            writer.setProducer(graphProject);

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
     * Set a property file to override the default configuration.
     * @param propertyFile The propertyFile to set.
     */
    public void setPropertyFile(File propertyFile) {
        this.propertyFile = propertyFile;
    }

    /* (non-Javadoc)
     * @see org.apache.tools.ant.ProjectComponent#setProject(org.apache.tools.ant.Project)
     */
    public void setProject(Project project) {
        super.setProject(project);

        Log.setProject(project);
    }
}
