package com.example.flutter.service;

import com.example.flutter.entities.Category;
import com.example.flutter.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public void addCategory(String name, String icon) {
        // Validate the category name
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Category name cannot be empty.");
        }

        // Check for duplicates (case-insensitive)
        if (categoryRepository.findByNameIgnoreCase(name).isPresent()) {
            throw new IllegalArgumentException("Category with this name already exists.");
        }

        // Create and save the new category
        Category category = new Category();
        category.setName(name);
        category.setIcon(icon);
        categoryRepository.save(category);
    }


    public void updateCategory(Long id, String updatedName, String updatedIcon) {
        // Find the category by ID
        Optional<Category> categoryOptional = categoryRepository.findById(id);
        if (categoryOptional.isEmpty()) {
            throw new IllegalArgumentException("Category not found.");
        }

        if (categoryRepository.findByNameIgnoreCase(updatedName).isPresent()) {
            throw new IllegalArgumentException("Category with this name already exists.");
        }

        // Update the category fields
        Category category = categoryOptional.get();
        if (updatedName != null && !updatedName.isEmpty()) {
            category.setName(updatedName);
        }
        if (updatedIcon != null && !updatedIcon.isEmpty()) {
            category.setIcon(updatedIcon);
        }

        // Save the updated category
        categoryRepository.save(category);
    }


    public void deleteCategory(Long id) {
        // Check if the category exists
        if (!categoryRepository.existsById(id)) {
            throw new IllegalArgumentException("Category not found.");
        }

        // Delete the category
        categoryRepository.deleteById(id);
    }
}
