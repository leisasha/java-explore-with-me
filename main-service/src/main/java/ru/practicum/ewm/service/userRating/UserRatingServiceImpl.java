package ru.practicum.ewm.service.userRating;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.user.UserRatingDto;
import ru.practicum.ewm.repository.UserRatingRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserRatingServiceImpl implements UserRatingService {
    private final UserRatingRepository userRatingRepository;

    public List<UserRatingDto> getTopUsers(int limit) {
        List<Object[]> results = userRatingRepository.findUserRatings();
        return results.stream()
                .limit(limit)
                .map(row -> new UserRatingDto(
                        (Long) row[0],
                        (String) row[1],
                        (Long) row[2])
                )
                .collect(Collectors.toList());
    }
}
