package ru.practicum.ewm_service.subscription.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionId implements Serializable {
    private long contentMakerId;
    private long followerId;
}