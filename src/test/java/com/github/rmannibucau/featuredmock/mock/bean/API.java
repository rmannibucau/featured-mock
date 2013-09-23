package com.github.rmannibucau.featuredmock.mock.bean;

import java.util.Collection;
import java.util.Map;

public interface API {
    Value jackson();
    Value[] jacksonArray();
    Collection<Value> jacksonCollection();
    Map<String, Value> jacksonMap();
    Value jaxb();
}
