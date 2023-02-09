package com.userservice.user;

import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    @Bean
    Optional<UserEntity> findByEmail(String email);


    // TODO: Use generic methods so that the below can be reduced to one method:

    @Transactional
    @Modifying
    @Query("UPDATE UserEntity user SET user.enabled = TRUE WHERE user.email = ?1")
    void enableAppUser(String email);

    @Transactional
    @Modifying
    @Query("UPDATE UserEntity user SET user.password = ?2 WHERE user.email = ?1")
    void updatePassword(String email, String password);

    @Transactional
    @Modifying
    @Query("UPDATE UserEntity user SET user.displayName = ?2 WHERE user.email = ?1")
    void updateDisplayName(String email, String displayName);

    @Transactional
    @Modifying
    @Query("UPDATE UserEntity user SET user.email = ?2 WHERE user.email = ?1")
    void updateEmail(String email, String newEmail);

    @Transactional
    @Modifying
    @Query("UPDATE UserEntity user SET user.firstName = ?2 WHERE user.email = ?1")
    void updateFirstName(String email, String firstName);

    @Transactional
    @Modifying
    @Query("UPDATE UserEntity user SET user.lastName = ?2 WHERE user.email = ?1")
    void updateLastName(String email, String lastName);

    @Transactional
    @Modifying
    @Query("UPDATE UserEntity user SET user.locked = ?2 WHERE user.email = ?1")
    void lockAccount(String email, Boolean locked);
}
