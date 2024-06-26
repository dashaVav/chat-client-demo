package com.ade.chatclient.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Класс, который представляет информацию о групповом чате
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupChatInfo {
    private String name;
    private LocalDate creationDate;
    private User creator;
    private String groupPhotoId;
}
