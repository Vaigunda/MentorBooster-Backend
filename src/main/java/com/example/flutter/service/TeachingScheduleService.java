//package com.example.flutter.service;
//
//import com.example.flutter.entities.TeachingSchedule;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.jdbc.core.BeanPropertyRowMapper;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.stereotype.Service;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Service
//public class TeachingScheduleService {
//
//    @Autowired
//    private JdbcTemplate jdbcTemplate;
//
//    public TeachingSchedule addTeachingSchedule(TeachingSchedule teachingSchedule) {
//        String sql = "INSERT INTO teaching_schedule (date_start, time_start, time_end, booked, mentor_id) " +
//                "VALUES (?, ?, ?, ?, ?) RETURNING id";
//
//        Long id = jdbcTemplate.queryForObject(sql, Long.class,
//                teachingSchedule.getDateStart(),
//                teachingSchedule.getTimeStart(),
//                teachingSchedule.getTimeEnd(),
//                teachingSchedule.getBooked(),
//                teachingSchedule.getMentor().getId());
//
//        teachingSchedule.setId(id);
//        return teachingSchedule;
//    }
//
//    public TeachingSchedule updateTeachingSchedule(Long id, TeachingSchedule teachingSchedule) {
//        StringBuilder sql = new StringBuilder("UPDATE teaching_schedule SET ");
//        List<Object> params = new ArrayList<>();
//
//        // Check each field and add it to the SQL query if it's not null
//        if (teachingSchedule.getDateStart() != null) {
//            sql.append("date_start = ?, ");
//            params.add(teachingSchedule.getDateStart());
//        }
//        if (teachingSchedule.getTimeStart() != null) {
//            sql.append("time_start = ?, ");
//            params.add(teachingSchedule.getTimeStart());
//        }
//        if (teachingSchedule.getTimeEnd() != null) {
//            sql.append("time_end = ?, ");
//            params.add(teachingSchedule.getTimeEnd());
//        }
//        if (teachingSchedule.getBooked() != null) {
//            sql.append("booked = ?, ");
//            params.add(teachingSchedule.getBooked());
//        }
//        if (teachingSchedule.getMentor() != null && teachingSchedule.getMentor().getId() != null) {
//            sql.append("mentor_id = ?, ");
//            params.add(teachingSchedule.getMentor().getId());
//        }
//
//        // Remove the trailing comma
//        sql.delete(sql.length() - 2, sql.length());
//        sql.append(" WHERE id = ?");
//        params.add(id);
//
//        // Perform the update
//        jdbcTemplate.update(sql.toString(), params.toArray());
//
//        // Fetch the updated TeachingSchedule from the database
//        String fetchSql = "SELECT * FROM teaching_schedule WHERE id = ?";
//        TeachingSchedule updatedTeachingSchedule = jdbcTemplate.queryForObject(fetchSql, new BeanPropertyRowMapper<>(TeachingSchedule.class), id);
//
//        // Set the ID for the updated TeachingSchedule and return it
//        updatedTeachingSchedule.setId(id);
//        return updatedTeachingSchedule;
//    }
//
//
//    public void deleteTeachingSchedule(Long id) {
//        String sql = "DELETE FROM teaching_schedule WHERE id = ?";
//        jdbcTemplate.update(sql, id);
//    }
//}
