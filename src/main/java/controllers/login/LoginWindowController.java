package controllers.login;

import application.App;
import controllers.Controller;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginWindowController implements Controller {
    private App app;

    @FXML
    private TextField usernameInput;

    @FXML
    private PasswordField passwordInput;

    @FXML
    void handleExit(ActionEvent event) {
        app.getPrimaryStage().close();
    }

    @FXML
    void handleLogin(ActionEvent event) {
        if (usernameInput.getText() == null || usernameInput.getText().isEmpty()) {
            app.showError("Enter username!");
            return;
        }

        if (passwordInput.getText() == null || passwordInput.getText().isEmpty()) {
            app.showError("Enter password!");
            return;
        }

        if (!app.getUsers().authenticate(usernameInput.getText(), passwordInput.getText())) {
            app.showError("Incorrect username or password!");
            return;
        }

        app.openWindow("/projects/ProjectsWindow.fxml", "Projects");
        app.getPrimaryStage().centerOnScreen();
    }

    @Override
    public void setApp(App app) {
        this.app = app;
    }
}
