package com.chemiq.exception;

public class DuplicateMemberIdException extends RuntimeException {
    public DuplicateMemberIdException(String message) {
        super(message);
    }
}