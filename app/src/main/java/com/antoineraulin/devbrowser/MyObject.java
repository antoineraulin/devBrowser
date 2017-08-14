package com.antoineraulin.devbrowser;

/**
 * Created by antoi on 12/08/2017.
 */

public class MyObject {
    private String file;
    private Long time;

    public MyObject(String file, Long time) {
        this.file = file;
        this.time = time;
    }

    public String getFile() {
        return file;
    }

    public Long getTimeMilli() {
        return time;
    }

    //getters & setters
}