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
package net.ggtools.grand.ant.taskhelpers;

import java.io.File;
import java.util.List;
import java.util.Vector;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Location;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.RuntimeConfigurable;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Property;
import org.apache.tools.ant.taskdefs.SubAnt;
import org.apache.tools.ant.taskdefs.Ant.Reference;
import org.apache.tools.ant.types.DirSet;
import org.apache.tools.ant.types.FileList;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.PropertySet;
import org.apache.tools.ant.types.Path.PathElement;

/**
 * A Proxy for the {@link org.apache.tools.ant.taskdefs.SubAnt}class allowing
 * to retrieve some data after configuration.
 *
 * @author Christophe Labouisse
 */
public class SubAntHelper extends Task {
    /**
     * Field antfile.
     */
    private String antfile = "build.xml";

    /**
     * Field buildpath.
     */
    private Path buildpath;

    /**
     * Field genericantfile.
     */
    private File genericantfile = null;

    /**
     * Field properties.
     */
    private final Vector<Property> properties = new Vector<Property>();

    /**
     * Field propertySets.
     */
    private final Vector<PropertySet> propertySets = new Vector<PropertySet>();

    /**
     * Field references.
     */
    private final Vector<Reference> references = new Vector<Reference>();

    /**
     * Field subAntTarget.
     */
    private String subAntTarget = null;

    /**
     * Field underlying.
     */
    final SubAnt underlying;

    /**
     * Constructor for SubAntHelper.
     */
    public SubAntHelper() {
        underlying = new SubAnt();
    }

    /**
     * Constructor for SubAntHelper.
     * @param underlying SubAnt
     */
    public SubAntHelper(final SubAnt underlying) {
        this.underlying = underlying;
    }

    /**
     * @param set
     */
    public void addDirset(final DirSet set) {
        getBuildpath().addDirset(set);
    }

    /**
     * @param list
     */
    public void addFilelist(final FileList list) {
        getBuildpath().addFilelist(list);
    }

    /**
     * @param set
     */
    public void addFileset(final FileSet set) {
        getBuildpath().addFileset(set);
    }

    /**
     * @param p
     */
    public void addProperty(final Property p) {
        properties.addElement(p);
        underlying.addProperty(p);
    }

    /**
     * @param ps
     */
    public void addPropertyset(final PropertySet ps) {
        propertySets.addElement(ps);
        underlying.addPropertyset(ps);
    }

    /**
     * @param r
     */
    public void addReference(final Reference r) {
        references.addElement(r);
        underlying.addReference(r);
    }

    /**
     * @return Path
     */
    public Path createBuildpath() {
        return getBuildpath().createPath();
    }

    /**
     * @return PathElement
     */
    public PathElement createBuildpathElement() {
        return getBuildpath().createPathElement();
    }

    /**
     * Method equals.
     * @param obj Object
     * @return boolean
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        return underlying.equals(obj);
    }

    /**
     * Method execute.
     * @throws BuildException
     * @see org.apache.tools.ant.Task#execute()
     */
    @Override
    public void execute() throws BuildException {
        underlying.setBuildpath(buildpath);
        underlying.execute();
    }

    /**
     * @return Returns the antfile.
     */
    public final String getAntfile() {
        return antfile;
    }

    /**
     * Gets the implicit build path, creating it if <code>null</code>.
     *
     * @return the implicit build path.
     */
    public Path getBuildpath() {
        if (buildpath == null) {
            buildpath = new Path(getProject());
        }
        return buildpath;
    }

    /**
     * Method getDescription.
     * @return String
     * @see org.apache.tools.ant.Task#getDescription()
     */
    @Override
    public String getDescription() {
        return underlying.getDescription();
    }

    /**
     * @return Returns the genericantfile.
     */
    public final File getGenericantfile() {
        return genericantfile;
    }

    /**
     * Method getLocation.
     * @return Location
     * @see org.apache.tools.ant.Task#getLocation()
     */
    @Override
    public Location getLocation() {
        return underlying.getLocation();
    }

    /**
     * Method getOwningTarget.
     * @return Target
     * @see org.apache.tools.ant.Task#getOwningTarget()
     */
    @Override
    public Target getOwningTarget() {
        return underlying.getOwningTarget();
    }

    /**
     * Method getProject.
     * @return Project
     * @see org.apache.tools.ant.ProjectComponent#getProject()
     */
    @Override
    public Project getProject() {
        return underlying.getProject();
    }

    /**
     * @return Returns the properties.
     */
    public final List<Property> getProperties() {
        return properties;
    }

    /**
     * @return Returns the propertySets.
     */
    public final List<PropertySet> getPropertySets() {
        return propertySets;
    }

    /**
     * @return Returns the references.
     */
    public final List<Reference> getReferences() {
        return references;
    }

    /**
     * Method getRuntimeConfigurableWrapper.
     * @return RuntimeConfigurable
     * @see org.apache.tools.ant.Task#getRuntimeConfigurableWrapper()
     */
    @Override
    public RuntimeConfigurable getRuntimeConfigurableWrapper() {
        return underlying.getRuntimeConfigurableWrapper();
    }

