
package com.example.flutter.repositories;

import com.example.flutter.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Find a category by its name.
     * @param name the name of the category
     * @return an Optional containing the category if found, or empty if not
     */
    Optional<Category> findByName(String name);

    /**
     * Check if a category with the given name exists.
     * @param name the name of the category
     * @return true if the category exists, false otherwise
     */
    boolean existsByName(String name);
}
