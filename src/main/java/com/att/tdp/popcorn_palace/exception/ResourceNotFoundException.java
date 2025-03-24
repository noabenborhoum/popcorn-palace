package com.att.tdp.popcorn_palace.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends ApiException {
    public ResourceNotFoundException(String resource, String key, Object value) {
        super(String.format("%s with %s = '%s' was not found", resource, key, value), HttpStatus.NOT_FOUND);
    }
}