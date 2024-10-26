package com.hackathon.bankingapp.Repositories;


import com.hackathon.bankingapp.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IUserRepository extends JpaRepository<User, String> {

    Optional<User> findByEmail(String email);
    Optional<User> findByAccountNumber(String accountNumber);
    Optional<User> findByPhoneNumber(String phoneNumber);

    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsByAccountNumber(String accountNumber);

    @Query("SELECT u FROM User u WHERE u.email = :identifier OR u.accountNumber = :identifier")
    Optional<User> findByEmailOrAccountNumber(@Param("identifier") String identifier);

    @Modifying
    @Query("UPDATE User u SET u.hashedPassword = :newPassword WHERE u.email = :email")
    void updatePassword(@Param("email") String email, @Param("newPassword") String newPassword);

    @Modifying
    @Query("UPDATE User u SET u.pin = :pin WHERE u.accountNumber = :accountNumber")
    void updatePin(@Param("accountNumber") String accountNumber, @Param("pin") String pin);

    Optional<User> findByEmailAndAccountNumber(String email, String accountNumber);

    Optional<User> findByPhoneNumberAndAccountNumber(String phoneNumber, String accountNumber);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.accountNumber = :accountNumber AND u.pin = :pin")
    boolean isPinValid(@Param("accountNumber") String accountNumber, @Param("pin") String pin);
}

