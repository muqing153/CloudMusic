package com.muqingbfq;

public class XM {
    public String id, name, message;
    public Object picurl;
    public XM(String id, String name, String picurl) {
        this.id = id;
        this.name = name;
        this.picurl = picurl;
    }
    public XM(String id, String name,String message, String picurl) {
        this.id = id;
        this.name = name;
        this.picurl = picurl;
        this.message = message;
    }
    public XM(String id, String name, int picurl) {
        this.id = id;
        this.name = name;
        this.picurl = picurl;
    }
}