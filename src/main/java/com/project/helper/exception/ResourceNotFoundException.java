package com.project.helper.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String resourceName, String fieldName, String fieldValue) {
        super(String.format("%s Not Found with %s '%s'", resourceName, fieldName, fieldValue));
    }
}