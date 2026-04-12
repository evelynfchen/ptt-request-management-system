package entity;

public class TrainingRecord {
    private String topic;
    private String date;

    public TrainingRecord(String topic, String date) {
        this.topic = topic;
        this.date = date;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}