package com.example.flutter.service;

import com.example.flutter.entities.*;
import com.example.flutter.dto.*;
import com.example.flutter.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

import java.sql.Timestamp;

import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

@Service
public class MentorService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private MentorRepository mentorRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BookingRepository bookingsRepository;

    //search with repo
    public List<MentorSearchDTO> searchMentors(String keyword) {
        return mentorRepository.searchMentors(keyword);
    }

    public String updateMentor(Long mentorId, Mentor mentorDetails) {
        // Fetch the mentor from the database
        Mentor existingMentor = mentorRepository.findById(mentorId)
                .orElseThrow(() -> new RuntimeException("Mentor not found with ID: " + mentorId));

        // Update basic fields
        existingMentor.setName(mentorDetails.getName());
        existingMentor.setEmail(mentorDetails.getEmail());
        existingMentor.setAvatarUrl(mentorDetails.getAvatarUrl());
        existingMentor.setBio(mentorDetails.getBio());
        existingMentor.setRole(mentorDetails.getRole());
        existingMentor.setFreePrice(mentorDetails.getFreePrice());
        existingMentor.setFreeUnit(mentorDetails.getFreeUnit());
        existingMentor.setVerified(mentorDetails.getVerified());
        existingMentor.setRate(mentorDetails.getRate());
        existingMentor.setNumberOfMentoree(mentorDetails.getNumberOfMentoree());

        // Update certificates
        if (mentorDetails.getCertificates() != null) {
            existingMentor.getCertificates().clear();
            for (Certificate cert : mentorDetails.getCertificates()) {
                cert.setMentor(existingMentor);
            }
            existingMentor.getCertificates().addAll(mentorDetails.getCertificates());
        }

        // Update experiences
        if (mentorDetails.getExperiences() != null) {
            existingMentor.getExperiences().clear();
            for (Experience exp : mentorDetails.getExperiences()) {
                exp.setMentor(existingMentor);
            }
            existingMentor.getExperiences().addAll(mentorDetails.getExperiences());
        }

        // Update time slots after deleting that time slot in bookings table
        if (mentorDetails.getTimeSlots() != null) {
            // Delete related data from bookings table before updating time slots
            for (FixedTimeSlot slot : existingMentor.getTimeSlots()) {
                bookingsRepository.deleteByTimeSlotId(slot.getId()); // Assume `deleteByTimeSlotId` exists in your repository
            }

            existingMentor.getTimeSlots().clear();
            for (FixedTimeSlot slot : mentorDetails.getTimeSlots()) {
                slot.setMentor(existingMentor);
            }
            existingMentor.getTimeSlots().addAll(mentorDetails.getTimeSlots());
        }

        // Update categories
        if (mentorDetails.getCategories() != null) {
            List<Category> updatedCategories = mentorDetails.getCategories().stream()
                    .map(cat -> categoryRepository.findByName(cat.getName())
                            .orElseGet(() -> {
                                Category newCategory = new Category();
                                newCategory.setName(cat.getName());
                                return categoryRepository.save(newCategory);
                            }))
                    .collect(Collectors.toList());
            existingMentor.setCategories(updatedCategories);
        }

        // Save the updated mentor
        mentorRepository.save(existingMentor);
        return "Mentor updated successfully!";
    }

    // add mentor with repo ( here in request body in categories we need id but we get name so we can't directly save
    public String saveMentor(Mentor mentorDto) {
        Mentor mentor = new Mentor();

        // Map basic fields
        mentor.setName(mentorDto.getName());
        mentor.setEmail(mentorDto.getEmail());
        mentor.setAvatarUrl(mentorDto.getAvatarUrl());
        mentor.setBio(mentorDto.getBio());
        mentor.setRole(mentorDto.getRole());
        mentor.setFreePrice(mentorDto.getFreePrice());
        mentor.setFreeUnit(mentorDto.getFreeUnit());
        mentor.setVerified(mentorDto.getVerified());
        mentor.setRate(mentorDto.getRate());
        mentor.setNumberOfMentoree(mentorDto.getNumberOfMentoree());

        // Map certificates
        List<Certificate> certificates = mentorDto.getCertificates().stream()
                .map(certDto -> {
                    Certificate certificate = new Certificate();
                    certificate.setName(certDto.getName());
                    certificate.setProvideBy(certDto.getProvideBy());
                    certificate.setCreateDate(certDto.getCreateDate());
                    certificate.setImageUrl(certDto.getImageUrl());
                    certificate.setMentor(mentor);
                    return certificate;
                })
                .collect(Collectors.toList());
        mentor.setCertificates(certificates);

        // Map experiences
        List<Experience> experiences = mentorDto.getExperiences().stream()
                .map(expDto -> {
                    Experience experience = new Experience();
                    experience.setRole(expDto.getRole());
                    experience.setCompanyName(expDto.getCompanyName());
                    experience.setStartDate(expDto.getStartDate());
                    experience.setEndDate(expDto.getEndDate());
                    experience.setDescription(expDto.getDescription());
                    experience.setMentor(mentor);
                    return experience;
                })
                .collect(Collectors.toList());
        mentor.setExperiences(experiences);

        // Map time slots
        List<FixedTimeSlot> timeSlots = mentorDto.getTimeSlots().stream()
                .map(slotDto -> {
                    FixedTimeSlot timeSlot = new FixedTimeSlot();
                    timeSlot.setTimeStart(slotDto.getTimeStart());
                    timeSlot.setTimeEnd(slotDto.getTimeEnd());
                    timeSlot.setMentor(mentor);
                    return timeSlot;
                })
                .collect(Collectors.toList());
        mentor.setTimeSlots(timeSlots);

        // Map categories (fetch or create categories by name)
        List<Category> categories = mentorDto.getCategories().stream()
                .map(catDto -> categoryRepository.findByName(catDto.getName())
                        .orElseGet(() -> {
                            Category newCategory = new Category();
                            newCategory.setName(catDto.getName());
                            return categoryRepository.save(newCategory);
                        }))
                .collect(Collectors.toList());
        mentor.setCategories(categories);

        // Save mentor
        mentorRepository.save(mentor);
        return "Mentor added successfully!";
    }

    public Mentor findById(Long id) {
        return mentorRepository.findById(id).get();
    }

    public Mentor findByEmail(String email) {
        return mentorRepository.findByEmail(email);
    }

    public boolean existsByEmail(String email) {
        return mentorRepository.existsByEmail(email);
    }

    public void deleteMentors(Long mentorId) {
        if (mentorRepository.existsById(mentorId)) {
            mentorRepository.deleteById(mentorId);
            System.out.println("Mentor and associated entities successfully deleted.");
        } else {
            throw new IllegalArgumentException("Mentor with ID " + mentorId + " not found.");
        }
    }

    public List<MentorDTO> getAllMentor() {
        List<Mentor> mentors = mentorRepository.findAll(); // Fetch all mentors
        List<MentorDTO> mentorDTOs = new ArrayList<>();

        for (Mentor mentor : mentors) {
            MentorDTO mentorDTO = new MentorDTO();
            mentorDTO.setId(mentor.getId());
            mentorDTO.setName(mentor.getName());
            mentorDTO.setEmail(mentor.getEmail());
            mentorDTO.setAvatarUrl(mentor.getAvatarUrl());
            mentorDTO.setBio(mentor.getBio());
            mentorDTO.setRole(mentor.getRole());
            mentorDTO.setVerified(mentor.getVerified());
            mentorDTO.setRate(mentor.getRate());
            mentorDTO.setNumberOfMentoree(mentor.getNumberOfMentoree());

            // Handle the "free" field structure
            if (mentor.getFreePrice() != 0 && mentor.getFreeUnit() != null) {
                FreeDTO freeDTO = new FreeDTO();
                freeDTO.setPrice(mentor.getFreePrice());
                UnitDTO unitDTO = new UnitDTO();
                unitDTO.setName(mentor.getFreeUnit());
                freeDTO.setUnit(unitDTO);
                mentorDTO.setFree(freeDTO);
            } else {
                mentorDTO.setFree(null);
            }

            // Map related entities
            mentorDTO.setCertificates(mapCertificates(mentor.getCertificates()));
            mentorDTO.setExperiences(mapExperiences(mentor.getExperiences()));
            mentorDTO.setReviews(mapReviews(mentor.getReviews()));
            mentorDTO.setTimeSlots(mapTimeSlots(mentor.getTimeSlots()));
            mentorDTO.setCategories(mapCategories(mentor.getCategories()));

            mentorDTOs.add(mentorDTO);
        }

        return mentorDTOs;
    }

    public List<MentorDTO> getAlMentor() {
        List<Mentor> mentors = mentorRepository.findAll(); // Fetch all mentors
        List<MentorDTO> mentorDTOs = new ArrayList<>();

        for (Mentor mentor : mentors) {
            MentorDTO mentorDTO = new MentorDTO();
            mentorDTO.setId(mentor.getId());
            mentorDTO.setName(mentor.getName());
            mentorDTO.setEmail(mentor.getEmail());
            mentorDTO.setAvatarUrl(mentor.getAvatarUrl());
            mentorDTO.setBio(mentor.getBio());
            mentorDTO.setRole(mentor.getRole());
            mentorDTO.setVerified(mentor.getVerified());
            mentorDTO.setRate(mentor.getRate());
            mentorDTO.setNumberOfMentoree(mentor.getNumberOfMentoree());

            // Handle the "free" field structure
            if (mentor.getFreePrice() != 0 && mentor.getFreeUnit() != null) {
                FreeDTO freeDTO = new FreeDTO();
                freeDTO.setPrice(mentor.getFreePrice());
                UnitDTO unitDTO = new UnitDTO();
                unitDTO.setName(mentor.getFreeUnit());
                freeDTO.setUnit(unitDTO);
                mentorDTO.setFree(freeDTO);
            } else {
                mentorDTO.setFree(null);
            }

            // Map related entities
            mentorDTO.setCertificates(mapCertificates(mentor.getCertificates()));
            mentorDTO.setExperiences(mapExperiences(mentor.getExperiences()));
            mentorDTO.setReviews(mapReviews(mentor.getReviews()));
//            mentorDTO.setTimeSlots(mapTimeSlots(mentor.getTimeSlots()));
            mentorDTO.setCategories(mapCategories(mentor.getCategories()));

            mentorDTOs.add(mentorDTO);
        }

        return mentorDTOs;
    }


    public MentorDTO getMentorById(Long id) {
        Mentor mentor = mentorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mentor not found with id: " + id));

        MentorDTO mentorDTO = new MentorDTO();
        mentorDTO.setId(mentor.getId());
        mentorDTO.setName(mentor.getName());
        mentorDTO.setEmail(mentor.getEmail());
        mentorDTO.setAvatarUrl(mentor.getAvatarUrl());
        mentorDTO.setBio(mentor.getBio());
        mentorDTO.setRole(mentor.getRole());
        mentorDTO.setVerified(mentor.getVerified());
        mentorDTO.setRate(mentor.getRate());
        mentorDTO.setNumberOfMentoree(mentor.getNumberOfMentoree());

        // Handle the "free" field structure

        FreeDTO freeDTO = new FreeDTO();
        freeDTO.setPrice(mentor.getFreePrice());
        UnitDTO unitDTO = new UnitDTO();
        unitDTO.setName(mentor.getFreeUnit());
        freeDTO.setUnit(unitDTO);
        mentorDTO.setFree(freeDTO);


        // Map related entities
        mentorDTO.setCertificates(mapCertificates(mentor.getCertificates()));
        mentorDTO.setExperiences(mapExperiences(mentor.getExperiences()));
        mentorDTO.setReviews(mapReviews(mentor.getReviews()));
        mentorDTO.setTimeSlots(mapTimeSlots(mentor.getTimeSlots()));
        mentorDTO.setCategories(mapCategories(mentor.getCategories()));

        return mentorDTO;
    }

    private List<CertificateDTO> mapCertificates(List<Certificate> certificates) {
        return certificates.stream().map(cert -> {
            CertificateDTO dto = new CertificateDTO();
            dto.setId(cert.getId());
            dto.setName(cert.getName());
            dto.setProvide_by(cert.getProvideBy());
            dto.setCreate_date(cert.getCreateDate());
            dto.setImage_url(cert.getImageUrl());
            dto.setMentor_id(cert.getMentor().getId());
            return dto;
        }).collect(Collectors.toList());
    }

    private List<ExperienceDTO> mapExperiences(List<Experience> experiences) {
        return experiences.stream().map(exp -> {
            ExperienceDTO dto = new ExperienceDTO();
            dto.setId(exp.getId());
            dto.setRole(exp.getRole());
            dto.setCompany_name(exp.getCompanyName());
            dto.setStart_date(String.valueOf(exp.getStartDate()));
            dto.setEnd_date(String.valueOf(exp.getEndDate()));
            dto.setDescription(exp.getDescription());
            dto.setMentor_id(exp.getMentor().getId());
            return dto;
        }).collect(Collectors.toList());
    }

    private List<ReviewDTO> mapReviews(List<Reviews> reviews) {
        return reviews.stream().map(review -> {
            ReviewDTO dto = new ReviewDTO();
            dto.setId(review.getId());
            dto.setMessage(review.getMessage());
            dto.setCreate_date(review.getCreateDate());
            dto.setCreated_by_id(review.getCreatedById());
            dto.setMentor_id(review.getMentor().getId());
            return dto;
        }).collect(Collectors.toList());
    }

    private List<TimeSlotDTO> mapTimeSlots(List<FixedTimeSlot> timeSlots) {
        return timeSlots.stream().map(slot -> {
            TimeSlotDTO dto = new TimeSlotDTO();
            dto.setId(slot.getId());
            dto.setTimeStart(String.valueOf(slot.getTimeStart()));
            dto.setTimeEnd(String.valueOf(slot.getTimeEnd()));
            dto.setMentorId(slot.getMentor().getId());
            return dto;
        }).collect(Collectors.toList());
    }

    private List<CategoryDTO> mapCategories(List<Category> categories) {
        return categories.stream().map(category -> {
            CategoryDTO dto = new CategoryDTO();
            dto.setId(category.getId());
            dto.setName(category.getName());
            dto.setIcon(category.getIcon());
            return dto;
        }).collect(Collectors.toList());
    }


    // In the MentorService class:
