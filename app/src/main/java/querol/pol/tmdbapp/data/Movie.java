package querol.pol.tmdbapp.data;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Pol Querol on 5/2/18.
 */

public class Movie implements Serializable{
    @SerializedName("id") private int id;
    @SerializedName("title") private String title;
    @SerializedName("release_date") private String year;
    @SerializedName("overview") private String overview;
    @SerializedName("poster_path") private String photo;

    public Movie(int id, String title, String year, String overview, String photo) {
        this.id = id;
        this.title = title;
        this.year = year;
        this.overview = overview;
        this.photo = photo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", year='" + year + '\'' +
                ", overview='" + overview + '\'' +
                ", photo='" + photo + '\'' +
                '}';
    }
}
