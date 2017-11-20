package dee.wallet;

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

    public RecordDetail(int id, String name, int cost, String date, String category, int type,int layout) {
        this.id = id;
        this.name = name;
        this.cost = cost;
        this.date = date;
        this.category = category;
        this.type = type;
        this.layout = layout;
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
}
