//package com.example.flutter.service;
//
//import com.example.flutter.entities.Certificate;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.jdbc.core.BeanPropertyRowMapper;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.stereotype.Service;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Service
//public class CertificateService {
//
//    @Autowired
//    private JdbcTemplate jdbcTemplate;
//
//    public Certificate addCertificate(Certificate certificate) {
//        String sql = "INSERT INTO certificates (name, provide_by, create_date, image_url, mentor_id) " +
//                "VALUES (?, ?, ?, ?, ?) RETURNING id";
//
//        Long id = jdbcTemplate.queryForObject(sql, Long.class,
//                certificate.getName(),
//                certificate.getProvideBy(),
//                certificate.getCreateDate(),
//                certificate.getImageUrl(),
//                certificate.getMentor().getId());
//
//        certificate.setId(id);
//        return certificate;
//    }
//
//    public Certificate updateCertificate(Long id, Certificate certificate) {
//        StringBuilder sql = new StringBuilder("UPDATE certificates SET ");
//        List<Object> params = new ArrayList<>();
//
//        if (certificate.getName() != null) {
//            sql.append("name = ?, ");
//            params.add(certificate.getName());
//        }
//        if (certificate.getProvideBy() != null) {
//            sql.append("provide_by = ?, ");
//            params.add(certificate.getProvideBy());
//        }
//        if (certificate.getCreateDate() != null) {
//            sql.append("create_date = ?, ");
//            params.add(certificate.getCreateDate());
//        }
//        if (certificate.getImageUrl() != null) {
//            sql.append("image_url = ?, ");
//            params.add(certificate.getImageUrl());
//        }
//        if (certificate.getMentor() != null && certificate.getMentor().getId() != null) {
//            sql.append("mentor_id = ?, ");
//            params.add(certificate.getMentor().getId());
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
//        // Fetch the updated certificate from the database
//        String fetchSql = "SELECT * FROM certificates WHERE id = ?";
//        Certificate updatedCertificate = jdbcTemplate.queryForObject(fetchSql, new BeanPropertyRowMapper<>(Certificate.class), id);
//
//        return updatedCertificate;
//    }
//
//
//    public void deleteCertificate(Long id) {
//        String sql = "DELETE FROM certificates WHERE id = ?";
//        jdbcTemplate.update(sql, id);
//    }
//}
