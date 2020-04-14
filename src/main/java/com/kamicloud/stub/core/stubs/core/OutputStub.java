package com.kamicloud.stub.core.stubs.core;

import com.kamicloud.stub.core.parsers.DocParser;
import com.kamicloud.stub.core.utils.UrlUtil;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.LinkedList;

public class OutputStub {
    private final HashMap<String, TemplateStub> templates = new HashMap<>();

    private final LinkedList<ErrorStub> errors = new LinkedList<>();

    public HashMap<String, BaseWithAnnotationStub> classHashMap = new HashMap<>();

    public HashMap<String, ModelStub> modelHashMap = new HashMap<>();

    /**
     * 当前的接口版本
     */
    private TemplateStub currentTemplate;

    public HashMap<String, TemplateStub> getTemplates() {
        return templates;
    }

    public void addTemplate(TemplateStub templateStub) {
        this.templates.put(templateStub.getName(), templateStub);
    }

    /**
     * 模板分析完成后需要链接stub信息
     */
    public void postParse() {
        classHashMap.forEach((classpath, commentInterface) -> {
            String comment = DocParser.classDocHashMap.get(commentInterface.getClasspath());
            commentInterface.setComment(comment);
        });
        modelHashMap.forEach((classpath, modelStub) -> {
            modelStub.setParent(modelHashMap.get(modelStub.getParentClasspath()));
        });
        templates.forEach((version, templateStub) -> {
            templateStub.getControllers().forEach((controllerStub -> {
                controllerStub.getActions().forEach((action) -> {
                    String actionName = action.getName();

                    String uri = "/" + UrlUtil.transformVersion(version) +
                        "/" + UrlUtil.transformController(controllerStub.getName()) +
                        "/" + UrlUtil.transformAction(actionName);
                    action.setUri(uri);
                    action.setFullUri("/api" + uri);
                });
            }));
        });
    }

    public void addError(ErrorStub errorStub) {
        errors.add(errorStub);
    }

    public LinkedList<ErrorStub> getErrors() {
        return errors;
    }

    public TemplateStub getCurrentTemplate() {
        return currentTemplate;
    }

    public void setCurrentTemplate(TemplateStub currentTemplate) {
        this.currentTemplate = currentTemplate;
        currentTemplate.setCurrent(true);
    }
}
