package com.ade.chatclient.viewmodel;

import com.ade.chatclient.application.AbstractChildViewModel;
import com.ade.chatclient.application.ViewHandler;
import com.ade.chatclient.application.ViewModelUtils;
import com.ade.chatclient.domain.Chat;
import com.ade.chatclient.dtos.GroupRequest;
import com.ade.chatclient.model.ClientModel;
import com.ade.chatclient.view.GroupCreationDialog;
import com.ade.chatclient.view.cellfactory.ChatListCellFactory;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import javafx.util.Duration;
import lombok.Getter;

import java.beans.PropertyChangeEvent;
import java.io.File;
import java.util.Optional;

import static com.ade.chatclient.application.ViewModelUtils.listReplacer;
import static com.ade.chatclient.application.ViewModelUtils.runLaterListener;
@Getter
public class AllChatsViewModel extends AbstractChildViewModel<ClientModel> {
    private final ListProperty<Chat> chatListProperty = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final String mediaPath = "src/main/resources/com/ade/chatclient/sounds/sound.mp3";
    private final MediaPlayer mediaPlayer = new MediaPlayer(new Media(new File(mediaPath).toURI().toString()));
    private Boolean isSearching = false;
    private Chat selected;

    public AllChatsViewModel(ViewHandler viewHandler, ClientModel model) {
        super(viewHandler, model);

        model.addListener("gotChats", runLaterListener(listReplacer(chatListProperty)));
        model.addListener("NewChatCreated", runLaterListener(this::newChatCreated));
        model.addListener("selectedChatModified", runLaterListener(this::selectedChatModified));
        model.addListener("chatReceivedMessages", runLaterListener(this::raiseChat));
        model.addListener("UnreadChats", runLaterListener(this::updateUnreadChatsCounter));
    }

    private void updateUnreadChatsCounter(PropertyChangeEvent event) {
        //todo счетчик непрочитанных чатов
        Long unreadChatCounter = (Long) event.getNewValue();
    }

    private void raiseChat(PropertyChangeEvent event) {
        if (isSearching) return;
        synchronized (chatListProperty) {
            Chat chat = (Chat) event.getNewValue();
            chatListProperty.remove(chat);
            chatListProperty.add(0, chat);
            if ((boolean) event.getOldValue()) {
                model.setSelectChat(chat);
                selected = chat;
            }
            else playSound();
        }
    }

    private void newChatCreated(PropertyChangeEvent event) {
        if (isSearching) return;
        synchronized (chatListProperty) {
            Chat chat = (Chat) event.getNewValue();
            chatListProperty.add(0, chat);
            selected = chat;
            model.setSelectChat(selected);
        }
    }

    private void selectedChatModified(PropertyChangeEvent evt) {
        if (isSearching) return;
        synchronized (chatListProperty) {
            Chat chat = (Chat) evt.getNewValue();
            int index = chatListProperty.indexOf(chat);
            chatListProperty.set(index, chat);
            selected = chat;
        }
    }

    @Override
    public void actionInParentOnOpen() {
        viewHandler.getViewModelProvider().getChatPageViewModel().changeButtonsParam(2);
    }

    public ListCell<Chat> getChatListCellFactory() {
        ChatListCellFactory factory = ViewModelUtils.loadCellFactory(
                ChatListCellFactory.class,
                "chat-list-cell-factory.fxml"
        );
        factory.init(model.getMyself().getId());
        return factory;
    }

    public void showDialogAndWait() {
        GroupCreationDialog dialog = GroupCreationDialog.getInstance();
        dialog.init(model.getAllUsers(), new GroupCreationDialogModel());

        Optional<GroupRequest> answer = dialog.showAndWait();
        if (answer.isPresent()) {
            System.out.println(answer.get());
            model.createGroupChat(answer.get());
        }
    }

    public void onTextChanged(String newText) {
        if (newText == null || newText.isBlank()) {
            isSearching = false;
            model.fetchChats();
            return;
        }
        isSearching = true;
        synchronized (chatListProperty) {
            chatListProperty.clear();
            chatListProperty.addAll(model.searchChat(newText));
        }
    }

    public void onMouseClickedListener(MouseEvent mouseEvent) {
        Chat changedChat = ((ListView<Chat>) mouseEvent.getSource()).getSelectionModel().getSelectedItem();
        if (changedChat == null || changedChat.equals(model.getSelectedChat())) {
            return;
        }
        selected = changedChat;
        model.setSelectChat(changedChat);
    }
    private void playSound(){
        mediaPlayer.seek(Duration.ZERO);
        mediaPlayer.play();
    }
}
