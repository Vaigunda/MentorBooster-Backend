package com.example.flutter.response;

public class PaymentResponse {
    private String id;
    private String status;
    private String clientSecret;

    public PaymentResponse() {
    }

    public PaymentResponse(String id, String status, String clientSecret) {
        this.id = id;
        this.status = status;
        this.clientSecret = clientSecret;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }


}