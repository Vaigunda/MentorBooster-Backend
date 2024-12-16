package com.example.flutter.repositories;

import com.example.flutter.entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<Users,Long> {
    Users findByUserName(String userName);

    Users findByEmailId(String emailId);
}
