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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import net.ggtools.grand.Log;
import net.ggtools.grand.exceptions.GrandException;

import org.apache.tools.ant.RuntimeConfigurable;

/**
 * A base class using reflectivity in order to invoke a method depending on the
 * visited wrapper.
 * 
 * @author Christophe Labouisse
 */
abstract class ReflectTaskVisitorBase implements TaskVisitor {

    private static final Class[] METHOD_PARAMETER_TYPES = new Class[]{RuntimeConfigurable.class};

    private Map methodCache = new HashMap();

    /**
     * Invoke the <em>right</em> method depending on the wrapper's element
     * tag. The algorithm is:
     * <ol>
     * <li>get the wrapper's element tag name,</li>
     * <li>if the cache already contain a method, use it,</li>
     * <li>call {@link #getAliasForTask(String)}to find out if there is an
     * alias for this task,</li>
     * <li>try to get the method called
     * <code>reflectVisit_<em>task_name</em></code>,</li>
     * <li>call the found method.</li>
     * </ol>
     * 
     * If the following algorithm fails then we'll use the
     * {@link #defaultVisit(RuntimeConfigurable)}fallback method.
     * 
     * @param wrapper
     *            Wrapper to visit.
     * @throws GrandException
     *             if something goes wrong.
     * @see net.ggtools.grand.ant.TaskVisitor#visit(org.apache.tools.ant.RuntimeConfigurable)
     */
    public void visit(RuntimeConfigurable wrapper) throws GrandException {
        final String taskName = wrapper.getElementTag();
        Method visitMethod = (Method) methodCache.get(taskName);

        if (visitMethod == null) {
            final String methodName = "reflectVisit_" + getAliasForTask(taskName);
            try {
                visitMethod = getClass().getDeclaredMethod(methodName, METHOD_PARAMETER_TYPES);
                methodCache.put(taskName, visitMethod);
            } catch (SecurityException e) {
                // TODO find a way to dump the stack trace.
                Log.log("Caught Security exception looking for" + methodName + ": "
                        + e.getMessage(), Log.MSG_WARN);
            } catch (NoSuchMethodException e) {
                Log.log("Cannot find method " + methodName, Log.MSG_VERBOSE);
            }
        }

        boolean invokationOk = false;

        if (visitMethod != null) {
            try {
                visitMethod.invoke(this, new Object[]{wrapper});
                invokationOk = true;
            } catch (IllegalAccessException e) {
                // TODO find a way to dump the stack trace.
                Log.log("Caught IllegalAccessException invoking " + visitMethod + ": "
                        + e.getMessage(), Log.MSG_WARN);
            } catch (InvocationTargetException e) {
                // Process the exception raised by the method invokation.
                // GrandException & RuntimeException are propagated.
                final Throwable cause = e.getCause();
                if (cause instanceof GrandException) {
                    throw (GrandException) cause;
                } else if (cause instanceof RuntimeException) {
                    throw (RuntimeException)cause;
                } else {
                    // TODO find a way to dump the stack trace.
                    // TODO That's a real exception what to do with it?
                    Log.log("Caught unexepected exception " + cause + " on " + visitMethod,
                            Log.MSG_ERR);
                }
            }
        }

        // If the reflective invokation hasn't taken place, use the default
        // method.
        if (!invokationOk) {
            defaultVisit(wrapper);
        }
    }

    public abstract void defaultVisit(RuntimeConfigurable wrapper) throws GrandException;

    /**
     * A default implementation returning the task name.
     * 
     * @param taskName
     * @return the name to use when look for the method to invoke. Should not be
     *         <code>null</code>.
     */
    public String getAliasForTask(String taskName) {
        return taskName;
    }
}