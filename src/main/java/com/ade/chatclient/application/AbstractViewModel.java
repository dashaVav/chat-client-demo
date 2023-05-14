package com.ade.chatclient.application;

public class AbstractViewModel<M> {
    /**
     * объект управляющий переключениями вью
     */
    protected final ViewHandler viewHandler;
    /**
     * модель является источником данных
     */
    protected final M model;

    /**
     * Ввыполняет привязку модели и объекта управляющего переключениями View к ViewModel
     */
    public AbstractViewModel(ViewHandler viewHandler, M model) {
        this.viewHandler = viewHandler;
        this.model = model;
    }
}
