package com.ade.chatclient.viewmodel;

import com.ade.chatclient.ClientApplication;
import com.ade.chatclient.model.ClientModel;
import com.ade.chatclient.domain.Chat;
import com.ade.chatclient.domain.Message;
import com.ade.chatclient.model.ClientModelImpl;
import com.ade.chatclient.view.ViewHandler;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lombok.Getter;

import java.beans.PropertyChangeEvent;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

@Getter
public class ChatPageViewModel {
    // первые 3 поля тоже должен был добавить Егор
    private final ListProperty<Chat> chatListProperty;
    private final ListProperty<Message> messageListProperty;
    private final StringProperty messageTextProperty;


    private final ClientModel model;
    private final ViewHandler viewHandler;


    // конструктор тоже егор

    ChatPageViewModel(ViewHandler viewHandler, ClientModel model) {
        this.model = model;
        this.viewHandler = viewHandler;
        chatListProperty = new SimpleListProperty<>(FXCollections.observableArrayList());
        messageListProperty = new SimpleListProperty<>(FXCollections.observableArrayList());
        messageTextProperty = new SimpleStringProperty();
        model.addListener("MyChatsUpdate", this::updateMyChats);
        model.addListener("MessageUpdate", this::updateMessage);
        runAutoUpdateMessages();
        runAutoUpdateChats();

    }

    private void updateMyChats(PropertyChangeEvent propertyChangeEvent) {
        Platform.runLater(() -> {
            List<Chat> myChats = (List<Chat>) propertyChangeEvent.getNewValue();
            System.out.println("updateChats");
            chatListProperty.clear();
            chatListProperty.addAll(myChats);
                }

        );


    }
    private void runAutoUpdateChats() {
        Thread thread = new Thread(() -> {
            Random r = new Random();
            while (true) {
                model.updateMyChats();
                try {
                    Thread.sleep(r.nextInt(500) + 1000);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        );
        thread.setDaemon(true);
        thread.start();
    }

    private void runAutoUpdateMessages() {
        Thread thread = new Thread(() -> {
            Random r = new Random();
            while (true) {
                model.updateMessages();
                try {
                    Thread.sleep(r.nextInt(250) + 100);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        );
        thread.setDaemon(true);
        thread.start();
    }

    private void updateMessage(PropertyChangeEvent propertyChangeEvent) {
        Platform.runLater(() -> {
            List<Message> selectedChatMessages = (List<Message>) propertyChangeEvent.getNewValue();
            System.out.println("updateMes");
            messageListProperty.clear();
            messageListProperty.addAll(selectedChatMessages);
            }
        );

    }

    // методы до следующего коммента - это Даша делает

//    public void updateChatList() {
//        chatListProperty.clear();
//        chatListProperty.addAll(model.getMyChats());
//    }

//    public void updateMessagesInSelectedChat() {
//        model.updateMessages();
//        messageListProperty.clear();
//        messageListProperty.addAll(model.getSelectedChatMessages());
//    }

    public void onSelectedItemChange(Observable observable, Chat oldValue, Chat newValue) {
        // теории тут потока
        if (newValue == null) {
            System.out.println("somehow selected chat is null");
            return;
        }
        model.selectChat(newValue);
//        updateMessagesInSelectedChat();
    }

    public void sendMessage() {
        if (messageTextProperty.get().isBlank())
            return;

        model.sendMessageToChat(messageTextProperty.get());
        messageTextProperty.set("");
//        updateMessagesInSelectedChat();
    }

    // все метод ниже должен был написать егор

    private String prepareChatToBeShown(Chat chat) {
        List<String> memberNames = new ArrayList<>();
        chat.getMembers().forEach(member -> {
            if (!Objects.equals(member.getId(), model.getMyself().getId()))
                memberNames.add(member.getUsername());
        });
        return String.join(", ", memberNames);
    }

    private String prepareMessageToBeShown(Message msg) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm, dd.MM");
        return "from " + msg.getAuthor().getUsername() +
                ": " + msg.getText() +
                " at " + msg.getDateTime().format(dtf);
    }

    public ListCell<Chat> getChatListCellFactory() {
        return new ListCell<>() {
            private final ImageView imageView = new ImageView();
            @Override
            protected void updateItem(Chat item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                }
                else {
                    setText(prepareChatToBeShown(item));
                    var imgStream = ClientApplication.class.getResourceAsStream("img/user_avatar_chat_icon.png");
                    if (imgStream == null)
                        throw new RuntimeException("image stream is null");
                    imageView.setImage(
                            new Image(imgStream));
                    setGraphic(imageView);
                }

            }
        };
    }

    public ListCell<Message> getMessageCellFactory() {
        return new ListCell<>() {
            @Override
            protected void updateItem(Message item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null)
                    setText(null);
                else
                    setText(prepareMessageToBeShown(item));
            }
        };
    }

    public void showUsers() {
        try {
            viewHandler.openView(ViewHandler.Views.ALL_USERS_VIEW);
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
            messageTextProperty.set("cannot switch to another view!");
        }
    }
}
