package weigl.gunitmaven;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.DirectoryWalker;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;


@Mojo(name = "gunit",
        defaultPhase = LifecyclePhase.GENERATE_TEST_SOURCES,
        requiresDependencyResolution = ResolutionScope.COMPILE,
        requiresProject = true)

public class GUnitMojo
        extends AbstractMojo {

    @Parameter(defaultValue = "${project.build.directory}/generated-test-sources/gunit")
    private File outputDirectory;

    @Parameter(defaultValue = "${basedir}/src/main/gunit/")
    private File inputDirectory;

    @Parameter(property = "project", required = true, readonly = true)
    protected MavenProject project;


    public void execute() throws MojoExecutionException {
        FilenameFilter filnmfilter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".gunit.xml");
            }
        };

        Log log = getLog();

        File outputDir = getOutputDirectory();

        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        if (!inputDirectory.exists()) {
            inputDirectory.mkdirs();
        }

        log.debug("Input File Dir: " + inputDirectory);

        File[] files = inputDirectory.listFiles(filnmfilter);

        log.info("Found files:" + Arrays.toString(files));
        log.info("Write to:" + outputDir);

        GUnitProcessor gUnitProcessor = new GUnitProcessor(outputDir, log, files);
        gUnitProcessor.run();

        getProject().addTestCompileSourceRoot(outputDir.getAbsolutePath());
    }


    public File getOutputDirectory() {
        return outputDirectory;
    }

    public void setOutputDirectory(File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    public File getInputDirectory() {
        return inputDirectory;
    }

    public void setInputDirectory(File inputDirectory) {
        this.inputDirectory = inputDirectory;
    }

    public MavenProject getProject() {
        return project;
    }

    public void setProject(MavenProject project) {
        this.project = project;
    }

}
