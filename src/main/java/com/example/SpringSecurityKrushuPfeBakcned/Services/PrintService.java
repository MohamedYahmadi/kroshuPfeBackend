package com.example.SpringSecurityKrushuPfeBakcned.Services;

import com.example.SpringSecurityKrushuPfeBakcned.Dto.*;
import com.example.SpringSecurityKrushuPfeBakcned.Entities.DailyValue;
import com.example.SpringSecurityKrushuPfeBakcned.Entities.Department;
import com.example.SpringSecurityKrushuPfeBakcned.Entities.Indicator;
import com.example.SpringSecurityKrushuPfeBakcned.Respositories.DepartmentRepository;
import com.example.SpringSecurityKrushuPfeBakcned.Respositories.IndicatorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PrintService {

    private final DepartmentRepository departmentRepository;
    private final IndicatorRepository indicatorRepository;

    @Autowired
    public PrintService(DepartmentRepository departmentRepository, IndicatorRepository indicatorRepository) {
        this.departmentRepository = departmentRepository;
        this.indicatorRepository = indicatorRepository;
    }

    public DepartmentPrintDTO getDepartmentPrintData(Long departmentId) {
        Department department = departmentRepository.findByIdWithIndicators(departmentId)
                .orElseThrow(() -> new NoSuchElementException("Department not found"));

        List<Integer> indicatorIds = department.getIndicators().stream()
                .map(Indicator::getId)
                .collect(Collectors.toList());

        Map<Integer, Indicator> indicatorsMap = indicatorRepository.findIndicatorsWithDailyValues(indicatorIds)
                .stream()
                .collect(Collectors.toMap(Indicator::getId, Function.identity()));

        Calendar todayCal = Calendar.getInstance();
        todayCal.set(Calendar.HOUR_OF_DAY, 0);
        todayCal.set(Calendar.MINUTE, 0);
        todayCal.set(Calendar.SECOND, 0);
        todayCal.set(Calendar.MILLISECOND, 0);
        Date today = todayCal.getTime();

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date currentWeekStart = calendar.getTime();

        List<IndicatorPrintDTO> indicators = department.getIndicators().stream()
                .map(indicator -> {
                    Indicator fullIndicator = indicatorsMap.get(indicator.getId());
                    return processIndicatorForPrinting(fullIndicator, currentWeekStart, today);
                })
                .collect(Collectors.toList());

        return DepartmentPrintDTO.builder()
                .id(department.getId())
                .name(department.getName())
                .printDate(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()))
                .indicators(indicators)
                .build();
    }

    private IndicatorPrintDTO processIndicatorForPrinting(Indicator indicator, Date currentWeekStart, Date today) {
        List<WeeklyPrintDTO> weeklyData = new ArrayList<>();
        Calendar weekCalendar = Calendar.getInstance();
        weekCalendar.setTime(currentWeekStart);

        for (int weekNum = 0; weekNum < 5; weekNum++) {
            Date weekStart = weekCalendar.getTime();

            Calendar endCalendar = (Calendar) weekCalendar.clone();
            endCalendar.add(Calendar.DAY_OF_WEEK, 6);
            endCalendar.set(Calendar.HOUR_OF_DAY, 23);
            endCalendar.set(Calendar.MINUTE, 59);
            endCalendar.set(Calendar.SECOND, 59);
            endCalendar.set(Calendar.MILLISECOND, 999);
            Date weekEnd = endCalendar.getTime();

            List<DailyPrintDTO> dailyValues = processDailyValues(
                    indicator.getDailyValues(),
                    weekStart,
                    weekEnd,
                    weekNum == 0 ? today : null
            );

            weeklyData.add(WeeklyPrintDTO.builder()
                    .weekLabel(getWeekLabel(weekNum))
                    .dateRange(formatDateRange(weekStart, weekEnd))
                    .dailyValues(dailyValues)
                    .build());

            weekCalendar.add(Calendar.WEEK_OF_YEAR, -1);
        }

        return IndicatorPrintDTO.builder()
                .id(indicator.getId())
                .name(indicator.getName())
                .target(indicator.getTargetPerWeek())
                .weeklyData(weeklyData)
                .build();
    }

    private List<DailyPrintDTO> processDailyValues(List<DailyValue> dailyValues, Date weekStart, Date weekEnd, Date today) {
        return Arrays.asList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
                .stream()
                .map(day -> {
                    String value = findDailyValue(dailyValues, day, weekStart, weekEnd, today);
                    return DailyPrintDTO.builder()
                            .day(day.substring(0, 3))
                            .value(value)
                            .build();
                })
                .collect(Collectors.toList());
    }

    private String findDailyValue(List<DailyValue> dailyValues, String dayName, Date weekStart, Date weekEnd, Date today) {
        return dailyValues.stream()
                .filter(dv -> {

                    Calendar dvCal = Calendar.getInstance();
                    dvCal.setTime(dv.getDay());
                    dvCal.set(Calendar.HOUR_OF_DAY, 0);
                    dvCal.set(Calendar.MINUTE, 0);
                    dvCal.set(Calendar.SECOND, 0);
                    dvCal.set(Calendar.MILLISECOND, 0);
                    Date normalizedDvDate = dvCal.getTime();

                    String dvDayName = new SimpleDateFormat("EEEE").format(dv.getDay());

                    if (today != null && normalizedDvDate.equals(today)) {
                        return dayName.equals(dvDayName);
                    }

                    return dayName.equals(dvDayName) &&
                            !normalizedDvDate.before(weekStart) &&
                            !normalizedDvDate.after(weekEnd);
                })
                .findFirst()
                .map(DailyValue::getValue)
                .orElse("-");
    }

    private String getWeekLabel(int weekNum) {
        switch (weekNum) {
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