package com.example.flutter.controller;

import com.example.flutter.entities.Mentor;
import com.example.flutter.entities.Reviews;
import com.example.flutter.entities.Users;
import com.example.flutter.request.ReviewsRequest;
import com.example.flutter.service.MentorService;
import com.example.flutter.service.ReviewsService;
import com.example.flutter.service.UsersService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@CrossOrigin(origins = "${cors.allow-origin}", maxAge = 3600)
@RequestMapping("/api/reviews")
@RestController
public class ReviewsController {

    private static final Logger logger = LoggerFactory.getLogger(ReviewsController.class);

    @Autowired
    ReviewsService reviewsService;

    @Autowired
    MentorService mentorService;

    @Autowired
    UsersService usersService;

    @PostMapping("/create")
    public ResponseEntity<String> createReviews (@RequestBody ReviewsRequest reviewsRequest){
        Reviews reviews = new Reviews();
        Mentor mentor = mentorService.findById(reviewsRequest.getMentorId());
        Users users = usersService.findById(Long.valueOf(reviewsRequest.getUserId()));
        // Get current date
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String date = currentDate.format(formatter);

        reviews.setMessage(reviewsRequest.getMessage());
        reviews.setRating(reviewsRequest.getRating());
        reviews.setCreatedById(reviewsRequest.getUserId());
        reviews.setUserName(users.getName());
        reviews.setMentor(mentor);
        reviews.setCreateDate(date);
        reviewsService.save(reviews);
        return ResponseEntity.ok("Thank you for your Feedback.");
    }

}
