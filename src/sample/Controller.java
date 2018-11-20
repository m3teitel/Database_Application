package sample;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.sql.*;
import java.util.ArrayList;

public class Controller {
    private Connection conn1 = null;
    @FXML
    private Label tablesList;
    @FXML
    private TextField customSQL;
    @FXML
    private TextArea customSQLCreate;
    @FXML
    private TextArea customPopulate;
    @FXML
    private TextField tableName;

    @FXML
    public void initialize() {
        try {
            Class.forName("oracle.jdbc.OracleDriver");
            String dbURL1 = "jdbc:oracle:thin:m3teitel/06137561@oracle.scs.ryerson.ca:1521:orcl";
            conn1 = DriverManager.getConnection(dbURL1);
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        try {
            StringBuilder stringBuilder = new StringBuilder();
            String getTablesQuery = "SELECT table_name FROM user_tables";
            Statement tablesStatement = conn1.createStatement();
            ResultSet resultSet = tablesStatement.executeQuery(getTablesQuery);
            while (resultSet.next()) {
                stringBuilder.append(resultSet.getString(1));
                stringBuilder.append("\n");
            }
            tablesList.setText(stringBuilder.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void shutdown() {
        try {
            conn1.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createDefaultButton() {
        String sqlCreate = "create table TEST\n" +
                "(\n" +
                "\tNAME VARCHAR2(40) not null\n" +
                "\t\tprimary key,\n" +
                "\tAGE NUMBER not null,\n" +
                "\tHEIGHT NUMBER not null,\n" +
                "\tWEIGHT NUMBER not null\n" +
                ")";
        executeSQLQuery(sqlCreate);
    }

    public void createCustomTables(){
        executeSQLQuery(customSQLCreate.getText());
    }

    public void populateDefault(){

    }

    public void populateCustomData(){
        executeSQLQuery(customPopulate.getText());
    }

    public void query1() {
        executeSQLQuery("SELECT * FROM TEST");
    }

    public void query2() {
        executeSQLQuery("(SELECT * FROM MOVIES) MINUS (SELECT m.* FROM MOVIES m, DISTRIBUTERS d WHERE (m.MOVIE_ID = d.MOVIES_OWNED AND d.COMPANY_NAME = 'Paramount') OR m.PRICE > 20)");
    }

    public void query3() {
        executeSQLQuery("SELECT * FROM MOVIES WHERE MPAA_RATING LIKE '%G%'");
    }

    public void query4() {
        executeSQLQuery("SELECT * FROM USERS WHERE DATE_OF_BIRTH >= TO_DATE('01/JAN/1990','dd/mon/yyyy')  AND DATE_OF_BIRTH <= TO_DATE('21/DEC/1999','dd/mon/yyyy') ");
    }

    public void query5() {
        executeSQLQuery("SELECT 'Average Movie Price: ', AVG(PRICE) FROM MOVIES");
    }

    public void query6() {
        executeSQLQuery("SELECT COUNT(USER_ID) AS Number_Of_Users_Over_28 FROM USERS WHERE DATE_OF_BIRTH <= TO_DATE('01/JAN/1990','dd/mon/yyyy')");
    }

    public void customQuery() {
        executeSQLQuery(customSQL.getText());
    }

    public void dropTable() {
        executeSQLQuery("drop table " + tableName.getText());
    }

    public void dropAllTables() {
        ArrayList<String> tables = new ArrayList<>();
        try {
            String getTablesQuery = "SELECT table_name FROM user_tables";
            Statement tablesStatement = conn1.createStatement();
            ResultSet resultSet = tablesStatement.executeQuery(getTablesQuery);
            while (resultSet.next()) {
                tables.add(resultSet.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        for (String table : tables){
            executeSQLQuery("drop table " + table);
        }
    }

    public void executeSQLQuery(String SQL) {
        ObservableList<ObservableList> data;
        data = FXCollections.observableArrayList();
        TableView tableview = new TableView();
        try {

            ResultSet rs = conn1.createStatement().executeQuery(SQL);
            for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
                final int j = i;
                TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i + 1));
                col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                    public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                        return new SimpleStringProperty(param.getValue().get(j).toString());
                    }
                });
                tableview.getColumns().addAll(col);
            }
            while (rs.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    row.add(rs.getString(i));
                }
                data.add(row);

            }

            //FINALLY ADDED TO TableView
            tableview.setItems(data);
            Stage stage = new Stage();
            stage.setTitle("Query: " + SQL);
            stage.setScene(new Scene(tableview));
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error on Building Data");
        }
    }
}
