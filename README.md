![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![Windows](https://img.shields.io/badge/Windows-0078D6?style=for-the-badge&logo=windows&logoColor=white)

## In Touch Desktop - Корпоративный мессенджер

Клиентское приложение для курсовой работы, разрабатываемое на 2ом и 3ем курсе.

Ссылки на связанные репозитрии:
* [Backend](https://github.com/Zeonx9/chat-server-demo)
* [Android mobile client](https://github.com/Zeonx9/in-touch-mobile-app)


## Использованные библиотеки

Проект написан на Java. Для написания графического интерфейса использована JavaFX

* JavaFx - основная графическая библиотека, экраны описаны в FXML файлах с подключаемыми CSS стилями. 
* Jackson - для сериализации и десериализации объектов передаваемых по сети в формате JSON
* Spring Websocket + Spring Messaging - Websocket Подключение к серверу и обмен сообщениям по протоколу STOMP


## Функционал приложения

1. Общение в личных чатах и создание групповых чатов внутри компании
2. Редактирование личной информации и просмотр информации о других пользователях
3. Отправка медиафайлов 
4. При входе под аккаунтом администратора отображается соответсвующий интерфейс, 
позволяющий добавлять в компанию новых сотрудников. 

## Архитектура приложения 

При проектировании была использована модель **MVVM**:
* Model - слой отвечающий за загрузку данных и бизнес логику приложения, включает в себя 
описание API и репозиториев, а также управляющие model-классы
* View - слой, отображающий пользователю интерфейс и предоставляющий элементы управления интерфейсом. 
* View-Model - слой, непосредственно связанный View и отвечающий за хранение состояние UI и логику отображения элементов

Фреймворки для внедрения зависимостей не использовались, 
все фабрики для создания компонентов приложения писались вручную.