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

package net.ggtools.grand.exceptions;

/**
 * Base class for all grand exceptions.
 * 
 * @author Christophe Labouisse
 */
public class GrandException extends Exception {

    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = -6883312368091628491L;

    /**
     * Creates an new "empty" exception.
     */
    public GrandException() {
        super();
    }

    /**
     * Creates an new exception.
     * 
     * @param message explainatory message of the exception cause.
     */
    public GrandException(final String message) {
        super(message);
    }

    /**
     * Creates an new exception.
     * 
     * @param cause root cause
     */
    public GrandException(final Throwable cause) {
        super(cause);
    }

    /**
     * Creates an new exception.
     * 
     * @param message explainatory message of the exception cause.
     * @param cause root cause
     */
    public GrandException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