    /**
     * Method getTaskName.
     * @return String
     * @see org.apache.tools.ant.Task#getTaskName()
     */
    @Override
    public String getTaskName() {
        return underlying.getTaskName();
    }

    /**
     * Method getTaskType.
     * @return String
     * @see org.apache.tools.ant.Task#getTaskType()
     */
    @Override
    public String getTaskType() {
        return underlying.getTaskType();
    }

    /**
     * Method hashCode.
     * @return int
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return underlying.hashCode();
    }

    /**
     * Method init.
     * @throws BuildException
     * @see org.apache.tools.ant.Task#init()
     */
    @Override
    public void init() throws BuildException {
        underlying.init();
    }

    /**
     * Method log.
     * @param msg String
     * @see org.apache.tools.ant.Task#log(java.lang.String)
     */
    @Override
    public void log(final String msg) {
        underlying.log(msg);
    }

    /**
     * Method log.
     * @param msg String
     * @param msgLevel int
     * @see org.apache.tools.ant.Task#log(java.lang.String, int)
     */
    @Override
    public void log(final String msg, final int msgLevel) {
        underlying.log(msg, msgLevel);
    }

    /**
     * Method maybeConfigure.
     * @throws BuildException
     * @see org.apache.tools.ant.Task#maybeConfigure()
     */
    @Override
    public void maybeConfigure() throws BuildException {
        underlying.maybeConfigure();
    }

    /**
     * Method reconfigure.
     * @see org.apache.tools.ant.Task#reconfigure()
     */
    @Override
    public void reconfigure() {
        underlying.reconfigure();
    }

    /**
     * @param antfile
     */
    public void setAntfile(final String antfile) {
        this.antfile = antfile;
        underlying.setAntfile(antfile);
    }

    /**
     * @param s
     */
    public void setBuildpath(final Path s) {
        getBuildpath().append(s);
    }

    /**
     * @param r
     */
    public void setBuildpathRef(final org.apache.tools.ant.types.Reference r) {
        createBuildpath().setRefid(r);
    }

    /**
     * Method setDescription.
     * @param desc String
     * @see org.apache.tools.ant.Task#setDescription(java.lang.String)
     */
    @Override
    public void setDescription(final String desc) {
        underlying.setDescription(desc);
    }

    /**
     * @param failOnError
     */
    public void setFailonerror(final boolean failOnError) {
        underlying.setFailonerror(failOnError);
    }

    /**
     * @param afile
     */
    public void setGenericAntfile(final File afile) {
        genericantfile = afile;
        underlying.setGenericAntfile(afile);
    }

    /**
     * @param b
     */
    public void setInheritall(final boolean b) {
        underlying.setInheritall(b);
    }

    /**
     * @param b
     */
    public void setInheritrefs(final boolean b) {
        underlying.setInheritrefs(b);
    }

    /**
     * Method setLocation.
     * @param location Location
     * @see org.apache.tools.ant.Task#setLocation(org.apache.tools.ant.Location)
     */
    @Override
    public void setLocation(final Location location) {
        underlying.setLocation(location);
    }

    /**
     * @return Returns the target.
     */
    public final String getTarget() {
        return subAntTarget;
    }

    /**
     * @param s
     */
    public void setOutput(final String s) {
        underlying.setOutput(s);
    }

    /**
     * Method setOwningTarget.
     * @param target Target
     * @see org.apache.tools.ant.Task#setOwningTarget(org.apache.tools.ant.Target)
     */
    @Override
    public void setOwningTarget(final Target target) {
        underlying.setOwningTarget(target);
    }

    /**
     * Method setProject.
     * @param project Project
     * @see org.apache.tools.ant.ProjectComponent#setProject(org.apache.tools.ant.Project)
     */
    @Override
    public void setProject(final Project project) {
        underlying.setProject(project);
    }

    /**
     * Method setRuntimeConfigurableWrapper.
     * @param wrapper RuntimeConfigurable
     * @see org.apache.tools.ant.Task#setRuntimeConfigurableWrapper(org.apache.tools.ant.RuntimeConfigurable)
     */
    @Override
    public void setRuntimeConfigurableWrapper(final RuntimeConfigurable wrapper) {
        underlying.setRuntimeConfigurableWrapper(wrapper);
    }

    /**
     * @param target
     */
    public void setTarget(final String target) {
        subAntTarget = target;
        underlying.setTarget(target);
    }

    /**
     * Method setTaskName.
     * @param name String
     * @see org.apache.tools.ant.Task#setTaskName(java.lang.String)
     */
    @Override
    public void setTaskName(final String name) {
        underlying.setTaskName(name);
    }

    /**
     * Method setTaskType.
     * @param type String
     * @see org.apache.tools.ant.Task#setTaskType(java.lang.String)
     */
    @Override
    public void setTaskType(final String type) {
        underlying.setTaskType(type);
    }
}
