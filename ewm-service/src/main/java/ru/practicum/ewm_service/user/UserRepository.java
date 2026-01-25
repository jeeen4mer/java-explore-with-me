package ru.practicum.ewm_service.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    Page<User> findByIdGreaterThanEqualOrderById(long from, Pageable pageable);

    @Query("SELECT u FROM User u WHERE (:ids IS NULL OR u.id IN :ids)")
    Page<User> findAllById(@Param("ids") List<Long> ids, Pageable pageable);

    boolean existsByEmail(String email);
}