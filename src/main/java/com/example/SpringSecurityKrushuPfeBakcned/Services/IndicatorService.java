package com.example.SpringSecurityKrushuPfeBakcned.Services;

import com.example.SpringSecurityKrushuPfeBakcned.Dto.*;
import com.example.SpringSecurityKrushuPfeBakcned.Entities.DailyValue;
import com.example.SpringSecurityKrushuPfeBakcned.Entities.Department;
import com.example.SpringSecurityKrushuPfeBakcned.Entities.Indicator;
import com.example.SpringSecurityKrushuPfeBakcned.Entities.User;
import com.example.SpringSecurityKrushuPfeBakcned.Respositories.DepartmentRepository;
import com.example.SpringSecurityKrushuPfeBakcned.Respositories.IndicatorRepository;
import com.example.SpringSecurityKrushuPfeBakcned.Respositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.example.SpringSecurityKrushuPfeBakcned.Util.DateUtil;


import java.text.SimpleDateFormat;
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

        if (optionalDepartment.isEmpty()) {
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

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.WEEK_OF_YEAR, -5);
        Date fiveWeeksAgo = calendar.getTime();

        System.out.println("Current date: " + currentDate);
        System.out.println("Five weeks ago: " + fiveWeeksAgo);

        List<DailyValue> valuesToRemove = new ArrayList<>();
        for (DailyValue dv : indicator.getDailyValues()) {
            System.out.println("Checking value from: " + dv.getDay() +
                    " - is before? " + dv.getDay().before(fiveWeeksAgo));
            if (dv.getDay().before(fiveWeeksAgo)) {
                valuesToRemove.add(dv);
            }
        }

        if (!valuesToRemove.isEmpty()) {
            System.out.println("Removing " + valuesToRemove.size() + " old values");
            indicator.getDailyValues().removeAll(valuesToRemove);
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
        Indicator indicator = indicatorRepository.findById(updateIndicatorData.getIndicatorId())
                .orElse(null);

        if (indicator == null) {
            return ResponseEntity.notFound().build();
        }

        if (updateIndicatorData.getNewName() != null && !updateIndicatorData.getNewName().trim().isEmpty()) {
            indicator.setName(updateIndicatorData.getNewName().trim());
        }

        if (updateIndicatorData.getNewTargetPerWeek() != null) {
            indicator.setTargetPerWeek(updateIndicatorData.getNewTargetPerWeek().trim());
        }

        indicatorRepository.save(indicator);

        return ResponseEntity.ok("Indicator updated successfully");
    }





    public List<DepartmentIndicatorsDTO> categorizeIndicatorsByDepartment() {
        List<Department> allDepartments = departmentRepository.findAllWithIndicators();


        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        Date weekStart = calendar.getTime();


        calendar.add(Calendar.DAY_OF_WEEK, 6);
        Date weekEnd = calendar.getTime();

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
                                        .filter(dv -> !dv.getDay().before(weekStart) && !dv.getDay().after(weekEnd))
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

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.WEEK_OF_YEAR, -5);
        Date fiveWeeksAgo = calendar.getTime();

        Iterator<DailyValue> iterator = indicator.getDailyValues().iterator();
        while (iterator.hasNext()) {
            DailyValue dv = iterator.next();
            if (dv.getDay().before(fiveWeeksAgo)) {
                iterator.remove();
            }
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





    public List<DepartmentHistoryDTO> getWeeklyHistory() {
        List<Department> departments = departmentRepository.findAllWithIndicators();

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        Date currentWeekStart = calendar.getTime();

        List<Integer> indicatorIds = departments.stream()
                .flatMap(d -> d.getIndicators().stream())
                .map(Indicator::getId)
                .collect(Collectors.toList());

        Map<Integer, Indicator> indicatorsMap = indicatorRepository
                .findIndicatorsWithDailyValues(indicatorIds)
                .stream()
                .collect(Collectors.toMap(Indicator::getId, Function.identity()));

        return departments.stream().map(department -> {
            List<IndicatorHistoryDTO> indicatorHistories = department.getIndicators().stream()
                    .map(indicator -> {
                        Indicator fullIndicator = indicatorsMap.get(indicator.getId());
                        List<WeeklyDataDTO> weeklyData = new ArrayList<>();

                        Calendar weekCalendar = Calendar.getInstance();
                        weekCalendar.setTime(currentWeekStart);

                        for (int weekNum = 0; weekNum < 5; weekNum++) {
                            Date weekStart = weekCalendar.getTime();

                            Calendar endCalendar = (Calendar) weekCalendar.clone();
                            endCalendar.add(Calendar.DAY_OF_WEEK, 6);
                            Date weekEnd = endCalendar.getTime();

                            String weekLabel = getWeekLabel(weekNum);
                            String dateRange = formatDateRange(weekStart, weekEnd);

                            Map<String, String> dailyValues = Arrays.stream(new String[]{"Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"})
                                    .collect(Collectors.toMap(
                                            day -> day,
                                            day -> "-"
                                    ));

                            fullIndicator.getDailyValues().stream()
                                    .filter(dv -> !dv.getDay().before(weekStart) &&
                                            !dv.getDay().after(weekEnd))
                                    .forEach(dv -> {
                                        String dayName = new SimpleDateFormat("EEEE").format(dv.getDay());
                                        dailyValues.put(dayName, dv.getValue());
                                    });

                            weeklyData.add(WeeklyDataDTO.builder()
                                    .weekLabel(weekLabel)
                                    .dateRange(dateRange)
                                    .dailyValues(dailyValues)
                                    .build());

                            weekCalendar.add(Calendar.WEEK_OF_YEAR, -1);
                        }

                        return IndicatorHistoryDTO.builder()
                                .id(indicator.getId())
                                .name(indicator.getName())
                                .target(indicator.getTargetPerWeek())
                                .weeklyData(weeklyData)
                                .build();
                    })
                    .collect(Collectors.toList());

            return DepartmentHistoryDTO.builder()
                    .id(department.getId())
                    .name(department.getName())
                    .indicators(indicatorHistories)
                    .build();
        }).collect(Collectors.toList());
    }

    private String getWeekLabel(int weekNum) {
        switch(weekNum) {
            case 0: return "Current Week";
            case 1: return "Last Week";
            case 2: return "2 Weeks Ago";
            case 3: return "3 Weeks Ago";
            case 4: return "4 Weeks Ago";
            default: return "Week " + weekNum;
        }
    }

    private String formatDateRange(Date start, Date end) {
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMM");
        SimpleDateFormat dayFormat = new SimpleDateFormat("d");
        SimpleDateFormat yearFormat = new SimpleDateFormat(", yyyy");

        if (monthFormat.format(start).equals(monthFormat.format(end))) {
            return monthFormat.format(start) + " " + dayFormat.format(start) + "-" +
                    dayFormat.format(end) + yearFormat.format(end);
        }
        return monthFormat.format(start) + " " + dayFormat.format(start) + "-" +
                monthFormat.format(end) + " " + dayFormat.format(end) + yearFormat.format(end);
    }



}