package com.github.rmannibucau.featuredmock.http;

public interface ContentTypeMapper {
    boolean handle(String accept);
    String contentType(String accept);
    String extension(String accept);
}
