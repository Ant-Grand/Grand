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

import java.io.File;
import java.io.IOException;

import net.ggtools.grand.ant.AntProject;
import net.ggtools.grand.exceptions.GrandException;
import net.ggtools.grand.filters.IsolatedNodeFilter;
import net.ggtools.grand.output.DotWriter;

/**
 * A simple application to convert an ant build file to a dot graph. This is
 * more a test application than anything else, so use with care.
 * 
 * @author Christophe Labouisse
 */
public final class App {

    /**
     * Starts the application
     * @param args command line arguments.
     */
    public static void main(final String[] args) {
        if (args.length < 2) {
            printUsage();
            return;
        }

        Log.setLogLevel(Log.MSG_DEBUG);
        Log.log("Start conversion of " + args[0]);
        App appli = new App(args[0]);

        try {
            appli.run(args[1]);
            System.err.println("Conversion done");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *  
     */
    private static void printUsage() {
        System.err.println("Usage: dependgraph input.xml output.dot");
    }

    private File buildFile;

    /**
     * Creates a new App
     * 
     * @param file to graph
     */
    private App(final String file) {
        buildFile = new File(file);
    }

    /**
     * @param output filename to output.
     * 
     * @throws IOException if a problem happened on file IO.
     * @throws GrandException if Grand cannot process the file.
     */
    private void run(final String output) throws IOException, GrandException {
        GraphProducer producer = new AntProject(buildFile);
        GraphWriter writer = new DotWriter();
        GraphFilter filter = new IsolatedNodeFilter();
        filter.setProducer(producer);
        writer.setProducer(filter);
        writer.write(new File(output));
    }
}
