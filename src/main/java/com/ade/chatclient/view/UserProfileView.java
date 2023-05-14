package com.ade.chatclient.view;

import com.ade.chatclient.application.structure.AbstractView;
import com.ade.chatclient.viewmodel.UserProfileViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * Класс выступает в роли контроллера для панели личного кабинета пользователя, управляет поведением и отображением элементов на экране
 */
public class UserProfileView extends AbstractView<UserProfileViewModel> {
    @FXML private Label fullName;
    @FXML private Label birthDate;
    @FXML private Label userName;
    @FXML private Label companyName;
    @FXML private Label passwordResultMessage;

    @Override
    protected void initialize() {
        fullName.textProperty().bind(viewModel.getFullNameProperty());
        birthDate.textProperty().bind(viewModel.getBirthDateProperty());
        userName.textProperty().bind(viewModel.getUserNameProperty());
        companyName.textProperty().bind(viewModel.getCompanyNameProperty());
        passwordResultMessage.textProperty().bind(viewModel.getResultMessageProperty());

        viewModel.setUserPersonalData();
    }

    /**
     * Метод вызывает функцию открытия диалогового окна для смены пароля, собирает данные от пользователя
     * и отправляет запрос на изменения пароля,
     * срабатывает по нажатию на кнопку Change password
     */
    public void onChangeButtonClicked() {
        viewModel.showDialogAndWait();
    }

    /**
     * Функция запускает процесс выхода из аккаунта,
     * срабатывает при нажатии на кнопку Log Out
     */
    public void onLogOutButtonClicked() {
        viewModel.logOut();
    }
}