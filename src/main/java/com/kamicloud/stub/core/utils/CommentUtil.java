package com.kamicloud.stub.core.utils;

public class CommentUtil {
    public static String getTitle(String comment) {
        if (comment == null) {
            return "";
        }

        String[] comments = comment.split("\\n");

        return comments[0];
    }
}
