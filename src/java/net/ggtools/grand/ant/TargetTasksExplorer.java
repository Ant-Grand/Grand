//$Id$
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
import java.util.Map;

import net.ggtools.grand.Log;

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
    
    /**
     * Creates a new TargetTasksExplorer instance for a specific project.
     * 
     * @param antProject associated project.
     */
    TargetTasksExplorer(final AntProject antProject) {
        this.antProject = antProject;
    }
    
    /**
     * Start exploring an ant target.
     * 
     * @param target
     */
    public void exploreTarget(final Target target, TaskVisitor vistor) {
        Log.log("Exploring target "+target.getName(),Log.MSG_DEBUG);
        System.err.println("Exploring target "+target.getName()); // TODO remove
        final Task[] taskArray = target.getTasks();
        for (int i = 0; i < taskArray.length; i++) {
            final Task task = taskArray[i];
            exploreTask(task.getRuntimeConfigurableWrapper());
        }
    }
    
    public void exploreTask(final RuntimeConfigurable wrapper) {
        System.err.println("Got "+wrapper.getElementTag());
        final Map attributes = wrapper.getAttributeMap();
        for (Iterator iter = attributes.entrySet().iterator(); iter.hasNext();) {
            Map.Entry entry = (Map.Entry) iter.next();
            System.err.println("\t"+entry.getKey()+" -> "+entry.getValue());
        }
        final Enumeration children = wrapper.getChildren();
        while (children.hasMoreElements()) {
            RuntimeConfigurable childWrapper = (RuntimeConfigurable) children.nextElement();
            exploreTask(childWrapper);
        }
    }
}
