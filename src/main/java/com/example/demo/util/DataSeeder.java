package com.example.demo.util;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.Permission;
import com.example.demo.entity.Role;
import com.example.demo.entity.RolePermission;
import com.example.demo.entity.User;
import com.example.demo.entity.enums.UserRole;
import com.example.demo.repository.PermissionRepository;
import com.example.demo.repository.RolePermissionRepository;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "app.seed-data.enabled", havingValue = "true", matchIfMissing = true)
public class DataSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void run(String... args) {
        if (shouldSeed()) {
            log.info("==========================================");
            log.info("  STARTING DATA SEEDING...");
            log.info("==========================================");
            
            seedPermissions();
            seedRoles();
            seedRolePermissions();
            seedAdminUser();
            
            log.info("==========================================");
            log.info("  DATA SEEDING COMPLETED!");
            log.info("==========================================");
        } else {
            log.info("Data already exists. Skipping seed.");
        }
    }

    /**
     * Kiểm tra xem có cần seed data không
     */
    private boolean shouldSeed() {
        return userRepository.count() == 0; // Chỉ seed nếu chưa có user nào
    }

    /**
     * Seed Permissions
     */
    private void seedPermissions() {
        log.info("Seeding permissions...");
        
        List<Permission> permissions = Arrays.asList(
            // User Module
            createPermission("USER_VIEW", "Xem danh sách người dùng"),
            createPermission("USER_MANAGE", "Thêm, sửa, xóa, khóa người dùng"),
            createPermission("USER_ROLE_ASSIGN", "Gán/đổi vai trò cho người dùng"),
            
            // Line Module
            createPermission("LINE_VIEW", "Xem danh sách dây chuyền sản xuất"),
            createPermission("LINE_MANAGE", "Thêm, sửa, xóa dây chuyền sản xuất"),
            
            // Category & Product Module
            createPermission("CATEGORY_VIEW", "Xem danh mục sản phẩm"),
            createPermission("CATEGORY_MANAGE", "Thêm, sửa, xóa danh mục"),
            createPermission("PRODUCT_VIEW", "Xem danh sách sản phẩm"),
            createPermission("PRODUCT_MANAGE", "Thêm, sửa, xóa sản phẩm"),
            
            // Production Order Module
            createPermission("ORDER_VIEW", "Tra cứu danh sách lệnh sản xuất"),
            createPermission("ORDER_CREATE", "Tạo lệnh sản xuất mới"),
            createPermission("ORDER_UPDATE", "Cập nhật thông tin lệnh"),
            createPermission("ORDER_DELETE", "Xóa lệnh sản xuất"),
            createPermission("ORDER_RELEASE", "Phát hành lệnh sản xuất"),
            createPermission("ORDER_CANCEL", "Hủy lệnh sản xuất"),
            createPermission("ORDER_PROCESS", "Cập nhật tiến độ sản xuất"),
            
            // Dashboard Module
            createPermission("DASHBOARD_VIEW", "Xem số liệu thống kê và biểu đồ")
        );

        permissionRepository.saveAll(permissions);
        log.info("✅ Created {} permissions", permissions.size());
    }

    /**
     * Seed Roles
     */
    private void seedRoles() {
        log.info("Seeding roles...");
        
        List<Role> roles = Arrays.asList(
            createRole(UserRole.MANAGER, "Ban quản lý - Chỉ xem"),
            createRole(UserRole.PLANNER, "Người lập kế hoạch - Toàn quyền sản phẩm & lệnh SX"),
            createRole(UserRole.OPERATOR, "Công nhân vận hành - Cập nhật tiến độ")
        );

        roleRepository.saveAll(roles);
        log.info("✅ Created {} roles", roles.size());
    }

    /**
     * Seed Role Permissions
     */
    private void seedRolePermissions() {
        log.info("Seeding role permissions...");
        
        // Lấy roles từ DB
        Role managerRole = roleRepository.findByNameRole(UserRole.MANAGER)
                .orElseThrow(() -> new RuntimeException("Manager role not found"));
        Role plannerRole = roleRepository.findByNameRole(UserRole.PLANNER)
                .orElseThrow(() -> new RuntimeException("Planner role not found"));
        Role operatorRole = roleRepository.findByNameRole(UserRole.OPERATOR)
                .orElseThrow(() -> new RuntimeException("Operator role not found"));

        // Lấy permissions từ DB
        Map<String, Permission> permMap = new HashMap<>();
        permissionRepository.findAll().forEach(p -> permMap.put(p.getNamePermission(), p));

        List<RolePermission> rolePermissions = new ArrayList<>();

        // === MANAGER Permissions ===
        List<String> managerPerms = Arrays.asList(
            "USER_VIEW", "LINE_VIEW", "CATEGORY_VIEW", "PRODUCT_VIEW",
            "ORDER_VIEW", "DASHBOARD_VIEW"
        );
        managerPerms.forEach(perm -> {
            rolePermissions.add(createRolePermission(managerRole, permMap.get(perm)));
        });

        // === PLANNER Permissions ===
        List<String> plannerPerms = Arrays.asList(
            "USER_VIEW", "LINE_VIEW",
            "CATEGORY_VIEW", "CATEGORY_MANAGE",
            "PRODUCT_VIEW", "PRODUCT_MANAGE",
            "ORDER_VIEW", "ORDER_CREATE", "ORDER_UPDATE", "ORDER_DELETE",
            "ORDER_RELEASE", "ORDER_CANCEL",
            "DASHBOARD_VIEW"
        );
        plannerPerms.forEach(perm -> {
            rolePermissions.add(createRolePermission(plannerRole, permMap.get(perm)));
        });

        // === OPERATOR Permissions ===
        List<String> operatorPerms = Arrays.asList(
            "ORDER_VIEW", "ORDER_PROCESS"
        );
        operatorPerms.forEach(perm -> {
            rolePermissions.add(createRolePermission(operatorRole, permMap.get(perm)));
        });

        rolePermissionRepository.saveAll(rolePermissions);
        log.info("✅ Created {} role-permission mappings", rolePermissions.size());
    }

    /**
     * Seed Admin/Root User
     */
    private void seedAdminUser() {
        log.info("Seeding admin users...");
        
        Role plannerRole = roleRepository.findByNameRole(UserRole.PLANNER)
                .orElseThrow(() -> new RuntimeException("Planner role not found"));
        Role managerRole = roleRepository.findByNameRole(UserRole.MANAGER)
                .orElseThrow(() -> new RuntimeException("Manager role not found"));

        List<User> users = new ArrayList<>();

        // Root Admin (Planner)
        User rootUser = new User();
        rootUser.setUsername("root");
        rootUser.setPasswordHash(PasswordUtil.hashPassword("Root@123"));
        rootUser.setFullName("Root Administrator");
        rootUser.setEmail("root@company.com");
        rootUser.setRole(plannerRole);
        rootUser.setIsActive(true);
        users.add(rootUser);

        // Manager account
        User manager = new User();
        manager.setUsername("manager");
        manager.setPasswordHash(PasswordUtil.hashPassword("Manager@123"));
        manager.setFullName("Quản lý sản xuất");
        manager.setEmail("manager@company.com");
        manager.setRole(managerRole);
        manager.setIsActive(true);
        users.add(manager);

        // Operator accounts
        User operator1 = new User();
        operator1.setUsername("operator1");
        operator1.setPasswordHash(PasswordUtil.hashPassword("Operator@123"));
        operator1.setFullName("Công nhân 1");
        operator1.setEmail("operator1@company.com");
        operator1.setRole(roleRepository.findByNameRole(UserRole.OPERATOR).orElse(null));
        operator1.setIsActive(true);
        users.add(operator1);

        User operator2 = new User();
        operator2.setUsername("operator2");
        operator2.setPasswordHash(PasswordUtil.hashPassword("Operator@123"));
        operator2.setFullName("Công nhân 2");
        operator2.setEmail("operator2@company.com");
        operator2.setRole(roleRepository.findByNameRole(UserRole.OPERATOR).orElse(null));
        operator2.setIsActive(true);
        users.add(operator2);

        userRepository.saveAll(users);
        
        log.info("✅ Created {} users:", users.size());
        log.info("   📧 root/root@company.com      - Root@123 (PLANNER)");
        log.info("   📧 manager/manager@company.com - Manager@123 (MANAGER)");
        log.info("   📧 operator1/operator1@company.com - Operator@123 (OPERATOR)");
        log.info("   📧 operator2/operator2@company.com - Operator@123 (OPERATOR)");
    }

    // ==================== HELPER METHODS ====================
    
    private Permission createPermission(String name, String description) {
        Permission permission = new Permission();
        permission.setNamePermission(name);
        permission.setDescription(description);
        return permission;
    }

    private Role createRole(UserRole nameRole, String description) {
        Role role = new Role();
        role.setNameRole(nameRole);
        role.setDescription(description);
        return role;
    }

    private RolePermission createRolePermission(Role role, Permission permission) {
        RolePermission rp = new RolePermission();
        rp.setRole(role);
        rp.setPermission(permission);
        return rp;
    }
}
