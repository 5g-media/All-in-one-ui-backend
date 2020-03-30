package com.allinoneuibackend.packageservice.exceptions;

public class DockerException extends Exception {
    public DockerException(String message) {
        super(message);
    }

    public DockerException(String message, Throwable cause) {
        super(message, cause);
    }

}
