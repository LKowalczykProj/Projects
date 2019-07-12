package entities;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "drug_groups", schema = "ak_db5", catalog = "")
public class DrugGroupsEntity {
    private int id;
    private String groupName;

    @Id
    @Column(name = "id", nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "group_name", nullable = false, length = 250)
    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DrugGroupsEntity that = (DrugGroupsEntity) o;
        return id == that.id &&
                Objects.equals(groupName, that.groupName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, groupName);
    }
}
