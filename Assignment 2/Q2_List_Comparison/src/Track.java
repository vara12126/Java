package q2_list;

import java.util.Objects;

/**
 * Domain model representing a high-fidelity digital audio track in a music playlist.
 * 
 * Student: Devi Vara Prasad (Reg: 192365032)
 */
public class Track {
    private final String trackId;
    private final String title;
    private final String artist;
    private final int durationSeconds;
    private final String album;
    private final String genre;

    public Track(String trackId, String title, String artist, int durationSeconds, String album, String genre) {
        this.trackId = trackId;
        this.title = title;
        this.artist = artist;
        this.durationSeconds = durationSeconds;
        this.album = album;
        this.genre = genre;
    }

    public String getTrackId() { return trackId; }
    public String getTitle() { return title; }
    public String getArtist() { return artist; }
    public int getDurationSeconds() { return durationSeconds; }
    public String getAlbum() { return album; }
    public String getGenre() { return genre; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Track)) return false;
        Track track = (Track) o;
        return Objects.equals(trackId, track.trackId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(trackId);
    }

    @Override
    public String toString() {
        int minutes = durationSeconds / 60;
        int seconds = durationSeconds % 60;
        return String.format("%s - \"%s\" by %s [%d:%02d]", trackId, title, artist, minutes, seconds);
    }
}
