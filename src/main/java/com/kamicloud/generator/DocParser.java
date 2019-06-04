package com.kamicloud.generator;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.comments.BlockComment;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.ast.comments.LineComment;
import com.github.javaparser.javadoc.Javadoc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Doclet;
import com.sun.javadoc.ProgramElementDoc;
import com.sun.javadoc.RootDoc;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("unused")
public class DocParser {
    static HashMap<String, String> classDocHashMap = new HashMap<>();


    public void parse(File file) throws FileNotFoundException {
        CompilationUnit compilationUnit = StaticJavaParser.parse(file);

        compilationUnit.getChildNodes().forEach(node -> {
            if (node instanceof ClassOrInterfaceDeclaration) {
                ClassOrInterfaceDeclaration template = (ClassOrInterfaceDeclaration) node;

                parseClassOrInterfaceDeclaration(template);
            }
            if (node instanceof EnumDeclaration) {
                EnumDeclaration error = (EnumDeclaration) node;

                parseEnumDeclaration(error);
            }
        });
//
//        Optional<ClassOrInterfaceDeclaration> classA = compilationUnit.getClassByName("TemplateV1");
//
//        // Template
//        classA.ifPresent(classX -> {
//            // Controllers Enums Models
//
//            NodeList<BodyDeclaration<?>> x = classX.getMembers();
//
//            classX.getMembers().forEach(bodyDeclaration -> {
//                bodyDeclaration.ifClassOrInterfaceDeclaration(this::parseClassOrInterfaceDeclaration);
//            });
//        });
//        List<Comment> x = compilationUnit.getAllContainedComments();
    }

    public void parseClassOrInterfaceDeclaration(ClassOrInterfaceDeclaration classOrInterfaceDeclaration) {
        Optional<String> classpath = classOrInterfaceDeclaration.getFullyQualifiedName();

        Optional<Comment> classComment = classOrInterfaceDeclaration.getComment();

        if (classpath.isPresent() && classComment.isPresent()) {
            classDocHashMap.put(classpath.get(), parseComment(classComment.get()));
        }

        classOrInterfaceDeclaration.getMembers().forEach(bodyDeclaration -> {
            bodyDeclaration.ifEnumDeclaration(this::parseEnumDeclaration);
            bodyDeclaration.ifClassOrInterfaceDeclaration(this::parseClassOrInterfaceDeclaration);
            bodyDeclaration.ifFieldDeclaration(fieldDeclaration -> {
                Optional<Comment> comment = fieldDeclaration.getComment();
                fieldDeclaration.getVariables().forEach(variableDeclarator -> {
                    String name = variableDeclarator.getNameAsString();
                    if (classpath.isPresent() && comment.isPresent()) {

                        classDocHashMap.put(classpath.get() + "." + name, parseComment(comment.get()));
                    }
                });
            });
        });
    }

    public String parseComment(Comment comment) {
        String string = null;

        if (comment.isJavadocComment()) {
            Javadoc javadoc = comment.asJavadocComment().parse();
            string = javadoc.getDescription().toText();
        } else {
            string = comment.getContent().trim();
        }

        return string;
    }

    public void parseEnumDeclaration(EnumDeclaration enumDeclaration) {
        Optional<Comment> enumComment = enumDeclaration.getComment();
        String classpath;
        if (!enumDeclaration.getFullyQualifiedName().isPresent()) {
            return;
        }

        classpath = enumDeclaration.getFullyQualifiedName().get();
        if (enumComment.isPresent()) {
            classDocHashMap.put(classpath, parseComment(enumComment.get()));
        }
        enumDeclaration.getEntries().forEach(enumConstantDeclaration -> {
            String name = enumConstantDeclaration.getNameAsString();
            Optional<Comment> comment = enumConstantDeclaration.getComment();

            if (!comment.isPresent()) {
                return;
            }
            classDocHashMap.put(classpath + "." + name, parseComment(comment.get()));
        });
    }

    public void parseEnumConstantDeclaration(EnumConstantDeclaration enumConstantDeclaration) {
        Optional<Node> node = enumConstantDeclaration.getParentNode();

        node.ifPresent(node1 -> {

        });

    }

    public static boolean start(RootDoc root) {







//        ClassDoc[] classes = root.classes();
//        for (ClassDoc cd : classes) {
//            classDocHashMap.put(cd.qualifiedTypeName(), cd);
////            System.out.println(cd.name() + "   " + cd.commentText());
//            ClassDoc[] innerClasses = cd.innerClasses();
//            for (ClassDoc innerClass : innerClasses) {
//                Arrays.asList(innerClass.innerClasses()).forEach(classDoc -> {
////                    System.out.println("classDoc   " + classDoc.name() + "   " + classDoc.commentText());
//                    classDocHashMap.put(classDoc.qualifiedTypeName(), classDoc);
//
//                    Arrays.asList(classDoc.fields()).forEach(fieldDoc -> {
//                        classDocHashMap.put(fieldDoc.qualifiedName(), fieldDoc);
////                        System.out.println("fieldDoc   " + fieldDoc.name() + "   " + fieldDoc.commentText());
//                    });
//                });
//            }
//
//            Arrays.asList(cd.fields()).forEach(fieldDoc -> {
//                classDocHashMap.put(fieldDoc.qualifiedName(), fieldDoc);
////                        System.out.println("fieldDoc   " + fieldDoc.name() + "   " + fieldDoc.commentText());
//            });
//        }
        return true;
    }
}
