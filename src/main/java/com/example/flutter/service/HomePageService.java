package com.example.flutter.service;

import com.example.flutter.dto.CategoryDTO;
import com.example.flutter.dto.MentorResponseDTO;
import com.example.flutter.entities.Category;
import com.example.flutter.entities.ConnectMethods;
import com.example.flutter.repositories.CategoryRepository;
import com.example.flutter.repositories.ConnectMethodsRepository;
import com.example.flutter.repositories.MentorRepositoryCustom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class HomePageService {

    @Autowired
    @Qualifier("mentorRepositoryImpl")  // Specify the correct bean here
    private MentorRepositoryCustom mentorRepositoryCustom;

    @Autowired
    private ConnectMethodsRepository connectMethodsRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    public List<MentorResponseDTO> getTopMentors() {
        return mentorRepositoryCustom.getTopMentors();
    }


    public List<MentorResponseDTO> getTopRatedMentors() {
        return mentorRepositoryCustom.getTopRatedMentors();
    }

    public List<MentorResponseDTO> getVerifiedMentors() {
        return mentorRepositoryCustom.getVerifiedMentors();
    }

    public List<CategoryDTO> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream()
                .map(category -> {
                    CategoryDTO categoryDTO = new CategoryDTO();
                    categoryDTO.setId(category.getId());
                    categoryDTO.setName(category.getName());
                    categoryDTO.setIcon(category.getIcon());
                    return categoryDTO;
                })
                .collect(Collectors.toList());
    }

    public List<ConnectMethods> getAllConnectMethods() {
        return connectMethodsRepository.findAll();
    }
}
