package ru.practicum.ewm_service.subscription;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm_service.subscription.model.Subscription;
import ru.practicum.ewm_service.subscription.model.SubscriptionId;
import ru.practicum.ewm_service.user.User;

import java.util.List;

public interface SubscriptionRepository extends JpaRepository<Subscription, SubscriptionId> {

    @Query("""
            select s.follower
            from Subscription s
            where s.contentMaker.id = :contentMakerId
            order by s.follower.id
            """
    )
    Page<User> findFollowersByContentMakerId(long contentMakerId, Pageable pageable);


    @Modifying
    @Query("""
            delete from Subscription s
            where s.contentMaker.id = :contentMakerId
            """
    )
    void deleteAllByContentMakerId(long contentMakerId);


    @Query("""
            select s.contentMaker.id
            from Subscription s
            where s.follower.id = :followerId
            """
    )
    List<Long> findContentMakersIdsByFollowerId(long followerId);
}