package com.ade.chatclient.view.cellfactory;

import com.ade.chatclient.domain.Chat;
import com.ade.chatclient.domain.Message;
import com.ade.chatclient.domain.User;
import com.ade.chatclient.view.components.UserPhoto;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * Фабрика ячеек списка чатов, предназначена для генерации и настройки ячеек в ListView, определяет, как они будут выглядеть для дальнейшей автоматической генерации
 */
public class ChatListCellFactory extends ListCell<Chat> {
    private Long selfId;
    @FXML private AnchorPane layout;
    @FXML private StackPane photoPane;
    @FXML private Label chatNameLabel;
    @FXML private Label lastMsgLabel;
    @FXML private Label lastMessageDateLabel;
    @FXML private Label countUnreadMessages;
    @FXML private Circle onlineStatus;
    private Function<String, CompletableFuture<Image>> imageRequest;

    public void init(Long selfId, Function<String, CompletableFuture<Image>> imageRequest) {
        this.selfId = selfId;
        this.imageRequest = imageRequest;
    }

    /**
     * Метод заполняет все значения в полях ячейки, а так же устанавливает layout в качестве графики - AnchorPane, в котором описан интерфейс одной ячейки
     * @param item объект класса Chat - чат
     * @param empty переменная типа boolean, показывает, является ли ячейка в списке пустой
     */
    @Override
    protected void updateItem(Chat item, boolean empty) {
        super.updateItem(item, empty);

        setText(null);
        if (empty || item == null) {
            setGraphic(null);
            return;
        }

        chatNameLabel.setText(prepareChatToBeShown(item));
        lastMsgLabel.setText(prepareLastMessage(item));
        lastMessageDateLabel.setText(prepareLastMessageDate(item));

        if (item.getUnreadCount() != 0) {
            countUnreadMessages.setText(String.valueOf(item.getUnreadCount()));
            countUnreadMessages.setStyle("-fx-background-color: #3E46FF");
        }
        else {
            countUnreadMessages.setText("");
            countUnreadMessages.setStyle("-fx-background-color: transparent");
        }

        photoPane.getChildren().clear();
        Objects.requireNonNull(UserPhoto.getPaneContent(item, selfId, 20, imageRequest))
                .thenAccept(children -> {
                    Platform.runLater(() -> {
                        photoPane.getChildren().clear();
                        photoPane.getChildren().addAll(children);
                        if (item.getIsPrivate()) {
                            for (User member : item.getMembers()) {
                                if (!Objects.equals(member.getId(), selfId))
                                    if (member.getIsOnline() != null && member.getIsOnline()) {
                                        onlineStatus.setOpacity(100);
                                    }
                                    else {
                                        onlineStatus.setOpacity(0);
                                    }
                             }
                        }
                        else {
                            onlineStatus.setOpacity(0);
                        }
                        setGraphic(layout);
                    });
                });

        setGraphic(layout);
    }

    private String prepareLastMessageDate(Chat chat) {
        Message msg = chat.getLastMessage();
        if (msg == null)
            return "";
        LocalDateTime messageDateTime = msg.getDateTime();
        LocalDateTime now = LocalDateTime.now();

        long daysAgo = ChronoUnit.DAYS.between(messageDateTime, now);

        if (messageDateTime.toLocalDate().isEqual(now.toLocalDate())) {
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            return messageDateTime.format(timeFormatter);
        } else if (daysAgo < 7) {
            DateTimeFormatter dayOfWeekFormatter = DateTimeFormatter.ofPattern("EEEE", Locale.ENGLISH);
            return messageDateTime.format(dayOfWeekFormatter);
        } else {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM");
            return messageDateTime.format(dateFormatter);
        }
    }

    /**
     * Метод определяет название чата: для личного диалога возвращает имя собеседника, для беседы - ее название
     * @param chat один из чатов
     * @return строку, которая содержит имя чата
     */
    private String prepareChatToBeShown(Chat chat) {
        if (chat.getGroup() != null) {
            return chat.getGroup().getName();
        }

        List<String> memberNames = new ArrayList<>();
        chat.getMembers().forEach(member -> {
            if (!Objects.equals(member.getId(), selfId))
                memberNames.add(member.getRealName() + " " + member.getSurname());
        });
        return String.join(", ", memberNames);
    }

    /**
     * Метод определяет последнее сообщение в диалоге и подготавливает его для отображения в ячейке чата
     * @param chat один из чатов
     * @return обрезанный текст последнего сообщения в диалоге
     */
    private String prepareLastMessage(Chat chat) {
        Message msg = chat.getLastMessage();
        if (msg == null) {
            return null;
        }
        if (msg.getText().length() < 20) {
            return msg.getText();
        }
        return msg.getText().substring(0, 20) + "...";
    }
}
