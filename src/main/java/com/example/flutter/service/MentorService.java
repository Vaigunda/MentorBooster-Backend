package com.example.flutter.service;

import com.example.flutter.entities.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import java.sql.Timestamp;

import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
public class MentorService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // In the MentorService class:
    public String addMentor(Mentor mentor) {
        // Insert the mentor data into the mentors table
        String sql = "INSERT INTO mentors (name, avatar_url, bio, role, free_price, free_unit, verified, rate, number_of_mentoree) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";

        Long mentorId = jdbcTemplate.queryForObject(sql, Long.class,
                mentor.getName(),
                mentor.getAvatarUrl(),
                mentor.getBio(),
                mentor.getRole(),
                mentor.getFreePrice(),
                mentor.getFreeUnit(),
                mentor.getVerified(),
                mentor.getRate(),
                mentor.getNumberOfMentoree());

        mentor.setId(mentorId);

        // Insert the certificates related to this mentor
        if (mentor.getCertificates() != null) {
            for (Certificate certificate : mentor.getCertificates()) {
                String certSql = "INSERT INTO certificates (mentor_id, name, provide_by, create_date, image_url) " +
                        "VALUES (?, ?, ?, ?, ?)";
                jdbcTemplate.update(certSql, mentorId, certificate.getName(), certificate.getProvideBy(), certificate.getCreateDate(), certificate.getImageUrl());
            }
        }

        // Insert the experiences related to this mentor
        if (mentor.getExperiences() != null) {
            for (Experience experience : mentor.getExperiences()) {
                String expSql = "INSERT INTO experiences (mentor_id, role, company_name, start_date, end_date, description) " +
                        "VALUES (?, ?, ?, ?, ?, ?)";
                jdbcTemplate.update(expSql, mentorId, experience.getRole(), experience.getCompanyName(), experience.getStartDate(), experience.getEndDate(), experience.getDescription());
            }
        }

        // Insert the list of fixed time slots related to this mentor
        if (mentor.getTimeSlots() != null && !mentor.getTimeSlots().isEmpty()) {
            for (FixedTimeSlot slot : mentor.getTimeSlots()) {
                if (slot.getTimeStart() == null || slot.getTimeEnd() == null) {
                    throw new IllegalArgumentException("Fixed time slot start and end times cannot be null.");
                }

                System.out.println("Fixed Time Slot Details:");
                System.out.println("Time Start: " + slot.getTimeStart());
                System.out.println("Time End: " + slot.getTimeEnd());

                // Insert the fixed time slot into the database
                String slotSql = "INSERT INTO fixed_time_slots (mentor_id, time_start, time_end) VALUES (?, ?, ?)";
                jdbcTemplate.update(slotSql, mentorId, slot.getTimeStart(), slot.getTimeEnd());
            }
        }


        // Handle categories for this mentor
        if (mentor.getCategories() != null) {
            for (Category category : mentor.getCategories()) {
                // Ensure the category exists in the categories table (if not already present)
                String fetchCategorySql = "SELECT id FROM categories WHERE name = ?";
                Long categoryId;
                try {
                    categoryId = jdbcTemplate.queryForObject(fetchCategorySql, Long.class, category.getName());
                } catch (EmptyResultDataAccessException e) {
                    // If the category does not exist, insert it
                    String insertCategorySql = "INSERT INTO categories (name) VALUES (?) RETURNING id";
                    categoryId = jdbcTemplate.queryForObject(insertCategorySql, Long.class, category.getName());
                }

                // Insert into mentor_categories table
                String mentorCategorySql = "INSERT INTO mentor_categories (mentor_id, category_id) VALUES (?, ?)";
                jdbcTemplate.update(mentorCategorySql, mentorId, categoryId);
            }
        }

        // Return a success message
        return "Mentor added successfully!";
    }

    // Update Mentor's general information
    public int updateMentorInfo(Long mentorId, String name, String avatarUrl, String bio,
                                String role, Double freePrice, String freeUnit, Boolean verified,
                                Double rate, Integer numberOfMentoree) {
        String sql = "UPDATE mentors SET name = ?, avatar_url = ?, bio = ?, role = ?, free_price = ?, " +
                "free_unit = ?, verified = ?, rate = ?, number_of_mentoree = ? WHERE id = ?";
        return jdbcTemplate.update(sql, name, avatarUrl, bio, role, freePrice, freeUnit, verified, rate, numberOfMentoree, mentorId);
    }


    // Method to update fixed time slots
    public void updateFixedTimeSlots(Long mentorId, List<FixedTimeSlot> updatedTimeSlots) {
        // Delete existing fixed time slots for the mentor
        String deleteSql = "DELETE FROM fixed_time_slots WHERE mentor_id = ?";
        jdbcTemplate.update(deleteSql, mentorId);

        // Insert the new list of fixed time slots
        if (updatedTimeSlots != null) {
            String insertSql = "INSERT INTO fixed_time_slots (mentor_id, time_start, time_end) VALUES (?, ?, ?)";
            for (FixedTimeSlot timeSlot : updatedTimeSlots) {
                jdbcTemplate.update(
                        insertSql,
                        mentorId,
                        timeSlot.getTimeStart(),
                        timeSlot.getTimeEnd()
                );
            }
        }
    }

