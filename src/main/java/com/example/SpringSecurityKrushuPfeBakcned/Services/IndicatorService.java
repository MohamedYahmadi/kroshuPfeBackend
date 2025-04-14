package com.example.SpringSecurityKrushuPfeBakcned.Services;

import com.example.SpringSecurityKrushuPfeBakcned.Dto.*;
import com.example.SpringSecurityKrushuPfeBakcned.Entities.Department;
import com.example.SpringSecurityKrushuPfeBakcned.Entities.Indicator;
import com.example.SpringSecurityKrushuPfeBakcned.Respositories.DepartmentRepository;
import com.example.SpringSecurityKrushuPfeBakcned.Respositories.IndicatorRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.example.SpringSecurityKrushuPfeBakcned.Util.DateUtil;


import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class IndicatorService {

    private final DepartmentRepository departmentRepository;
    private final IndicatorRepository indicatorRepository;

    public IndicatorService(DepartmentRepository departmentRepository, IndicatorRepository indicatorRepository) {
        this.departmentRepository = departmentRepository;
        this.indicatorRepository = indicatorRepository;
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

        // 2. Find indicator by name within the department
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

        // 3. Check for existing value
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

        // 4. Add and save new value
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




    public ResponseEntity<String> updateTargetPerWeek(UpdateTargetPerWeekDto updateTargetPerWeekData) {
        Indicator indicator = indicatorRepository.findById(updateTargetPerWeekData.getIndicatorId()).orElse(null);

        if (indicator == null) {
            return ResponseEntity.badRequest().body("Indicator not found");
        }

        indicator.setTargetPerWeek(updateTargetPerWeekData.getNewTargetPerWeek());
        indicatorRepository.save(indicator);

        return ResponseEntity.ok("Target per week updated successfully");
    }


    public List<DepartmentIndicatorsDTO> categorizeIndicatorsByDepartment() {
        // 1. Fetch departments with indicators
        List<Department> allDepartments = departmentRepository.findAllWithIndicators();

        // 2. Collect all indicator IDs for batch loading
        List<Integer> indicatorIds = allDepartments.stream()
                .flatMap(d -> d.getIndicators().stream())
                .map(Indicator::getId)
                .collect(Collectors.toList());

        // 3. Batch fetch daily values for all indicators
        Map<Integer, Indicator> indicatorsWithValues = indicatorRepository
                .findIndicatorsWithDailyValues(indicatorIds)
                .stream()
                .collect(Collectors.toMap(Indicator::getId, Function.identity()));

        // 4. Build the response
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

}