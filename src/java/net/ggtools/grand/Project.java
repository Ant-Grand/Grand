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

package net.ggtools.grand;

import java.util.Iterator;

/**
 * Interface for projects.
 * 
 * @author Christophe Labouisse
 */
public interface Project {
    
    /**
     * Returns the project's name.
     * @return name
     */
    String getName();
    
    /**
     * Returns the nodes contained in the project. The nodes can be accessed
     * throught an Iterator. This Iterator won't have to implement the
     * <code>remove</code> method.
     * 
     * @return iterator to the nodes.
     */
    Iterator getNodes();
    
    /**
     * Returns the project's first node. The definition of the <i>first node</i> depends
     * on the system. In Ant it'll be the default target.
     * 
     * @return the first/default node or null if no such node exists.
     */
    Node getStartNode();

}
