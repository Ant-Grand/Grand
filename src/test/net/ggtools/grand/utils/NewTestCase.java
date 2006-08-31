package net.ggtools.grand.utils;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import junit.framework.AssertionFailedError;

import org.junit.Test;

public class NewTestCase {
    private static final File BUILD_SIMPLE = new File("src/etc/testcases/build-simple.dot");

    private static final File BUILD_IMPORT = new File("src/etc/testcases/build-import.dot");

    private static final File OVERRIDE = new File("src/etc/testcases/override.dot");

    @Test
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

    @Test
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
            assertTrue("build-simple and build-import do not have the same size", e.getMessage()
                    .indexOf("Sizes do not match") >= 0);
        }
    }

}