//    public void updateTeachingSchedules(Long mentorId, List<TeachingSchedule> updatedSchedules) {
//        // Delete existing teaching schedules for the mentor
//        String deleteSql = "DELETE FROM teaching_schedule WHERE mentor_id = ?";
//        jdbcTemplate.update(deleteSql, mentorId);
//
//        // Insert the new list of teaching schedules
//        if (updatedSchedules != null) {
//            String insertSql = "INSERT INTO teaching_schedule (mentor_id, date_start, time_start, time_end, booked) " +
//                    "VALUES (?, ?, ?, ?, ?)";
//            for (TeachingSchedule schedule : updatedSchedules) {
//                jdbcTemplate.update(
//                        insertSql,
//                        mentorId,
//                        schedule.getDateStart(),
//                        schedule.getTimeStart(),
//                        schedule.getTimeEnd(),
//                        schedule.getBooked()
//                );
//            }
//        }
//    }


    // Update Certificates for a Mentor
    public int updateCertificates(Long mentorId, List<Certificate> certificates) {
        // First delete existing certificates
        String deleteSql = "DELETE FROM certificates WHERE mentor_id = ?";
        jdbcTemplate.update(deleteSql, mentorId);

        // Insert new certificates
        String insertSql = "INSERT INTO certificates (name, provide_by, create_date, image_url, mentor_id) " +
                "VALUES (?, ?, ?, ?, ?)";
        for (Certificate certificate : certificates) {
            jdbcTemplate.update(insertSql, certificate.getName(), certificate.getProvideBy(),
                    certificate.getCreateDate(), certificate.getImageUrl(), mentorId);
        }
        return certificates.size();  // Return number of updated certificates
    }

    // Update Experience for a Mentor
    public int updateExperience(Long mentorId, List<Experience> experiences) {
        // First delete existing experiences
        String deleteSql = "DELETE FROM experiences WHERE mentor_id = ?";
        jdbcTemplate.update(deleteSql, mentorId);

        // Insert new experiences
        String insertSql = "INSERT INTO experiences (role, company_name, start_date, end_date, description, mentor_id) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        for (Experience experience : experiences) {
            jdbcTemplate.update(insertSql, experience.getRole(), experience.getCompanyName(),
                    experience.getStartDate(), experience.getEndDate(), experience.getDescription(), mentorId);
        }
        return experiences.size();  // Return number of updated experiences
    }

    // Update Mentor Categories
    public int updateCategories(Long mentorId, List<Category> categories) {
        // First delete existing categories for the given mentor
        String deleteSql = "DELETE FROM mentor_categories WHERE mentor_id = ?";
        jdbcTemplate.update(deleteSql, mentorId);

        // Insert new categories based on name-to-id mapping
        String insertSql = "INSERT INTO mentor_categories (mentor_id, category_id) VALUES (?, ?)";

        // Map category names to ids using the categories table
        for (Category category : categories) {
            String categoryName = category.getName();

            // Query to fetch the category_id for the category name
            String selectCategoryIdSql = "SELECT id FROM categories WHERE name = ?";
            Integer categoryId = jdbcTemplate.queryForObject(selectCategoryIdSql, Integer.class, categoryName);

            if (categoryId != null) {
                // Insert into mentor_categories table
                jdbcTemplate.update(insertSql, mentorId, categoryId);
            } else {
                System.out.println("Category with name " + categoryName + " not found in categories table.");
            }
        }
        return categories.size();  // Return the number of successfully updated categories
    }




