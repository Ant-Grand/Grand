package net.ggtools.grand;

import java.io.File;
import java.io.IOException;

import net.ggtools.grand.ant.AntProject;

/**
 * A simple application to convert an ant build file to a dot graph.
 * This is more a test application than anything else, so use with care.
 *
 * @author Christophe Labouisse
 */
public class App 
{

    public static void main( String[] args )
    {
        if (args.length < 2) {
            printUsage();
            return;
        }
        
        System.err.println("Start conversion of "+args[0]);
        App appli = new App(args[0]);
        
        try {
            appli.run(args[1]);
            System.err.println("Conversion done");
        } catch (IOException e) {
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
     * @param string
     */
    private App(String file) {
        buildFile = new File(file);
    }

    
    /**
     * 
     */
    private void run(String output) throws IOException {
        Project project = new AntProject(buildFile);
        GraphWriter writer = new DotWriter(project);
        writer.write(new File(output));
    }
}
