package com.zhh.generator.writers.components.common;

import java.util.ArrayList;

public class MultiLinesCombiner extends ArrayList<String> {
    public void wrapBody(ArrayList<String> header, ArrayList<String> footer) {
        this.addAll(0, header);
//        this.rep;
    }
}
