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
 * Exception raised when trying to create two elements with the same key in
 * a container. For instance two nodes with the same name in one graph.
 *
 * @author Christophe Labouisse
 */
public class DuplicateElementException extends GrandException {

    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = -6223346009226211821L;

    /**
     * Creates a new exception.
     */
    public DuplicateElementException() {
        super();
    }

    /**
     * Creates a new exception.
     *
     * @param message explanatory message of the exception cause.
     */
    public DuplicateElementException(final String message) {
        super(message);
    }

    /**
     * Creates a new exception.
     *
     * @param message explanatory message of the exception cause.
     * @param cause root cause.
     */
    public DuplicateElementException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a new exception.
     *
     * @param cause root cause
     */
    public DuplicateElementException(final Throwable cause) {
        super(cause);
    }
}
