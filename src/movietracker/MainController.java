package movietracker;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.*;
import java.io.File;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainController {

    @FXML
    private TreeTableView<Movie> movieTable;
    @FXML
    private TreeTableColumn<Movie, String> titleColumn;
    @FXML
    private TreeTableColumn<Movie, String> directorColumn;
    @FXML
    private TreeTableColumn<Movie, String> dateWatchedColumn;
    @FXML
    private TreeTableColumn<Movie, String> genreColumn;
    @FXML
    private TreeTableColumn<Movie, Integer> releaseYearColumn;
    @FXML
    private TreeTableColumn<Movie, Double> ratingColumn;

    private ObservableList<Movie> movieData = FXCollections.observableArrayList();
    private Stage primaryStage;
    private File currentFile; // Reference to the currently opened file
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML
    private void initialize() {
        titleColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("title"));
        directorColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("director"));
        dateWatchedColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("dateWatched"));
        genreColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("genre"));
        releaseYearColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("releaseYear"));
        ratingColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("rating"));

        TreeItem<Movie> root = new TreeItem<>(new Movie("Root", "", "", "", 0, 0.0));
        movieTable.setRoot(root);
        movieTable.setShowRoot(false);
    }

    @FXML
    private void handleCreateNewInstance() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Resources/NewMovieDialog.fxml"));
            Parent page = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Create New Movie");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            NewMovieDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);

            dialogStage.showAndWait();

            if (controller.isSaveClicked()) {
                Movie newMovie = new Movie(controller.getTitle(), controller.getDirector(),
                                           controller.getDateWatched().toString(), controller.getGenre(),
                                           controller.getReleaseYear(), controller.getRating());
                movieData.add(newMovie);
                movieTable.getRoot().getChildren().add(new TreeItem<>(newMovie));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleOpenFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showOpenDialog(primaryStage);
        if (file != null) {
            loadMovieDataFromFile(file);
        }
    }

    @FXML
    private void handleSaveFile() {
    	if (currentFile != null) {
    		saveMovieDataToFile(currentFile); //check if there is a currently openend file 
    	} else {
    		
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showSaveDialog(primaryStage);
        if (file != null) {
        	currentFile = file; //Update current file reference 
            saveMovieDataToFile(file);
        }
        }
    }

    private void loadMovieDataFromFile(File file) {
    	currentFile = file; //Store the file reference 
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            movieData.clear();
            movieTable.getRoot().getChildren().clear(); // clear current items 
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 6) {
                    Movie movie = new Movie(data[0], data[1], data[2], data[3],
                                            Integer.parseInt(data[4]), Double.parseDouble(data[5]));
                    movieData.add(movie);
                    movieTable.getRoot().getChildren().add(new TreeItem<>(movie));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveMovieDataToFile(File file) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (Movie movie : movieData) {
                String line = String.format("%s,%s,%s,%s,%d,%.2f",
                        movie.getTitle(), movie.getDirector(), movie.getDateWatched(),
                        movie.getGenre(), movie.getReleaseYear(), movie.getRating());
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAbout() {
        // Add your logic to show the About dialog here
    }

    @FXML
    private void handleGetHelp() {
        // Add your logic to show the Help dialog here
    }

    @FXML
    private void handleAppearance() {
        // Add your logic to handle Appearance settings here
    }
}
