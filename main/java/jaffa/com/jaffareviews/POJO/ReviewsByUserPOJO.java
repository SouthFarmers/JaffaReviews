package jaffa.com.jaffareviews.POJO;

/**
 * Created by gautham on 11/22/17.
 */

public class ReviewsByUserPOJO {

    String MovieName;
    String MovieTag;
    String MovieRating;
    String MovieGif;
    String Review;

    public String getMovieTag() {
        return MovieTag;
    }

    public void setMovieTag(String Movietag) {
        MovieTag = Movietag;
    }

    public String getMovieName() {
        return MovieName;
    }

    public void setMovieName(String Moviename) {
        MovieName = Moviename;
    }

    public String getMovieRating() {
        return MovieRating;
    }

    public void setMovieRating(String movierating) {
        MovieRating = movierating;
    }

    public String getMovieGif() {
        return MovieGif;
    }

    public void setRMovieGif(String Moviegif) {
        MovieGif = Moviegif;
    }

    public String getReview() {
        return Review;
    }

    public void setReview(String review) {
        Review = review;
    }
}
