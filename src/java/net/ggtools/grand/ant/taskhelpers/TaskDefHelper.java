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

import org.apache.tools.ant.taskdefs.Taskdef;

/**
 * Task helper forcing <code>taskdef</code> task to ignore class path
 * problems. This works by setting error to <i>report </i> and overriding the
 * {@link org.apache.tools.ant.taskdefs.Definer#setOnError(org.apache.tools.ant.taskdefs.Definer.OnError)}
 * not to change anything.
 * 
 * This is a crude hack for ticket #79 and there is probably a cleaner way to do
 * it.
 * 
 * @author Christophe Labouisse
 */
public class TaskDefHelper extends Taskdef {
    /**
     * 
     */
    public TaskDefHelper() {
        super();
        super.setOnError(new OnError("report"));
    }

    /*
     * (non-Javadoc)
     * @see org.apache.tools.ant.taskdefs.Definer#setOnError(org.apache.tools.ant.taskdefs.Definer.OnError)
     */
    public void setOnError(OnError onError) {
        // Do nothing.
    }
}