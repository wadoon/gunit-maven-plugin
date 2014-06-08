package weigl.gunitmaven;

import org.apache.maven.plugin.logging.Log;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.stringtemplate.v4.*;

/**
 * Created by weigl on 08.06.14.
 */

public class GUnitProcessor {
    private final File[] inputFiles;
    private final File outputDir;
    private final Log log;
    private final STGroupFile g;

    public GUnitProcessor(File outputDir, Log log, File... inputFiles) {
        outputDir.mkdirs();
        this.log = log;
        this.outputDir = outputDir;
        this.inputFiles = inputFiles;


        final URL url = getClass().getResource("/TestCaseTemplate.stg");
        STGroup.trackCreationEvents = true;
        g = new STGroupFile(url, "utf-8", '<', '>');

        log.debug("GUNIT: Templates loaded");
    }

    public void run() {
        for (File f : inputFiles) {
            try {
                processFile(f);
            } catch (JDOMException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void processFile(File file) throws JDOMException, IOException {
        SAXBuilder builder = new SAXBuilder();
        Document document = (Document) builder.build(file);
        Element rootNode = document.getRootElement();

        String pkg = rootNode.getAttributeValue("package");
        String lexer = rootNode.getAttributeValue("lexer");
        String parserc = rootNode.getAttributeValue("parser");
        String name = file.getName().replace(".gunit.xml", "");


        List<GUnitTestCase> list = new ArrayList<>();
        for (Element parser : rootNode.getChildren("parser")) {
            for (Element c : parser.getChildren("cases")) {
                renderCases(list, c);
            }

            for (Element c : parser.getChildren("case")) {
                renderCase(list, c);
            }
        }

        log.debug("GUNIT: File "+file+" has "+list.size() + " @Tests");

        ST testCase = g.getInstanceOf("testCase");

        testCase.add("package", pkg);
        testCase.add("lexerclass", lexer);
        testCase.add("parserclass", parserc);
        testCase.add("testcasename", name);
        testCase.add("testcases", list);

//        testCase.inspect();
        String path = pkg.replace('.', '/');
        File output = new File(outputDir, path);

        if (!output.exists()) {
            output.mkdirs();
        }

        output = new File(output, name + ".java");
        String content = testCase.render();

        log.debug("GUNIT: Write file: " + output);
        FileWriter fw = new FileWriter(output);
        fw.write(content);
        fw.close();


    }

    public void renderCases(List<GUnitTestCase> list, Element c) {
        String rule = c.getAttributeValue("rule");
        String text = c.getTextTrim();
        String expect = c.getAttributeValue("expect", "ok");

        for (String s : text.split("\n")) {
            GUnitTestCase tc = createTestCase(rule, s, expect);
            list.add(tc);
        }
    }

    public void renderCase(List<GUnitTestCase> list, Element c) {
        String rule = c.getAttributeValue("rule");
        String text = c.getTextTrim();
        String expect = c.getAttributeValue("expect", "ok");

        GUnitTestCase tc = createTestCase(rule, text, expect);
        list.add(tc);
    }

    private GUnitTestCase createTestCase(String rule, String text, String expect) {
        GUnitTestCase tc = new GUnitTestCase();
        tc.rule = rule;
        tc.expect = "ok".equals(expect);
        tc.input = text.replaceAll("\n", "\\\\n").trim(); //escape newlines
        return tc;
    }
}

class GUnitTestCase {
    public
    static int ID_COUNTER = 0;
    public String id = "_" + ID_COUNTER++;
    public boolean expect;
    public String rule = "n/a";
    public String input = "n/a";
}