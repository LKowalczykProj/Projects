//package database;

//import utils.Drug;

import java.sql.*;
import java.util.LinkedList;

public class DatabaseConnection {
    private static DatabaseConnection instance = null;
    private String username = "ak_user5";
    private String password = "tyuiop";
    private String url = "jdbc:mysql://212.182.24.236:3306/ak_db5";
    private Connection connection;

    public static DatabaseConnection getInstance() {
        if (instance == null)
            instance = new DatabaseConnection();
        return instance;
    }

    private DatabaseConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(url, username, password);
            String sqlCreateDrugGroups =
                    "CREATE TABLE IF NOT EXISTS drug_groups"
                            + " (id             INTEGER(11)     PRIMARY KEY NOT NULL AUTO_INCREMENT,"
                            + "  group_name     VARCHAR(50)     NOT NULL);";
            String sqlCreateDrugs =
                    "CREATE TABLE IF NOT EXISTS drugs"
                            + "  (id            INTEGER(11)     PRIMARY KEY NOT NULL AUTO_INCREMENT,"
                            + "   drug_name     VARCHAR (100)   NOT NULL,"
                            + "   price         FLOAT           NOT NULL,"
                            + "   drug_group_id INTEGER(11)     NOT NULL,"
                            + "   composirion   VARCHAR(250)    NOT NULL,"
                            + "   quantity      INTEGER(11)     NOT NULL,"
                            + "   FOREIGN KEY(drug_group_id)    REFERENCES drug_groups(id));";

            Statement stmt = connection.createStatement();
            stmt.executeUpdate(sqlCreateDrugGroups);
            stmt.executeUpdate(sqlCreateDrugs);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void addDrug(String drugName, String composition, int quantity, float price, int drugGroup) throws SQLException {

        if (drugExists(drugName)) {
            String query = "UPDATE drugs SET quantity = quantity+" + quantity + " WHERE drug_name like '" + drugName + "';";
            connection.createStatement().executeUpdate(query);
        } else {
            PreparedStatement preparedStatement =
                    connection.prepareStatement("INSERT INTO drugs (drug_name, price, drug_group_id, composition, quantity)"
                            + " VALUES (?, ?, ?, ?, ?)");
            preparedStatement.setString(1, drugName);
            preparedStatement.setFloat(2, price);
            preparedStatement.setInt(3, drugGroup);
            preparedStatement.setString(4, composition);
            preparedStatement.setInt(5, quantity);
            preparedStatement.executeUpdate();
        }
    }

    private boolean drugExists(String drugName) throws SQLException {
        String query = "SELECT * FROM drugs WHERE drug_name like '" + drugName + "';";
        ResultSet resultSet = connection.createStatement().executeQuery(query);
        return resultSet.next();
    }

    public LinkedList<Drug> getAlldrugs() {
        String sql = "SELECT * FROM drugs ORDER BY drug_name;";
        return executeQueryAndGetDrugs(sql);
    }

    public LinkedList<Drug> getDrug(String drugName) {
        String sql = "SELECT * FROM drugs WHERE drug_name like '%" + drugName + "%' ORDER BY drug_name COLLATE utf8mb4_general_ci;";
        return executeQueryAndGetDrugs(sql);
    }

    public LinkedList<Drug> getSameGroupDrugs(String drugName){
        String sql = "SELECT * FROM drugs WHERE drug_group_id = (SELECT drug_group_id FROM drugs WHERE drug_name = '"
                +drugName +"') AND drug_name != '" + drugName + "'ORDER BY price DESC;";
        return executeQueryAndGetDrugs(sql);
    }

    private LinkedList<Drug> executeQueryAndGetDrugs(String query) {
        LinkedList<Drug> allDrugs = new LinkedList<Drug>();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                allDrugs.add(new Drug(resultSet.getString("drug_name"),
                        resultSet.getString("composition"), resultSet.getInt("quantity"),
                        resultSet.getFloat("price"), resultSet.getInt("drug_group_id")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return allDrugs;
    }

    public void updateQuantity(LinkedList<Drug> boughtDrugs) throws SQLException {
        for (Drug drug : boughtDrugs) {
            executeQuantityUpdate(drug.getDrugName(), drug.getQuantity());
        }
    }

    private void executeQuantityUpdate(String drugName, int boughtQuantity) throws SQLException {
        String sql = "UPDATE drugs SET quantity=quantity-" + boughtQuantity + " WHERE drug_name='" + drugName + "';";
        connection.createStatement().executeUpdate(sql);
        System.out.println(sql);
    }

}

