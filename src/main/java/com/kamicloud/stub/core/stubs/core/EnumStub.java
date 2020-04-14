package com.kamicloud.stub.core.stubs.core;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class EnumStub extends BaseWithAnnotationStub {
    private LinkedHashMap<String, EnumStubItem> items = new LinkedHashMap<>();

    public EnumStub(String name, String classpath) {
        super(name, classpath);
    }

    public void addItem(String key, String classpath, String value, EnumStubItemType type) {
        items.put(key, new EnumStubItem(value, classpath, type));
    }

    public void addItem(String key, EnumStubItem enumStubItem) {
        items.put(key, enumStubItem);
    }

    public HashMap<String, EnumStubItem> getItems() {
        return items;
    }

    public static class EnumStubItem extends BaseWithAnnotationStub {
        private EnumStubItemType type;
        public EnumStubItem(String name, String classpath, EnumStubItemType type) {
            super(name, classpath);
            this.type = type;
        }

        public EnumStubItemType getType() {
            return type;
        }
    }

    public enum EnumStubItemType {
        INTEGER,
        STRING,
        EXPRESSION,
    }
}
