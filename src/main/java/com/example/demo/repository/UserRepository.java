package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.User;

@Repository
@Transactional(readOnly = true)
public interface UserRepository extends JpaRepository<User, Long> {

    // Derived Query Methods
    Optional<User> findByIdAndIsDeletedFalse(Long id);
    
    Optional<User> findByEmailAndIsDeletedFalse(String email);
    
    Optional<User> findByUsernameAndIsDeletedFalse(String username);

    Optional<User> findByEmailAndPasswordAndIsDeletedFalse(String email, String passwordHash);
    
    boolean existsByEmail(String email);
    
    boolean existsByUsername(String username);
    
    Page<User> findAllByIsDeletedFalse(Pageable pageable);

    List<User> findAllByIsDeletedFalseAndRole_Id(Long roleId);

    // JPQL Query: Phục vụ tìm kiếm trên Dashboard
    @Query("SELECT u FROM User u " +
           "WHERE u.isDeleted = false " +
           "AND (:keyword IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<User> searchUsers(
                           @Param("keyword") String keyword, 
                           Pageable pageable);

    // Soft Delete (Mục 7.1)
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.isDeleted = true WHERE u.id = :id")
    void softDeleteById(@Param("id") Long id);
    
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.role.id = :roleId WHERE u.id = :userId")
    void updateUserRole(@Param("userId") Long userId, @Param("roleId") Long roleId);

    @Query("""
        SELECT DISTINCT u
        FROM User u
        LEFT JOIN FETCH u.role r
        LEFT JOIN FETCH r.rolePermissions rp
        LEFT JOIN FETCH rp.permission
        WHERE u.username = :username
        AND u.isDeleted = false
        """)
    Optional<User> findSecurityUserByUsername(@Param("username") String username);
}