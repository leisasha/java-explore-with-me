package ru.practicum.ewm.dto.participationRequest;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ParticipationRequestDto {
    private Long id;
    private Long event;
    private Long requester;
    private String status;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;
}
