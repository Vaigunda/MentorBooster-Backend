package com.example.flutter.repositories;


import com.example.flutter.dto.MentorSearchDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.util.*;


import com.example.flutter.dto.MentorResponseDTO;

import java.util.stream.Collectors;

@Repository
public class MentorRepositoryImpl implements MentorRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<MentorSearchDTO> searchMentors(String keyword) {
        String searchQuery = """
        SELECT
            m.id AS mentorId,
            m.name AS name,
            m.number_of_mentoree AS mentees,
            m.rate AS rating,
            m.role AS role,
            m.avatar_url AS avatar
        FROM
            mentors m
        LEFT JOIN
            mentor_categories mc ON m.id = mc.mentor_id
        LEFT JOIN
            categories c ON mc.category_id = c.id
        WHERE
            m.name ILIKE :keyword
            OR m.role ILIKE :keyword
            OR c.name ILIKE :keyword  -- Searching in categories
        GROUP BY
            m.id
        ORDER BY
            m.number_of_mentoree DESC
    """;

        Query query = entityManager.createNativeQuery(searchQuery);
        query.setParameter("keyword", "%" + keyword + "%");

        List<Object[]> results = query.getResultList();
        List<MentorSearchDTO> mentorDTOs = new ArrayList<>();

        for (Object[] row : results) {
            Long mentorId = ((Number) row[0]).longValue();
            String name = (String) row[1];
            Integer mentees = ((Number) row[2]).intValue();
            Double rating = (Double) row[3];
            String role = (String) row[4];
            String avatar = (String) row[5];

            // Fetch skills for the mentor
            String categoryQuery = """
            SELECT c.name
            FROM mentor_categories mc
            JOIN categories c ON mc.category_id = c.id
            WHERE mc.mentor_id = :mentorId
        """;

            Query categoryResult = entityManager.createNativeQuery(categoryQuery);
            categoryResult.setParameter("mentorId", mentorId);

            @SuppressWarnings("unchecked")
            List<String> skills = categoryResult.getResultList();

            mentorDTOs.add(new MentorSearchDTO(mentorId, name, mentees, rating, skills, role, avatar));
        }

        return mentorDTOs;
    }

    @Override
    public List<MentorResponseDTO> getTopMentors() {
        String sql = """
            SELECT m.id, m.name, m.avatar_url, m.number_of_mentoree, m.rate, c.name AS category_name
            FROM mentors m
            JOIN mentor_categories mc ON m.id = mc.mentor_id
            JOIN categories c ON mc.category_id = c.id
            ORDER BY m.number_of_mentoree DESC
        """;

        Query query = entityManager.createNativeQuery(sql);
        List<Object[]> results = query.getResultList();

        // Group data by mentor ID
        Map<Long, MentorResponseDTO> mentorMap = new LinkedHashMap<>();

        for (Object[] row : results) {
            Long mentorId = ((Number) row[0]).longValue();
            String name = (String) row[1];
            String avatarUrl = (String) row[2];
            Integer numberOfMentoree = ((Number) row[3]).intValue();
            Double rate = (Double) row[4];
            String categoryName = (String) row[5];

            MentorResponseDTO dto = mentorMap.getOrDefault(mentorId, new MentorResponseDTO(
                    String.valueOf(mentorId),
                    name,
                    avatarUrl,
                    rate,
                    numberOfMentoree,
                    new ArrayList<>()
            ));
            dto.getCategory_names().add(categoryName);
            mentorMap.put(mentorId, dto);
        }

        return new ArrayList<>(mentorMap.values());
    }

    @Override
    public List<MentorResponseDTO> getTopRatedMentors() {
        String sql = """
        SELECT m.name, m.avatar_url, m.number_of_mentoree, m.rate, c.name AS category_name
        FROM mentors m
        JOIN mentor_categories mc ON m.id = mc.mentor_id
        JOIN categories c ON mc.category_id = c.id
        ORDER BY m.rate DESC
    """;

        Query query = entityManager.createNativeQuery(sql);
        List<Object[]> results = query.getResultList();

        // Group data by mentor name (since we're not using id)
        Map<String, MentorResponseDTO> mentorMap = new LinkedHashMap<>();

        for (Object[] row : results) {
            String name = (String) row[0];
            String avatarUrl = (String) row[1];
            Integer numberOfMentoree = ((Number) row[2]).intValue();
            Double rate = (Double) row[3];
            String categoryName = (String) row[4];

            // Create or get the existing DTO for the mentor
            MentorResponseDTO dto = mentorMap.getOrDefault(name, new MentorResponseDTO(
                    "",  // id is not being used here
                    name,
                    avatarUrl,
                    rate,
                    numberOfMentoree,
                    new ArrayList<>()
            ));

            // Add category to the mentor's category list
            dto.getCategory_names().add(categoryName);
            mentorMap.put(name, dto);
        }

        // Return a list of grouped mentor data
        return new ArrayList<>(mentorMap.values());
    }

    @Override
    public List<MentorResponseDTO> getVerifiedMentors() {
        String sql = """
            SELECT m.id, m.name, m.avatar_url, m.number_of_mentoree, m.rate, c.name AS category_name
            FROM mentors m
            JOIN mentor_categories mc ON m.id = mc.mentor_id
            JOIN categories c ON mc.category_id = c.id
            WHERE m.verified = true
            ORDER BY m.number_of_mentoree DESC
        """;

        Query query = entityManager.createNativeQuery(sql);
        List<Object[]> results = query.getResultList();

        // Group data by mentor ID
        Map<Long, MentorResponseDTO> mentorMap = new LinkedHashMap<>();

        for (Object[] row : results) {
            Long mentorId = ((Number) row[0]).longValue();
            String name = (String) row[1];
            String avatarUrl = (String) row[2];
            Integer numberOfMentoree = ((Number) row[3]).intValue();
            Double rate = (Double) row[4];
            String categoryName = (String) row[5];

            // Create or get the existing DTO for the mentor
            MentorResponseDTO dto = mentorMap.getOrDefault(mentorId, new MentorResponseDTO(
                    String.valueOf(mentorId),
                    name,
                    avatarUrl,
                    rate,
                    numberOfMentoree,
                    new ArrayList<>()
            ));

            // Add category to the mentor's category list
            dto.getCategory_names().add(categoryName);
            mentorMap.put(mentorId, dto);
        }

        // Return a list of grouped mentor data
        return new ArrayList<>(mentorMap.values());
    }


}
