package com.RisingSun.Controllers;

import com.RisingSun.client.AuthService;
import com.RisingSun.models.User;
import com.RisingSun.models.ApiResponse;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Button registerButton;
    @FXML private ProgressIndicator progressIndicator;
    @FXML private Label statusLabel;

    private AuthService authService;

    @FXML
    public void initialize() {
        authService = new AuthService();
        progressIndicator.setVisible(false);
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (validateInput(username, password)) {
            setUiDisabled(true);
            statusLabel.setText("Выполняется вход...");

            new Thread(() -> {
                User user = authService.login(username, password);

                javafx.application.Platform.runLater(() -> {
                    if (user != null) {
                        openChatWindow(user);
                    } else {
                        showAlert("Ошибка", "Неверное имя пользователя или пароль", Alert.AlertType.ERROR);
                        statusLabel.setText("Ошибка входа");
                    }
                    setUiDisabled(false);
                });
            }).start();
        }
    }

    @FXML
    private void handleRegister() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (validateInput(username, password)) {
            setUiDisabled(true);
            statusLabel.setText("Регистрация...");

            new Thread(() -> {
                ApiResponse response = authService.register(username, password);

                javafx.application.Platform.runLater(() -> {
                    if (response.isSuccess()) {
                        showAlert("Успех", response.getMessage(), Alert.AlertType.INFORMATION);
                        statusLabel.setText("Регистрация успешна");
                    } else {
                        showAlert("Ошибка", response.getMessage(), Alert.AlertType.ERROR);
                        statusLabel.setText("Ошибка регистрации");
                    }
                    setUiDisabled(false);
                });
            }).start();
        }
    }

    private boolean validateInput(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Ошибка", "Заполните все поля", Alert.AlertType.WARNING);
            return false;
        }

        if (username.length() < 3 || username.length() > 50) {
            showAlert("Ошибка", "Имя пользователя должно быть от 3 до 50 символов", Alert.AlertType.WARNING);
            return false;
        }

        if (password.length() < 6) {
            showAlert("Ошибка", "Пароль должен быть не менее 6 символов", Alert.AlertType.WARNING);
            return false;
        }

        return true;
    }

    private void setUiDisabled(boolean disabled) {
        usernameField.setDisable(disabled);
        passwordField.setDisable(disabled);
        loginButton.setDisable(disabled);
        registerButton.setDisable(disabled);
        progressIndicator.setVisible(disabled);
    }

    private void openChatWindow(User user) {
        try {
            Stage currentStage = (Stage) loginButton.getScene().getWindow();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/chat.fxml"));
            Parent root = loader.load();

            ChatController chatController = loader.getController();
            chatController.setUser(user);

            Scene scene = new Scene(root, 800, 600);
            scene.getStylesheets().add(getClass().getResource("/styles/styles.css").toExternalForm());

            Stage chatStage = new Stage();
            chatStage.setTitle("RisingSun Messenger - " + user.getUsername());
            chatStage.setScene(scene);
            chatStage.setMinWidth(600);
            chatStage.setMinHeight(400);

            chatStage.setOnCloseRequest(e -> chatController.shutdown());

            currentStage.close();
            chatStage.show();

        } catch (IOException e) {
            showAlert("Ошибка", "Не удалось открыть чат: " + e.getMessage(), Alert.AlertType.ERROR);
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
