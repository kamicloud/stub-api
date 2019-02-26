package com.kamicloud.generator.stubs;

import com.kamicloud.generator.utils.UrlUtil;

import java.util.HashMap;
import java.util.Observable;

public class OutputStub extends Observable {
    private HashMap<String, TemplateStub> templates = new HashMap<>();

    public HashMap<String, TemplateStub> getTemplates() {
        return templates;
    }

    public void setTemplates(HashMap<String, TemplateStub> templates) {
        setChanged();
        this.templates = templates;
    }

    public void addTemplate(TemplateStub templateStub) {
        setChanged();
        this.templates.put(templateStub.getName(), templateStub);
    }

    public void setActionUrl() {
        templates.forEach((version, templateStub) -> {
            templateStub.getControllers().forEach((controllerStub -> {
                controllerStub.getActions().forEach((actionName, action) -> {
                    String uri = "/" + UrlUtil.transformVersion(version) +
                        "/" + UrlUtil.transformController(controllerStub.getName()) +
                        "/" + UrlUtil.transformAction(actionName);
                    action.setUri(uri);
                    action.setFullUri("/api" + uri);
                });
            }));
        });
    }
}
