package Model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;

public class GameModel {

    private final StringProperty name;
    private final StringProperty description;
    private final StringProperty rating;
    private final StringProperty genre;
    private final Image image;
    private final StringProperty developer;
    private final StringProperty releaseDate;
    private final StringProperty purchaseLink;


    public GameModel(String name, String description, String rating,
                     String genre, Image image, String developer,
                     String releaseDate, String purchaseLink) {

        this.name = new SimpleStringProperty(name);
        this.description = new SimpleStringProperty(description);
        this.rating = new SimpleStringProperty(rating);
        this.genre = new SimpleStringProperty(genre);
        this.image = image;
        this.developer = new SimpleStringProperty(developer);
        this.releaseDate = new SimpleStringProperty(releaseDate);
        this.purchaseLink = new SimpleStringProperty(purchaseLink);
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getDescription() {
        return description.get();
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public String getRating() {
        return rating.get();
    }

    public StringProperty ratingProperty() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating.set(rating);
    }

    public String getGenre() {
        return genre.get();
    }

    public StringProperty genreProperty() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre.set(genre);
    }

    public Image getImage() {
        return image;
    }

    public String getDeveloper() {
        return developer.get();
    }

    public StringProperty developerProperty() {
        return developer;
    }

    public void setDeveloper(String developer) {
        this.developer.set(developer);
    }

    public String getReleaseDate() {
        return releaseDate.get();
    }

    public StringProperty releaseDateProperty() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate.set(releaseDate);
    }

    public String getPurchaseLink() {
        return purchaseLink.get();
    }

    public StringProperty purchaseLinkProperty() {
        return purchaseLink;
    }

    public void setPurchaseLink(String purchaseLink) {
        this.purchaseLink.set(purchaseLink);
    }
}
