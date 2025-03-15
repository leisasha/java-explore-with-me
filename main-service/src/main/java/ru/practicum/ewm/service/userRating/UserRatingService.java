package ru.practicum.ewm.service.userRating;

import ru.practicum.ewm.dto.user.UserRatingDto;

import java.util.List;

public interface UserRatingService {
    List<UserRatingDto> getTopUsers(int limit);
}
