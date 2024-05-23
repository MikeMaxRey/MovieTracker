/* Controller in MVC: Controls the main window's UI components and manages movie data.*/
package movietracker;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
        // Initialize columns
        titleColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("title"));
        directorColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("director"));
        dateWatchedColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("dateWatched"));
        genreColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("genre"));
        releaseYearColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("releaseYear"));
        ratingColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("rating"));
        
        // Set custom cell factory for dateWatchedColumn to format date
        dateWatchedColumn.setCellFactory(column -> new TreeTableCell<Movie, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    // Parse the date and format it to dd.mm.yy
                    setText(LocalDate.parse(item).format(DateTimeFormatter.ofPattern("dd.MM.yy")));
                }
            }
        });
        
        

        // Initialize root item
        TreeItem<Movie> root = new TreeItem<>(new Movie("Root", "", "", "", 0, 0.0));
        movieTable.setRoot(root);
        movieTable.setShowRoot(false);

        // Set context menu handler
        movieTable.setOnMouseClicked(this::showContextMenu);
        // Set cell factory for dateWatchedColumn to handle editing
        dateWatchedColumn.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
    }

    private void showContextMenu(MouseEvent event) {
        if (event.getButton() == MouseButton.SECONDARY) {
            TreeItem<Movie> selectedMovie = movieTable.getSelectionModel().getSelectedItem();
            if (selectedMovie != null && selectedMovie.getValue() != null) {
                ContextMenu contextMenu = new ContextMenu();

                MenuItem editItem = new MenuItem("Edit");
                editItem.setOnAction(e -> handleEditMovie(selectedMovie.getValue()));

                MenuItem deleteItem = new MenuItem("Delete");
                deleteItem.setOnAction(e -> handleDeleteMovie(selectedMovie));

                contextMenu.getItems().addAll(editItem, deleteItem);
                contextMenu.show(movieTable, event.getScreenX(), event.getScreenY());
            }
        }
    }

    @FXML
    private void handleDeleteMovie(TreeItem<Movie> movieItem) {
        movieTable.getRoot().getChildren().remove(movieItem);
        movieData.remove(movieItem.getValue());
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
            controller.setMovieData(movieData);  // Pass movieData to the controller

            dialogStage.showAndWait();

            if (controller.isSaveClicked()) {
                Movie newMovie = new Movie(controller.getTitle(), controller.getDirector(),
                        controller.getDateWatched().toString(), controller.getGenre(),
                        controller.getReleaseYear(), controller.getRating());
                movieTable.getRoot().getChildren().add(new TreeItem<>(newMovie));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleEditMovie(Movie movie) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Resources/NewMovieDialog.fxml"));
            Parent page = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edit Movie");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            NewMovieDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setMovie(movie);  // Pass the movie to the controller
            controller.setMovieData(movieData);  // Pass movieData to the controller

            dialogStage.showAndWait();

            if (controller.isSaveClicked()) {
                // Update the movie data
                movieTable.refresh();
            }
        } catch (IOException e) {
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
            saveMovieDataToFile(currentFile); //check if there is a currently opened file
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
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Resources/AboutWindow.fxml"));
            Parent root = loader.load();
            
            Stage aboutStage = new Stage();
            aboutStage.setTitle("About");
            aboutStage.setScene(new Scene(root));
            aboutStage.initModality(Modality.APPLICATION_MODAL);
            aboutStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
    
    
}