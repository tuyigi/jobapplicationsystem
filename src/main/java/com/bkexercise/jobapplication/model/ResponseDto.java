package com.bkexercise.jobapplication.model;

import org.springframework.http.HttpStatus;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class ResponseDto  {

    private int status;
    private String message;
    private Object data;
    private String timestamp;

    Map<String, Object> noData = new HashMap<>();

    public ResponseDto() {
    }

    // format our date

    DateTimeFormatter timestampFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // success response
    public ResponseDto(HttpStatus status, String message, Object data) {
        this.status = status.value();
        this.message = message;
        this.data = data;
        this.timestamp = LocalDateTime.now().format(timestampFormat);
    }

    // unsuccessful reposne
    public ResponseDto(HttpStatus status,String message) {
        this.status =status.value();
        this.message = message;
        this.data = noData;
        this.timestamp = LocalDateTime.now().format(timestampFormat);
    }

    // exception
    public ResponseDto(Exception exception) {
        this.status =HttpStatus.BAD_REQUEST.value();
        this.message = exception.getMessage();
        this.data = noData;
        this.timestamp = LocalDateTime.now().format(timestampFormat);
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public Object getData() {
        return data;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setTimestampFormat(DateTimeFormatter timestampFormat) {
        this.timestampFormat = timestampFormat;
    }
}
