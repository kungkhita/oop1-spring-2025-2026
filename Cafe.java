package cafemanagementsystem.entity;

public class Cafe {
    private String itemId;
    private String itemName;
    private String price;
    private String category;

    public Cafe(String itemId, String itemName, String price, String category) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.price = price;
        this.category = category;
    }

    public String getItemId() {
        return itemId;
    }
    public String getItemName() {
        return itemName;
    }
    public String getPrice() {
        return price;
    }
    public String getCategory() {
        return category;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
    public void setPrice(String price) {
        this.price = price;
    }
    public void setCategory(String category) {
        this.category = category;
    }

    public String toLine() {
        return itemId + "," + itemName + "," + price + "," + category;
    }

    public static Cafe fromLine(String line) {
        if (line == null)
            return null;
        String[] data = line.split(",", -1);
        if (data.length != 4)
            return null;
        return new Cafe(data[0], data[1], data[2], data[3]);
    }

    public Object[] toRow() {
        return new Object[] { itemId, itemName, price, category };
    }
}
