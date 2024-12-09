package com.example.flutter.repositories;

import com.example.flutter.entities.Users;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<Users,Long> {
    Users findByUserName(String userName);
}
