package com.prasanna.cachingproxy.proxy;

import jakarta.validation.constraints.NotBlank;

public record ProxyRequest(
    @NotBlank String path,
    String method,
    String body
) {
    public String normalizedMethod() {
        return method == null || method.isBlank() ? "GET" : method.toUpperCase();
    }
}