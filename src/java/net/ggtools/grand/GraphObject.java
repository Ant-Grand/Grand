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


/**
 * Base for objects populating graphs. GraphObjects have three
 * main traits: a name, a owner graph and attributes. Attributes
 * should be implemented by setting or clearing a bit(s) in an
 * <code>int</code>. It is recommanded that implementing classes
 * or children interfaces define constants to represent attributes.
 * 
 * @author Christophe Labouisse
 */
public interface GraphObject {
    
    /**
     * An attribute mask representing all attributes.
     */
    int ATTR_ALL = -1;
    
    /**
     * An attribute mask representing no attribute.
     */
    int ATTR_NONE = 0;
    
    /**
     * Gets the owner graph of the object.
     * @return
     */
    Graph getGraph();
    
    /**
     * Gets the object's name.
     * @return
     */
    String getName();

    /**
     * Sets one or more attributes of the object. Multiple attributes
     * should be combined by <i>oring</i> individual attributes:
     * 
     * <code>setAttributes(ATTR_ONE | ATTR_TWO);</code>
     * 
     * @param attributeMask a bit mask of attributes to set.
     */
    void setAttributes(int attributeMask);
    
    /**
     * Sets one or more attributes of the object. Multiple attributes
     * should be combined by <i>oring</i> individual attributes:
     * 
     * <code>clearAttributes(ATTR_ONE | ATTR_TWO);</code>
     * 
     * @param attributeMask a bit mask of attributes to clean.
     */
    void clearAttributes(int attributeMask);
    
    /**
     * Returns true if all the attributes specified by the
     * bit mask are set.
     * 
     * @param attributeMask a bit mask of attributes to test.
     * @return true if all attributes are set.
     */
    boolean hasAttributes(int attributeMask);
}
