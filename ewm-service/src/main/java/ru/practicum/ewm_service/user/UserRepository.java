package ru.practicum.ewm_service.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    Page<User> findByIdGreaterThanEqualOrderById(long from, Pageable pageable);

    @Query("""
            select u
            from User u
            where :ids is null or u.id in :ids
            order by u.id
            """)
    Page<User> findAllById(List<Long> ids, Pageable pageable);

    @Modifying
    @Query("""
            update User u
            set u.followsProhibited = :prohibited
            where u.id = :id
            """)
    int changeFollowsPermission(long id, boolean prohibited);
}
