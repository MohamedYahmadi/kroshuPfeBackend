package com.example.SpringSecurityKrushuPfeBakcned.Services;

import com.example.SpringSecurityKrushuPfeBakcned.Dto.*;
import com.example.SpringSecurityKrushuPfeBakcned.Entities.Department;
import com.example.SpringSecurityKrushuPfeBakcned.Entities.Indicator;
import com.example.SpringSecurityKrushuPfeBakcned.Entities.TeamMember;
import com.example.SpringSecurityKrushuPfeBakcned.Entities.User;
import com.example.SpringSecurityKrushuPfeBakcned.Respositories.DepartmentRepository;
import com.example.SpringSecurityKrushuPfeBakcned.Respositories.IndicatorRepository;
import com.example.SpringSecurityKrushuPfeBakcned.Respositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.example.SpringSecurityKrushuPfeBakcned.Util.DateUtil;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;


import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class IndicatorService {

    private final DepartmentRepository departmentRepository;
    private final IndicatorRepository indicatorRepository;
    private final UserRepository userRepository;

    public IndicatorService(DepartmentRepository departmentRepository, IndicatorRepository indicatorRepository, UserRepository userRepository) {
        this.departmentRepository = departmentRepository;
        this.indicatorRepository = indicatorRepository;
        this.userRepository = userRepository;
    }

    public ResponseEntity<String> createIndicator(CreateIndicatorDto indicatorDto) {
        Optional<Department> optionalDepartment = departmentRepository.findByName(indicatorDto.getDepartmentName());

        if (!optionalDepartment.isPresent()) {
            return ResponseEntity.badRequest().body("Department not found");
        }

        Department department = optionalDepartment.get();

        Indicator newIndicator = Indicator.builder()
                .name(indicatorDto.getName())
                .department(department)
                .targetPerWeek(indicatorDto.getTargetPerWeek())
                .build();

        indicatorRepository.save(newIndicator);

        return ResponseEntity.ok("Indicator created successfully");
    }





    public ResponseEntity<IndicatorValueResponseDTO> setIndicatorValue(SetIndicatorValueDTO requestDTO) {
        // 1. Find department by name
        Department department = departmentRepository.findByName(requestDTO.getDepartmentName())
                .orElse(null);

        if (department == null) {
            return ResponseEntity.badRequest().body(
                    IndicatorValueResponseDTO.builder()
                            .success(false)
                            .message("Department not found")
                            .build()
            );
        }

        Indicator indicator = indicatorRepository.findByNameAndDepartment(
                requestDTO.getIndicatorName(),
                department
        ).orElse(null);

        if (indicator == null) {
            return ResponseEntity.badRequest().body(
                    IndicatorValueResponseDTO.builder()
                            .success(false)
                            .message("Indicator not found in specified department")
                            .build()
            );
        }

        Date currentDate = new Date();

        boolean exists = indicator.getDailyValues().stream()
                .anyMatch(dv -> DateUtil.isSameDay(dv.getDay(), currentDate));

        if (exists) {
            return ResponseEntity.badRequest().body(
                    IndicatorValueResponseDTO.builder()
                            .success(false)
                            .message("Value for today already exists")
                            .build()
            );
        }

        indicator.addDailyValue(currentDate, requestDTO.getValue());
        indicatorRepository.save(indicator);

        return ResponseEntity.ok(
                IndicatorValueResponseDTO.builder()
                        .success(true)
                        .message("Value saved successfully")
                        .day(currentDate)
                        .value(requestDTO.getValue())
                        .build()
        );
    }





    public List<Indicator> getIndicatorsByDepartmentId(int departmentId) {
        return indicatorRepository.findByDepartmentId(departmentId);
    }



   public List<IndicatorWithoutValuesDTO> getIndicatorsByDepartmentName(String departmentName) {
        Department department = departmentRepository.findByName(departmentName)
                .orElseThrow(() -> new IllegalArgumentException("Department not found"));

        return indicatorRepository.findByDepartmentId(department.getId())
                .stream()
                .map(indicator -> new IndicatorWithoutValuesDTO(indicator.getId(), indicator.getName(), indicator.getTargetPerWeek()))
                .collect(Collectors.toList());
    }




    public ResponseEntity<String> updateIndicator(UpdateTargetPerWeekDto updateIndicatorData) {
        // Find the indicator
        Indicator indicator = indicatorRepository.findById(updateIndicatorData.getIndicatorId())
                .orElse(null);

        if (indicator == null) {
            return ResponseEntity.notFound().build(); // Changed from badRequest() to notFound()
        }

        // Update the name if provided
        if (updateIndicatorData.getNewName() != null && !updateIndicatorData.getNewName().trim().isEmpty()) {
            indicator.setName(updateIndicatorData.getNewName().trim()); // Added trim()
        }

        // Update the target per week if provided
        if (updateIndicatorData.getNewTargetPerWeek() != null) {
            indicator.setTargetPerWeek(updateIndicatorData.getNewTargetPerWeek().trim()); // Added trim()
        }

        indicatorRepository.save(indicator);

        return ResponseEntity.ok("Indicator updated successfully");
    }





    public List<DepartmentIndicatorsDTO> categorizeIndicatorsByDepartment() {
        List<Department> allDepartments = departmentRepository.findAllWithIndicators();

        List<Integer> indicatorIds = allDepartments.stream()
                .flatMap(d -> d.getIndicators().stream())
                .map(Indicator::getId)
                .collect(Collectors.toList());
        Map<Integer, Indicator> indicatorsWithValues = indicatorRepository
                .findIndicatorsWithDailyValues(indicatorIds)
                .stream()
                .collect(Collectors.toMap(Indicator::getId, Function.identity()));

        return allDepartments.stream()
                .map(department -> {
                    List<IndicatorWithValuesDto> indicatorDtos = department.getIndicators().stream()
                            .map(indicator -> {
                                Indicator fullIndicator = indicatorsWithValues.get(indicator.getId());
                                List<DailyValueDto> dailyValues = fullIndicator.getDailyValues().stream()
                                        .map(dv -> new DailyValueDto(
                                                dv.getDay().toString(),
                                                dv.getValue()
                                        ))
                                        .collect(Collectors.toList());

                                return new IndicatorWithValuesDto(
                                        indicator.getId(),
                                        indicator.getName(),
                                        indicator.getTargetPerWeek(),
                                        dailyValues
                                );
                            })
                            .collect(Collectors.toList());

                    return new DepartmentIndicatorsDTO(
                            department.getId(),
                            department.getName(),
                            indicatorDtos
                    );
                })
                .collect(Collectors.toList());
    }



    public ResponseEntity<String> deleteIndicator(int indicatorId) {
        if (indicatorRepository.existsById(indicatorId)) {
            indicatorRepository.deleteById(indicatorId);
            return ResponseEntity.ok("Indicator deleted successfully");
        } else {
            return ResponseEntity.status(404).body("Indicator not found");
        }
    }






    public ResponseEntity<IndicatorValueResponseDTO> setTeamMemberIndicatorValue(
            TeamMemberSetIndicatorValueDTO requestDTO,
            int userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!"TEAM_MEMBER".equalsIgnoreCase(user.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    IndicatorValueResponseDTO.builder()
                            .success(false)
                            .message("Only team members can perform this action")
                            .build()
            );
        }

        String userDepartmentName = user.getDepartment();
        if (userDepartmentName == null || userDepartmentName.isEmpty()) {
            return ResponseEntity.badRequest().body(
                    IndicatorValueResponseDTO.builder()
                            .success(false)
                            .message("User is not assigned to any department")
                            .build()
            );
        }

        Department department = departmentRepository.findByName(userDepartmentName)
                .orElse(null);

        if (department == null) {
            return ResponseEntity.badRequest().body(
                    IndicatorValueResponseDTO.builder()
                            .success(false)
                            .message("Department not found")
                            .build()
            );
        }

        Indicator indicator = indicatorRepository.findByNameAndDepartment(
                requestDTO.getIndicatorName(),
                department
        ).orElse(null);

        if (indicator == null) {
            return ResponseEntity.badRequest().body(
                    IndicatorValueResponseDTO.builder()
                            .success(false)
                            .message("Indicator not found in your department")
                            .build()
            );
        }

        Date currentDate = new Date();

        boolean exists = indicator.getDailyValues().stream()
                .anyMatch(dv -> DateUtil.isSameDay(dv.getDay(), currentDate));

        if (exists) {
            return ResponseEntity.badRequest().body(
                    IndicatorValueResponseDTO.builder()
                            .success(false)
                            .message("Value for today already exists")
                            .build()
            );
        }


        indicator.addDailyValue(currentDate, requestDTO.getValue());
        indicatorRepository.save(indicator);

        return ResponseEntity.ok(
                IndicatorValueResponseDTO.builder()
                        .success(true)
                        .message("Value saved successfully")
                        .day(currentDate)
                        .value(requestDTO.getValue())
                        .build()
        );
    }

}