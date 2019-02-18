package com.kamicloud.generator.writers.components.common;

import java.util.ArrayList;

public class MultiLinesCombiner extends ArrayList<String> {
    private static final long serialVersionUID = 1L;

	public void wrapBody(ArrayList<String> header, ArrayList<String> footer) {
        this.addAll(0, header);
//        this.rep;
    }
}