//    public String addMentor(Mentor mentor) {
//        // Insert the mentor data into the mentors table
//        String sql = "INSERT INTO mentors (name, email, avatar_url, bio, role, free_price, free_unit, verified, rate, number_of_mentoree) " +
//                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";
//
//        Long mentorId = jdbcTemplate.queryForObject(sql, Long.class,
//                mentor.getName(),
//                mentor.getEmail(),
//                mentor.getAvatarUrl(),
//                mentor.getBio(),
//                mentor.getRole(),
//                mentor.getFreePrice(),
//                mentor.getFreeUnit(),
//                mentor.getVerified(),
//                mentor.getRate(),
//                mentor.getNumberOfMentoree());
//
//        mentor.setId(mentorId);
//
//        // Insert the certificates related to this mentor
//        if (mentor.getCertificates() != null) {
//            for (Certificate certificate : mentor.getCertificates()) {
//                String certSql = "INSERT INTO certificates (mentor_id, name, provide_by, create_date, image_url) " +
//                        "VALUES (?, ?, ?, ?, ?)";
//                jdbcTemplate.update(certSql, mentorId, certificate.getName(), certificate.getProvideBy(), certificate.getCreateDate(), certificate.getImageUrl());
//            }
//        }
//
//        // Insert the experiences related to this mentor
//        if (mentor.getExperiences() != null) {
//            for (Experience experience : mentor.getExperiences()) {
//                String expSql = "INSERT INTO experiences (mentor_id, role, company_name, start_date, end_date, description) " +
//                        "VALUES (?, ?, ?, ?, ?, ?)";
//                jdbcTemplate.update(expSql, mentorId, experience.getRole(), experience.getCompanyName(), experience.getStartDate(), experience.getEndDate(), experience.getDescription());
//            }
//        }
//
//        // Insert the list of fixed time slots related to this mentor
//        if (mentor.getTimeSlots() != null && !mentor.getTimeSlots().isEmpty()) {
//            for (FixedTimeSlot slot : mentor.getTimeSlots()) {
//                if (slot.getTimeStart() == null || slot.getTimeEnd() == null) {
//                    throw new IllegalArgumentException("Fixed time slot start and end times cannot be null.");
//                }
//
//                System.out.println("Fixed Time Slot Details:");
//                System.out.println("Time Start: " + slot.getTimeStart());
//                System.out.println("Time End: " + slot.getTimeEnd());
//
//                // Insert the fixed time slot into the database
//                String slotSql = "INSERT INTO fixed_time_slots (mentor_id, time_start, time_end) VALUES (?, ?, ?)";
//                jdbcTemplate.update(slotSql, mentorId, slot.getTimeStart(), slot.getTimeEnd());
//            }
//        }
//
//        // Handle categories for this mentor
//        if (mentor.getCategories() != null) {
//            for (Category category : mentor.getCategories()) {
//                // Ensure the category exists in the categories table (if not already present)
//                String fetchCategorySql = "SELECT id FROM categories WHERE name = ?";
//                Long categoryId;
//                try {
//                    categoryId = jdbcTemplate.queryForObject(fetchCategorySql, Long.class, category.getName());
//                } catch (EmptyResultDataAccessException e) {
//                    // If the category does not exist, insert it
//                    String insertCategorySql = "INSERT INTO categories (name) VALUES (?) RETURNING id";
//                    categoryId = jdbcTemplate.queryForObject(insertCategorySql, Long.class, category.getName());
//                }
//
//                // Insert into mentor_categories table
//                String mentorCategorySql = "INSERT INTO mentor_categories (mentor_id, category_id) VALUES (?, ?)";
//                jdbcTemplate.update(mentorCategorySql, mentorId, categoryId);
//            }
//        }
//        // Return a success message
//        return "Mentor added successfully!";
//    }
//
//    // Update Mentor's general information
//    public int updateMentorInfo(Long mentorId, String name, String email, String avatarUrl, String bio,
//                                String role, Double freePrice, String freeUnit, Boolean verified,
//                                Double rate, Integer numberOfMentoree) {
//        String sql = "UPDATE mentors SET name = ?, email = ?, avatar_url = ?, bio = ?, role = ?, free_price = ?, " +
//                "free_unit = ?, verified = ?, rate = ?, number_of_mentoree = ? WHERE id = ?";
//        return jdbcTemplate.update(sql, name, email, avatarUrl, bio, role, freePrice, freeUnit, verified, rate, numberOfMentoree, mentorId);
//    }
//
//
//    // Method to update fixed time slots
//    public void updateFixedTimeSlots(Long mentorId, List<FixedTimeSlot> updatedTimeSlots) {
//        try {
//            // Delete existing fixed time slots for the mentor
//            String deleteSql = "DELETE FROM fixed_time_slots WHERE mentor_id = ?";
//            jdbcTemplate.update(deleteSql, mentorId);
//
//            // Insert the new list of fixed time slots
//            if (updatedTimeSlots != null) {
//                String insertSql = "INSERT INTO fixed_time_slots (mentor_id, time_start, time_end) VALUES (?, ?, ?)";
//                for (FixedTimeSlot timeSlot : updatedTimeSlots) {
//                    jdbcTemplate.update(
//                            insertSql,
//                            mentorId,
//                            timeSlot.getTimeStart(),
//                            timeSlot.getTimeEnd()
//                    );
//                }
//            }
//        } catch (Exception e) {
//            System.out.println("Error updating fixed time slots: " + e.getMessage());
//            e.printStackTrace();  // Print stack trace to help with debugging
//        }
//    }


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
//    public int updateCertificates(Long mentorId, List<Certificate> certificates) {
//        // First delete existing certificates
//        String deleteSql = "DELETE FROM certificates WHERE mentor_id = ?";
//        jdbcTemplate.update(deleteSql, mentorId);
//
//        // Insert new certificates
//        String insertSql = "INSERT INTO certificates (name, provide_by, create_date, image_url, mentor_id) " +
//                "VALUES (?, ?, ?, ?, ?)";
//        for (Certificate certificate : certificates) {
//            jdbcTemplate.update(insertSql, certificate.getName(), certificate.getProvideBy(),
//                    certificate.getCreateDate(), certificate.getImageUrl(), mentorId);
//        }
//        return certificates.size();  // Return number of updated certificates
//    }
//
//    // Update Experience for a Mentor
//    public int updateExperience(Long mentorId, List<Experience> experiences) {
//        // First delete existing experiences
//        String deleteSql = "DELETE FROM experiences WHERE mentor_id = ?";
//        jdbcTemplate.update(deleteSql, mentorId);
//
//        // Insert new experiences
//        String insertSql = "INSERT INTO experiences (role, company_name, start_date, end_date, description, mentor_id) " +
//                "VALUES (?, ?, ?, ?, ?, ?)";
//        for (Experience experience : experiences) {
//            jdbcTemplate.update(insertSql, experience.getRole(), experience.getCompanyName(),
//                    experience.getStartDate(), experience.getEndDate(), experience.getDescription(), mentorId);
//        }
//        return experiences.size();  // Return number of updated experiences
//    }
//
//    // Update Mentor Categories
//    public int updateCategories(Long mentorId, List<Category> categories) {
//        // First delete existing categories for the given mentor
//        String deleteSql = "DELETE FROM mentor_categories WHERE mentor_id = ?";
//        jdbcTemplate.update(deleteSql, mentorId);
//
//        // Insert new categories based on name-to-id mapping
//        String insertSql = "INSERT INTO mentor_categories (mentor_id, category_id) VALUES (?, ?)";
//
//        // Map category names to ids using the categories table
//        for (Category category : categories) {
//            String categoryName = category.getName();
//
//            // Query to fetch the category_id for the category name
//            String selectCategoryIdSql = "SELECT id FROM categories WHERE name = ?";
//            Integer categoryId = jdbcTemplate.queryForObject(selectCategoryIdSql, Integer.class, categoryName);
//
//            if (categoryId != null) {
//                // Insert into mentor_categories table
//                jdbcTemplate.update(insertSql, mentorId, categoryId);
//            } else {
//                System.out.println("Category with name " + categoryName + " not found in categories table.");
//            }
//        }
//        return categories.size();  // Return the number of successfully updated categories
//    }




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


