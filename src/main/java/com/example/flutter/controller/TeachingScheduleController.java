//package com.example.flutter.controller;
//
//import com.example.flutter.entities.TeachingSchedule;
//import com.example.flutter.service.TeachingScheduleService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/teaching-schedules")
//public class TeachingScheduleController {
//
//    @Autowired
//    private TeachingScheduleService teachingScheduleService;
//
//    @PostMapping
//    public ResponseEntity<TeachingSchedule> addTeachingSchedule(@RequestBody TeachingSchedule teachingSchedule) {
//        TeachingSchedule createdTeachingSchedule = teachingScheduleService.addTeachingSchedule(teachingSchedule);
//        return new ResponseEntity<>(createdTeachingSchedule, HttpStatus.CREATED);
//    }
//
//
//    @PutMapping("/{id}")
//    public ResponseEntity<TeachingSchedule> updateTeachingSchedule(@PathVariable Long id, @RequestBody TeachingSchedule teachingSchedule) {
//        TeachingSchedule updatedTeachingSchedule = teachingScheduleService.updateTeachingSchedule(id, teachingSchedule);
//        return ResponseEntity.ok(updatedTeachingSchedule);
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteTeachingSchedule(@PathVariable Long id) {
//        teachingScheduleService.deleteTeachingSchedule(id);
//        return ResponseEntity.noContent().build();
//    }
//}
