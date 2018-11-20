package cps510;

import com.sun.deploy.model.Resource;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.ArrayList;

public class Controller {
    private Connection conn1 = null;
    @FXML
    private ChoiceBox tablesList;
    @FXML
    private TextField customSQL;
    @FXML
    private TextArea customSQLCreate;
    @FXML
    private TextArea customPopulate;

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
        fillTablesList();
    }

    public void fillTablesList(){
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
        tablesList.getItems().clear();
        for (String table: tables){
            tablesList.getItems().add(table);
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
        String createMovie = "create table MOVIE\n" +
                "(\n" +
                "\tMOVIE_ID VARCHAR2(10) not null\n" +
                "\t\tprimary key,\n" +
                "\tTITLE VARCHAR2(100) not null,\n" +
                "\tGENRE VARCHAR2(50) not null,\n" +
                "\tRELEASE_DATE DATE not null,\n" +
                "\tDIRECTOR VARCHAR2(100) not null,\n" +
                "\tWRITER VARCHAR2(100) not null,\n" +
                "\tMAIN_ACTOR VARCHAR2(100) not null,\n" +
                "\tSUMMARY VARCHAR2(500) not null,\n" +
                "\tPRICE NUMBER not null,\n" +
                "\tQUALITIES_AVAILABLE VARCHAR2(100) not null,\n" +
                "\tMPAA_RATING VARCHAR2(10) not null\n" +
                ")";
        String createReview = "create table REVIEW\n" +
                "(\n" +
                "\tREVIEW_ID VARCHAR2(10) not null\n" +
                "\t\tprimary key,\n" +
                "\tMOVIE_ID VARCHAR2(10) not null\n" +
                "\t\tconstraint REVIEW_MOVIE_MOVIE_ID_FK\n" +
                "\t\t\treferences MOVIE\n" +
                "\t\t\t\ton delete cascade,\n" +
                "\t\"COMMENT\" VARCHAR2(500) not null,\n" +
                "\tRATING NUMBER not null\n" +
                ")";
        String createUser = "create table \"USER\"\n" +
                "(\n" +
                "\tUSER_ID VARCHAR2(10) not null\n" +
                "\t\tprimary key,\n" +
                "\tNAME VARCHAR2(100) not null,\n" +
                "\tEMAIL VARCHAR2(100) not null,\n" +
                "\tDATE_OF_BIRTH DATE not null\n" +
                ")";
        String createUserH = "create table USER_HISTORY\n" +
                "(\n" +
                "\tUSER_ID VARCHAR2(10) not null\n" +
                "\t\tconstraint USER_HISTORY_USER_USER_ID_FK\n" +
                "\t\t\treferences \"USER\"\n" +
                "\t\t\t\ton delete cascade,\n" +
                "\tMOVIE_ID VARCHAR2(10) not null\n" +
                "\t\tconstraint USER_HISTORY_MOVIE_MOVIE_ID_FK\n" +
                "\t\t\treferences MOVIE\n" +
                "\t\t\t\ton delete cascade\n" +
                ")";
        String createUserB = "create table USER_BOUGHT\n" +
                "(\n" +
                "\tUSER_ID VARCHAR2(10) not null\n" +
                "\t\tconstraint USER_BOUGHT_USER_USER_ID_FK\n" +
                "\t\t\treferences \"USER\"\n" +
                "\t\t\t\ton delete cascade,\n" +
                "\tMOVIE_ID VARCHAR2(10) not null\n" +
                "\t\tconstraint USER_BOUGHT_MOVIE_MOVIE_ID_FK\n" +
                "\t\t\treferences MOVIE\n" +
                "\t\t\t\ton delete cascade\n" +
                ")";
        String createPayment = "create table PAYMENT\n" +
                "(\n" +
                "\tPAYMENT_ID VARCHAR2(10) not null\n" +
                "\t\tprimary key,\n" +
                "\tCARD_TYPE VARCHAR2(50) not null,\n" +
                "\tCARD_NUMBER VARCHAR2(20) not null,\n" +
                "\tNAME_ON_CARD VARCHAR2(100) not null\n" +
                ")";
        String createUPayment = "create table USER_PAYMENT\n" +
                "(\n" +
                "\tPAYMENT_ID VARCHAR2(20) not null\n" +
                "\t\tconstraint USER_PAYMENT_PAYMENT_PAY_ID_FK\n" +
                "\t\t\treferences PAYMENT\n" +
                "\t\t\t\ton delete cascade,\n" +
                "\tUSER_ID VARCHAR2(20) not null\n" +
                "\t\tconstraint USER_PAYMENT_USER_USER_ID_FK\n" +
                "\t\t\treferences \"USER\"\n" +
                "\t\t\t\ton delete cascade\n" +
                ")";
        executeSQLQuery(createMovie);
        executeSQLQuery(createReview);
        executeSQLQuery(createUser);
        executeSQLQuery(createUserH);
        executeSQLQuery(createUserB);
        executeSQLQuery(createPayment);
        executeSQLQuery(createUPayment);
        fillTablesList();
    }

    public void createCustomTables(){
        executeSQLQuery(customSQLCreate.getText());
        fillTablesList();
    }

    public void populateDefault(){
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("insert_records.txt")));
            while (br.ready()){
                String insertSQL = br.readLine();
                executeSQLQuery(insertSQL);
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void populateCustomData(){
        executeSQLQuery(customPopulate.getText());
    }

    public void query1() {
        executeSQLQueryGetTable("select MOVIE.TITLE, REVIEW.\"COMMENT\", REVIEW.RATING FROM MOVIE INNER JOIN REVIEW on MOVIE.MOVIE_ID = REVIEW.MOVIE_ID");
    }

    public void query2() {
        executeSQLQueryGetTable("SELECT \"USER\".NAME,  M.TITLE FROM \"USER\" INNER JOIN USER_HISTORY ON \"USER\".USER_ID = USER_HISTORY.USER_ID INNER JOIN MOVIE M ON USER_HISTORY.MOVIE_ID = M.MOVIE_ID order by NAME");
    }

    public void query3() {
        executeSQLQueryGetTable("SELECT * FROM MOVIE WHERE MPAA_RATING LIKE '%G%'");
    }

    public void query4() {
        executeSQLQueryGetTable("SELECT * FROM \"USER\" WHERE DATE_OF_BIRTH >= TO_DATE('01/JAN/1990','dd/mon/yyyy')  AND DATE_OF_BIRTH <= TO_DATE('21/DEC/1999','dd/mon/yyyy') ");
    }

    public void query5() {
        executeSQLQueryGetTable("SELECT 'Average Movie Price: ', AVG(PRICE) FROM MOVIE");
    }

    public void query6() {
        executeSQLQueryGetTable("SELECT COUNT(USER_ID) AS Number_Of_Users_Over_28 FROM \"USER\" WHERE DATE_OF_BIRTH <= TO_DATE('01/JAN/1990','dd/mon/yyyy')");
    }

    public void customQuery() {
        executeSQLQueryGetTable(customSQL.getText());
    }

    public void dropTable() {
        String table = (String) tablesList.getValue();
        executeSQLQuery("drop table \"" + table + "\"");
        fillTablesList();
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
        executeSQLQuery("drop table USER_HISTORY");
        executeSQLQuery("drop table USER_BOUGHT");
        executeSQLQuery("drop table USER_PAYMENT");
        executeSQLQuery("drop table REVIEW");
        executeSQLQuery("drop table PAYMENT");
        executeSQLQuery("drop table MOVIE");
        executeSQLQuery("drop table \"USER\"");

        for (String table : tables){
            executeSQLQuery("drop table \"" + table + "\"");
        }
        fillTablesList();
    }

    public void executeSQLQuery(String SQL) {
        try {

            conn1.createStatement().execute(SQL);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error on Building Data");
        }
    }
    public void executeSQLQueryGetTable(String SQL) {
        BorderPane borderPane = new BorderPane();
        Label label = new Label();
        label.setText(SQL);
        label.setFont(new Font(14));
        label.setAlignment(Pos.CENTER);
        label.setWrapText(true);
        borderPane.setTop(label);
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
            tableview.setItems(data);
            Stage stage = new Stage();
            stage.setTitle("Query: " + SQL);
            borderPane.setCenter(tableview);
            // stage.getIcons().add(new Image(Main.class.getResourceAsStream("icon.png")));
            Scene scene = new Scene(borderPane, 1000, 400);
            stage.setScene(scene);
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error on Building Data");
        }
    }
}
