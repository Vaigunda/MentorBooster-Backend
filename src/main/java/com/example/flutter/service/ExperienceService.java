//package com.example.flutter.service;
//
//import com.example.flutter.entities.Experience;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.jdbc.core.BeanPropertyRowMapper;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.stereotype.Service;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Service
//public class ExperienceService {
//
//    @Autowired
//    private JdbcTemplate jdbcTemplate;
//
//    public Experience addExperience(Experience experience) {
//        String sql = "INSERT INTO experiences (role, company_name, start_date, end_date, description, mentor_id) " +
//                "VALUES (?, ?, ?, ?, ?, ?) RETURNING id";
//
//        Long id = jdbcTemplate.queryForObject(sql, Long.class,
//                experience.getRole(),
//                experience.getCompanyName(),
//                experience.getStartDate(),
//                experience.getEndDate(),
//                experience.getDescription(),
//                experience.getMentor().getId());
//
//        experience.setId(id);
//        return experience;
//    }
//
//    public Experience updateExperience(Long id, Experience experience) {
//        StringBuilder sql = new StringBuilder("UPDATE experiences SET ");
//        List<Object> params = new ArrayList<>();
//
//        if (experience.getRole() != null) {
//            sql.append("role = ?, ");
//            params.add(experience.getRole());
//        }
//        if (experience.getCompanyName() != null) {
//            sql.append("company_name = ?, ");
//            params.add(experience.getCompanyName());
//        }
//        if (experience.getStartDate() != null) {
//            sql.append("start_date = ?, ");
//            params.add(experience.getStartDate());
//        }
//        if (experience.getEndDate() != null) {
//            sql.append("end_date = ?, ");
//            params.add(experience.getEndDate());
//        }
//        if (experience.getDescription() != null) {
//            sql.append("description = ?, ");
//            params.add(experience.getDescription());
//        }
//        if (experience.getMentor() != null && experience.getMentor().getId() != null) {
//            sql.append("mentor_id = ?, ");
//            params.add(experience.getMentor().getId());
//        }
//
//        // Remove trailing comma
//        sql.delete(sql.length() - 2, sql.length());
//        sql.append(" WHERE id = ?");
//        params.add(id);
//
//        // Perform the update
//        jdbcTemplate.update(sql.toString(), params.toArray());
//
//        // Fetch the updated experience from the database
//        String fetchSql = "SELECT * FROM experiences WHERE id = ?";
//        Experience updatedExperience = jdbcTemplate.queryForObject(fetchSql, new BeanPropertyRowMapper<>(Experience.class), id);
//
//        return updatedExperience;
//    }
//
//
//    public void deleteExperience(Long id) {
//        String sql = "DELETE FROM experiences WHERE id = ?";
//        jdbcTemplate.update(sql, id);
//    }
//}
