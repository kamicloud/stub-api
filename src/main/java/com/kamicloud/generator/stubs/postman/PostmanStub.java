package com.kamicloud.generator.stubs.postman;

import java.util.ArrayList;

public class PostmanStub {
    private PostmanInfoStub info;
    private ArrayList<PostmanItemStub> item = new ArrayList<>();

    public PostmanStub() {
        this.info = new PostmanInfoStub();
    }

    public PostmanInfoStub getInfo() {
        return info;
    }

    public ArrayList<PostmanItemStub> getItem() {
        return item;
    }

    public void addItem(PostmanItemStub item) {
        this.item.add(item);
    }
}
