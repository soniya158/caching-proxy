package com.prasanna.cachingproxy.proxy;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/proxy")
public class ProxyController {
    private final ProxyService service;

    public ProxyController(ProxyService service) {
        this.service = service;
    }

    @GetMapping
    public ProxyResponse get(@RequestParam String path) {
        return service.get(path);
    }

    @DeleteMapping
    public void evict(@RequestParam String path) {
        service.evict(path);
    }

    @PostMapping("/request")
    public ProxyResponse request(@Valid @RequestBody ProxyRequest request) {
        return service.get(request.path());
    }
}