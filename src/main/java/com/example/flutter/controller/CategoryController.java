package com.example.flutter.controller;

import com.example.flutter.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin(origins = "${cors.allow-origin}", maxAge = 3600)
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;


    // Update category
    @PutMapping("/edit/{id}")
    public ResponseEntity<String> updateCategory(@PathVariable Long id, @RequestBody Map<String, String> updates) {
        try {
            String updatedName = updates.get("name");
            String updatedIcon = updates.get("icon");
            categoryService.updateCategory(id, updatedName, updatedIcon);
            return ResponseEntity.ok("Category updated successfully.");
        } catch (IllegalArgumentException e) {
            // Send BadRequest response if category already exists
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Delete category
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long id) {
        try {
            categoryService.deleteCategory(id);
            return ResponseEntity.ok("Category deleted successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/add")
    public ResponseEntity<String> addCategory(@RequestBody Map<String, String> newCategory) {

        try {
            String name = newCategory.get("name");
            String icon = newCategory.get("icon");
            categoryService.addCategory(name, icon);
            return ResponseEntity.ok("Category added successfully.");
        } catch (IllegalArgumentException e) {
            // Send BadRequest response if category already exists
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            // Send a generic error message for other exceptions
            return ResponseEntity.status(500).body("An error occurred while adding the category.");
        }
    }
}
