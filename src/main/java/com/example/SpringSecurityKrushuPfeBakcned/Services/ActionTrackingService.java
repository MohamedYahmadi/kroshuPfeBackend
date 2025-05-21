package com.example.SpringSecurityKrushuPfeBakcned.Services;

import com.example.SpringSecurityKrushuPfeBakcned.Dto.ActionItemDto;
import com.example.SpringSecurityKrushuPfeBakcned.Dto.wasteReasonRequest;
import com.example.SpringSecurityKrushuPfeBakcned.Entities.ActionItem;
import com.example.SpringSecurityKrushuPfeBakcned.Entities.Department;
import com.example.SpringSecurityKrushuPfeBakcned.Entities.User;
import com.example.SpringSecurityKrushuPfeBakcned.Entities.WasteReason;
import com.example.SpringSecurityKrushuPfeBakcned.Respositories.ActionItemRepository;
import com.example.SpringSecurityKrushuPfeBakcned.Respositories.DepartmentRepository;
import com.example.SpringSecurityKrushuPfeBakcned.Respositories.UserRepository;
import com.example.SpringSecurityKrushuPfeBakcned.Respositories.WasteReasonRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ActionTrackingService {
    private final ActionItemRepository actionItemRepository;
    private final WasteReasonRepository wasteReasonRepository;
    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;

    public ActionTrackingService(ActionItemRepository actionItemRepository,
                                 WasteReasonRepository wasteReasonRepository,
                                 DepartmentRepository departmentRepository,
                                 UserRepository userRepository) {
        this.actionItemRepository = actionItemRepository;
        this.wasteReasonRepository = wasteReasonRepository;
        this.departmentRepository = departmentRepository;
        this.userRepository = userRepository;
    }

    public String adminCreateWasteReasons(int userId, wasteReasonRequest request) {
        User user = validateUserAndRole(userId, "ADMIN");
        Department department = validateDepartment(request.getDepartmentName());

        List<WasteReason> savedReasons = request.getReasons().stream()
                .filter(reason -> reason != null && !reason.trim().isEmpty())
                .map(reason -> buildWasteReason(reason.trim(), department))
                .map(wasteReasonRepository::save)
                .toList();

        return String.format("Added %d waste reasons to %s",
                savedReasons.size(), department.getName());
    }

    public String adminCreateActionItem(int userId, ActionItemDto actionItemData) {
        User user = validateUserAndRole(userId, "ADMIN");
        Department department = validateDepartment(actionItemData.getDepartmentName());

        ActionItem actionItem = buildActionItem(actionItemData.getAction(), department);
        actionItemRepository.save(actionItem);

        return "Action item created for " + department.getName();
    }

    public String teamMemberCreateWasteReasons(int userId, wasteReasonRequest request) {
        User user = validateUserAndRole(userId, "TEAM_MEMBER");
        Department department = getTeamMemberDepartment(user);

        List<WasteReason> savedReasons = request.getReasons().stream()
                .filter(reason -> reason != null && !reason.trim().isEmpty())
                .map(reason -> buildWasteReason(reason.trim(), department))
                .map(wasteReasonRepository::save)
                .toList();

        return String.format("Added %d waste reasons to your department", savedReasons.size());
    }

    public String teamMemberCreateActionItem(int userId, ActionItemDto actionItemData) {
        User user = validateUserAndRole(userId, "TEAM_MEMBER");
        Department department = getTeamMemberDepartment(user);

        ActionItem actionItem = buildActionItem(actionItemData.getAction(), department);
        actionItemRepository.save(actionItem);

        return "Action item created for your department";
    }

    private User validateUserAndRole(int userId, String requiredRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        if (!user.getRole().equalsIgnoreCase(requiredRole)) {
            throw new IllegalStateException("Only " + requiredRole.toLowerCase() + " can perform this action");
        }
        return user;
    }

    private Department validateDepartment(String departmentName) {
        if (departmentName == null || departmentName.trim().isEmpty()) {
            throw new IllegalStateException("Department name is required");
        }
        return departmentRepository.findByName(departmentName.trim())
                .orElseThrow(() -> new IllegalStateException("Department not found"));
    }

    private Department getTeamMemberDepartment(User user) {
        return departmentRepository.findByName(user.getDepartment())
                .orElseThrow(() -> new IllegalStateException("User's department not found"));
    }

    private WasteReason buildWasteReason(String reason, Department department) {
        return WasteReason.builder()
                .reason(reason)
                .department(department)
                .createdAt(LocalDateTime.now())
                .build();
    }

    private ActionItem buildActionItem(String action, Department department) {
        return ActionItem.builder()
                .action(action)
                .department(department)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public String updateWasteReason(int userId, int wasteReasonId, String newReason) {
        User user = validateUserAndRole(userId, "ADMIN");
        WasteReason wasteReason = wasteReasonRepository.findById(wasteReasonId)
                .orElseThrow(() -> new IllegalStateException("Waste reason not found"));

        wasteReason.setReason(newReason.trim());
        wasteReasonRepository.save(wasteReason);

        return "Waste reason updated successfully";
    }

    public String deleteWasteReason(int userId, int wasteReasonId) {
        User user = validateUserAndRole(userId, "ADMIN");
        WasteReason wasteReason = wasteReasonRepository.findById(wasteReasonId)
                .orElseThrow(() -> new IllegalStateException("Waste reason not found"));

        wasteReasonRepository.delete(wasteReason);

        return "Waste reason deleted successfully";
    }

    public String updateActionItem(int userId, int actionItemId, String newAction) {
        User user = validateUserAndRole(userId, "ADMIN");
        ActionItem actionItem = actionItemRepository.findById(actionItemId)
                .orElseThrow(() -> new IllegalStateException("Action item not found"));

        actionItem.setAction(newAction.trim());
        actionItemRepository.save(actionItem);

        return "Action item updated successfully";
    }

    public String deleteActionItem(int userId, int actionItemId) {
        User user = validateUserAndRole(userId, "ADMIN");
        ActionItem actionItem = actionItemRepository.findById(actionItemId)
                .orElseThrow(() -> new IllegalStateException("Action item not found"));

        actionItemRepository.delete(actionItem);

        return "Action item deleted successfully";
    }
}