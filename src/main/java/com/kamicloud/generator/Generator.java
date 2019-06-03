package com.kamicloud.generator;

import definitions.annotations.*;
import com.kamicloud.generator.config.ApplicationProperties;
import com.kamicloud.generator.config.DefaultProfileUtil;
import com.kamicloud.generator.stubs.*;
import com.kamicloud.generator.writers.*;
import definitions.types.CustomizeInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import templates.TemplateList;
import com.sun.javadoc.ProgramElementDoc;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.boot.SpringApplication;

import javax.annotation.PostConstruct;
import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;


@SpringBootApplication
@EnableConfigurationProperties({ApplicationProperties.class})
@SuppressWarnings("unchecked")
public class Generator {
    private Environment env;

    @Autowired
    public void setEnv(Environment env) {
        this.env = env;
    }

    private static final Logger log = LoggerFactory.getLogger(Generator.class);

    public static HashMap<String, ModelStub> modelHashMap = new HashMap<>();
    public static ArrayList<BaseWithAnnotationStub> classHashMap = new ArrayList<>();

    @Autowired
    protected PostmanWriter postmanWriter;
    @Autowired
    protected LaravelWriter laravelWriter;
    @Autowired
    protected TestCaseWriter testCaseWriter;
    @Autowired
    protected DocWriter docWriter;
    @Autowired
    protected AutoTestWriter autoTestWriter;
    @Autowired
    protected NodeJsClientWriter nodeJsClientWriter;

    @Autowired
    public void setPostmanWriter(PostmanWriter postmanWriter) {
        this.postmanWriter = postmanWriter;
    }

    protected Parser parser;

    @Autowired
    public void setParser(Parser parser) {
        this.parser = parser;
    }

    @PostConstruct
    public void initApplication() {
        DefaultProfileUtil.setEnv(env);
        OutputStub output = this.parser.parse();
        getComments();
        syncComments();
        syncModels();

        String process = env.getProperty("process", "code");

        try {
            output.setActionUrl();
            if (process.equals("code")) {
                output.addObserver(postmanWriter);
                output.addObserver(testCaseWriter);
                output.addObserver(docWriter);
                output.addObserver(laravelWriter);
            } else if (process.equals("client")) {
                output.addObserver(nodeJsClientWriter);
            } else {
                output.addObserver(autoTestWriter);
            }
            output.notifyObservers();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(Generator.class);
        DefaultProfileUtil.addDefaultProfile(app);
        app.run(args);
    }

    public void getComments() {
        String codePath = env.getProperty("generator.template-path", "./src/main/java/templates");
        File templateDir = new File(codePath + "/templates");
        File[] templateFiles = templateDir.listFiles();

        if (templateFiles == null) {
            return;
        }
        Arrays.asList(templateFiles).forEach(templateFile -> {
            if (!templateFile.getName().contains(".java")) {
                return;
            }
            com.sun.tools.javadoc.Main.execute(new String[]{
//                "-verbose",
                "-package",
                "-doclet", "com.kamicloud.generator.DocParser",
                "-encoding", "utf-8",
                templateFile.getAbsolutePath()
            });
        });

    }

    public static void syncComments() {
        classHashMap.forEach((commentInterface) -> {
            ProgramElementDoc programElementDoc = DocParser.classDocHashMap.get(commentInterface.getClasspath());

            if (programElementDoc != null && !programElementDoc.commentText().isEmpty()) {
                commentInterface.setComment(programElementDoc.commentText());
            }
        });
    }

    private static void syncModels() {
        modelHashMap.forEach((s, modelStub) -> {
            modelStub.setParent(modelHashMap.get(modelStub.getParentKey()));
        });
    }
}
