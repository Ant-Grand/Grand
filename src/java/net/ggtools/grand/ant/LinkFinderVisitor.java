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

import java.io.File;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import net.ggtools.grand.ant.taskhelpers.SubAntHelper;
import net.ggtools.grand.exceptions.DuplicateElementException;
import net.ggtools.grand.exceptions.GrandException;
import net.ggtools.grand.graph.Node;
import net.ggtools.grand.log.LoggerManager;

import org.apache.commons.logging.Log;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.RuntimeConfigurable;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;

/**
 * A task visitor looking for links created by tasks like <code>ant</code>,
 * <code>antcall</code>, etc.
 * 
 * @author Christophe Labouisse
 */
public class LinkFinderVisitor extends ReflectTaskVisitorBase {
    private static final Log log = LoggerManager.getLog(LinkFinderVisitor.class);

    private final static Map aliases = new HashMap();

    // Initialize the alias list
    static {
        aliases.put("runtarget", "antcall");
        aliases.put("foreach", "antcall");
    }

    private static final String ANT_FILE_PROPERTY = "ant.file";

    private static final String ATTR_ANTFILE = "antfile";

    private static final String ATTR_BUILDPATH = "buildpath";

    private static final String ATTR_BUILDPATHREF = "buildpathref";

    private static final String ATTR_DIR = "dir";

    private static final String ATTR_NAME = "name";

    private static final String ATTR_TARGET = "target";

    private static final String ATTR_VALUE = "value";

    private static final String BUILD_XML = "build.xml";

    private static final String PARAM_ELEMENT = "param";

    private static final String PROPERTY_ELEMENT = "property";

    private AntGraph graph;

    private final AntProject project;

    private AntTargetNode startNode;

    public LinkFinderVisitor(final AntProject project) {
        this.project = project;
    }

