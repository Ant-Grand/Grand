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

package net.ggtools.grand.utils;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Utility class to compare two files.
 *
 * @author Christophe Labouisse
 */
public class FileComparator {
    /**
     * Field source.
     */
    private final File source;

    /**
     * Field dest.
     */
    private final File dest;

    /**
     * Creates a new file comparator.
     *
     * @param source File
     * @param dest File
     */
    public FileComparator(final File source, final File dest) {
        this.source = source;
        this.dest = dest;
    }

    /**
     * Asserts that both files have the same length.
     */
    public final void assertSizesMatch() {
        assertEquals("Sizes do not match", source.length(), dest.length());
    }

    /**
     * Asserts that both files match line for line. This method also assert
     * the both files have the same length.
     *
     * @throws IOException
     */
    public final void assertLinesMatch() throws IOException {
        assertSizesMatch();

        final BufferedReader sourceReader = new BufferedReader(new FileReader(source));
        final BufferedReader destReader = new BufferedReader(new FileReader(dest));

        int line = 0;

        while (true) {
            final String srcLine = sourceReader.readLine();
            final String dstLine = destReader.readLine();
            line++;

            // End reached, files match.
            if ((srcLine == null) && (dstLine == null)) {
                break;
            }

            // Since both files have the same length and are identical
            // so far, it should not happen.
            assert ((srcLine != null) && (dstLine != null));

            assertEquals("Files differ on line " + line, srcLine, dstLine);
        }
        sourceReader.close();
        destReader.close();
    }

}
