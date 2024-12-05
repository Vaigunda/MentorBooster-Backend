package com.example.flutter.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "certificates")  // Ensure the table name matches the one in your database
public class Certificate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // Primary key, auto-generated

    @Column(name = "name", nullable = false)  // Ensure the column name matches
    private String name;  // Name of the certificate

    @Column(name = "provide_by")  // Ensure the column name matches
    private String provideBy;  // Who provided the certificate

    @Column(name = "create_date")  // Ensure the column name matches
    private String createDate;  // The date the certificate was issued

    @Column(name = "image_url")  // Ensure the column name matches
    private String imageUrl;  // URL of the certificate image


    @ManyToOne  // Many certificates can belong to one mentor
    @JoinColumn(name = "mentor_id")  // Foreign key column in the certificates table
    private Mentor mentor;  // The mentor associated with this certificate



    // Getters and Setters
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

    public String getProvideBy() {
        return provideBy;
    }

    public void setProvideBy(String provideBy) {
        this.provideBy = provideBy;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Mentor getMentor() {
        return mentor;
    }

    public void setMentor(Mentor mentor) {
        this.mentor = mentor;
    }
}
