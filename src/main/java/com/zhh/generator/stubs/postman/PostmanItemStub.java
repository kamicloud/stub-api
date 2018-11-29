package com.zhh.generator.stubs.postman;

import java.util.ArrayList;

public class PostmanItemStub {
    private String name;

    private ArrayList<PostmanItemStub> item;
    private PostmanItemRequestStub request;


    public PostmanItemStub(String name) {
        this.name = name;
    }

    public ArrayList getItem() {
        return item;
    }

    public void addItem(PostmanItemStub item) {
        if (this.item == null) {
            this.item = new ArrayList<>();
        }
        this.item.add(item);
    }

    public void setRequest(PostmanItemRequestStub request) {
        this.request = request;
    }
}
