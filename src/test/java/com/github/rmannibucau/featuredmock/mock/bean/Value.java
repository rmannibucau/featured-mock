package com.github.rmannibucau.featuredmock.mock.bean;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Value {
    private String attr1;
    private String attr2;

    public String getAttr1() {
        return attr1;
    }

    public void setAttr1(final String attr1) {
        this.attr1 = attr1;
    }

    public String getAttr2() {
        return attr2;
    }

    public void setAttr2(final String attr2) {
        this.attr2 = attr2;
    }
}