//    public Mentor addMentor(Mentor mentor) {
//        String sql = "INSERT INTO mentors (name, avatar_url, bio, role, free_price, free_unit, verified, rate, number_of_mentoree) " +
//                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";
//
//        Long id = jdbcTemplate.queryForObject(sql, Long.class,
//                mentor.getName(),
//                mentor.getAvatarUrl(),
//                mentor.getBio(),
//                mentor.getRole(),
//                mentor.getFreePrice(),
//                mentor.getFreeUnit(),
//                mentor.getVerified(),
//                mentor.getRate(),
//                mentor.getNumberOfMentoree());
//
//        mentor.setId(id);
//        return mentor;
//    }


    public void deleteMentor(Long mentorId) {
        // Delete from mentor_categories
        jdbcTemplate.update("DELETE FROM mentor_categories WHERE mentor_id = ?", mentorId);

        // Delete from certificates
        jdbcTemplate.update("DELETE FROM certificates WHERE mentor_id = ?", mentorId);

        // Delete from experiences
        jdbcTemplate.update("DELETE FROM experiences WHERE mentor_id = ?", mentorId);

        // Delete from reviews
        jdbcTemplate.update("DELETE FROM reviews WHERE mentor_id = ?", mentorId);

        // Delete from teaching_schedule
        jdbcTemplate.update("DELETE FROM fixed_time_slots WHERE mentor_id = ?", mentorId);

        // Finally, delete from mentors
        jdbcTemplate.update("DELETE FROM mentors WHERE id = ?", mentorId);
    }



    public List<Map<String, Object>> getAllMentors() {
        String mentorQuery = """
        SELECT 
            m.id AS mentor_id, m.name, m.avatar_url, m.bio, m.role, 
            m.free_price, m.free_unit, m.verified, m.rate, m.number_of_mentoree
        FROM mentors m
    """;

        List<Map<String, Object>> mentors = jdbcTemplate.query(mentorQuery, (rs, rowNum) -> {
            Map<String, Object> mentor = new HashMap<>();
            Long mentorId = rs.getLong("mentor_id");

            mentor.put("id", mentorId);
            mentor.put("name", rs.getString("name"));
            mentor.put("avatarUrl", rs.getString("avatar_url"));
            mentor.put("bio", rs.getString("bio"));
            mentor.put("role", rs.getString("role"));
            mentor.put("free", Map.of(
                    "price", rs.getDouble("free_price"),
                    "unit", Map.of("name", rs.getString("free_unit"))
            ));
            mentor.put("verified", rs.getBoolean("verified"));
            mentor.put("rate", rs.getDouble("rate"));
            mentor.put("numberOfMentoree", rs.getInt("number_of_mentoree"));

            mentor.put("experiences", getMentorExperiences(mentorId));
            mentor.put("certificates", getMentorCertificates(mentorId));
            mentor.put("reviews", getMentorReviews(mentorId));
            mentor.put("categories", getMentorCategories(mentorId));
            mentor.put("timeSlots", getTimeSlots(mentorId)); // Replace teachingSchedules with timeSlots

            return mentor;
        });

        return mentors;
    }

    // New getTimeSlots Method
    private List<Map<String, Object>> getTimeSlots(Long mentorId) {
        String timeSlotQuery = """
        SELECT 
            id AS time_slot_id, time_start, time_end, mentor_id
        FROM fixed_time_slots
        WHERE mentor_id = ?
    """;

        return jdbcTemplate.query(timeSlotQuery, new Object[]{mentorId}, (rs, rowNum) -> {
            Map<String, Object> timeSlot = new HashMap<>();
            timeSlot.put("id", rs.getLong("time_slot_id"));
            timeSlot.put("timeStart", rs.getTime("time_start").toString());
            timeSlot.put("timeEnd", rs.getTime("time_end").toString());
            timeSlot.put("mentorId", rs.getLong("mentor_id")); // Include mentorId in the result
            return timeSlot;
        });
    }


    private List<Map<String, Object>> getMentorExperiences(Long mentorId) {
        String query = """
            SELECT id, mentor_id, role, company_name, start_date, end_date, description
            FROM experiences WHERE mentor_id = ?
        """;

        return jdbcTemplate.query(query, new Object[]{mentorId}, (rs, rowNum) -> {
            Map<String, Object> experience = new HashMap<>();
            experience.put("id", rs.getLong("id"));
            experience.put("mentor_id", rs.getLong("mentor_id"));
            experience.put("role", rs.getString("role"));
            experience.put("company_name", rs.getString("company_name"));
            experience.put("start_date", rs.getDate("start_date"));
            experience.put("end_date", rs.getDate("end_date"));
            experience.put("description", rs.getString("description"));
            return experience;
        });
    }

    private List<Map<String, Object>> getMentorCertificates(Long mentorId) {
        String query = """
            SELECT id, mentor_id, name, provide_by, create_date, image_url
            FROM certificates WHERE mentor_id = ?
        """;

        return jdbcTemplate.query(query, new Object[]{mentorId}, (rs, rowNum) -> {
            Map<String, Object> certificate = new HashMap<>();
            certificate.put("id", rs.getLong("id"));
            certificate.put("mentor_id", rs.getLong("mentor_id"));
            certificate.put("name", rs.getString("name"));
            certificate.put("provide_by", rs.getString("provide_by"));
            certificate.put("create_date", rs.getDate("create_date"));
            certificate.put("image_url", rs.getString("image_url"));
            return certificate;
        });
    }

    private List<Map<String, Object>> getMentorReviews(Long mentorId) {
        String query = """
            SELECT id, mentor_id, message, create_date, created_by_id
            FROM reviews WHERE mentor_id = ?
        """;

        return jdbcTemplate.query(query, new Object[]{mentorId}, (rs, rowNum) -> {
            Map<String, Object> review = new HashMap<>();
            review.put("id", rs.getLong("id"));
            review.put("mentor_id", rs.getLong("mentor_id"));
            review.put("message", rs.getString("message"));
            review.put("create_date", rs.getTimestamp("create_date"));
            review.put("created_by_id", rs.getString("created_by_id"));
            return review;
        });
    }

    private List<Map<String, Object>> getMentorCategories(Long mentorId) {
        String query = """
            SELECT c.id, c.name, c.icon 
            FROM categories c
            JOIN mentor_categories mc ON c.id = mc.category_id
            WHERE mc.mentor_id = ?
        """;

        return jdbcTemplate.query(query, new Object[]{mentorId}, (rs, rowNum) -> {
            Map<String, Object> category = new HashMap<>();
            category.put("id", rs.getLong("id"));
            category.put("name", rs.getString("name"));
            category.put("icon", rs.getString("icon"));
            return category;
        });
    }

    private List<Map<String, Object>> getTeachingSchedules(Long mentorId) {
        String query = """
            SELECT id, date_start, time_start, time_end, booked, mentor_id
            FROM teaching_schedule WHERE mentor_id = ?
        """;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return jdbcTemplate.query(query, new Object[]{mentorId}, (rs, rowNum) -> {
            Map<String, Object> schedule = new HashMap<>();

            // Correcting the order: fetch values from ResultSet and put them in the map
            Timestamp dateStartTimestamp = rs.getTimestamp("date_start");
            Timestamp timeStartTimestamp = rs.getTimestamp("time_start");
            Timestamp timeEndTimestamp = rs.getTimestamp("time_end");

            LocalDateTime dateStart = dateStartTimestamp.toLocalDateTime();
            LocalDateTime timeStart = timeStartTimestamp.toLocalDateTime();
            LocalDateTime timeEnd = timeEndTimestamp.toLocalDateTime();

            // Populate the map with values
            schedule.put("id", rs.getLong("id"));
            schedule.put("date_start", dateStart.format(formatter));
            schedule.put("time_start", timeStart.format(formatter));
            schedule.put("time_end", timeEnd.format(formatter));
            schedule.put("booked", rs.getBoolean("booked"));
            schedule.put("mentor_id", rs.getLong("mentor_id"));

            // For debugging, print out the schedule map
            System.out.println(schedule);

            return schedule;
        });
    }

    public List<Map<String, Object>> getTeachingSchedulesByMentor(Long mentorId) {
        // SQL query to filter by mentor_id
        String sql = "SELECT id, date_start, time_start, time_end, booked, mentor_id FROM teaching_schedule WHERE mentor_id = ?";
        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql, mentorId);

        // Formatter for converting Timestamp to String
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // Process each row
        for (Map<String, Object> schedule : result) {
            // Convert Timestamp to LocalDateTime
            Timestamp dateStartTimestamp = (Timestamp) schedule.get("date_start");
            Timestamp timeStartTimestamp = (Timestamp) schedule.get("time_start");
            Timestamp timeEndTimestamp = (Timestamp) schedule.get("time_end");

            LocalDateTime dateStart = dateStartTimestamp.toLocalDateTime();
            LocalDateTime timeStart = timeStartTimestamp.toLocalDateTime();
            LocalDateTime timeEnd = timeEndTimestamp.toLocalDateTime();

            // Format and update the schedule map
            schedule.put("dateStart", dateStart.format(formatter));
            schedule.put("timeStart", timeStart.format(formatter));
            schedule.put("timeEnd", timeEnd.format(formatter));
            schedule.put("booked", schedule.get("booked"));
            schedule.put("mentorId", schedule.get("mentor_id"));

            // Remove original keys if necessary
            schedule.remove("date_start");
            schedule.remove("time_start");
            schedule.remove("time_end");
            schedule.remove("mentor_id");
        }

        return result;
    }





}
