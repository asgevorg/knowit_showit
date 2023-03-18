package com.example.know_it_show_it;

import com.google.gson.annotations.SerializedName;

public class UrbanDictionaryDefinition {
    @SerializedName("word")
    private String word;

    @SerializedName("definition")
    private String definition;

    @SerializedName("author")
    private String author;

    @SerializedName("written_on")
    private String writtenOn;

    @SerializedName("thumbs_up")
    private int thumbsUp;

    @SerializedName("thumbs_down")
    private int thumbsDown;

    public String getWord() {
        return word;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getWrittenOn() {
        return writtenOn;
    }

    public void setWrittenOn(String writtenOn) {
        this.writtenOn = writtenOn;
    }

    public int getThumbsUp() {
        return thumbsUp;
    }

    public void setThumbsUp(int thumbsUp) {
        this.thumbsUp = thumbsUp;
    }

    public int getThumbsDown() {
        return thumbsDown;
    }

    public void setThumbsDown(int thumbsDown) {
        this.thumbsDown = thumbsDown;
    }
}
