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

import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.ggtools.grand.Log;
import net.ggtools.grand.ant.AntTargetNode.SourceElement;

import org.apache.tools.ant.RuntimeConfigurable;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.Task;

/**
 * A class to recursively explore the tasks of a target.
 * 
 * @author Christophe Labouisse
 */
class TargetTasksExplorer {
    private final AntProject antProject;

    private List textElements;

    /**
     * Creates a new TargetTasksExplorer instance for a specific project.
     * 
     * @param antProject
     *            associated project.
     */
    TargetTasksExplorer(final AntProject antProject) {
        this.antProject = antProject;
    }

    /**
     * Start exploring an ant target.
     * 
     * @param target
     */
    public void exploreTarget(final AntTargetNode node, final Target target) {
        Log.log("Exploring target " + target.getName(), Log.MSG_DEBUG);
        textElements = new LinkedList();
        addText("<target name=\"", AntTargetNode.SOURCE_MARKUP);
        addText(target.getName(), AntTargetNode.SOURCE_ATTRIBUTE);
        addText("\"", AntTargetNode.SOURCE_MARKUP);

        if (node.getIfCondition() != null) {
            addText(" if=\"", AntTargetNode.SOURCE_MARKUP);
            addText(node.getIfCondition(), AntTargetNode.SOURCE_ATTRIBUTE);
            addText("\"", AntTargetNode.SOURCE_MARKUP);
        }

        if (node.getUnlessCondition() != null) {
            addText(" if=\"", AntTargetNode.SOURCE_MARKUP);
            addText(node.getUnlessCondition(), AntTargetNode.SOURCE_ATTRIBUTE);
            addText("\"", AntTargetNode.SOURCE_MARKUP);
        }

        final Enumeration dependencies = target.getDependencies();
        if (dependencies.hasMoreElements()) {
            addText(" depends=\"", AntTargetNode.SOURCE_MARKUP);
            while (dependencies.hasMoreElements()) {
                String dependency = (String) dependencies.nextElement();
                addText(dependency, AntTargetNode.SOURCE_ATTRIBUTE);
                if (dependencies.hasMoreElements()) addText(", ", AntTargetNode.SOURCE_ATTRIBUTE);
            }
            addText("\"", AntTargetNode.SOURCE_MARKUP);
        }

        if (target.getDescription() != null) {
            addText(" description=\"", AntTargetNode.SOURCE_MARKUP);
            addText(target.getDescription(), AntTargetNode.SOURCE_ATTRIBUTE);
            addText("\"", AntTargetNode.SOURCE_MARKUP);
        }

        final Task[] taskArray = target.getTasks();
        boolean hasChildren = taskArray.length > 0;

        if (hasChildren) {
            addText(">\n", AntTargetNode.SOURCE_MARKUP);
        }
        else {
            addText(" />\n", AntTargetNode.SOURCE_MARKUP);
        }

        for (int i = 0; i < taskArray.length; i++) {
            final Task task = taskArray[i];
            exploreTask(task.getRuntimeConfigurableWrapper(), 1);
        }

        if (hasChildren) addText("</target>", AntTargetNode.SOURCE_MARKUP);

        // Purge extraneous elements
        AntTargetNode.SourceElement previous = null;
        for (Iterator iter = textElements.iterator(); iter.hasNext();) {
            AntTargetNode.SourceElement element = (AntTargetNode.SourceElement) iter.next();
            if (previous == null) {
                previous = element;
            }
            else {
                if (previous.getStyle() == element.getStyle()) {
                    previous.setText(previous.getText().concat(element.getText()));
                    iter.remove();
                }
                else {
                    previous = element;
                }
            }
        }
        node.setRichSource((SourceElement[]) textElements.toArray(new SourceElement[0]));

        final StringBuffer buffer = new StringBuffer();
        for (Iterator iter = textElements.iterator(); iter.hasNext();) {
            AntTargetNode.SourceElement element = (AntTargetNode.SourceElement) iter.next();
            buffer.append(element.getText());
        }
        node.setSource(buffer.toString());
    }

    public void exploreTask(final RuntimeConfigurable wrapper, final int level) {
        indent(level);
        addText("<", AntTargetNode.SOURCE_MARKUP);
        addText(wrapper.getElementTag(), AntTargetNode.SOURCE_MARKUP);
        final Map attributes = wrapper.getAttributeMap();
        for (Iterator iter = attributes.entrySet().iterator(); iter.hasNext();) {
            Map.Entry entry = (Map.Entry) iter.next();
            addText(" ", AntTargetNode.SOURCE_MARKUP);
            addText((String) entry.getKey(), AntTargetNode.SOURCE_MARKUP);
            addText("=\"", AntTargetNode.SOURCE_MARKUP);
            addText((String) entry.getValue(), AntTargetNode.SOURCE_ATTRIBUTE);
            addText("\"", AntTargetNode.SOURCE_MARKUP);
        }

        final Enumeration children = wrapper.getChildren();
        final String trimmedText = wrapper.getText().toString().trim();
        final boolean hasText = !"".equals(trimmedText);
        final boolean hasChildren = children.hasMoreElements();
        boolean hasNestedElements = hasChildren || hasText;

        // TODO process text elements.
        if (hasNestedElements) {
            addText(">", AntTargetNode.SOURCE_MARKUP);
            if (hasChildren) {
                addText("\n", AntTargetNode.SOURCE_MARKUP);
            }
        }
        else {
            addText(" />\n", AntTargetNode.SOURCE_MARKUP);
        }

        while (children.hasMoreElements()) {
            RuntimeConfigurable childWrapper = (RuntimeConfigurable) children.nextElement();
            exploreTask(childWrapper, level + 1);
        }

        if (hasText) {
            addText(trimmedText, AntTargetNode.SOURCE_TEXT);
        }

        if (hasNestedElements) {
            if (!hasText) {
                indent(level);
            }
            addText("</", AntTargetNode.SOURCE_MARKUP);
            addText(wrapper.getElementTag(), AntTargetNode.SOURCE_MARKUP);
            addText(">\n", AntTargetNode.SOURCE_MARKUP);
        }
    }

    private void addText(final String text, final int style) {
        textElements.add(new AntTargetNode.SourceElement(text, style));
    }

    private void indent(final int level) {
        final StringBuffer buffer = new StringBuffer(level * 4);
        for (int i = 0; i < level; i++) {
            buffer.append("   ");
        }
        addText(buffer.toString(), AntTargetNode.SOURCE_TEXT);
    }
}