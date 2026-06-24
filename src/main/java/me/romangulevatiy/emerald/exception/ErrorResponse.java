package me.romangulevatiy.emerald.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorResponse {

    @Builder.Default
    private final Instant timestamp = Instant.now();

    private int status;
    private String error;
    private String message;
    private String path;
}
