package com.kamicloud.generator.stubs.postman;

import java.util.ArrayList;

public class PostmanItemStub {
    private String name;

    private ArrayList<PostmanItemStub> item;
    private PostmanItemRequestStub request;


    public PostmanItemStub(String name) {
        this.name = name;
    }

    public ArrayList<PostmanItemStub> getItem() {
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PostmanItemRequestStub getRequest() {
        return request;
    }
}
