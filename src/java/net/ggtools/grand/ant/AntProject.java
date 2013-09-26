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
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;

import net.ggtools.grand.ant.taskhelpers.SubAntHelper;
import net.ggtools.grand.ant.taskhelpers.TaskDefHelper;
import net.ggtools.grand.exceptions.DuplicateElementException;
import net.ggtools.grand.exceptions.GrandException;
import net.ggtools.grand.graph.Graph;
import net.ggtools.grand.graph.GraphProducer;
import net.ggtools.grand.graph.Node;
import net.ggtools.grand.log.LoggerManager;

import org.apache.commons.logging.Log;
import org.apache.tools.ant.AntTypeDefinition;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ComponentHelper;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.Task;

/**
 * A graph producer from ant build files or {@link org.apache.tools.ant.Project}
 * objects. The nodes will be the project's target and the links will be the
 * dependencies between targets. Beside <i>hard</i> dependencies, this producer
 * is also able to create weaks links from dependencies introduced by the use of
 * the <code>antcall</code> or <code>foreach</code> tasks.
 *
 * @author Christophe Labouisse
 * @see <a href="http://ant-contrib.sourceforge.net/">Ant contrib tasks</a> for
 *      the <code>foreach</code> task.
 */
public class AntProject implements GraphProducer {
    /**
     * Field log.
     */
    private static final Log log = LoggerManager.getLog(AntProject.class);

    /**
     * A condition helper using the {@link Target#getIf()}&
     * {@link Target#getUnless()} methods introduced in Ant 1.6.2.
     * @author Christophe Labouisse
     */
    private static class GetterConditionHelper implements TargetConditionHelper {

        /**
         * Build a new GetterConditionHelper. Since the need methods are only
         * available in the last versions of Ant, we check if those method can
         * be loaded.
         * @throws NoSuchMethodException
         * @throws SecurityException
         */
        public GetterConditionHelper() throws SecurityException, NoSuchMethodException {
            final Class<?>[] parameters = new Class[]{};
            Target.class.getMethod("getIf", parameters);
            Target.class.getMethod("getUnless", parameters);
        }

        /**
         * Method getIfCondition.
         * @param target Target
         * @return String
         * @see net.ggtools.grand.ant.AntProject$TargetConditionHelper#getIfCondition(org.apache.tools.ant.Target)
         */
        public String getIfCondition(final Target target) {
            return target.getIf();
        }

        /**
         * Method getUnlessCondition.
         * @param target Target
         * @return String
         * @see net.ggtools.grand.ant.AntProject$TargetConditionHelper#getUnlessCondition(org.apache.tools.ant.Target)
         */
        public String getUnlessCondition(final Target target) {
            return target.getUnless();
        }

    }

    /**
     * A condition helper always returning <code>null</code>. This class will
     * be used as a fallback helper
     *
     * @author Christophe Labouisse
     */
    private static class NullConditionHelper implements TargetConditionHelper {

        /**
         * Method getIfCondition.
         * @param target Target
         * @return String
         * @see net.ggtools.grand.ant.AntProject$TargetConditionHelper#getIfCondition(org.apache.tools.ant.Target)
         */
        public String getIfCondition(final Target target) {
            return null;
        }

        /**
         * Method getUnlessCondition.
         * @param target Target
         * @return String
         * @see net.ggtools.grand.ant.AntProject$TargetConditionHelper#getUnlessCondition(org.apache.tools.ant.Target)
         */
        public String getUnlessCondition(final Target target) {
            return null;
        }

    }

    /**
     * A dirty hack using {@link Field}methods in order to gain access to the
     * private {@link Target#ifCondition}and {@link Target#unlessCondition}
     * attributes.
     *
     * @author Christophe Labouisse
     */
    private static class ReflectHelper implements TargetConditionHelper {
        /**
         * Field ifCondition.
         */
        private final Field ifCondition;

        /**
         * Field unlessCondition.
         */
        private final Field unlessCondition;

        /**
         * Constructor for ReflectHelper.
         * @throws NoSuchFieldException
         * @throws SecurityException
         */
        public ReflectHelper() throws SecurityException, NoSuchFieldException {
            ifCondition = Target.class.getDeclaredField("ifCondition");
            unlessCondition = Target.class.getDeclaredField("unlessCondition");
            AccessibleObject.setAccessible(new AccessibleObject[]{ifCondition, unlessCondition}, true);
        }

        /**
         * Method getIfCondition.
         * @param target Target
         * @return String
         * @see net.ggtools.grand.ant.AntProject$TargetConditionHelper#getIfCondition(org.apache.tools.ant.Target)
         */
        public String getIfCondition(final Target target) {
            String result = null;

            try {
                result = (String) ifCondition.get(target);
                if ("".equals(result)) {
                    result = null;
                }
            } catch (final Exception e) {
                log.error("Caugh exception, ignoring if condition", e);
            }

            return result;
        }

