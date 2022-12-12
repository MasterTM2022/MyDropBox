package ru.gb.perov.netty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor

public class StringMessage extends AbstractMessage {
    private String content;
    private LocalDateTime time;
}
