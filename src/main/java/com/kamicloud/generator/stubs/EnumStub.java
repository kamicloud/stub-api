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

    public HashMap<String, EnumStubItem> getItems() {
        return items;
    }

    public class EnumStubItem {
        private String name;
        private EnumStubItemType type;
        EnumStubItem(String name, EnumStubItemType type) {
            this.name = name;
            this.type = type;
        }

        public String getName() {
            return name;
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
