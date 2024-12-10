package com.example.flutter.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse {

   private String data;

    public ApiResponse(String data) {
        this.data = data;
    }

}