        /**
         * Method getUnlessCondition.
         * @param target Target
         * @return String
         * @see net.ggtools.grand.ant.AntProject$TargetConditionHelper#getUnlessCondition(org.apache.tools.ant.Target)
         */
        public String getUnlessCondition(final Target target) {
            String result = null;

            try {
                result = (String) unlessCondition.get(target);
                if ("".equals(result)) {
                    result = null;
                }
            } catch (final Exception e) {
                log.error("Caugh exception, ignoring unless condition", e);
            }

            return result;
        }

    }

    /**
     * Helper interface to access the <em>if</em> and <em>unless</em>
     * conditions of targets.
     *
     * @author Christophe Labouisse
     */
    private static interface TargetConditionHelper {
        /**
         * Returns the <em>if condition</em> for a specific target.
         *
         * @param target
         * @return the <em>if condition</em> or <code>null</code> if none
         *         defined.
         */
        String getIfCondition(final Target target);

        /**
         * Returns the <em>unless condition</em> for a specific target.
         *
         * @param target
         * @return the <em>unless condition</em> or <code>null</code> if
         *         none defined.
         */
        String getUnlessCondition(final Target target);
    }

    /**
     * Factory class creating TargetConditionHelper objects.
     *
     * @author Christophe Labouisse
     */
    private static class TargetConditionHelperFactory {

        /**
         * Creates a new TargetConditionHelper.
         *
         * @return the best help available.
         */
        public static TargetConditionHelper getTargetConditionHelper() {
            TargetConditionHelper result;

            try {
                result = new GetterConditionHelper();
                log.debug("Using ant 1.6.2 getter");
            } catch (final Exception e) {
                log.debug("Cannot create GetterConditionHelper, trying next one");
                result = null;
            }

            if (result == null) {
                try {
                    result = new ReflectHelper();
                    log.debug("Using ReflectHelper");
                } catch (final Exception e) {
                    log.debug("Cannot create ReflectHelper, trying next one");
                    result = null;
                }
            }

            if (result == null) {
                result = new NullConditionHelper();
            }

            return result;
        }

        /**
         * Constructor for TargetConditionHelperFactory.
         */
        private TargetConditionHelperFactory() {
        }
    }

    /**
     * Field antProject.
     */
    private org.apache.tools.ant.Project antProject;

    /**
     * Field targetConditionHelper.
     */
    private final TargetConditionHelper targetConditionHelper = TargetConditionHelperFactory
            .getTargetConditionHelper();

    /**
     * Field targetExplorer.
     */
    private final TargetTasksExplorer targetExplorer = new TargetTasksExplorer(this);

    /**
     * Field taskLinkFinder.
     */
    private LinkFinderVisitor taskLinkFinder;

    /**
     * Creates a new project from an ant build file.
     *
     * The source object can be anything supported by {@link ProjectHelper}
     * which is at least a File.
     *
     * @param source
     *            The source for XML configuration.
     * @see ProjectHelper#parse(org.apache.tools.ant.Project, java.lang.Object)
     * @throws GrandException
     *             if the project cannot be loaded.
     */
    public AntProject(final File source) throws GrandException {
        this(source, null);
    }

    /**
     * Creates a new project from an ant build file.
     *
     * The source object can be anything supported by {@link ProjectHelper}
     * which is at least a File.
     *
     * @param source
     *            The source for XML configuration.
     * @param properties
     *            a set of properties to be preset when opening the graph or
     *            <code>null</code> if no properties should be preset.
     * @see ProjectHelper#parse(org.apache.tools.ant.Project, java.lang.Object)
     * @throws GrandException
     *             if the project cannot be loaded.
     */
    public AntProject(final File source, final Properties properties) throws GrandException {
        log.info("Parsing from " + source);
        antProject = new Project();
        if (properties != null) {
            for (final Map.Entry<Object,Object> element : properties.entrySet()) {
                antProject.setProperty((String) element.getKey(), (String) element.getValue());
            }
        }
        antProject.setSystemProperties();
        antProject.init();
        antProject.setUserProperty("ant.file", source.getAbsolutePath());

        postInit();

        try {
            final ProjectHelper loader = ProjectHelper.getProjectHelper();
            antProject.addReference("ant.projectHelper", loader);
            loader.parse(antProject, source);
            log.debug("Done parsing");
        } catch (final BuildException e) {
            final String message = "Cannot open project file " + source;
            log.error(message, e);
            // TODO Better rethrowing?
            throw new GrandException(message, e);
        }
    }

