//package com.example.flutter.controller;
//
//import com.example.flutter.entities.Experience;
//import com.example.flutter.service.ExperienceService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/experiences")
//public class ExperienceController {
//
//    @Autowired
//    private ExperienceService experienceService;
//
//    @PostMapping
//    public ResponseEntity<Experience> addExperience(@RequestBody Experience experience) {
//        Experience createdExperience = experienceService.addExperience(experience);
//        return new ResponseEntity<>(createdExperience, HttpStatus.CREATED);
//    }
//
//
//    @PutMapping("/{id}")
//    public ResponseEntity<Experience> updateExperience(@PathVariable Long id, @RequestBody Experience experience) {
//        Experience updatedExperience = experienceService.updateExperience(id, experience);
//        return ResponseEntity.ok(updatedExperience);
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteExperience(@PathVariable Long id) {
//        experienceService.deleteExperience(id);
//        return ResponseEntity.noContent().build();
//    }
//}
