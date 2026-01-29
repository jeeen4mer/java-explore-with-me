package ru.practicum.ewm_service.subscription.service;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm_service.event.dal.EventRepository;
import ru.practicum.ewm_service.event.dto.EventShortDto;
import ru.practicum.ewm_service.event.model.Event;
import ru.practicum.ewm_service.event.model.EventSortType;
import ru.practicum.ewm_service.event.model.EventState;
import ru.practicum.ewm_service.event.service.EventsFilterHandler;
import ru.practicum.ewm_service.exception.ConditionsNotMetException;
import ru.practicum.ewm_service.exception.NotFoundException;
import ru.practicum.ewm_service.subscription.SubscriptionMapper;
import ru.practicum.ewm_service.subscription.SubscriptionRepository;
import ru.practicum.ewm_service.subscription.dto.SubscriptionDto;
import ru.practicum.ewm_service.subscription.model.Subscription;
import ru.practicum.ewm_service.subscription.model.SubscriptionId;
import ru.practicum.ewm_service.user.User;
import ru.practicum.ewm_service.user.UserMapper;
import ru.practicum.ewm_service.user.UserRepository;
import ru.practicum.ewm_service.user.dto.UserShortDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final SubscriptionMapper subscriptionMapper;
    private final UserMapper userMapper;
    private final EntityManager entityManager;
    private final EventsFilterHandler eventsFilterHandler;


    @Override
    @Transactional
    public SubscriptionDto createSubscription(long followerId, long contentMakerId) {
        Map<Long, User> usersMap = userRepository.findAllById(List.of(followerId, contentMakerId)).stream()
                .collect(Collectors.toMap(User::getId, user -> user));
        User contentMaker = usersMap.get(contentMakerId);
        if (contentMaker.isFollowsProhibited())
            throw new ConditionsNotMetException("Пользователь с id = " + contentMakerId + " запретил подписки на себя");
        SubscriptionId id = new SubscriptionId(contentMakerId, followerId);
        if (subscriptionRepository.existsById(id)) {
            throw new ConditionsNotMetException("Подписка на пользователя с id = " + contentMakerId + " уже существует");
        }
        Subscription subscription = subscriptionRepository.save(
                new Subscription(contentMaker, usersMap.get(followerId), id)
        );
        entityManager.flush();
        return subscriptionMapper.toSubscriptionDto(subscription);
    }

    @Override
    @Transactional
    public void cancelSubscription(long followerId, long contentMakerId) {
        Subscription subscription = subscriptionRepository.findById(new SubscriptionId(contentMakerId, followerId))
                .orElseThrow(() -> new NotFoundException("Подписка на пользователя с id = " + contentMakerId + " не найдена"));
        subscriptionRepository.delete(subscription);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserShortDto> getFollowers(long userId, int from, int size) {
        validateUserExists(userId);
        List<User> followers = subscriptionRepository.findFollowersByContentMakerId(
                        userId, PageRequest.of(from / size, size))
                .getContent();
        return followers.stream()
                .map(userMapper::toUserShortDto)
                .toList();
    }

    @Override
    @Transactional
    public void changeFollowsPermission(long userId, boolean prohibit) {
        User user = getUserById(userId);
        if (user.isFollowsProhibited() == prohibit)
            throw new ConditionsNotMetException("Статус разрешения на подписки уже = " + prohibit);
        userRepository.changeFollowsPermission(userId, prohibit);
        cancelAllFollows(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getSubsEvents(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                             LocalDateTime rangeEnd, boolean onlyAvailable, EventSortType sort,
                                             int from, int size, String ip, long userId) {
        validateUserExists(userId);
        text = text == null || text.isEmpty() ? null : "%" + text.toLowerCase() + "%";
        if (categories == null || categories.isEmpty()) categories = null;
        if (rangeStart == null) rangeStart = LocalDateTime.now();
        int candidateCount = Math.min(size + 500, 1000);
        List<Long> ids = subscriptionRepository.findContentMakersIdsByFollowerId(userId);
        List<Event> events = eventRepository.findAllByInitiatorIdAndFilters(text, categories, paid, rangeStart, rangeEnd,
                        EventState.PUBLISHED, ids, PageRequest.of(0, candidateCount, Sort.by("eventDate")))
                .getContent();
        return eventsFilterHandler.handleFiltering(events, onlyAvailable, sort, from, size, ip);
    }

    private void validateUserExists(long userId) {
        if (!userRepository.existsById(userId))
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
    }

    private User getUserById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
    }

    private void cancelAllFollows(long userId) {
        subscriptionRepository.deleteAllByContentMakerId(userId);
    }
}