package com.RisingSun.Controllers;

import com.RisingSun.client.MessageService;
import com.RisingSun.models.User;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ChatController {
    @FXML private ListView<String> messagesListView;
    @FXML private TextField messageField;
    @FXML private Button sendButton;
    @FXML private Label statusLabel;
    @FXML private Label usernameLabel;

    private User currentUser;
    private MessageService messageService;
    private ObservableList<String> messages;
    private Timer messageTimer;

    public void setUser(User user) {
        this.currentUser = user;
        this.messageService = new MessageService(user.getToken());
        this.messages = FXCollections.observableArrayList();

        initializeUI();
        startMessagePolling();
        loadMessages();
    }

    private void initializeUI() {
        messagesListView.setItems(messages);
        usernameLabel.setText("Пользователь: " + currentUser.getUsername());
        statusLabel.setText("Статус: Подключено");
        statusLabel.setStyle("-fx-text-fill: #00ff00;");
    }

    @FXML
    private void handleSendMessage() {
        String message = messageField.getText().trim();
        if (!message.isEmpty()) {
            new Thread(() -> {
                boolean success = messageService.sendMessage(message);

                javafx.application.Platform.runLater(() -> {
                    if (success) {
                        messageField.clear();
                        loadMessages();
                    } else {
                        showAlert("Ошибка", "Не удалось отправить сообщение", Alert.AlertType.ERROR);
                    }
                });
            }).start();
        }
    }
    @FXML
    private void handleKeyPress() {
        // Отправка сообщения по Enter
        javafx.application.Platform.runLater(() -> {
            if (messageField.isFocused()) {
                handleSendMessage();
            }
        });
    }

    private void loadMessages() {
        new Thread(() -> {
            List<String> newMessages = messageService.getMessages();

            if (newMessages != null) {
                javafx.application.Platform.runLater(() -> {
                    messages.setAll(newMessages);
                    // Автопрокрутка к последнему сообщению
                    if (!messages.isEmpty()) {
                        messagesListView.scrollTo(messages.size() - 1);
                    }
                });
            }
        }).start();
    }

    private void startMessagePolling() {
        messageTimer = new Timer(true);
        messageTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                loadMessages();
            }
        }, 0, 3000); // Обновление каждые 3 секунды
    }

    public void shutdown() {
        if (messageTimer != null) {
            messageTimer.cancel();
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

