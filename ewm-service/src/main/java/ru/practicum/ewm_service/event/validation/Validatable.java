package ru.practicum.ewm_service.event.validation;


import java.time.LocalDateTime;

public interface Validatable {

    LocalDateTime getEventDate();

    String getTitle();

    String getAnnotation();

    String getDescription();


}
