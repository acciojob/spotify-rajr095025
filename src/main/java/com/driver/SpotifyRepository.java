package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class SpotifyRepository {
    public HashMap<Artist, List<Album>> artistAlbumMap;
    public HashMap<Album, List<Song>> albumSongMap;
    public HashMap<Playlist, List<Song>> playlistSongMap;
    public HashMap<Playlist, List<User>> playlistListenerMap;
    public HashMap<User, Playlist> creatorPlaylistMap;
    public HashMap<User, List<Playlist>> userPlaylistMap;
    public HashMap<Song, List<User>> songLikeMap;

    public List<User> users;
    public List<Song> songs;
    public List<Playlist> playlists;
    public List<Album> albums;
    public List<Artist> artists;

    public SpotifyRepository(){
        //To avoid hitting apis multiple times, initialize all the hashmaps here with some dummy data
        artistAlbumMap = new HashMap<>();
        albumSongMap = new HashMap<>();
        playlistSongMap = new HashMap<>();
        playlistListenerMap = new HashMap<>();
        creatorPlaylistMap = new HashMap<>();
        userPlaylistMap = new HashMap<>();
        songLikeMap = new HashMap<>();

        users = new ArrayList<>();
        songs = new ArrayList<>();
        playlists = new ArrayList<>();
        albums = new ArrayList<>();
        artists = new ArrayList<>();
    }

    public User createUser(String name, String mobile) {
        User user = new User(name,mobile);
        users.add(user);
        return user;
    }

    public Artist createArtist(String name) {
        Artist artist = new Artist(name);
        artists.add(artist);
        return artist;
    }

    public Album createAlbum(String title, String artistName) {
        Album album = new Album(title);
        Artist artist = null;
        for(Artist artistTemp : artists){
            if(Objects.equals(artistTemp.getName(), artistName)){
                artist = artistTemp;
            }
        }
        if(artist == null){
            artist = new Artist(artistName);
            artists.add(artist);
        }
        albums.add(album);
        List<Album> list = artistAlbumMap.get(artist);
        if(list == null){
            list = new ArrayList<>();
        }
        list.add(album);
        artistAlbumMap.put(artist,list);
        return album;
    }

    public Song createSong(String title, String albumName, int length) throws Exception{
        Song song = new Song(title,length);
        Album album = null;
        for(Album albumTemp : albums){
            if(Objects.equals(albumTemp.getTitle(), albumName)){
                album = albumTemp;
            }
        }
        if(album == null){
            throw new Exception("Album does not exist");
        }
        songs.add(song);
        List<Song> list = albumSongMap.get(album);
        if(list == null){
            list = new ArrayList<>();
        }
        list.add(song);
        albumSongMap.put(album,list);
        return song;
    }

    public Playlist createPlaylistOnLength(String mobile, String title, int length) throws Exception {
        User user = null;
        for(User userTemp : users){
            if(userTemp.getMobile() == mobile){
                user = userTemp;
            }
        }
        if(user == null){
            throw new Exception("User does not exist");
        }
        Playlist playlist = new Playlist(title);
        playlists.add(playlist);
        List<Song> songList = new ArrayList<>();
        for(Song song : songs){
            if(song.getLength() == length){
                songList.add(song);
            }
        }
        playlistSongMap.put(playlist,songList);
        List <User> userList = new ArrayList<>();
        userList.add(user);
        playlistListenerMap.put(playlist,userList);
        creatorPlaylistMap.put(user,playlist);
        List <Playlist> playlistList = userPlaylistMap.get(user);
        if(playlistList == null){
            playlistList = new ArrayList<>();
        }
        playlistList.add(playlist);
        userPlaylistMap.put(user,playlistList);
        return playlist;

    }

    public Playlist createPlaylistOnName(String mobile, String title, List<String> songTitles) throws Exception {
        User user = null;
        for(User userTemp : users){
            if(userTemp.getMobile() == mobile){
                user = userTemp;
            }
        }
        if(user == null){
            throw new Exception("User does not exist");
        }
        Playlist playlist = new Playlist(title);
        playlists.add(playlist);
        List<Song> songList = new ArrayList<>();
        for(Song song : songs){
            if(songTitles.contains(song.getTitle())){
                songList.add(song);
            }
        }
        playlistSongMap.put(playlist,songList);
        List <User> userList = new ArrayList<>();
        userList.add(user);
        playlistListenerMap.put(playlist,userList);
        creatorPlaylistMap.put(user,playlist);
        List <Playlist> playlistList = userPlaylistMap.get(user);
        if(playlistList == null){
            playlistList = new ArrayList<>();
        }
        playlistList.add(playlist);
        userPlaylistMap.put(user,playlistList);
        return playlist;
    }

    public Playlist findPlaylist(String mobile, String playlistTitle) throws Exception {
        //Find the playlist with given title and add user as listener of that playlist and update user accordingly
        //If the user is creater or already a listener, do nothing
        //If the user does not exist, throw "User does not exist" exception
        //If the playlist does not exists, throw "Playlist does not exist" exception
        //Return the playlist after updating
        Playlist playlist = null;
        for(Playlist playlistTemp : playlists){
            if(playlistTemp.getTitle() == playlistTitle){
                playlist = playlistTemp;
            }
        }
        if(playlist == null){
            throw new Exception("Playlist does not exist");
        }
        User user = null;
        for(User userTemp : users){
            if(userTemp.getMobile() == mobile){
                user = userTemp;
            }
        }
        if(user == null){
            throw new Exception("User does not exist");
        }
        List<User> userList = playlistListenerMap.get(playlist);
        if(userList.contains(user)){
            return playlist;
        }
        userList.add(user);
        playlistListenerMap.put(playlist,userList);
        List <Playlist> playlistList = userPlaylistMap.get(user);
        if(playlistList == null){
            playlistList = new ArrayList<>();
        }
        playlistList.add(playlist);
        userPlaylistMap.put(user,playlistList);
        creatorPlaylistMap.put(user,playlist);
        return playlist;
        /*
        public HashMap<Playlist, List<Song>> playlistSongMap;
        public HashMap<Playlist, List<User>> playlistListenerMap;
        public HashMap<User, Playlist> creatorPlaylistMap;
        public HashMap<User, List<Playlist>> userPlaylistMap;
         */
    }

    public Song likeSong(String mobile, String songTitle) throws Exception {
        //The user likes the given song. The corresponding artist of the song gets auto-liked
        //A song can be liked by a user only once. If a user tried to like a song multiple times, do nothing
        //However, an artist can indirectly have multiple likes from a user, if the user has liked multiple songs of that artist.
        //If the user does not exist, throw "User does not exist" exception
        //If the song does not exist, throw "Song does not exist" exception
        //Return the song after updating
        User user = null;
        for(User userTemp : users){
            if(userTemp.getMobile() == mobile){
                user = userTemp;
            }
        }
        if(user == null){
            throw new Exception("User does not exist");
        }
        Song song = null;
        for(Song songTemp : songs){
            if(songTemp.getTitle() == songTitle){
                song = songTemp;
            }
        }
        if(song == null){
            throw new Exception("Song does not exist");
        }
        List<User> userList = songLikeMap.get(song);

        if(userList != null && userList.contains(user)){
            return song;
        }
        song.setLikes(song.getLikes() + 1);
        Album album = null;
        for(Album albumTemp : albumSongMap.keySet()){
            if(albumSongMap.get(albumTemp).contains(song)){
                album = albumTemp;
            }
        }
        if(album == null){
            return song;
        }
        Artist artist = null;
        for(Artist artistTemp : artistAlbumMap.keySet()){
            if(artistAlbumMap.get(artistTemp).contains(album)){
                artist = artistTemp;
            }
        }
        artist.setLikes(artist.getLikes() + 1);
        return song;
    }

    public String mostPopularArtist() throws Exception {
        int maxLikes = 0;
        String ans = "";
        try {
            for (Artist artist : artists) {
                if (artist.getLikes() >= maxLikes) {
                    ans = artist.getName();
                    maxLikes = artist.getLikes();
                }
            }
        }
        catch (Exception e){
            throw new Exception();
        }
        return ans;
    }

    public String mostPopularSong() throws Exception {
        int maxLikes = 0;
        String ans = "";
        try {
            for (Song song : songs) {
                if (song.getLikes() >= maxLikes) {
                    ans = song.getTitle();
                    maxLikes = song.getLikes();
                }
            }
        }
        catch (Exception e){
            throw new Exception();
        }
        return ans;
    }
}
