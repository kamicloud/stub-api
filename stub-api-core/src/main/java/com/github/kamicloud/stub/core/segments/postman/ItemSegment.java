package com.github.kamicloud.stub.core.segments.postman;

import java.util.ArrayList;

public class ItemSegment {
    private String name;

    private ArrayList<ItemSegment> item;
    private ItemRequestSegment request;


    public ItemSegment(String name) {
        this.name = name;
    }

    public ArrayList<ItemSegment> getItem() {
        return item;
    }

    public void addItem(ItemSegment item) {
        if (this.item == null) {
            this.item = new ArrayList<>();
        }
        this.item.add(item);
    }

    public void setRequest(ItemRequestSegment request) {
        this.request = request;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ItemRequestSegment getRequest() {
        return request;
    }
}
