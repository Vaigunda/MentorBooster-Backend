package com.example.flutter.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse {
    
    protected ApiHeaders headers = new ApiHeaders();

    protected List<?> data;

    protected Object recordInfo;

    protected Integer totalpages;

    protected Integer currentpage;

    protected Integer recordlimit;

    protected Long totalrecords;

    protected String recordId;

    public ApiResponse(String message, Integer statuscode) {
        headers.setMessage(message);
        headers.setStatusCode(statuscode);
    }

}
