package ru.practicum.ewm_service.event.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class Location {
    @Column(name = "location_lat")
    private String lat;

    @Column(name = "location_lon")
    private String lon;
}
