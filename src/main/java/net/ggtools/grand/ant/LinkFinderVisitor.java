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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.ggtools.grand.ant.taskhelpers.SubAntHelper;
import net.ggtools.grand.exceptions.DuplicateElementException;
import net.ggtools.grand.exceptions.GrandException;
import net.ggtools.grand.graph.Node;
import net.ggtools.grand.log.LoggerManager;

import org.apache.commons.logging.Log;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.RuntimeConfigurable;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Property;
import org.apache.tools.ant.types.Path;

/**
 * A task visitor looking for links created by tasks like <code>ant</code>,
 * <code>antcall</code>, etc.
 *
 * @author Christophe Labouisse
 */
public class LinkFinderVisitor extends ReflectTaskVisitorBase {
    /**
     * Field log.
     */
    private static final Log LOG =
            LoggerManager.getLog(LinkFinderVisitor.class);

    /**
     * Field aliases.
     */
    private static final Map<String, String> ALIASES =
            new HashMap<String, String>();

    // Initialize the alias list
    static {
        ALIASES.put("runtarget", "antcall");
        ALIASES.put("foreach", "antcall");
        // TODO check those tasks.
        ALIASES.put("antcallback", "antcall");
        ALIASES.put("antfetch", "ant");
        ALIASES.put("switch", "if");
        ALIASES.put("trycatch", "if");
    }

    /**
     * Field ANT_FILE_PROPERTY.
     * (value is ""ant.file"")
     */
    private static final String ANT_FILE_PROPERTY = "ant.file";

    /**
     * Field ATTR_ANTFILE.
     * (value is ""antfile"")
     */
    private static final String ATTR_ANTFILE = "antfile";

    /**
     * Field ATTR_DIR.
     * (value is ""dir"")
     */
    private static final String ATTR_DIR = "dir";

    /**
     * Field ATTR_NAME.
     * (value is ""name"")
     */
    private static final String ATTR_NAME = "name";

    /**
     * Field ATTR_TARGET.
     * (value is ""target"")
     */
    private static final String ATTR_TARGET = "target";

    /**
     * Field ATTR_VALUE.
     * (value is ""value"")
     */
    private static final String ATTR_VALUE = "value";

    /**
     * Field BUILD_XML.
     * (value is ""build.xml"")
     */
    private static final String BUILD_XML = "build.xml";

    /**
     * Field PARAM_ELEMENT.
     * (value is ""param"")
     */
    private static final String PARAM_ELEMENT = "param";

    /**
     * Field PROPERTY_ELEMENT.
     * (value is ""property"")
     */
    private static final String PROPERTY_ELEMENT = "property";

    /**
     * Field graph.
     */
    private AntGraph graph;

    /**
     * Field project.
     */
    private final AntProject project;

    /**
     * Field startNode.
     */
    private AntTargetNode startNode;

    /**
     * Constructor for LinkFinderVisitor.
     * @param project AntProject
     */
    public LinkFinderVisitor(final AntProject project) {
        this.project = project;
    }

    /**
     * Default action for unknown task. The default behavior is to recurse in
     * the children to find a possible task.
     *
     * @param wrapper
     *            wrapper to check.
     * @throws GrandException if an error occurs in visit()
     * @see net.ggtools.grand.ant.ReflectTaskVisitorBase#defaultVisit(org.apache.tools.ant.RuntimeConfigurable)
     */
    @Override
    public final void defaultVisit(final RuntimeConfigurable wrapper)
            throws GrandException {
        final Enumeration<RuntimeConfigurable> children = wrapper.getChildren();
        while (children.hasMoreElements()) {
            visit(children.nextElement());
        }
    }