//    public void deleteMentor(Long mentorId) {
//        // Delete from mentor_categories
//        jdbcTemplate.update("DELETE FROM mentor_categories WHERE mentor_id = ?", mentorId);
//        System.out.println("SUCCESS");
//
//        // Delete from certificates
//        jdbcTemplate.update("DELETE FROM certificates WHERE mentor_id = ?", mentorId);
//
//
//        // Delete from experiences
//        jdbcTemplate.update("DELETE FROM experiences WHERE mentor_id = ?", mentorId);
//
//        // Delete from reviews
//        jdbcTemplate.update("DELETE FROM reviews WHERE mentor_id = ?", mentorId);
//
//        // Delete from fixed_time_slots
//        jdbcTemplate.update("DELETE FROM fixed_time_slots WHERE mentor_id = ?", mentorId);
//
//        // Finally, delete from mentors
//        jdbcTemplate.update("DELETE FROM mentors WHERE id = ?", mentorId);
//    }







//    public List<Map<String, Object>> getAllMentors() {
//        String mentorQuery = """
//        SELECT
//            m.id AS mentor_id, m.name, m.email, m.avatar_url, m.bio, m.role,
//            m.free_price, m.free_unit, m.verified, m.rate, m.number_of_mentoree
//        FROM mentors m
//    """;
//
//        List<Map<String, Object>> mentors = jdbcTemplate.query(mentorQuery, (rs, rowNum) -> {
//            Map<String, Object> mentor = new HashMap<>();
//            Long mentorId = rs.getLong("mentor_id");
//
//            mentor.put("id", mentorId);
//            mentor.put("name", rs.getString("name"));
//            mentor.put("email", rs.getString("email"));
//            mentor.put("avatarUrl", rs.getString("avatar_url"));
//            mentor.put("bio", rs.getString("bio"));
//            mentor.put("role", rs.getString("role"));
//            mentor.put("free", Map.of(
//                    "price", rs.getDouble("free_price"),
//                    "unit", Map.of("name", rs.getString("free_unit"))
//            ));
//            mentor.put("verified", rs.getBoolean("verified"));
//            mentor.put("rate", rs.getDouble("rate"));
//            mentor.put("numberOfMentoree", rs.getInt("number_of_mentoree"));
//
//            mentor.put("experiences", getMentorExperiences(mentorId));
//            mentor.put("certificates", getMentorCertificates(mentorId));
//            mentor.put("reviews", getMentorReviews(mentorId));
//            mentor.put("categories", getMentorCategories(mentorId));
//            mentor.put("timeSlots", getTimeSlots(mentorId)); // Replace teachingSchedules with timeSlots
//
//            return mentor;
//        });
//
//        return mentors;
//    }
//
//    // New getTimeSlots Method
//    private List<Map<String, Object>> getTimeSlots(Long mentorId) {
//        String timeSlotQuery = """
//        SELECT
//            id AS time_slot_id, time_start, time_end, mentor_id
//        FROM fixed_time_slots
//        WHERE mentor_id = ?
//    """;
//
//        return jdbcTemplate.query(timeSlotQuery, new Object[]{mentorId}, (rs, rowNum) -> {
//            Map<String, Object> timeSlot = new HashMap<>();
//            timeSlot.put("id", rs.getLong("time_slot_id"));
//            timeSlot.put("timeStart", rs.getTime("time_start").toString());
//            timeSlot.put("timeEnd", rs.getTime("time_end").toString());
//            timeSlot.put("mentorId", rs.getLong("mentor_id")); // Include mentorId in the result
//            return timeSlot;
//        });
//    }
//
//
//    private List<Map<String, Object>> getMentorExperiences(Long mentorId) {
//        String query = """
//            SELECT id, mentor_id, role, company_name, start_date, end_date, description
//            FROM experiences WHERE mentor_id = ?
//        """;
//
//        return jdbcTemplate.query(query, new Object[]{mentorId}, (rs, rowNum) -> {
//            Map<String, Object> experience = new HashMap<>();
//            experience.put("id", rs.getLong("id"));
//            experience.put("mentor_id", rs.getLong("mentor_id"));
//            experience.put("role", rs.getString("role"));
//            experience.put("company_name", rs.getString("company_name"));
//            experience.put("start_date", rs.getDate("start_date"));
//            experience.put("end_date", rs.getDate("end_date"));
//            experience.put("description", rs.getString("description"));
//            return experience;
//        });
//    }
//
//    private List<Map<String, Object>> getMentorCertificates(Long mentorId) {
//        String query = """
//            SELECT id, mentor_id, name, provide_by, create_date, image_url
//            FROM certificates WHERE mentor_id = ?
//        """;
//
//        return jdbcTemplate.query(query, new Object[]{mentorId}, (rs, rowNum) -> {
//            Map<String, Object> certificate = new HashMap<>();
//            certificate.put("id", rs.getLong("id"));
//            certificate.put("mentor_id", rs.getLong("mentor_id"));
//            certificate.put("name", rs.getString("name"));
//            certificate.put("provide_by", rs.getString("provide_by"));
//            certificate.put("create_date", rs.getDate("create_date"));
//            certificate.put("image_url", rs.getString("image_url"));
//            return certificate;
//        });
//    }
//
//    private List<Map<String, Object>> getMentorReviews(Long mentorId) {
//        String query = """
//            SELECT id, mentor_id, message, create_date, created_by_id
//            FROM reviews WHERE mentor_id = ?
//        """;
//
//        return jdbcTemplate.query(query, new Object[]{mentorId}, (rs, rowNum) -> {
//            Map<String, Object> review = new HashMap<>();
//            review.put("id", rs.getLong("id"));
//            review.put("mentor_id", rs.getLong("mentor_id"));
//            review.put("message", rs.getString("message"));
//            review.put("create_date", rs.getTimestamp("create_date"));
//            review.put("created_by_id", rs.getString("created_by_id"));
//            return review;
//        });
//    }
//
//    private List<Map<String, Object>> getMentorCategories(Long mentorId) {
//        String query = """
//            SELECT c.id, c.name, c.icon
//            FROM categories c
//            JOIN mentor_categories mc ON c.id = mc.category_id
//            WHERE mc.mentor_id = ?
//        """;
//
//        return jdbcTemplate.query(query, new Object[]{mentorId}, (rs, rowNum) -> {
//            Map<String, Object> category = new HashMap<>();
//            category.put("id", rs.getLong("id"));
//            category.put("name", rs.getString("name"));
//            category.put("icon", rs.getString("icon"));
//            return category;
//        });
//    }
//
//    private List<Map<String, Object>> getTeachingSchedules(Long mentorId) {
//        String query = """
//            SELECT id, date_start, time_start, time_end, booked, mentor_id
//            FROM teaching_schedule WHERE mentor_id = ?
//        """;
//
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//
//        return jdbcTemplate.query(query, new Object[]{mentorId}, (rs, rowNum) -> {
//            Map<String, Object> schedule = new HashMap<>();
//
//            // Correcting the order: fetch values from ResultSet and put them in the map
//            Timestamp dateStartTimestamp = rs.getTimestamp("date_start");
//            Timestamp timeStartTimestamp = rs.getTimestamp("time_start");
//            Timestamp timeEndTimestamp = rs.getTimestamp("time_end");
//
//            LocalDateTime dateStart = dateStartTimestamp.toLocalDateTime();
//            LocalDateTime timeStart = timeStartTimestamp.toLocalDateTime();
//            LocalDateTime timeEnd = timeEndTimestamp.toLocalDateTime();
//
//            // Populate the map with values
//            schedule.put("id", rs.getLong("id"));
//            schedule.put("date_start", dateStart.format(formatter));
//            schedule.put("time_start", timeStart.format(formatter));
//            schedule.put("time_end", timeEnd.format(formatter));
//            schedule.put("booked", rs.getBoolean("booked"));
//            schedule.put("mentor_id", rs.getLong("mentor_id"));
//
//            // For debugging, print out the schedule map
//            System.out.println(schedule);
//
//            return schedule;
//        });
//    }
//
//    public List<Map<String, Object>> getTeachingSchedulesByMentor(Long mentorId) {
//        // SQL query to filter by mentor_id
//        String sql = "SELECT id, date_start, time_start, time_end, booked, mentor_id FROM teaching_schedule WHERE mentor_id = ?";
//        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql, mentorId);
//
//        // Formatter for converting Timestamp to String
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//
//        // Process each row
//        for (Map<String, Object> schedule : result) {
//            // Convert Timestamp to LocalDateTime
//            Timestamp dateStartTimestamp = (Timestamp) schedule.get("date_start");
//            Timestamp timeStartTimestamp = (Timestamp) schedule.get("time_start");
//            Timestamp timeEndTimestamp = (Timestamp) schedule.get("time_end");
//
//            LocalDateTime dateStart = dateStartTimestamp.toLocalDateTime();
//            LocalDateTime timeStart = timeStartTimestamp.toLocalDateTime();
//            LocalDateTime timeEnd = timeEndTimestamp.toLocalDateTime();
//
//            // Format and update the schedule map
//            schedule.put("dateStart", dateStart.format(formatter));
//            schedule.put("timeStart", timeStart.format(formatter));
//            schedule.put("timeEnd", timeEnd.format(formatter));
//            schedule.put("booked", schedule.get("booked"));
//            schedule.put("mentorId", schedule.get("mentor_id"));
//
//            // Remove original keys if necessary
//            schedule.remove("date_start");
//            schedule.remove("time_start");
//            schedule.remove("time_end");
//            schedule.remove("mentor_id");
//        }
//
//        return result;
//    }


}
