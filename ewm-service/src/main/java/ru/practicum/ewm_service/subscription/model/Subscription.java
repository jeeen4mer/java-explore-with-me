package ru.practicum.ewm_service.subscription.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import ru.practicum.ewm_service.user.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "subscriptions")
@Getter
@Setter
@NoArgsConstructor
public class Subscription {

    @EmbeddedId
    private SubscriptionId subscriptionId;

    @MapsId("contentMakerId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_maker_id")
    private User contentMaker;

    @MapsId("followerId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follower_id")
    private User follower;

    @Column(name = "created_on")
    @CreationTimestamp
    private LocalDateTime created;

    public Subscription(User contentMaker, User follower, SubscriptionId id) {
        this.contentMaker = contentMaker;
        this.follower = follower;
        this.subscriptionId = id;
    }
}