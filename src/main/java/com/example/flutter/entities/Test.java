package com.example.flutter.entities;

import java.time.LocalDateTime;

public class Test {
    private LocalDateTime dateStart;
    private LocalDateTime timeStart;
    private LocalDateTime timeEnd;
    private Boolean booked;

    // Getters and Setters
    public LocalDateTime getDateStart() {
        return dateStart;
    }

    public void setDateStart(LocalDateTime dateStart) {
        this.dateStart = dateStart;
    }

    public LocalDateTime getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(LocalDateTime timeStart) {
        this.timeStart = timeStart;
    }

    public LocalDateTime getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(LocalDateTime timeEnd) {
        this.timeEnd = timeEnd;
    }

    public Boolean getBooked() {
        return booked;
    }

    public void setBooked(Boolean booked) {
        this.booked = booked;
    }

    @Override
    public String toString() {
        return "Test{" +
                "dateStart=" + dateStart +
                ", timeStart=" + timeStart +
                ", timeEnd=" + timeEnd +
                ", booked=" + booked +
                '}';
    }
}
