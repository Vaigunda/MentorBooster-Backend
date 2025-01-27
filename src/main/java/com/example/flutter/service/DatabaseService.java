package com.example.flutter.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import java.util.*;
import java.lang.*;
import org.springframework.dao.DataAccessException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


import java.math.BigDecimal;

@Service
public class DatabaseService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private MentorService mentorService;

    // Get details of all mentors
    public List<Map<String, Object>> getAllMentors() {
        String sql = "SELECT * FROM mentors";
        List<Map<String, Object>> mentorList = jdbcTemplate.queryForList(sql);

        List<Map<String, Object>> response = new ArrayList<>();
        for (Map<String, Object> mentor : mentorList) {
            Long mentorId = ((Number) mentor.get("id")).longValue();

            // Format mentor info to match the desired response structure
            Map<String, Object> formattedMentor = new HashMap<>();

            // Use the mentor ID directly without prefix
            formattedMentor.put("id", mentor.get("id"));
            formattedMentor.put("name", mentor.get("name"));
            formattedMentor.put("avatarUrl", mentor.get("avatar_url"));
            formattedMentor.put("verified", mentor.get("verified"));
            formattedMentor.put("role", mentor.get("role"));
            formattedMentor.put("numberOfMentoree", mentor.get("number_of_mentoree"));
            formattedMentor.put("rate", mentor.get("rate"));
            formattedMentor.put("bio", mentor.get("bio"));

            // Format mentor's free pricing
            Map<String, Object> freePricing = new HashMap<>();
            freePricing.put("price", mentor.get("free_price"));

            // Assuming the "unit" is a string like "hour" or "session"
            Map<String, Object> unit = new HashMap<>();
            unit.put("name", mentor.get("free_unit"));
            freePricing.put("unit", unit);

            formattedMentor.put("free", freePricing);

            // Get mentor categories
            List<Map<String, Object>> categories = getMentorCategories(mentorId);
            formattedMentor.put("categories", categories);

            // Get mentor experiences
            List<Map<String, Object>> experiences = getMentorExperiences(mentorId);
            formattedMentor.put("experiences", experiences);

            // Get mentor reviews
            List<Map<String, Object>> reviews = getMentorReviews(mentorId);
            formattedMentor.put("reviews", reviews);

            // Get mentor certificates
            List<Map<String, Object>> certificates = getMentorCertificates(mentorId);
            formattedMentor.put("certificates", certificates);

            // Add the formatted mentor to response
            response.add(formattedMentor);
        }

        return response;
    }

    public List<Map<String, Object>> getAllMentorsInfo() {
        String sql = "SELECT * FROM mentors";
        List<Map<String, Object>> mentorList = jdbcTemplate.queryForList(sql);

        List<Map<String, Object>> response = new ArrayList<>();
        for (Map<String, Object> mentor : mentorList) {
            Long mentorId = ((Number) mentor.get("id")).longValue();

            // Format mentor info to match the desired response structure
            Map<String, Object> formattedMentor = new HashMap<>();

            // Use the mentor ID directly without prefix
            formattedMentor.put("id", mentor.get("id"));
            formattedMentor.put("name", mentor.get("name"));
            formattedMentor.put("avatarUrl", mentor.get("avatar_url"));
            formattedMentor.put("verified", mentor.get("verified"));
            formattedMentor.put("role", mentor.get("role"));
            formattedMentor.put("numberOfMentoree", mentor.get("number_of_mentoree"));
            formattedMentor.put("rate", mentor.get("rate"));
            formattedMentor.put("bio", mentor.get("bio"));

            // Format mentor's free pricing
            Map<String, Object> freePricing = new HashMap<>();
            freePricing.put("price", mentor.get("free_price"));

            // Assuming the "unit" is a string like "hour" or "session"
            Map<String, Object> unit = new HashMap<>();
            unit.put("name", mentor.get("free_unit"));
            freePricing.put("unit", unit);

            formattedMentor.put("free", freePricing);

            // Get mentor categories
            List<Map<String, Object>> categories = getMentorCategories(mentorId);
            formattedMentor.put("categories", categories);

            // Get mentor experiences
            List<Map<String, Object>> experiences = getMentorExperiences(mentorId);
            formattedMentor.put("experiences", experiences);

            // Get mentor reviews
            List<Map<String, Object>> reviews = getMentorReviews(mentorId);
            formattedMentor.put("reviews", reviews);

            // Get mentor certificates
            List<Map<String, Object>> certificates = getMentorCertificates(mentorId);
            formattedMentor.put("certificates", certificates);


            // Add the formatted mentor to response
            response.add(formattedMentor);
        }

        return response;
    }





    public List<Map<String, Object>> searchMentors(String keyword) {
        // SQL query to fetch mentor details with aggregated category names and avatar URL
        String query = """
            SELECT
                m.id AS mentorId,
                m.name AS name,
                m.number_of_mentoree AS mentees,
                m.rate AS rating,
                string_agg(c.name, ', ') AS skills,  -- Aggregated categories for display
                m.role AS role,
                m.avatar_url AS avatar  -- Fetch avatar_url as well
            FROM
                mentors m
            LEFT JOIN
                mentor_categories mc ON m.id = mc.mentor_id
            LEFT JOIN
                categories c ON mc.category_id = c.id
            WHERE
                m.name ILIKE ?
                OR m.role ILIKE ?
                OR c.name ILIKE ?  -- Searching in categories as well
            GROUP BY
                m.id
            ORDER BY
                m.number_of_mentoree DESC;
        """;

        String searchKeyword = "%" + keyword + "%";

        try {
            // Execute the query to get mentors and aggregated category names, including avatar_url
            List<Map<String, Object>> results = jdbcTemplate.queryForList(query,
                    searchKeyword, searchKeyword, searchKeyword);

            // Step 2: Iterate over the results to fetch and replace the aggregated category names
            for (Map<String, Object> mentor : results) {
                // Get mentor ID

                if (mentor.containsKey("mentorid")) {
                    mentor.put("mentorId", mentor.remove("mentorid"));
                }

                Object mentorIdObj = mentor.get("mentorId");

                // Check if the mentor ID is an instance of Long or Integer and cast accordingly
                Integer mentorId = null;
                if (mentorIdObj instanceof Long) {
                    mentorId = ((Long) mentorIdObj).intValue(); // Convert Long to Integer
                } else if (mentorIdObj instanceof Integer) {
                    mentorId = (Integer) mentorIdObj; // It's already an Integer
                }

                // SQL query to fetch individual categories for each mentor
                String categoryQuery = """
                    SELECT c.name AS skills
                    FROM mentor_categories mc
                    JOIN categories c ON mc.category_id = c.id
                    WHERE mc.mentor_id = ?
                """;

                // Fetch categories for the given mentor
                List<String> categories = jdbcTemplate.queryForList(categoryQuery, String.class, mentorId);

                // Replace the aggregated category names with the detailed list
                mentor.put("skills", categories);
            }
            // Return the updated mentor list with detailed categories and avatar_url
            return results;

        } catch (DataAccessException e) {
            // Log the exception for debugging
            System.err.println("Error executing query: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Database query failed", e);
        }
    }








    public List<Map<String, String>> getAllCategories() {
        // SQL query to fetch 'id', 'name', and 'icon' columns
        String sql = "SELECT id, name, icon FROM categories"; // Include 'icon' column in the query

        // Execute query to get results
        List<Map<String, Object>> resultList = jdbcTemplate.queryForList(sql);

        // Prepare the list to hold categories with id, name, and icon
        List<Map<String, String>> categories = new ArrayList<>();

        // Iterate over the result set and build the response
        for (Map<String, Object> row : resultList) {
            Map<String, String> category = new HashMap<>();
            category.put("id", row.get("id").toString()); // Convert 'id' to String if it's not
            category.put("name", (String) row.get("name"));

            // Directly get the icon from the database result
            String icon = (String) row.get("icon"); // Assuming the icon in the database is stored as a string
            category.put("icon", icon);

            // Add the category to the list
            categories.add(category);
        }

        return categories;
    }




    public List<Map<String, Object>> getTeachingSchedules() {
        String sql = "SELECT id, date_start, time_start, time_end, booked, mentor_id FROM teaching_schedule";
        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql);

        // Use a formatter with a space instead of 'T'
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // Format and print each row
        for (Map<String, Object> schedule : result) {
            // Convert Timestamp to LocalDateTime
            Timestamp dateStartTimestamp = (Timestamp) schedule.get("date_start");
            Timestamp timeStartTimestamp = (Timestamp) schedule.get("time_start");
            Timestamp timeEndTimestamp = (Timestamp) schedule.get("time_end");

            LocalDateTime dateStart = dateStartTimestamp.toLocalDateTime();
            LocalDateTime timeStart = timeStartTimestamp.toLocalDateTime();
            LocalDateTime timeEnd = timeEndTimestamp.toLocalDateTime();

            // Format the LocalDateTime as a string with a space instead of 'T'
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


    public List<Map<String, Object>> getConnectMethods() {
        String sql = "SELECT * FROM connect_methods";
        return jdbcTemplate.queryForList(sql);
    }


    // Get categories for a specific mentor
    public List<Map<String, Object>> getMentorCategories(Long mentorId) {
        String sql = "SELECT c.* FROM categories c " +
                "JOIN mentor_categories mc ON c.id = mc.category_id " +
                "WHERE mc.mentor_id = ?";
        return jdbcTemplate.queryForList(sql, mentorId);
    }

    // Get experiences for a specific mentor
    public List<Map<String, Object>> getMentorExperiences(Long mentorId) {
        String sql = "SELECT * FROM experiences WHERE mentor_id = ?";
        return jdbcTemplate.queryForList(sql, mentorId);
    }

    // Get reviews for a specific mentor
    public List<Map<String, Object>> getMentorReviews(Long mentorId) {
        String sql = "SELECT * FROM reviews WHERE mentor_id = ?";
        return jdbcTemplate.queryForList(sql, mentorId);
    }

    // Get certificates for a specific mentor
    public List<Map<String, Object>> getMentorCertificates(Long mentorId) {
        String sql = "SELECT * FROM certificates WHERE mentor_id = ?";
        return jdbcTemplate.queryForList(sql, mentorId);
    }

    public List<Map<String, Object>> getMentorTeachingSchedules(Long mentorId) {
        String sql = "SELECT * FROM teaching_schedule WHERE mentor_id = ?";
        return jdbcTemplate.queryForList(sql, mentorId);
    }

    // Method to fetch verified mentors with required details, grouping by mentor
    public List<Map<String, Object>> getVerifiedMentors() {
        String sql = "SELECT m.name, m.avatar_url, m.number_of_mentoree, c.name AS category_name " +
                "FROM mentors m " +
                "JOIN mentor_categories mc ON m.id = mc.mentor_id " +
                "JOIN categories c ON mc.category_id = c.id " +
                "WHERE m.verified = true " +
                "ORDER BY m.number_of_mentoree DESC"; // Sort by number_of_mentoree in descending order

        List<Map<String, Object>> results = jdbcTemplate.queryForList(sql);

        // Group mentors by name and combine categories
        Map<String, Map<String, Object>> mentorMap = new LinkedHashMap<>();

        for (Map<String, Object> row : results) {
            String name = (String) row.get("name");
            String categoryName = (String) row.get("category_name");

            // If mentor already exists, append the category, otherwise create a new entry
            if (mentorMap.containsKey(name)) {
                Map<String, Object> mentorData = mentorMap.get(name);
                List<String> categories = (List<String>) mentorData.get("category_names");
                categories.add(categoryName);
            } else {
                Map<String, Object> mentorData = new HashMap<>();
                mentorData.put("name", name);
                mentorData.put("avatar_url", row.get("avatar_url"));
                mentorData.put("number_of_mentoree", row.get("number_of_mentoree"));
                mentorData.put("category_names", new ArrayList<>(Collections.singletonList(categoryName)));
                mentorMap.put(name, mentorData);
            }
        }

        // Return a list of grouped mentor data
        return new ArrayList<>(mentorMap.values());
    }

    // Method to fetch top mentors sorted by number of mentees, grouping by mentor
    public List<Map<String, Object>> getTopMentors() {
        String sql = "SELECT m.id, m.name, m.avatar_url, m.number_of_mentoree, m.rate, c.name AS category_name " +
                "FROM mentors m " +
                "JOIN mentor_categories mc ON m.id = mc.mentor_id " +
                "JOIN categories c ON mc.category_id = c.id " +
                "ORDER BY m.number_of_mentoree DESC"; // Sorting by number of mentees

        List<Map<String, Object>> results = jdbcTemplate.queryForList(sql);

        // Group mentors by name and combine categories
        Map<String, Map<String, Object>> mentorMap = new LinkedHashMap<>();

        for (Map<String, Object> row : results) {
            String id = row.get("id").toString();
            String name = (String) row.get("name");
            String categoryName = (String) row.get("category_name");

            // If mentor already exists, append the category; otherwise, create a new entry
            if (mentorMap.containsKey(id)) {
                Map<String, Object> mentorData = mentorMap.get(id);
                List<String> categories = (List<String>) mentorData.get("category_names");
                categories.add(categoryName);
            } else {
                Map<String, Object> mentorData = new HashMap<>();
                mentorData.put("id", id);
                mentorData.put("name", name);
                mentorData.put("avatar_url", row.get("avatar_url"));
                mentorData.put("number_of_mentoree", row.get("number_of_mentoree"));
                mentorData.put("rate", row.get("rate"));
                mentorData.put("category_names", new ArrayList<>(Collections.singletonList(categoryName)));
                mentorMap.put(id, mentorData);
            }
        }

        // Return a list of grouped mentor data
        return new ArrayList<>(mentorMap.values());
    }


    // Method to fetch top-rated mentors sorted by rating, grouping by mentor
    public List<Map<String, Object>> getTopRatedMentors() {
        String sql = "SELECT m.name, m.avatar_url, m.number_of_mentoree, m.rate, c.name AS category_name " +
                "FROM mentors m " +
                "JOIN mentor_categories mc ON m.id = mc.mentor_id " +
                "JOIN categories c ON mc.category_id = c.id " +
                "ORDER BY m.rate DESC";

        List<Map<String, Object>> results = jdbcTemplate.queryForList(sql);

        // Group mentors by name and combine categories
        Map<String, Map<String, Object>> mentorMap = new LinkedHashMap<>();

        for (Map<String, Object> row : results) {
            String name = (String) row.get("name");
            String categoryName = (String) row.get("category_name");

            // If mentor already exists, append the category, otherwise create a new entry
            if (mentorMap.containsKey(name)) {
                Map<String, Object> mentorData = mentorMap.get(name);
                List<String> categories = (List<String>) mentorData.get("category_names");
                categories.add(categoryName);
            } else {
                Map<String, Object> mentorData = new HashMap<>();
                mentorData.put("name", name);
                mentorData.put("avatar_url", row.get("avatar_url"));
                mentorData.put("number_of_mentoree", row.get("number_of_mentoree"));
                mentorData.put("rate", row.get("rate"));
                mentorData.put("category_names", new ArrayList<>(Collections.singletonList(categoryName)));
                mentorMap.put(name, mentorData);
            }
        }

        // Return a list of grouped mentor data
        return new ArrayList<>(mentorMap.values());
    }

    // Get all data about a mentor (mentor details, categories, experiences, reviews, certificates)
    public Map<String, Object> getAllDataForMentor(Long mentorId) {
        Map<String, Object> mentor = new HashMap<>();
        // Get mentor details
        String mentorSql = "SELECT * FROM mentors WHERE id = ?";
        Map<String, Object> mentorData = jdbcTemplate.queryForMap(mentorSql, mentorId);

        mentor.put("id", mentorId);
        mentor.put("name", mentorData.get("name"));
        mentor.put("email", mentorData.get("email"));
        mentor.put("avatarUrl", mentorData.get("avatar_url"));
        mentor.put("bio", mentorData.get("bio"));
        mentor.put("role", mentorData.get("role"));
        mentor.put("free", Map.of(
                "price", mentorData.get("free_price"),
                "unit", Map.of("name", mentorData.get("free_unit"))
        ));
        mentor.put("verified", mentorData.get("verified"));
        mentor.put("rate", mentorData.get("rate"));
        mentor.put("numberOfMentoree", mentorData.get("number_of_mentoree"));

        // Get categories for the mentor
        String categoriesSql = "SELECT c.* FROM categories c " +
                "JOIN mentor_categories mc ON c.id = mc.category_id " +
                "WHERE mc.mentor_id = ?";
        List<Map<String, Object>> categories = jdbcTemplate.queryForList(categoriesSql, mentorId);

        // Get experiences for the mentor
        String experiencesSql = "SELECT * FROM experiences WHERE mentor_id = ?";
        List<Map<String, Object>> experiences = jdbcTemplate.queryForList(experiencesSql, mentorId);

        // Get reviews for the mentor
        String reviewsSql = "SELECT * FROM reviews WHERE mentor_id = ?";
        List<Map<String, Object>> reviews = jdbcTemplate.queryForList(reviewsSql, mentorId);

        // Get certificates for the mentor
        String certificatesSql = "SELECT * FROM certificates WHERE mentor_id = ?";
        List<Map<String, Object>> certificates = jdbcTemplate.queryForList(certificatesSql, mentorId);

        // Get timeslots for the mentor
        String timeslotsSql = "SELECT * FROM fixed_time_slots WHERE mentor_id = ?";
        List<Map<String, Object>> timeslots = jdbcTemplate.queryForList(timeslotsSql, mentorId);

        // Combine all the data into one map
        mentor.put("experiences", mentorService.getMentorExperiences(mentorId));
        mentor.put("certificates", mentorService.getMentorCertificates(mentorId));
        mentor.put("reviews", mentorService.getMentorReviews(mentorId));
        mentor.put("categories", mentorService.getMentorCategories(mentorId));
        mentor.put("timeSlots", mentorService.getTimeSlots(mentorId));

        return mentor;
    }
}
