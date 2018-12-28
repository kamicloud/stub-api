package com.kamicloud.generator.stubs;

import java.util.HashMap;

public class EnumStub extends BaseWithAnnotationStub {
    private HashMap<String, EnumStubItem> items = new HashMap<>();

    public EnumStub(String name) {
        super(name);
    }

    public void addItem(String key, String value, EnumStubItemType type) {
        items.put(key, new EnumStubItem(value, type));
    }

    public void addItem(String key, EnumStubItem enumStubItem) {
        items.put(key, enumStubItem);
    }

    public HashMap<String, EnumStubItem> getItems() {
        return items;
    }

    public static class EnumStubItem extends BaseWithAnnotationStub {
        private EnumStubItemType type;
        public EnumStubItem(String name, EnumStubItemType type) {
            super(name);
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
