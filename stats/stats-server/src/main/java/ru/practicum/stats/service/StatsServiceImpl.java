package ru.practicum.stats.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.stats.EndpointHitDto;
import ru.practicum.stats.ViewStatsDto;
import ru.practicum.stats.exception.ValidationException;
import ru.practicum.stats.model.EndpointHit;
import ru.practicum.stats.repository.StatsRepository;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {
    private final StatsRepository statsRepository;

    public void saveHit(EndpointHitDto hitDto) {
        EndpointHit hit = new EndpointHit(
                null,
                hitDto.getApp(),
                hitDto.getUri(),
                hitDto.getIp(),
                hitDto.getTimestamp()
        );
        statsRepository.save(hit);
    }

    public List<ViewStatsDto> getStats(String start, String end, List<String> uris, boolean unique) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        LocalDateTime startTime = (start == null || start.isBlank()) ?
                LocalDateTime.now().minusYears(10) :
                LocalDateTime.parse(URLDecoder.decode(start, StandardCharsets.UTF_8), formatter);

        LocalDateTime endTime = (end == null || end.isBlank()) ?
                LocalDateTime.now() :
                LocalDateTime.parse(URLDecoder.decode(end, StandardCharsets.UTF_8), formatter);

        if (start == null || start.isBlank()) {
            throw new ValidationException("Дата начала `start` не указана");
        }
        if (end == null || end.isBlank()) {
            throw new ValidationException("Дата конца `end` не указана");
        }
        if (startTime.isAfter(endTime)) {
            throw new ValidationException("Дата начала `start` не может быть позже даты окончания `end`");
        }

        String urisString = (uris == null || uris.isEmpty()) ? null : String.join(",", uris);

        List<Object[]> rawStats = unique ?
                statsRepository.getUniqueStats(startTime, endTime, urisString) :
                statsRepository.getStats(startTime, endTime, urisString);

        return rawStats.stream()
                .map(obj -> new ViewStatsDto((String) obj[0], (String) obj[1], ((Number) obj[2]).longValue()))
                .collect(Collectors.toList());
    }
}
