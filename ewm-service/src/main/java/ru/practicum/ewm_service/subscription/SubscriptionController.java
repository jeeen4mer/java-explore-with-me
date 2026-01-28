package ru.practicum.ewm_service.subscription;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm_service.event.dto.EventShortDto;
import ru.practicum.ewm_service.event.model.EventSortType;
import ru.practicum.ewm_service.subscription.dto.SubscriptionDto;
import ru.practicum.ewm_service.subscription.service.SubscriptionService;
import ru.practicum.ewm_service.user.dto.UserShortDto;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/users/{userId}/subscriptions")
@RequiredArgsConstructor
@Slf4j
public class SubscriptionController {

    private final SubscriptionService service;

    @PostMapping("/{contentMakerId}")
    @ResponseStatus(HttpStatus.CREATED)
    public SubscriptionDto createSubscription(@PathVariable(name = "userId") long followerId, @PathVariable long contentMakerId) {
        return service.createSubscription(followerId, contentMakerId);
    }

    @DeleteMapping("/{contentMakerId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelSubscription(@PathVariable(name = "userId") long followerId, @PathVariable long contentMakerId) {
        service.cancelSubscription(followerId, contentMakerId);
    }

    @GetMapping("/followers")
    public List<UserShortDto> getFollowers(@PathVariable long userId,
                                           @RequestParam(defaultValue = "0") int from,
                                           @RequestParam(defaultValue = "10") int size) {
        return service.getFollowers(userId, from, size);
    }

    @PatchMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changeFollowsPermission(@PathVariable long userId, @RequestParam boolean prohibitFollows) {
        service.changeFollowsPermission(userId, prohibitFollows);
    }

    @GetMapping
    public List<EventShortDto> getSubsEvents(@RequestParam(required = false) String text,
                                             @RequestParam(required = false) List<Long> categories,
                                             @RequestParam(required = false) Boolean paid,
                                             @RequestParam(required = false) LocalDateTime rangeStart,
                                             @RequestParam(required = false) LocalDateTime rangeEnd,
                                             @RequestParam(required = false) boolean onlyAvailable,
                                             @RequestParam(required = false) EventSortType sort,
                                             @RequestParam(defaultValue = "0") int from,
                                             @RequestParam(defaultValue = "10") int size,
                                             @PathVariable long userId,
                                             HttpServletRequest request) {
        return service.getSubsEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size,
                request.getRemoteAddr(), userId);
    }
}