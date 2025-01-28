package com.example.flutter.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ReviewsRequest {

    private Long rating;

    private String message;

    private Long mentorId;

    private String userId;
}
