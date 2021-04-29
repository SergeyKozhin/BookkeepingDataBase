package controllers;

import javafx.stage.Stage;

public interface ModalController extends Controller {
    void setStage(Stage stage);
    Stage getStage();
}
