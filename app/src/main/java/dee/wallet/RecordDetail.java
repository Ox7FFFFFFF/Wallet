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

    public RecordDetail(int id, String name, int cost, String date, String category, int type) {
        this.id = id;
        this.name = name;
        this.cost = cost;
        this.date = date;
        this.category = category;
        this.type = type;
    }

    public RecordDetail(int id, String name) {
        this.id = id;
        this.name = name;
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
}
