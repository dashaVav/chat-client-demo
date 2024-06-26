package com.ade.chatclient.domain;

import lombok.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * Класс, который представляет сообщение
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    private Long id;
    private String text;
    private LocalDateTime dateTime;
    private User author;
    private Long chatId;
    private Boolean isAuxiliary;
    private String attachmentId;


    public void setDateTime(LocalDateTime dateTime) {
        var utcTime = ZonedDateTime.of(dateTime, ZoneId.of("UTC"));
        this.dateTime = utcTime.withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Message message)) return false;
        return id.equals(message.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return text + " (" + chatId + ") " + author;
    }
}

