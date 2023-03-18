package com.example.know_it_show_it;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UrbanDictionaryResponse {
    @SerializedName("list")
    private List<UrbanDictionaryDefinition> definitions;

    public List<UrbanDictionaryDefinition> getDefinitions() {
        return definitions;
    }

    public void setDefinitions(List<UrbanDictionaryDefinition> definitions) {
        this.definitions = definitions;
    }
}
