package com.kamicloud.stub.core.segments.postman;

import java.util.ArrayList;
import java.util.Arrays;

public class ItemRequestUrlSegment {
    private String raw;
    private String protocol = "http";

    // www.xxx.com
    private ArrayList<String> host = new ArrayList<>();
    // xxx/xxx/xxx
    private ArrayList<String> path = new ArrayList<>();

    public ItemRequestUrlSegment() {

    }

    public ItemRequestUrlSegment(String[] hosts, String[] paths) {
        this.host.addAll(Arrays.asList(hosts));

        this.path.addAll(Arrays.asList(paths));
    }

    public String getRaw() {
        return raw;
    }

    public String getProtocol() {
        return protocol;
    }

    public ArrayList<String> getHost() {
        return host;
    }

    public void addHost(String host) {
        this.host.add(host);
    }

    public ArrayList<String> getPath() {
        return path;
    }

    public void addPath(String path) {
        this.path.add(path);
    }
}
