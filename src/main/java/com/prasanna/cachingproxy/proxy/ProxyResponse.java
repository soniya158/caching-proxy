package com.prasanna.cachingproxy.proxy;

public record ProxyResponse(
    String source,
    int status,
    String body
) {}