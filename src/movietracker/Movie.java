package movietracker;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Movie {
    private SimpleStringProperty title;
    private SimpleStringProperty director;
    private SimpleStringProperty dateWatched;
    private SimpleStringProperty genre;
    private SimpleIntegerProperty releaseYear;
    private SimpleDoubleProperty rating;

    public Movie(String title, String director, String dateWatched, String genre, int releaseYear, double rating) {
        this.title = new SimpleStringProperty(title);
        this.director = new SimpleStringProperty(director);
        this.dateWatched = new SimpleStringProperty(dateWatched);
        this.genre = new SimpleStringProperty(genre);
        this.releaseYear = new SimpleIntegerProperty(releaseYear);
        this.rating = new SimpleDoubleProperty(rating);
    }

    public String getTitle() {
        return title.get();
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public String getDirector() {
        return director.get();
    }

    public void setDirector(String director) {
        this.director.set(director);
    }

    public String getDateWatched() {
        return dateWatched.get();
    }

    public void setDateWatched(String dateWatched) {
        this.dateWatched.set(dateWatched);
    }

    public String getGenre() {
        return genre.get();
    }

    public void setGenre(String genre) {
        this.genre.set(genre);
    }

    public int getReleaseYear() {
        return releaseYear.get();
    }

    public void setReleaseYear(int releaseYear) {
        this.releaseYear.set(releaseYear);
    }

    public double getRating() {
        return rating.get();
    }

    public void setRating(double rating) {
        this.rating.set(rating);
    }
}
