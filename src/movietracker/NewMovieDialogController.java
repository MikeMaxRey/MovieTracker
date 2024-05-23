// Controller in MVC: Handles user input for adding or editing movie instances in the dialog window.
package movietracker;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class NewMovieDialogController {

    @FXML
    private TextField titleField;

    @FXML
    private TextField directorField;

    @FXML
    private TextField dateWatchedField;

    @FXML
    private TextField genreField;

    @FXML
    private TextField releaseYearField;

    @FXML
    private TextField ratingField;

    private Stage dialogStage;
    private boolean saveClicked = false;

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public boolean isSaveClicked() {
        return saveClicked;
    }
    
    private ObservableList<Movie> movieData;

    public void setMovieData(ObservableList<Movie> movieData) {
        this.movieData = movieData;
    }

    
    

    @FXML
    private void handleSave() {
        if (isInputValid()) {
            if (movie == null) {
                movie = new Movie(getTitle(), getDirector(), getDateWatched().toString(), getGenre(), getReleaseYear(), getRating());
                if (movieData != null) {
                    movieData.add(movie);  // Add the new movie to the movieData list
                }
            } else {
                movie.setTitle(getTitle());
                movie.setDirector(getDirector());
                movie.setDateWatched(getDateWatched().toString());
                movie.setGenre(getGenre());
                movie.setReleaseYear(getReleaseYear());
                movie.setRating(getRating());
            }
            saveClicked = true;
            dialogStage.close();
        }
    }



    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    private boolean isInputValid() {
        String errorMessage = "";

        if (titleField.getText() == null || titleField.getText().isEmpty()) {
            errorMessage += "No valid title!\n";
        }

        if (directorField.getText() == null || directorField.getText().isEmpty()) {
            errorMessage += "No valid director!\n";
        }

        if (dateWatchedField.getText() == null || dateWatchedField.getText().isEmpty()) {
            errorMessage += "No valid date watched!\n";
        } else {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy");
                LocalDate.parse(dateWatchedField.getText(), formatter);
            } catch (DateTimeParseException e) {
                errorMessage += "No valid date watched (must be in format dd.MM.yy)!\n";
            }
        }

        if (genreField.getText() == null || genreField.getText().isEmpty()) {
            errorMessage += "No valid genre!\n";
        }

        if (releaseYearField.getText() == null || releaseYearField.getText().isEmpty()) {
            errorMessage += "No valid release year!\n";
        } else {
            try {
                Integer.parseInt(releaseYearField.getText());
            } catch (NumberFormatException e) {
                errorMessage += "No valid release year (must be an integer)!\n";
            }
        }

        if (ratingField.getText() == null || ratingField.getText().isEmpty()) {
            errorMessage += "No valid rating!\n";
        } else {
            try {
                Double.parseDouble(ratingField.getText());
            } catch (NumberFormatException e) {
                errorMessage += "No valid rating (must be a double)!\n";
            }
        }

        if (errorMessage.isEmpty()) {
            return true;
        } else {
            // Show the error message
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initOwner(dialogStage);
            alert.setTitle("Invalid Fields");
            alert.setHeaderText("Please correct invalid fields");
            alert.setContentText(errorMessage);

            alert.showAndWait();

            return false;
        }
    }
    
    private Movie movie;

    public void setMovie(Movie movie) {
        this.movie = movie;

        if (movie != null) {
            titleField.setText(movie.getTitle());
            directorField.setText(movie.getDirector());
            dateWatchedField.setText(movie.getDateWatched());
            genreField.setText(movie.getGenre());
            releaseYearField.setText(String.valueOf(movie.getReleaseYear()));
            ratingField.setText(String.valueOf(movie.getRating()));
        }
    }
    public String getTitle() {
        return titleField.getText();
    }

    public String getDirector() {
        return directorField.getText();
    }

    public LocalDate getDateWatched() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy");
        return LocalDate.parse(dateWatchedField.getText(), formatter);
    }

    public String getGenre() {
        return genreField.getText();
    }

    public int getReleaseYear() {
        return Integer.parseInt(releaseYearField.getText());
    }

    public double getRating() {
        return Double.parseDouble(ratingField.getText());
    }
}
