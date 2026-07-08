package com.example.demo.util;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.Category;
import com.example.demo.entity.Permission;
import com.example.demo.entity.Product;
import com.example.demo.entity.Role;
import com.example.demo.entity.RolePermission;
import com.example.demo.entity.User;
import com.example.demo.entity.enums.UserRole;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.PermissionRepository;
import com.example.demo.repository.ProductRepository;
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
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

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
            seedCategories();
            seedProducts();
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
     * Seed Categories 
     */
    private void seedCategories() {
        log.info("Seeding categories...");
        
        // Kiểm tra nếu đã có dữ liệu thì không seed lại
        if (categoryRepository.count() > 0) {
            log.info("Categories already exist, skipping...");
            return;
        }
        
        List<Category> categories = Arrays.asList(
            createCategory("C01", "Hệ thống phanh", "Chi tiết cơ khí cho hệ thống phanh ô tô"),
            createCategory("C02", "Hệ thống lái", "Chi tiết cơ khí cho hệ thống lái và vô lăng"),
            createCategory("C03", "Hệ thống truyền động", "Chi tiết cơ khí cho hộp số và trục truyền động"),
            createCategory("C04", "Hệ thống khung gầm", "Chi tiết cơ khí cho khung xe và gầm ô tô"),
            createCategory("C05", "Hệ thống động cơ", "Chi tiết cơ khí cho động cơ và các bộ phận liên quan"),
            createCategory("C06", "Hệ thống treo", "Chi tiết cơ khí cho hệ thống giảm xóc và treo ô tô"),
            createCategory("C07", "Hệ thống ống xả", "Chi tiết cơ khí cho ống xả và bộ giảm thanh"),
            createCategory("C08", "Hệ thống làm mát", "Chi tiết cơ khí cho két nước và hệ thống làm mát")
        );

        categoryRepository.saveAll(categories);
        log.info("✅ Created {} categories", categories.size());
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
            "USER_VIEW", "USER_MANAGE", "USER_ROLE_ASSIGN",
            "LINE_VIEW","LINE_MANAGE",
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

    /* *
         Seed Products 
     */
    private void seedProducts() {
        log.info("Seeding products...");
        
        // Kiểm tra nếu đã có dữ liệu thì không seed lại
        if (productRepository.count() > 0) {
            log.info("Products already exist, skipping...");
            return;
        }
        
        // Lấy tất cả categories
        List<Category> categories = categoryRepository.findAll();
        if (categories.isEmpty()) {
            log.warn("No categories found, skipping product seeding...");
            return;
        }
        
        // Tạo map để dễ dàng lấy category theo code
        Map<String, Category> categoryMap = categories.stream()
            .collect(Collectors.toMap(Category::getCategoryCode, Function.identity()));
        
        List<Product> products = Arrays.asList(
            // Hệ thống phanh
            createProduct("PR001", "Đĩa phanh trước", 
                "Đĩa phanh trước bằng thép hợp kim, đường kính 280mm", 
                true, categoryMap.get("C01")),
            createProduct("PR002", "Đĩa phanh sau", 
                "Đĩa phanh sau bằng gang cầu, đường kính 250mm", 
                true, categoryMap.get("C01")),
            createProduct("PR003", "Xi lanh phanh chính", 
                "Xi lanh phanh chính bằng nhôm hợp kim, áp suất hoạt động 15Mpa", 
                true, categoryMap.get("C01")),
            
            // Hệ thống lái
            createProduct("PR004", "Trục lái chính", 
                "Trục lái bằng thép carbon, chiều dài 450mm", 
                true, categoryMap.get("C02")),
            createProduct("PR005", "Càng lái", 
                "Càng lái bằng thép hợp kim, độ bền cao", 
                true, categoryMap.get("C02")),
            createProduct("PR006", "Vô lăng", 
                "Vô lăng thể thao bọc da, đường kính 350mm", 
                true, categoryMap.get("C02")),
            
            // Hệ thống truyền động
            createProduct("PR007", "Bánh răng số 1", 
                "Bánh răng truyền động cấp 1, mô đun 2.5, 32 răng", 
                true, categoryMap.get("C03")),
            createProduct("PR008", "Bánh răng số 2", 
                "Bánh răng truyền động cấp 2, mô đun 2.5, 28 răng", 
                true, categoryMap.get("C03")),
            createProduct("PR009", "Trục truyền động chính", 
                "Trục truyền động chính bằng thép hợp kim, đường kính 45mm", 
                true, categoryMap.get("C03")),
            
            // Hệ thống khung gầm
            createProduct("PR010", "Dầm khung chính", 
                "Dầm khung xe bằng thép cường độ cao, kích thước 2000x150mm", 
                true, categoryMap.get("C04")),
            createProduct("PR011", "Gia cường khung", 
                "Thanh gia cường khung bằng thép ống, đường kính 50mm", 
                true, categoryMap.get("C04")),
            
            // Hệ thống động cơ
            createProduct("PR012", "Trục khuỷu động cơ", 
                "Trục khuỷu động cơ bằng thép rèn, 8 bánh đối trọng", 
                true, categoryMap.get("C05")),
            createProduct("PR013", "Piston động cơ", 
                "Piston động cơ bằng nhôm hợp kim, đường kính 86mm", 
                true, categoryMap.get("C05")),
            createProduct("PR014", "Thanh truyền", 
                "Thanh truyền động cơ bằng thép hợp kim, chiều dài 150mm", 
                true, categoryMap.get("C05")),
            createProduct("PR015", "Trục cam", 
                "Trục cam động cơ bằng thép hợp kim, 16 mấu cam", 
                true, categoryMap.get("C05")),
            
            // Hệ thống treo
            createProduct("PR016", "Lò xo giảm xóc", 
                "Lò xo giảm xóc bằng thép lò xo, độ cứng 150 N/mm", 
                true, categoryMap.get("C06")),
            createProduct("PR017", "Thanh cân bằng", 
                "Thanh cân bằng gầm xe bằng thép hợp kim, đường kính 22mm", 
                true, categoryMap.get("C06")),
            
            // Hệ thống ống xả
            createProduct("PR018", "Bộ giảm thanh chính", 
                "Bộ giảm thanh chính bằng thép không gỉ, dung tích 6L", 
                true, categoryMap.get("C07")),
            createProduct("PR019", "Ống xả trung tâm", 
                "Ống xả trung tâm bằng thép không gỉ, đường kính 60mm", 
                true, categoryMap.get("C07")),
            
            // Hệ thống làm mát
            createProduct("PR020", "Két nước làm mát", 
                "Két nước bằng nhôm hợp kim, dung tích 5L", 
                true, categoryMap.get("C08")),
            createProduct("PR021", "Quạt làm mát", 
                "Quạt làm mát động cơ, đường kính 400mm, 7 cánh", 
                true, categoryMap.get("C08")),
            
            // Thêm vài sản phẩm khác
            createProduct("PR022", "Má phanh trước", 
                "Má phanh trước bằng vật liệu gốm, kích thước 120x45mm", 
                true, categoryMap.get("C01")),
            createProduct("PR023", "Dây curoa động cơ", 
                "Dây curoa động cơ bằng cao su, chiều dài 1200mm", 
                true, categoryMap.get("C05")),
            createProduct("PR024", "Bạc đạn bánh xe", 
                "Bạc đạn bánh xe bằng thép chịu lực, cỡ 40x80mm", 
                true, categoryMap.get("C04")),
            createProduct("PR025", "Dầu bôi trơn hộp số", 
                "Dầu bôi trơn hộp số, dung tích 5L", 
                true, categoryMap.get("C03"))
        );

        productRepository.saveAll(products);
        log.info("✅ Created {} products", products.size());
    }

    // ==================== HELPER METHODS ====================
    
     private Category createCategory(String code, String name, String description) {
        Category category = new Category();
        category.setCategoryCode(code);
        category.setName(name);
        category.setDescription(description);
        return category;
    }

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

    private Product createProduct(String code, String name, String description, 
                                   Boolean isActive, Category category) {
        Product product = new Product();
        product.setProductCode(code);
        product.setName(name);
        product.setDescription(description);
        product.setIsActive(isActive);
        product.setCategory(category);
        return product;
    }
}
