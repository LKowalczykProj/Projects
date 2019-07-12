package entities;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "drugs", schema = "ak_db5", catalog = "")
public class DrugsEntity {
    private int id;
    private String drugName;
    private double price;
    private int drugGroupId;

    @Id
    @Column(name = "id", nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "drug_name", nullable = false)
    public String getDrugName() {
        return drugName;
    }

    public void setDrugName(String drugName) {
        this.drugName = drugName;
    }

    @Basic
    @Column(name = "price", nullable = false, precision = 0)
    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Basic
    @Column(name = "drug_group_id", nullable = false)
    public int getDrugGroupId() {
        return drugGroupId;
    }

    public void setDrugGroupId(int drugGroupId) {
        this.drugGroupId = drugGroupId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DrugsEntity that = (DrugsEntity) o;
        return id == that.id &&
                drugName == that.drugName &&
                Double.compare(that.price, price) == 0 &&
                drugGroupId == that.drugGroupId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, drugName, price, drugGroupId);
    }
}
