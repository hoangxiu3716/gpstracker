package de.gimik.apps.gpstracker.backend.web.viewmodel;

public class ErrorInfo {
    private String errorCode;
    private String errorMessage;
    private String message;

    public ErrorInfo(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public ErrorInfo(String errorCode, String errorMessage, String message) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.message = message;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getMessage() {
        return message;
    }
}
