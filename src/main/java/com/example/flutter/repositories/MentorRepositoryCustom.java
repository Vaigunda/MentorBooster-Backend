package com.example.flutter.repositories;

import com.example.flutter.dto.MentorResponseDTO;
import com.example.flutter.dto.MentorSearchDTO;

import java.util.List;

public interface MentorRepositoryCustom {
    List<MentorSearchDTO> searchMentors(String keyword);

    List<MentorResponseDTO> getTopMentors();

    List<MentorResponseDTO> getTopRatedMentors();

    List<MentorResponseDTO> getVerifiedMentors();
}
