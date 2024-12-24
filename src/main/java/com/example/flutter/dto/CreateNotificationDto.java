package com.example.flutter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateNotificationDto {

    private String title;

    private String message;

    private Long recipientId;

    private Long mentorId;

}
