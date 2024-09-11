package com.imures.kaadbackend.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@RequiredArgsConstructor
@Getter
@Setter
public class ErrorMessage {
    private final OffsetDateTime timestamp;
    private final String message;
}
