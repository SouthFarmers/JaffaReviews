package jaffa.com.jaffareviews.POJO;

public class MovieDetailPOJO {

    String movieTitle;
    String movieID;
    String avgRating;
    String movieImage;
    String friendRatingCount;
    String criticRatingCount;

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public String getMovieID() {
        return movieID;
    }

    public void setMovieID(String movieID) {
        this.movieID = movieID;
    }

    public String getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(String avgRating) {
        this.avgRating = avgRating;
    }

    public String getMovieImage() {
        return movieImage;
    }

    public void setMovieImage(String movieImage) {
        this.movieImage = movieImage;
    }

    public String getFriendRatingCount() {
        return friendRatingCount;
    }

    public void setFriendRatingCount(String friendRatingCount) {
        this.friendRatingCount = friendRatingCount;
    }

    public String getCriticRatingCount() {
        return criticRatingCount;
    }

    public void setCriticRatingCount(String criticRatingCount) {
        this.criticRatingCount = criticRatingCount;
    }
}
