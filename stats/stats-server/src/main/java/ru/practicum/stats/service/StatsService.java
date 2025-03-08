package ru.practicum.stats.service;

import ru.practicum.stats.EndpointHitDto;
import ru.practicum.stats.ViewStatsDto;

import java.util.List;

public interface StatsService {
    void saveHit(EndpointHitDto hitDto);

    List<ViewStatsDto> getStats(String start, String end, List<String> uris, boolean unique);
}
