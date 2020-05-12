module com.github.kamicloud.stub {
    requires com.google.gson;
    requires com.google.common;
    requires spring.beans;
    requires com.github.javaparser.core;
    requires okhttp3;
    requires org.slf4j;
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires java.annotation;
    requires spring.context;

    exports com.github.kamicloud.stub.core;

    uses org.springframework.stereotype.Component;
}
