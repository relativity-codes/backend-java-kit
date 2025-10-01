package com.standard.commerce_maven.common;

public class ResponseDto<T> {
    private T data;
    private int status;
    private String error;
    private String message;

    public ResponseDto() {}

    public ResponseDto(T data, int status, String error, String message) {
        this.data = data;
        this.status = status;
        this.error = error;
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
