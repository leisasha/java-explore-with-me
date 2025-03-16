package ru.practicum.ewm.dto.event;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.dto.category.CategoryMapper;
import ru.practicum.ewm.dto.user.UserMapper;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.model.event.Event;
import ru.practicum.ewm.model.event.EventState;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EventMapper {

    public static EventFullDto toEventFullDto(Event event, Long confirmedRequests, Long views) {
        return new EventFullDto(
                event.getId(),
                event.getTitle(),
                event.getAnnotation(),
                event.getDescription(),
                CategoryMapper.toCategoryDto(event.getCategory()),
                UserMapper.toUserShortDto(event.getInitiator()),
                event.getEventDate(),
                event.getPaid(),
                event.getParticipantLimit(),
                event.getRequestModeration(),
                event.getCreatedOn(),
                event.getPublishedOn(),
                event.getState().name(),
                new LocationEventDto(event.getLatitude(), event.getLongitude()),
                confirmedRequests,
                views
        );
    }

    public static EventShortDto toEventShortDto(Event event, Long confirmedRequests, Long views) {
        return new EventShortDto(
                event.getId(),
                event.getTitle(),
                event.getAnnotation(),
                CategoryMapper.toCategoryDto(event.getCategory()),
                UserMapper.toUserShortDto(event.getInitiator()),
                event.getEventDate(),
                event.getPaid(),
                confirmedRequests,
                views,
                event.getRating()
        );
    }

    public static Event toEvent(NewEventDto newEventDto, Category category, User initiator) {
        return new Event(
                null,
                newEventDto.getTitle(),
                newEventDto.getAnnotation(),
                newEventDto.getDescription(),
                category,
                initiator,
                newEventDto.getEventDate(),
                newEventDto.getPaid(),
                newEventDto.getParticipantLimit(),
                newEventDto.getRequestModeration(),
                null,
                null,
                EventState.PENDING,
                newEventDto.getLocation().getLat(),
                newEventDto.getLocation().getLon(),
                0L
        );
    }
}

