package com.example.flutter.repositories;

import com.example.flutter.entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<Users,Long> {
    Users findByUserName(String userName);

    Users findByEmailId(String emailId);

    boolean existsByEmailId(String emailId);

    @Query("SELECT u.userName FROM Users u WHERE u.id = :recipientId")
    String findRecipientNameByRecipientId(@Param("recipientId") Long recipientId);
}
