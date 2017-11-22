package dee.wallet;

import java.util.ArrayList;

/**
 * Created by dee on 11/19/17
 */

public class RecordDetail {
    private int id;
    private String name;
    private int cost;
    private String date;
    private String category;
    private int type;
    private int layout;
    private String title;
    private String value;
    private int hour;
    private int minute;
    private int turn;
    private ArrayList<Integer> duration = new ArrayList<>();

    private ArrayList<RecordDetail> recordDetails = new ArrayList<>();

    public RecordDetail(int id, String name, int cost, String date, String category, int type,int layout) {
        this.id = id;
        this.name = name;
        this.cost = cost;
        this.date = date;
        this.category = category;
        this.type = type;
        this.layout = layout;
    }

    public RecordDetail(ArrayList<RecordDetail> recordDetails,int layout) {
        this.layout = layout;
        this.recordDetails.clear();
        this.recordDetails.addAll(recordDetails);
    }

    public RecordDetail(int cost, int type, int layout) {
        this.cost = cost;
        this.type = type;
        this.layout = layout;
    }

    public RecordDetail(int id, String name, int layout) {
        this.id = id;
        this.name = name;
        this.layout = layout;
    }

    public RecordDetail(int id, String name, int cost ,int type ,int layout) {
        this.id = id;
        this.name = name;
        this.cost = cost;
        this.type = type;
        this.layout = layout;
    }

    public RecordDetail(String title, String value ,int layout) {
        this.title = title;
        this.value = value;
        this.layout = layout;
    }

    public RecordDetail(int id, int hour, int minute, ArrayList<Integer> duration, int turn, int layout) {
        this.id = id;
        this.layout = layout;
        this.hour = hour;
        this.minute = minute;
        this.turn = turn;
        this.duration.clear();
        this.duration.addAll(duration);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getCost() {
        return cost;
    }

    public String getDate() {
        return date;
    }

    public String getCategory() {
        return category;
    }

    public int getType() {
        return type;
    }

    public int getLayout() {
        return layout;
    }

    public ArrayList<RecordDetail> getRecordDetails() {
        return recordDetails;
    }

    public String getTitle() {
        return title;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public int getTurn() {
        return turn;
    }

    public ArrayList<Integer> getDuration() {
        return duration;
    }
}
