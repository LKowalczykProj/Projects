//package utils;

public class Drug {
    private String drugName;
    private String composition;
    private int quantity;
    private int drugGroup;
    private float price;

    public Drug(String drugName, String composition, int quantity, float price, int drugGroup) {
        this.drugName = drugName;
        this.composition = composition;
        this.quantity = quantity;
        this.price = price;
        this.drugGroup = drugGroup;
    }

    public String getDrugName() {
        return drugName;
    }

    public void setDrugName(String drugName) {
        this.drugName = drugName;
    }

    public String getComposition() {
        return composition;
    }

    public void setComposition(String composition) {
        this.composition = composition;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public int getDrugGroup() { return drugGroup; }

    public void setDrugGroup(int drugGroup) { this.drugGroup = drugGroup; }

}
