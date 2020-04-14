package com.kamicloud.stub.core.generators.components.postman;

import java.util.ArrayList;

public class MainSegment {
    private InfoSegment info;
    private ArrayList<ItemSegment> item = new ArrayList<>();

    public MainSegment() {
        this.info = new InfoSegment();
    }

    public InfoSegment getInfo() {
        return info;
    }

    public ArrayList<ItemSegment> getItem() {
        return item;
    }

    public void addItem(ItemSegment item) {
        this.item.add(item);
    }
}
