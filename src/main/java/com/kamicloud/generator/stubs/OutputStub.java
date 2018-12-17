package com.kamicloud.generator.stubs;

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
}
