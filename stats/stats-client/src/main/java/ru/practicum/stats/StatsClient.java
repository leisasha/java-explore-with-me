package ru.practicum.stats;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class StatsClient {
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${stats-server.url}")
    private String statsServerUrl;

    public void saveHit(EndpointHitDto hitDto) {
        String url = statsServerUrl + "/hit";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<EndpointHitDto> request = new HttpEntity<>(hitDto, headers);
        restTemplate.postForEntity(url, request, Void.class);
    }

    public List<ViewStatsDto> getStats(String start, String end, List<String> uris, boolean unique) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(statsServerUrl + "/stats")
                .queryParam("start", start)
                .queryParam("end", end)
                .queryParam("unique", unique);

        if (uris != null && !uris.isEmpty()) {
            for (String uri : uris) {
                uriBuilder.queryParam("uris", uri);
            }
        }

        ResponseEntity<ViewStatsDto[]> response = restTemplate.getForEntity(uriBuilder.toUriString(), ViewStatsDto[].class);
        return List.of(Objects.requireNonNull(response.getBody()));
    }
}
