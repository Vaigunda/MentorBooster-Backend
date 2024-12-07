package com.example.flutter.entities;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "mentors")
public class Mentor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String avatarUrl;
    private String bio;
    private String role;
    private Double freePrice;
    private String freeUnit;
    private Boolean verified;
    private Double rate;
    private Integer numberOfMentoree;

    @OneToMany(mappedBy = "mentor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Certificate> certificates;

    @OneToMany(mappedBy = "mentor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Experience> experiences;

    @OneToMany(mappedBy = "mentor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FixedTimeSlot> timeSlots;


    @ManyToMany
    @JoinTable(
            name = "mentor_categories",
            joinColumns = @JoinColumn(name = "mentor_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<Category> categories;


    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Double getFreePrice() {
        return freePrice;
    }

    public void setFreePrice(Double freePrice) {
        this.freePrice = freePrice;
    }

    public String getFreeUnit() {
        return freeUnit;
    }

    public void setFreeUnit(String freeUnit) {
        this.freeUnit = freeUnit;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public Integer getNumberOfMentoree() {
        return numberOfMentoree;
    }

    public void setNumberOfMentoree(Integer numberOfMentoree) {
        this.numberOfMentoree = numberOfMentoree;
    }

    public List<Certificate> getCertificates() {
        return certificates;
    }

    public void setCertificates(List<Certificate> certificates) {
        this.certificates = certificates;
    }

    public List<Experience> getExperiences() {
        return experiences;
    }

    public void setExperiences(List<Experience> experiences) {
        this.experiences = experiences;
    }


    public List<FixedTimeSlot> getTimeSlots() {
        return timeSlots;
    }

    public void setTimeSlots(List<FixedTimeSlot> timeSlots) {
        this.timeSlots = timeSlots;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }
}
