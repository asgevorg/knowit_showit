package com.example.know_it_show_it;

import java.util.ArrayList;
import java.util.List;
import java.util.Observer;

public class LocalDB {
    private DBObserver observer;
    private List<User> data = new ArrayList<>();

    public void addData(User newData) {
        // New data
        data.add(newData);

        // Obsever notifier
        observer.onDatabaseChange(data);
    }

    public void setObserver(DBObserver observer) {
        this.observer = observer;
    }
}
