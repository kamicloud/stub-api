package com.kamicloud.generator.utils;

public class CommentUtil {
    public static String getTitle(String comment) {
        if (comment == null) {
            return null;
        }

        String[] comments = comment.split("\\n");

        return comments[0];
    }
}