    /**
     * Method getAliasForTask.
     * @param taskName String
     * @return String
     * @see net.ggtools.grand.ant.ReflectTaskVisitorBase#getAliasForTask(java.lang.String)
     */
    @Override
    public final String getAliasForTask(final String taskName) {
        String result = ALIASES.get(taskName);
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
    public final void reflectVisit_ant(final RuntimeConfigurable wrapper)
            throws DuplicateElementException {
        final Project antProject = project.getAntProject();
        LOG.info("Processing ant target in " + startNode.getName());
        // Find the build file.
        final String targetBuildDirectoryName =
                (String) wrapper.getAttributeMap().get(ATTR_DIR);
        String antFile = (String) wrapper.getAttributeMap().get(ATTR_ANTFILE);
        if (antFile == null) {
            antFile = BUILD_XML;
        } else {
            antFile = antProject.replaceProperties(antFile);
        }

        File targetBuildFile = new File(antFile);
        if (!targetBuildFile.isAbsolute()) {
            if (targetBuildDirectoryName == null) {
                targetBuildFile = new File(antProject.getBaseDir(), antFile);
            } else {
                final String parentDirectoryName =
                        antProject.replaceProperties(targetBuildDirectoryName);
                File parentDirectory = new File(parentDirectoryName);
                if (!parentDirectory.isAbsolute()) {
                    parentDirectory = new File(antProject.getBaseDir(),
                            parentDirectoryName);
                }
                targetBuildFile = new File(parentDirectory, antFile);
            }
        }

        final List<Object> targetElements = getTargetElementNames(wrapper);

        final AntTaskLink[] links;

        if (targetElements.size() > 0) {
            links = new AntTaskLink[targetElements.size()];
            int i = 0;
            for (Object object : targetElements) {
                links[i++] = createAntTaskLink(targetBuildFile,
                        wrapper.getElementTag(), (String) object);
            }
        } else {
            links = new AntTaskLink[]{createAntTaskLink(targetBuildFile,
                    wrapper.getElementTag(),
                    (String) wrapper.getAttributeMap().get(ATTR_TARGET))};
        }

        // Look to params children.
        addNestPropertiesParameters(wrapper, links, PROPERTY_ELEMENT);
    }

    /**
     * @param wrapper RuntimeConfigurable
     * @return List<Object>
     */
    private List<Object> getTargetElementNames(final RuntimeConfigurable wrapper) {
        final List<Object> targetElements = new ArrayList<Object>();
        final Enumeration<RuntimeConfigurable> children = wrapper.getChildren();
        while (children.hasMoreElements()) {
            final RuntimeConfigurable child = children.nextElement();
            if ("target".equals(child.getElementTag())) {
                final Map<String, Object> childAttributeMap =
                        child.getAttributeMap();
                // name is supposed to be a string however since we are putting
                // it in an object collection, there is no need to cast it as a
                // String right now.
                final Object name = childAttributeMap.get(ATTR_NAME);
                if (name != null) {
                    targetElements.add(name);
                }
            }
        }
        return targetElements;
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
    public final void reflectVisit_antcall(final RuntimeConfigurable wrapper)
            throws DuplicateElementException {
        LOG.info("Processing antcall target in " + startNode.getName());
        final Project antProject = project.getAntProject();

        final List<Object> targetElements = getTargetElementNames(wrapper);

        final AntTaskLink[] links;

        final String elementTag = wrapper.getElementTag();
        if (targetElements.size() > 0) {
            links = new AntTaskLink[targetElements.size()];
            int i = 0;
            for (Object object : targetElements) {
                final String endNodeName =
                        antProject.replaceProperties((String) object);

                final AntTargetNode endNode = findOrCreateNode(endNodeName);

                LOG.debug("Creating link from " + startNode
                        + " to " + endNodeName);

                links[i++] = graph.createTaskLink(null,
                        startNode, endNode, elementTag);
            }
        } else {
            final String endNodeName = antProject.replaceProperties((String)
                    wrapper.getAttributeMap().get(ATTR_TARGET));

            final AntTargetNode endNode = findOrCreateNode(endNodeName);

            LOG.debug("Creating link from " + startNode + " to " + endNodeName);

            links = new AntTaskLink[]{graph.createTaskLink(null,
                    startNode, endNode, elementTag)};
        }

        // Look to params children.
        addNestPropertiesParameters(wrapper, links, PARAM_ELEMENT);
    }

    /**
     * Process <code>subant</code> task. Depending of the existence of the
     * <code>genericantfile</code> attribute, this method will either create a
     * special link holding a list of directories or a set of <i>ant taskish</i>
     * links. During those creations, the end nodes will be created with
     * the {@link Node#ATTR_MISSING_NODE}attribute if needed.
     *
     * @param wrapper
     *            wrapper to process.
     * @throws DuplicateElementException
     *             if a duplicate node is created (should not happen).
     */
    public final void reflectVisit_subant(final RuntimeConfigurable wrapper)
            throws DuplicateElementException {
        LOG.info("Processing subant target in " + startNode.getName());
        final Project antProject = project.getAntProject();

        // Configure the wrapper's proxy and get the configured task.
        ((Task) wrapper.getProxy()).maybeConfigure();
        final Object proxy = wrapper.getProxy();
        if (proxy instanceof SubAntHelper) {
            final SubAntHelper helper = (SubAntHelper) proxy;

            final Path buildPath = helper.getBuildpath();
            final String antfile = helper.getAntfile();
            final File genericantfile = helper.getGenericantfile();
            final Collection<Property> properties = helper.getProperties();
            final String target = helper.getTarget();

            final List<File> genericantfileDirs = new LinkedList<File>();

            if ((buildPath == null) || (buildPath.size() == 0)) {
                LOG.warn("buildPath is null or empty, subant task probably won't work");
                return;
            }

            final String[] filenames = buildPath.list();

            for (final String currentFileName : filenames) {
                File directory = null;
                File file = new File(currentFileName);
                if (file.isDirectory()) {
                    if (genericantfile != null) {
                        directory = file;
                        file = genericantfile;
                    } else {
                        file = new File(file, antfile);
                    }
                }

                if (directory == null) {
                    // First case: antfile.
                    final AntTaskLink link =
                            createAntTaskLink(file, wrapper.getElementTag(),
                            target);

                    for (final Property property : properties) {
                        if (property.getName() != null) {
                            // Simple property
                            link.setParameter(property.getName(),
                                    antProject.replaceProperties(property.getValue()));
                        } else if (property.getFile() != null) {
                            // Property file.
                            final File propFile = property.getFile();
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("Loading " + propFile.getAbsolutePath());
                            }
                            link.addPropertyFile(propFile.getAbsolutePath());
                        }
                    }
                } else {
                    // Second case, genericantfile, push the directory on a list
                    // to be used latter.
                    genericantfileDirs.add(directory);
                }
            }

            if (genericantfileDirs.size() > 0) {
                final AntTargetNode endNode =
                        findOrCreateNode(target, genericantfile);
                LOG.debug("Creating link from " + startNode
                        + " to " + endNode.getName());
                final SubantTaskLink link = graph.createSubantTaskLink(null,
                        startNode, endNode, wrapper.getElementTag());

                for (File currentDir : genericantfileDirs) {
                    link.addDirectory(currentDir.getAbsolutePath());
                }
            }
        } else {
            LOG.warn("Cannot get information for subant task");
            LOG.debug("Task should be instance of SubAntHelper but is " + proxy);
        }
    }

    /**
     * @param graph
     *            The graph to set.
     */
    public final void setGraph(final AntGraph graph) {
        this.graph = graph;
    }

    /**
     * @param startNode
     *            The startNode to set.
     */
    public final void setStartNode(final AntTargetNode startNode) {
        this.startNode = startNode;
    }

    /**
     * Add to a given link the properties contained in an element.
     *
     * @param wrapper
     *            wrapper for the task.
     * @param links AntTaskLink[]
     * @param elementName
     *            name of the elements holding the properties.
     */
    private void addNestPropertiesParameters(final RuntimeConfigurable wrapper,
            final AntTaskLink[] links, final String elementName) {
        final Project antProject = project.getAntProject();
        final Enumeration<RuntimeConfigurable> children = wrapper.getChildren();
        while (children.hasMoreElements()) {
            final RuntimeConfigurable child = children.nextElement();
            if (elementName.equals(child.getElementTag())) {
                final Map<String, Object> childAttributeMap =
                        child.getAttributeMap();
                final String name = (String) childAttributeMap.get(ATTR_NAME);
                if (name != null) {
                    final String propertyValue = antProject.replaceProperties((String)
                            childAttributeMap.get(ATTR_VALUE));
                    for (final AntTaskLink link : links) {
                        link.setParameter(name, propertyValue);
                    }
                } else {
                    final String fileName = (String) childAttributeMap.get("file");
                    if (fileName != null) {
                        for (final AntTaskLink link : links) {
                            link.addPropertyFile(antProject.replaceProperties(fileName));
                        }
                    }
                }
            }
        }
    }

    /**
     * @param targetBuildFile File
     * @param taskName String
     * @param target String
     * @return AntTaskLink
     * @throws DuplicateElementException
     *             if there is already a link with the same name.
     */
    private AntTaskLink createAntTaskLink(final File targetBuildFile, final String taskName,
            final String target) throws DuplicateElementException {
        final AntTargetNode endNode = findOrCreateNode(target, targetBuildFile);

        LOG.debug("Creating link from " + startNode + " to " + endNode.getName());
        return graph.createTaskLink(null, startNode, endNode, taskName);
    }

    /**
     * @param endNodeName String
     * @return AntTargetNode
     * @throws DuplicateElementException
     *             if there is already a node with the same name.
     */
    private AntTargetNode findOrCreateNode(final String endNodeName)
            throws DuplicateElementException {
        return findOrCreateNode(endNodeName, null);
    }

    /**
     * @param target String
     * @param targetBuildFile File
     * @return AntTargetNode
     * @throws DuplicateElementException
     *             if there is already a node with the same name.
     */
    private AntTargetNode findOrCreateNode(final String target,
            File targetBuildFile) throws DuplicateElementException {
        final Project antProject = project.getAntProject();
        final File projectFile = new File(antProject.getProperty(ANT_FILE_PROPERTY));

        if (targetBuildFile == null) {
            targetBuildFile = projectFile;
        }

        final boolean isSameBuildFile = projectFile.equals(targetBuildFile);

        String endNodeName;

        String targetName = antProject.replaceProperties(target);

        AntTargetNode endNode;
        if (isSameBuildFile) {
            endNodeName = (targetName == null) ? antProject.getDefaultTarget() : targetName;
            endNode = (AntTargetNode) graph.getNode(endNodeName);
        } else {
            if (targetName == null) {
                try {
                    // TODO caching.
                    LOG.debug("Reading project file " + targetBuildFile);
                    final AntProject tmpProj = new AntProject(targetBuildFile);
                    targetName = tmpProj.getAntProject().getDefaultTarget();
                } catch (final GrandException e) {
                    LOG.info("Caught exception trying to read " + targetBuildFile
                            + " using default target name", e);
                    targetName = "'default'";
                }

            }

            // Find out the "right" node avoiding conflicts.
            // FIXME the current algorithm seems really bad, check if a cache is worth implementing.
            int index = 1;
            boolean conflict = false;
            endNodeName = "[" + targetName + "]";
            do {
                conflict = false;
                endNode = (AntTargetNode) graph.getNode(endNodeName);
                if ((endNode != null)
                        && !targetBuildFile.getAbsolutePath().equals(endNode.getBuildFile())) {
                    LOG.error("Conflict on build file " + targetBuildFile + " vs "
                            + endNode.getBuildFile());
                    conflict = true;
                    index++;
                    endNodeName = "[" + targetName + " (" + index + ")]";
                }
            } while (conflict);
        }

        // Creates an new node if none found.
        if (endNode == null) {
            LOG.info("Target " + startNode + " has dependency to non existent target "
                    + endNodeName + ", creating a dummy node");
            endNode = (AntTargetNode) graph.createNode(endNodeName);
            endNode.setAttributes(Node.ATTR_MISSING_NODE);
        }

        if (!isSameBuildFile) {
            endNode.setBuildFile(targetBuildFile.getAbsolutePath());
        }
        return endNode;
    }
}
