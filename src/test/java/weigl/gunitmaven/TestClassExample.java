package weigl.gunitmaven;

import java.io.File;

import org.junit.Test;

public class TestClassExample {
    public static void main(String args[]) {
        File outputDir = new File("/tmp/");
        GUnitProcessor gUnitProcessor = new GUnitProcessor(outputDir,
                new File("example.xml"));
        gUnitProcessor.run();
    }
}
