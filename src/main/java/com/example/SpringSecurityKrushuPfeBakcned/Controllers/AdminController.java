package com.example.SpringSecurityKrushuPfeBakcned.Controllers;

import com.example.SpringSecurityKrushuPfeBakcned.Dto.*;
import com.example.SpringSecurityKrushuPfeBakcned.Entities.Department;
import com.example.SpringSecurityKrushuPfeBakcned.Entities.Indicator;
import com.example.SpringSecurityKrushuPfeBakcned.Entities.User;
import com.example.SpringSecurityKrushuPfeBakcned.Services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("api/admin")
public class AdminController {

    private final AdminService adminService;
    private final DepartmentService departmentService;
    private final IndicatorService indicatorService;
    private final PrintService printService;
    private final ActionTrackingService actionTrackingService;
    @Autowired
    public AdminController(AdminService adminService, DepartmentService departmentService, IndicatorService indicatorService, PrintService printService, ActionTrackingService actionTrackingService) {
        this.adminService = adminService;
        this.departmentService = departmentService;
        this.indicatorService = indicatorService;
        this.printService = printService;
        this.actionTrackingService = actionTrackingService;
    }




    @PostMapping("/create-user")
    public String createUser(@RequestBody SignupDto signData) {
        return adminService.createUser(signData);
    }

    @PutMapping("/update-profile/{id}")
    public ResponseEntity<String> updateProfile(@PathVariable int id, @RequestBody UpdateAdminProfileDto updateAdminProfileDto) {
        return adminService.updateProfile(id, updateAdminProfileDto);
    }
    @GetMapping("/all-users")
    public List<User>getAllUsers(){
        return adminService.getAllUsers();
    }


    @DeleteMapping("delete-user/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable int id){
        return adminService.deleteUser(id);
    }

    @PutMapping("/update-user-profile/{id}")
    public ResponseEntity<String> updateUserProfile(@PathVariable int id, @RequestBody UpdateProfileDto updateProfileDto) {
        return adminService.updateUserProfile(id, updateProfileDto);
    }
    //Sprint 2 methods
    @PostMapping("/create-department")
    public String createDepartment(@RequestBody CreateDepartement departmentData) {
        return departmentService.CreateDepartment(departmentData);
    }
    @PostMapping("/create-indicator")
    public ResponseEntity<String> createIndicator(@RequestBody CreateIndicatorDto indicatorDto) {
        return indicatorService.createIndicator(indicatorDto);
    }

    @PostMapping("/set-value")
    public ResponseEntity<IndicatorValueResponseDTO> setIndicatorValue(
            @RequestBody SetIndicatorValueDTO requestDTO
    ) {
        return indicatorService.setIndicatorValue(requestDTO);
    }

    @GetMapping("/indicators/{departmentId}")
    public List<Indicator> getIndicatorsByDepartment(@PathVariable int departmentId) {
        return indicatorService.getIndicatorsByDepartmentId(departmentId);
    }

   @GetMapping("/indicators-by-department-name/{departmentName}")
    public List<IndicatorWithoutValuesDTO> getIndicatorsByDepartmentName(@PathVariable String departmentName) {
        return indicatorService.getIndicatorsByDepartmentName(departmentName);
    }

   @PutMapping("/update-indicator/{id}")
    public ResponseEntity<String> updateIndicator(@PathVariable int id, @RequestBody UpdateTargetPerWeekDto updateIndicatorData) {
        updateIndicatorData.setIndicatorId(id);
        return indicatorService.updateIndicator(updateIndicatorData);
    }
    @GetMapping("/indicators-by-department")
    public List<DepartmentIndicatorsWithActionsDTO> getIndicatorsByDepartment() {
        return indicatorService.categorizeIndicatorsByDepartment();
    }
    @GetMapping("/all-departments-names")
    public List<String> getDepartmentNames() {
        return departmentService.getAllDepartmentNames();
    }
    @GetMapping("/all-departments")
    public List<Department> getAllDepartments() {
        return departmentService.getAllDepartments();
    }


    @DeleteMapping("/delete-department/{id}")
    public ResponseEntity<String> deleteDepartment(@PathVariable int id) {
        return departmentService.deleteDepartment(id);
    }
    @PutMapping("/rename-department/{departmentId}")
    public ResponseEntity<String> renameDepartment(@PathVariable int departmentId, @RequestBody RenameDepartmentDto renameDepartmentDto) {
        return departmentService.renameDepartment(departmentId, renameDepartmentDto);
    }


    @DeleteMapping("/delete-indicator/{id}")
    public ResponseEntity<String> deleteIndicator(@PathVariable int id) {
        return indicatorService.deleteIndicator(id);
    }

    @GetMapping("/weekly-history")
    public List<DepartmentHistoryDTO> getWeeklyHistory() {
        return indicatorService.getWeeklyHistory();
    }

    @GetMapping("/print-department/{departmentId}")
    public ResponseEntity<DepartmentPrintDTO> getDepartmentForPrint(@PathVariable Long departmentId) {
        DepartmentPrintDTO response = printService.getDepartmentPrintData(departmentId);
        return ResponseEntity.ok(response);
    }


    @PutMapping("/update-indicator-value/{userId}")
    public ResponseEntity<IndicatorValueResponseDTO> adminUpdateIndicatorValue(
            @RequestBody AdminUpdateIndicatorValueDTO updateRequest,
            @PathVariable int userId) {
        return indicatorService.adminUpdateIndicatorValue(updateRequest, userId);
    }


    // Admin endpoints
    @PostMapping("/create-waste-reasons/{userId}")
    public ResponseEntity<String> adminCreateWasteReasons(
            @PathVariable int userId,
            @RequestBody wasteReasonRequest request) {
        String response = actionTrackingService.adminCreateWasteReasons(userId, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/create-action-items/{userId}")
    public ResponseEntity<String> adminCreateActionItem(
            @PathVariable int userId,
            @RequestBody ActionItemDto actionItemData) {
        String response = actionTrackingService.adminCreateActionItem(userId, actionItemData);
        return ResponseEntity.ok(response);
    }
    @PutMapping("/waste-reasons/{wasteReasonId}")
    public ResponseEntity<String> updateWasteReason(
            @PathVariable int wasteReasonId,
            @RequestBody WasteReasonUpdateRequest request) {
        String result = actionTrackingService.updateWasteReason(
                request.getUserId(),
                wasteReasonId,
                request.getNewReason()
        );
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/waste-reasons/{wasteReasonId}")
    public ResponseEntity<String> deleteWasteReason(
            @PathVariable int wasteReasonId,
            @RequestBody UserIdRequest request) {
        String result = actionTrackingService.deleteWasteReason(
                request.getUserId(),
                wasteReasonId
        );
        return ResponseEntity.ok(result);
    }

    // Action Item Endpoints
    @PutMapping("/action-items/{actionItemId}")
    public ResponseEntity<String> updateActionItem(
            @PathVariable int actionItemId,
            @RequestBody ActionItemUpdateRequest request) {
        String result = actionTrackingService.updateActionItem(
                request.getUserId(),
                actionItemId,
                request.getNewAction()
        );
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/action-items/{actionItemId}")
    public ResponseEntity<String> deleteActionItem(
            @PathVariable int actionItemId,
            @RequestBody UserIdRequest request) {
        String result = actionTrackingService.deleteActionItem(
                request.getUserId(),
                actionItemId
        );
        return ResponseEntity.ok(result);
    }













}



