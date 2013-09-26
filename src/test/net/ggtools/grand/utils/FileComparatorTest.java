// $Id$
/*
 * ====================================================================
 * Copyright (c) 2002-2003, Christophe Labouisse All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: 1.
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution.
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

package net.ggtools.grand.utils;

import java.io.File;
import java.io.IOException;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

/**
 * Test case for the FileComparator class.
 * 
 * @author Christophe Labouisse
 */
public class FileComparatorTest extends TestCase {

    /**
     * Field BUILD_SIMPLE.
     */
    private static final File BUILD_SIMPLE = new File(
            "src/etc/testcases/build-simple.dot");

    /**
     * Field BUILD_IMPORT.
     */
    private static final File BUILD_IMPORT = new File(
            "src/etc/testcases/build-import.dot");

    /**
     * Field OVERRIDE.
     */
    private static final File OVERRIDE = new File(
            "src/etc/testcases/override.dot");

    /**
     * Constructor for FileComparatorTest.
     * 
     * @param name
     */
    public FileComparatorTest(final String name) {
        super(name);
    }

    /**
     * Method testAssertSizesMatch.
     */
    public final void testAssertSizesMatch() {
        FileComparator comparator;

        // Same file
        comparator = new FileComparator(BUILD_SIMPLE, BUILD_SIMPLE);
        comparator.assertSizesMatch();

        // Different file, same size.
        comparator = new FileComparator(BUILD_SIMPLE, OVERRIDE);
        comparator.assertSizesMatch();

        // Different files.
        comparator = new FileComparator(BUILD_SIMPLE, BUILD_IMPORT);
        try {
            comparator.assertSizesMatch();
            fail("build-simple and build-import do not have the same size");
        } catch (final AssertionFailedError e) {
        }
    }

    /**
     * Method testAssertLinesMatch.
     * @throws IOException
     */
    public final void testAssertLinesMatch() throws IOException {
        FileComparator comparator;

        // Same file.
        comparator = new FileComparator(BUILD_SIMPLE, BUILD_SIMPLE);
        comparator.assertLinesMatch();

        // Different files, same size.
        comparator = new FileComparator(BUILD_SIMPLE, OVERRIDE);
        try {
            comparator.assertLinesMatch();
            fail("build-simple and override should differ");
        } catch (final AssertionFailedError e) {
        }

        // Different files.
        comparator = new FileComparator(BUILD_SIMPLE, BUILD_IMPORT);
        try {
            comparator.assertLinesMatch();
            fail("build-simple and build-import should differ");
        } catch (final AssertionFailedError e) {
            assertTrue("build-simple and build-import do not have the same size",
                    e.getMessage().indexOf("Sizes do not match") >= 0);
        }
    }
}
