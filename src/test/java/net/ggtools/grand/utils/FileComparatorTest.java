// $Id$
/*
 * ====================================================================
 * Copyright (c) 2002-2003, Christophe Labouisse All rights reserved.
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

package net.ggtools.grand.utils;

import static net.ggtools.grand.utils.AbstractAntTester.TESTCASES_DIR;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

/**
 * Test case for the FileComparator class.
 *
 * @author Christophe Labouisse
 */
public class FileComparatorTest {

    /**
     * Field BUILD_SIMPLE.
     */
    private static final File BUILD_SIMPLE =
            new File(TESTCASES_DIR, "build-simple.dot");

    /**
     * Field BUILD_IMPORT.
     */
    private static final File BUILD_IMPORT =
            new File(TESTCASES_DIR, "build-import.dot");

    /**
     * Field OVERRIDE.
     */
    private static final File OVERRIDE =
            new File(TESTCASES_DIR, "override.dot");

    /**
     * Method testAssertSelfSizesMatch.
     */
    @Test
    public final void testAssertSelfSizesMatch() {
        // Same file
        FileComparator comparator = new FileComparator(BUILD_SIMPLE, BUILD_SIMPLE);
        comparator.assertSizesMatch();
    }

    /**
     * Method testAssertOverrideSizesMatch.
     */
    @Test
    public final void testAssertOverrideSizesMatch() {
        // Different file, same size.
        FileComparator comparator = new FileComparator(BUILD_SIMPLE, OVERRIDE);
        comparator.assertSizesMatch();
    }

    /**
     * Method testAssertDifferentSizesMatch.
     */
    @Test(expected = AssertionError.class)
    public final void testAssertDifferentSizesMatch() {
        // Different files.
        FileComparator comparator = new FileComparator(BUILD_SIMPLE, BUILD_IMPORT);
        comparator.assertSizesMatch();
    }

    /**
     * Method testAssertSelfLinesMatch.
     * @throws IOException if comparator fails
     */
    @Test
    public final void testAssertSelfLinesMatch() throws IOException {
        // Same file.
        FileComparator comparator = new FileComparator(BUILD_SIMPLE, BUILD_SIMPLE);
        comparator.assertLinesMatch();
    }

    /**
     * Method testAssertOverrideLinesMatch.
     * @throws IOException if comparator fails
     */
    @Test(expected = AssertionError.class)
    public final void testAssertOverrideLinesMatch() throws IOException {
        // Different files, same size.
        FileComparator comparator = new FileComparator(BUILD_SIMPLE, OVERRIDE);
        comparator.assertLinesMatch();
    }

    /**
     * Method testAssertDifferentLinesMatch.
     * @throws IOException if comparator fails
     */
    @Test(expected = AssertionError.class)
    public final void testAssertDifferentLinesMatch() throws IOException {
        // Different files.
        FileComparator comparator = new FileComparator(BUILD_SIMPLE, BUILD_IMPORT);
        comparator.assertLinesMatch();
    }
}
