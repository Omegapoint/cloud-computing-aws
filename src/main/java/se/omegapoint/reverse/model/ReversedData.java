package se.omegapoint.reverse.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ReversedData {

    public String applicationName;
    public String timeStamp;
    public String data;

    public ReversedData() {
        this.applicationName = "Reversed-Richard";
    }

    public ReversedData(String s) {
        this();
        this.data = s;
        this.timeStamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
