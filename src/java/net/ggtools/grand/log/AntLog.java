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
package net.ggtools.grand.log;

import org.apache.commons.logging.Log;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;

/**
 * @author Christophe Labouisse
 */
public class AntLog extends SimpleLog implements Log {

    /**
     * Field currentProject.
     */
    private static Project currentProject;

    /**
     * Field currentTask.
     */
    private static Task currentTask;

    /**
     * Method setCurrentProject.
     * @param newProject Project
     */
    public static void setCurrentProject(final Project newProject) {
        currentProject = newProject;
    }

    /**
     * Method setCurrentTask.
     * @param newTask Task
     */
    public static void setCurrentTask(final Task newTask) {
        currentTask = newTask;
    }

    /**
     * Package only instanciation.
     */
    AntLog() {
    }

    /**
     * Method log.
     * @param message Object
     * @param t Throwable
     * @param level int
     * @see net.ggtools.grand.log.SimpleLog#log(java.lang.Object, java.lang.Throwable, int)
     */
    @Override
    protected final void log(final Object message, final Throwable t,
            final int level) {
        if (currentProject != null) {
            // Translate into ant log levels.
            int antMsgLevel = level - LEVEL_ERROR + Project.MSG_ERR;
            if (antMsgLevel < Project.MSG_ERR) {
                antMsgLevel = Project.MSG_ERR;
            } else if (antMsgLevel > Project.MSG_DEBUG) {
                antMsgLevel = Project.MSG_DEBUG;
            }

            if (currentTask == null) {
                currentProject.log(message.toString(), antMsgLevel);
            } else {
                currentProject.log(currentTask, message.toString(),
                        antMsgLevel);
            }
        } else {
            super.log(message, t, level);
        }
    }

}
