package com.muqingbfq.list;

import java.util.List;

public class CloudSearch {
    public Result result;
    public int code;

    // Getters and setters
    public Result getResult() {
        return result;
    }

    public class Result {
        public Object searchQcReminder; // Can be null
        public List<Song> songs;
        public int songCount;

        // Getters and setters
        public Object getSearchQcReminder() {
            return searchQcReminder;
        }

        public void setSearchQcReminder(Object searchQcReminder) {
            this.searchQcReminder = searchQcReminder;
        }

        public List<Song> getSongs() {
            return songs;
        }

        public void setSongs(List<Song> songs) {
            this.songs = songs;
        }

        public int getSongCount() {
            return songCount;
        }

        public void setSongCount(int songCount) {
            this.songCount = songCount;
        }
    }

    public class Song {
        public String name;
        public String id;
        public List<Artist> ar;
        public List<String> alia;
        public int pop;
        public Album al;
        public int dt; // duration in ms
        public Quality h; // high quality
        public Quality m; // medium quality
        public Quality l; // low quality
        public Quality sq; // standard quality
        public Quality hr; // high resolution
        public String cd;
        public int no;
        public long publishTime;

        // Getters and setters
        // (Omitted for brevity but should be implemented)
    }

    public class Artist {
        public long id;
        public String name;
        public List<String> tns;
        public List<String> alias;
        // Getters and setters
    }

    public class Album {
        public long id;
        public String name;
        public String picUrl;
        public List<String> tns;
        public String pic_str;
        public long pic;

        // Getters and setters
    }

    public class Quality {
        public int br; // bitrate
        public int fid;
        public int size;
        public int vd;
        public int sr; // sample rate

        // Getters and setters
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

}
