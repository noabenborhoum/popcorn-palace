package com.att.tdp.popcorn_palace.exception;

import org.springframework.http.HttpStatus;

public class ResourceExistsException extends ApiException {
    public ResourceExistsException(String resource, String key, Object value) {
        super(String.format("%s with %s = '%s' already exists", resource, key, value), HttpStatus.CONFLICT);
    }
}