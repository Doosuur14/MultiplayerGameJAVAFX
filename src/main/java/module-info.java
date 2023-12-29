module com.example.multiplayergame4 {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.multiplayergame4 to javafx.fxml;
    exports com.example.multiplayergame4;
}