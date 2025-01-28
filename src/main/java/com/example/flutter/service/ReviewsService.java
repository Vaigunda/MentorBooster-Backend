package com.example.flutter.service;

import com.example.flutter.entities.Reviews;
import com.example.flutter.repositories.ReviewsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReviewsService {

    @Autowired
    ReviewsRepository reviewsRepository;

    public Reviews save(Reviews reviews) {
        return reviewsRepository.save(reviews);
    }
}