    /**
     * Perform common operations after the object building.
     */
    private void postInit() {
        taskLinkFinder = new LinkFinderVisitor(this);

        // Change the component helper to instanciate SubAntHelper for subant
        // task.
        final ComponentHelper helper = (ComponentHelper) antProject
                .getReference("ant.ComponentHelper");
        if (helper != null) {
            final AntTypeDefinition subAntDef = helper.getDefinition("subant");
            if (subAntDef == null) {
                log
                        .warn("No definition found for the subant task in ComponentHelper, disabling subant");
            }
            else {
                subAntDef.setClass(SubAntHelper.class);
            }
            final AntTypeDefinition taskDefDef = helper.getDefinition("taskdef");
            if (taskDefDef == null) {
                log
                        .warn("No definition found for the taskdef task in ComponentHelper, some file may not load properly");
            }
            else {
                taskDefDef.setClass(TaskDefHelper.class);
            }
        }
        else {
            log.warn("No component helper in current project");
        }
    }

    /**
     * Creates a new project from an existing ant project.
     *
     * @param project
     *            project to create the graph from.
     */
    public AntProject(final Project project) {
        antProject = project;
        postInit();
    }

    /**
     * Returns the underlying ant project.
     *
     * @return underlying ant project.
     */
    public Project getAntProject() {
        return antProject;
    }

    /**
     * Convert an Ant project to a Grand Graph.
     *
     * The conversion is done in several steps:
     *
     * <ol>
     * <li>Each ant target will be converted to a Grand Node using both the
     * target's name and description the the node's ones. Targets with a non
     * empty description will be converted to Nodes with the
     * {@link Node#ATTR_MAIN_NODE}attribute set. If the project element has a
     * valid default target, the corresponding node will be the graph start
     * target.</li>
     * <li><code>depends</code> attributes on targets will be translated into
     * links. <code>antcall</code> s or the contributed <code>foreach</code>
     * task will be translated in links with the
     * {@link net.ggtools.grand.graph.Link#ATTR_WEAK_LINK}set.</li>
     * </ol>
     *
     * @return a graph representing the dependency of the ant project.
     * @throws GrandException
     *             if the project cannot be converted to a graph.
     * @see GraphProducer#getGraph()
     */
    public Graph getGraph() throws GrandException {
        log.debug("Triggering AntProject");

        final AntGraph graph = new AntGraph(antProject);

        // First pass, create the nodes.
        for (final Target target : antProject.getTargets().values()) {
            if (target.getName().equals("")) {
                continue;
            }

            final String targetName = target.getName();
            final AntTargetNode node = (AntTargetNode) graph.createNode(targetName);

            // Mark nodes with a description as MAIN.
            final String targetDescription = target.getDescription();
            if ((targetDescription != null) && (!targetDescription.equals(""))) {
                node.setAttributes(Node.ATTR_MAIN_NODE);
                node.setDescription(targetDescription);
            }

            node.setIfCondition(targetConditionHelper.getIfCondition(target));
            node.setUnlessCondition(targetConditionHelper.getUnlessCondition(target));
            targetExplorer.exploreTarget(node, target);
        }

        // Sets the start node if needed.
        final String defaultTarget = antProject.getDefaultTarget();
        if (defaultTarget != null) {
            final Node startNode = graph.getNode(defaultTarget);

            if (startNode != null) {
                graph.setStartNode(startNode);
            }
        }

        // Second pass, create the links
        for (final Target target : antProject.getTargets().values()) {
            if (target.getName().equals("")) {
                continue;
            }

            final Enumeration<String> deps = target.getDependencies();
            final String startNodeName = target.getName();
            final AntTargetNode startNode = (AntTargetNode) graph.getNode(startNodeName);

            while (deps.hasMoreElements()) {
                createLink(graph, null, startNode, deps.nextElement());
            }

            taskLinkFinder.setGraph(graph);
            taskLinkFinder.setStartNode(startNode);
            final Task[] tasks = target.getTasks();
            for (final Task element : tasks) {
                taskLinkFinder.visit(element.getRuntimeConfigurableWrapper());
            }
        }

        return graph;
    }

    /**
     * Creates a new link. The end node will be created if needed with the
     * MISSING_NODE attribute set.
     *
     * @param graph
     *            owning graph
     * @param linkName
     *            name of the created link, can be <code>null</code>.
     * @param startNode
     *            start node of the link.
     * @param endNodeName
     *            name of the end node.
     * @return a new link.
     * @throws DuplicateElementException
     *             if there is already a node name <code>endNodeName</code> in
     *             the graph.
     */
    private AntLink createLink(final Graph graph, final String linkName, final Node startNode,
            final String endNodeName) throws DuplicateElementException {
        Node endNode = graph.getNode(endNodeName);

        if (endNode == null) {
            log.warn("Target " + startNode + " has dependency to non existent target "
                    + endNodeName + ", creating a dummy node");
            endNode = graph.createNode(endNodeName);
            endNode.setAttributes(Node.ATTR_MISSING_NODE);
        }

        log.debug("Creating link from " + startNode + " to " + endNodeName);
        return (AntLink) graph.createLink(linkName, startNode, endNode);
    }
}