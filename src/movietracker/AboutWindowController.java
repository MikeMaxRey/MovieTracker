package movietracker;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class AboutWindowController {

    @FXML
    private void openGitHubPage(ActionEvent event) {
        String url = "https://github.com/MikeMaxRey/MovieTracker";
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
