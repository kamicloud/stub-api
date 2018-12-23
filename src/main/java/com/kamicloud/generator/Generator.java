package com.kamicloud.generator;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.kamicloud.generator.annotations.Request;
import com.kamicloud.generator.config.ApplicationProperties;
import com.kamicloud.generator.config.DefaultProfileUtil;
import com.kamicloud.generator.stubs.*;
import com.kamicloud.generator.writers.*;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.boot.SpringApplication;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;



@SpringBootApplication
@EnableConfigurationProperties({ApplicationProperties.class})
public class Generator {
    private final Environment env;


    public Generator(Environment env) {
        this.env = env;
    }

    @PostConstruct
    @SuppressWarnings("unused")
    public void initApplication() {
        OutputStub output = this.parse();

        String process = env.getProperty("process", "code");

        try {
            output.setActionUrl();
            if (process.equals("code")) {
                output.addObserver(new PostmanWriter(env));
//                output.addObserver(new TestCaseWriter(env));
                output.addObserver(new DocWriter(env));
                output.addObserver(new LaravelWriter(env));
            } else {
                output.addObserver(new AutoTestWriter(env));
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

    public OutputStub parse() {
        OutputStub output = new OutputStub();
        parseTemplate(output);

        return output;
    }

    private void parseTemplate(OutputStub output) {
        try {
            File dir = new File("");
            TemplateStub templateStub = new TemplateStub("V1");

            ClassOrInterfaceDeclaration template = getClassOrInterfaceByNameFromFile(dir.getAbsolutePath() + "/src/main/java/com/kamicloud/generator/Template.java", "Template");
            template.getMembers().forEach(groupTemplate -> {
                if (groupTemplate instanceof ClassOrInterfaceDeclaration) {
                    ClassOrInterfaceDeclaration group = (ClassOrInterfaceDeclaration) groupTemplate;
                    switch (group.getNameAsString()) {
                        case "Models":
                            parseModels(group, templateStub);
                            break;
                        case "Enums":
                            parseEnums(group, templateStub);
                            break;
                        case "Controllers":
                            parseControllers(group, templateStub);
                            break;
                    }
                }
            });
            EnumDeclaration errors = getEnumDeclarationByNameFromFile(dir.getAbsolutePath() + "/src/main/java/com/kamicloud/generator/Errors.java", "Errors");
            parseErrors(errors, templateStub);

            output.addTemplate(templateStub);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseErrors(EnumDeclaration errorsTemplate, TemplateStub templateStub) {
        NodeList<EnumConstantDeclaration> nodeList = errorsTemplate.getEntries();

        nodeList.forEach(enumConstantDeclaration -> {
            ErrorStub errorStub = new ErrorStub(
                enumConstantDeclaration.getNameAsString(),
                ((IntegerLiteralExpr) enumConstantDeclaration.getArgument(0)).getValue(),
                ((StringLiteralExpr) enumConstantDeclaration.getArgument(1)).getValue()
            );
            parseAnnotations(enumConstantDeclaration.getAnnotations(), errorStub);
            templateStub.addError(errorStub);
        });

    }

    private void parseControllers(ClassOrInterfaceDeclaration controllersTemplate, TemplateStub templateStub) {
        controllersTemplate.getMembers().forEach(controllerTemplate -> {
            ControllerStub controllerStub = new ControllerStub(((ClassOrInterfaceDeclaration) controllerTemplate).getNameAsString());
            templateStub.addController(controllerStub);
            // 注释
            Optional<Comment> controllerTemplateComment = controllerTemplate.getComment();
            controllerTemplateComment.ifPresent(comment -> controllerStub.setComment(comment.getContent()));

            ((ClassOrInterfaceDeclaration) controllerTemplate).getMembers().forEach(actionTemplate -> {
                ActionStub actionStub = new ActionStub(((ClassOrInterfaceDeclaration) actionTemplate).getNameAsString());
                controllerStub.addAction(actionStub);
                // 注释
                Optional<Comment> actionCommentTemplate = actionTemplate.getComment();
                actionCommentTemplate.ifPresent(comment -> actionStub.setComment(comment.getContent()));
                // 注解
                parseAnnotations(actionTemplate.getAnnotations(), actionStub);


                // 遍历每一个参数，注解+类型+变量
                ((ClassOrInterfaceDeclaration) actionTemplate).getMembers().forEach(parameterTemplate -> {
                    // 类型+变量
                    VariableDeclarator variableStub = ((FieldDeclaration) parameterTemplate).getVariable(0);
                    ParameterStub parameterStub = new ParameterStub(variableStub.getNameAsString(), variableStub.getTypeAsString());

                    // 注解
                    NodeList<AnnotationExpr> annotationTemplates = parameterTemplate.getAnnotations();
                    parseAnnotations(annotationTemplates, parameterStub);

                    // 注释
                    Optional<Comment> parameterCommentTemplate = parameterTemplate.getComment();
                    parameterCommentTemplate.ifPresent(comment -> parameterStub.setComment(comment.getContent()));

                    if (parameterStub.hasAnnotation(Request.name)) {
                        actionStub.addRequest(parameterStub);
                    } else {
                        actionStub.addResponse(parameterStub);
                    }
                });
            });
        });
    }

    private void parseEnums(ClassOrInterfaceDeclaration enumsTemplate, TemplateStub output) {
        enumsTemplate.getMembers().forEach(enumTemplate -> {
            EnumStub enumStub = new EnumStub(((EnumDeclaration) enumTemplate).getNameAsString());
            output.addEnum(enumStub);


            parseAnnotations(enumTemplate.getAnnotations(), enumStub);

            Optional<Comment> commentTemplate = enumTemplate.getComment();
            commentTemplate.ifPresent(comment -> enumStub.setComment((comment).getContent()));

            AtomicInteger i = new AtomicInteger();
            ((EnumDeclaration) enumTemplate).getEntries().forEach(entryTemplate -> {
                String key = entryTemplate.getNameAsString();
                String value;
                EnumStub.EnumStubItemType type = EnumStub.EnumStubItemType.INTEGER;
                if (((EnumDeclaration) enumTemplate).getImplementedTypes().isEmpty()) {
                    // 不是自定义值的
//                    type = EnumStub.EnumStubItemType.STRING;
                    value = String.valueOf(i.getAndIncrement());
                } else {
                    Expression argument = entryTemplate.getArgument(0);
                    value = argument.asIntegerLiteralExpr().getValue();
                }
                enumStub.addItem(key, value, type);
            });
        });
    }

    private void parseModels(ClassOrInterfaceDeclaration modelsTemplate, TemplateStub output) {
        modelsTemplate.getMembers().forEach(modelTemplate -> {
            ModelStub modelStub = new ModelStub(((ClassOrInterfaceDeclaration) modelTemplate).getNameAsString());
            Optional<Comment> commentTemplate = modelTemplate.getComment();
            commentTemplate.ifPresent(comment -> modelStub.setComment((comment).getContent()));
            // 继承关系
            NodeList extendedTypeTemplates = ((ClassOrInterfaceDeclaration) modelTemplate).getExtendedTypes();
            if (!extendedTypeTemplates.isEmpty()) {
                modelStub.setExtendsFrom(extendedTypeTemplates.get(0).toString());
            }

            output.addModel(modelStub);
            // 遍历每一个参数，注解+类型+变量
            ((ClassOrInterfaceDeclaration) modelTemplate).getMembers().forEach(parameterTemplate -> {
                // 类型+变量
                VariableDeclarator variableStub = ((FieldDeclaration) parameterTemplate).getVariable(0);
                ParameterStub parameterStub = new ParameterStub(variableStub.getNameAsString(), variableStub.getTypeAsString());
                modelStub.addParameter(parameterStub);

                // 注解
                NodeList<AnnotationExpr> annotationTemplates = parameterTemplate.getAnnotations();
                parseAnnotations(annotationTemplates, parameterStub);
                // 注释
                Optional<Comment> parameterCommentTemplate = parameterTemplate.getComment();
                parameterCommentTemplate.ifPresent(comment -> parameterStub.setComment(comment.getContent()));
            });
        });
    }

    private void parseAnnotations(NodeList<AnnotationExpr> annotationTemplates, AnnotationsInterface baseStub) {
        annotationTemplates.forEach(annotationTemplate -> {
            AnnotationStub annotationStub = new AnnotationStub(annotationTemplate.getNameAsString());
            baseStub.addAnnotation(annotationStub);

//            if (annotationTemplate instanceof NormalAnnotationExpr) {
//
//            } else if (annotationTemplate instanceof MarkerAnnotationExpr) {
//
//            }
        });

    }

    /**
     *
     * @param filePath 文件路径
     * @param className 类名
     * @return ClassOrInterfaceDeclaration
     * @throws Exception 异常
     */
    private ClassOrInterfaceDeclaration getClassOrInterfaceByNameFromFile(String filePath, String className) throws Exception {
        Reader reader = new FileReader(filePath);
        CompilationUnit compilationUnit = JavaParser.parse(reader);
        Optional<ClassOrInterfaceDeclaration> classOrInterfaceDeclaration = compilationUnit.getClassByName(className);

        if (!classOrInterfaceDeclaration.isPresent()) {
            throw new Exception(className + " not found in " + filePath);
        }
        return classOrInterfaceDeclaration.get();
    }

    private EnumDeclaration getEnumDeclarationByNameFromFile(String filePath, String enumName) throws Exception {
        Reader reader = new FileReader(filePath);
        CompilationUnit compilationUnit = JavaParser.parse(reader);
        Optional<EnumDeclaration> enumDeclaration = compilationUnit.getEnumByName(enumName);

        if (!enumDeclaration.isPresent()) {
            throw new Exception(enumName + " not found in " + filePath);
        }
        return enumDeclaration.get();
    }
}