    /**
     * Default action for unknown task. The default behavior is to recurse in
     * the children to find a possible task.
     * 
     * @param wrapper
     *            wrapper to check.
     * @see net.ggtools.grand.ant.ReflectTaskVisitorBase#defaultVisit(org.apache.tools.ant.RuntimeConfigurable)
     */
    public void defaultVisit(final RuntimeConfigurable wrapper) throws GrandException {
        final Enumeration children = wrapper.getChildren();
        while (children.hasMoreElements()) {
            visit((RuntimeConfigurable) children.nextElement());
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.ggtools.grand.ant.ReflectTaskVisitorBase#getAliasForTask(java.lang.String)
     */
    public String getAliasForTask(final String taskName) {
        String result = (String) aliases.get(taskName);
        if (result == null) {
            result = taskName;
        }
        return result;
    }

    /**
     * Process the <code>ant</code> task. This method will find or create the
     * destination node of the task, create an {@link AntTaskLink}and find the
     * nested <code>property</code> nodes to set the link properties. Only
     * <code>name</code>,<code>value</code> property nodes will be
     * processed: the <code>file</code> property nodes will be ignored.
     * 
     * The called node name will be either the plain <code>target</code>
     * attribute value if it is located in the current build file or
     * <code>[<em>target</em>]</code>.
     * 
     * @param wrapper
     *            the wrapper to process.
     * @throws DuplicateElementException
     *             if a duplicate node is created, should not happen.
     */
    public void reflectVisit_ant(final RuntimeConfigurable wrapper)
            throws DuplicateElementException {
        final Project antProject = project.getAntProject();
        log.info("Processing ant target in " + startNode.getName());
        // Find the build file.
        final String targetBuildDirectoryName = (String) wrapper.getAttributeMap().get(ATTR_DIR);
        String antFile = (String) wrapper.getAttributeMap().get(ATTR_ANTFILE);
        if (antFile == null) {
            antFile = BUILD_XML;
        }
        else {
            antFile = antProject.replaceProperties(antFile);
        }

        File targetBuildFile = new File(antFile);
        if (!targetBuildFile.isAbsolute()) {
            if (targetBuildDirectoryName == null) {
                targetBuildFile = new File(antProject.getBaseDir(), antFile);
            }
            else {
                final String parentDirectoryName = antProject
                        .replaceProperties(targetBuildDirectoryName);
                File parentDirectory = new File(parentDirectoryName);
                if (!parentDirectory.isAbsolute()) {
                    parentDirectory = new File(antProject.getBaseDir(), parentDirectoryName);
                }
                targetBuildFile = new File(parentDirectory, antFile);
            }
        }

        final AntTaskLink links[];

        if (false) {
            links = null;
        }
        else {
            links = new AntTaskLink[]{createAntTaskLink(targetBuildFile, wrapper.getElementTag(),
                    (String) wrapper.getAttributeMap().get(ATTR_TARGET))};
        }

        // Look to params children.
        addNestPropertiesParameters(wrapper, links, PROPERTY_ELEMENT);
    }

    /**
     * Process <code>antcall</code> and similar tasks. The method will create
     * a link between the current start node and the node referenced by the
     * <code>target</code> attribute creating it with the
     * {@link Node#ATTR_MISSING_NODE}if no such node exists. It will then
     * create an {@link AntTaskLink}link and look for nested <code>param</code>
     * elements to set parameters to newly created link.
     * 
     * @param wrapper
     *            wrapper to process.
     * @throws DuplicateElementException
     *             if a duplicate node is created (should not happen).
     */
    public void reflectVisit_antcall(final RuntimeConfigurable wrapper)
            throws DuplicateElementException {
        log.info("Processing antcall target in " + startNode.getName());
        final Project antProject = project.getAntProject();
        final AntTaskLink links[];

        if (false) {
            links = null;
        }
        else {
            final String endNodeName = antProject.replaceProperties((String) wrapper
                    .getAttributeMap().get(ATTR_TARGET));

            final AntTargetNode endNode = findOrCreateNode(endNodeName);

            log.debug("Creating link from " + startNode + " to " + endNodeName);

            links = new AntTaskLink[]{graph.createTaskLink(null, startNode, endNode, wrapper
                    .getElementTag())};
        }

        // Look to params children.
        addNestPropertiesParameters(wrapper, links, PARAM_ELEMENT);
    }

    /**
     * Process <code>subant</code> task. Depending of the existence of the
     * <code>genericantfile</code> attribute, this method will either create a
     * special link holding a list of directories or a set of <i>ant taskish
     * </i> links. During those creations, the end nodes will be created with
     * the {@link Node#ATTR_MISSING_NODE}attribute if needed.
     * 
     * @param wrapper
     *            wrapper to process.
     * @throws DuplicateElementException
     *             if a duplicate node is created (should not happen).
     */
    public void reflectVisit_subant(final RuntimeConfigurable wrapper)
            throws DuplicateElementException {
        log.info("Processing subant target in " + startNode.getName());
        final Project antProject = project.getAntProject();

        // Configure the wrapper's proxy and get the configured task.
        ((Task) wrapper.getProxy()).maybeConfigure();
        final Object proxy = wrapper.getProxy();
        if (proxy instanceof SubAntHelper) {
            final SubAntHelper helper = (SubAntHelper) proxy;

            final Path buildPath = helper.getBuildpath();
            final String antfile = helper.getAntfile();
            final File genericantfile = helper.getGenericantfile();
            final Collection properties = helper.getProperties();
            final String target = helper.getTarget();

            final List genericantfileDirs = new LinkedList();

            if ((buildPath == null) || (buildPath.size() == 0)) {
                log.warn("buildPath is null or empty, subant task probably won't work");
                return;
            }

            final String[] filenames = buildPath.list();

            for (int i = 0; i < filenames.length; i++) {
                final String currentFileName = filenames[i];
                File file = null;
                File directory = null;
                file = new File(filenames[i]);
                if (file.isDirectory()) {
                    if (genericantfile != null) {
                        directory = file;
                        file = genericantfile;
                    }
                    else {
                        file = new File(file, antfile);
                    }
                }

                if (directory == null) {
                    // First case: antfile.
                    final AntTaskLink link = createAntTaskLink(file, wrapper.getElementTag(),
                            target);

                    for (Iterator iter = properties.iterator(); iter.hasNext();) {
                        final Set entries = ((Properties) iter.next()).entrySet();
                        for (Iterator iterator = entries.iterator(); iterator.hasNext();) {
                            final Map.Entry current = (Map.Entry) iterator.next();
                            link.setParameter((String) current.getKey(), antProject
                                    .replaceProperties((String) current.getValue()));
                        }
                    }
                }
                else {
                    // Second case, genericantfile, push the directory on a list
                    // to be used latter.
                    genericantfileDirs.add(directory);
                }
            }

            if (genericantfileDirs.size() > 0) {
                final AntTargetNode endNode = findOrCreateNode(target, genericantfile);
                log.debug("Creating link from " + startNode + " to " + endNode.getName());
                final SubantTaskLink link = graph.createSubantTaskLink(null, startNode, endNode,
                        wrapper.getElementTag());

                for (Iterator iter = genericantfileDirs.iterator(); iter.hasNext();) {
                    final File currentDir = (File) iter.next();
                    link.addDirectory(currentDir.getAbsolutePath());
                }
            }
        }
        else {
            log.warn("Cannot get information for subant task");
            log.debug("Task should be instance of SubAntHelper but is " + proxy);
        }
    }

    /**
     * @param graph
     *            The graph to set.
     */
    public void setGraph(AntGraph graph) {
        this.graph = graph;
    }

    /**
     * @param startNode
     *            The startNode to set.
     */
    public void setStartNode(AntTargetNode startNode) {
        this.startNode = startNode;
    }

    /**
     * Add to a given link the properties contained in an element.
     * 
     * @param wrapper
     *            wrapper for the task.
     * @param link
     *            link to populate.
     * @param elementName
     *            name of the elements holding the properties.
     */
    private void addNestPropertiesParameters(final RuntimeConfigurable wrapper,
            final AntTaskLink links[], final String elementName) {
        final Project antProject = project.getAntProject();
        final Enumeration children = wrapper.getChildren();
        while (children.hasMoreElements()) {
            final RuntimeConfigurable child = (RuntimeConfigurable) children.nextElement();
            if (elementName.equals(child.getElementTag())) {
                final Hashtable childAttributeMap = child.getAttributeMap();
                final String name = (String) childAttributeMap.get(ATTR_NAME);
                if (name != null) {
                    for (int i = 0; i < links.length; i++) {
                        final AntTaskLink link = links[i];
                        link.setParameter(name, antProject
                                .replaceProperties((String) childAttributeMap.get(ATTR_VALUE)));

                    }
                }
            }
        }
    }

    /**
     * @param antProject
     * @param targetBuildFile
     * @param taskName
     * @param target
     * @return
     * @throws DuplicateElementException
     */
    private AntTaskLink createAntTaskLink(final File targetBuildFile, final String taskName,
            final String target) throws DuplicateElementException {
        final AntTargetNode endNode = findOrCreateNode(target, targetBuildFile);

        log.debug("Creating link from " + startNode + " to " + endNode.getName());
        return graph.createTaskLink(null, startNode, endNode, taskName);
    }

    /**
     * @param endNodeName
     * @return
     * @throws DuplicateElementException
     */
    private AntTargetNode findOrCreateNode(final String endNodeName)
            throws DuplicateElementException {
        return findOrCreateNode(endNodeName, null);
    }

    /**
     * @param target
     * @param targetBuildFile
     * @param antProject
     * @return
     * @throws DuplicateElementException
     */
    private AntTargetNode findOrCreateNode(final String target, File targetBuildFile)
            throws DuplicateElementException {
        final Project antProject = project.getAntProject();
        final File projectFile = new File(antProject.getProperty(ANT_FILE_PROPERTY));

        if (targetBuildFile == null) targetBuildFile = projectFile;

        final boolean isSameBuildFile = projectFile.equals(targetBuildFile);

        final String endNodeName;

        String targetName = antProject.replaceProperties(target);

        if (isSameBuildFile) {
            endNodeName = targetName == null ? antProject.getDefaultTarget() : targetName;
        }
        else {
            if (targetName == null) {
                try {
                    // TODO Caching.
                    log.debug("Reading project file " + targetBuildFile);
                    final AntProject tmpProj = new AntProject(targetBuildFile);
                    targetName = tmpProj.getAntProject().getDefaultTarget();
                } catch (GrandException e) {
                    log.error("Caught exception trying to read " + targetBuildFile
                            + " using default target name", e);
                    targetName = "'default'";
                }

            }
            endNodeName = "[" + targetName + "]";
        }

        // final AntTargetNode endNode = findOrCreateNode(endNodeName);
        AntTargetNode endNode = (AntTargetNode) graph.getNode(endNodeName);

        // Creates an new node if none found.
        if (endNode == null) {
            log.info("Target " + startNode + " has dependency to non existent target "
                    + endNodeName + ", creating a dummy node");
            endNode = (AntTargetNode) graph.createNode(endNodeName);
            endNode.setAttributes(Node.ATTR_MISSING_NODE);
        }

        // TODO check that I'm not overriding a previously set file.
        if (!isSameBuildFile) endNode.setBuildFile(targetBuildFile.getAbsolutePath());
        return endNode;
    }
}