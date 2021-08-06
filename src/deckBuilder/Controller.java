package deckBuilder;
import card.*;
import card.CardDB;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.Console;
import java.util.ArrayList;
import java.util.Locale;

public class Controller {

    private CardDB database;

    // GUI controls defined in FXML and used by the controller's code
    @FXML
    private TextField searchBar;

    @FXML
    private TextField deckNameBar;

    @FXML
    private TableView<Card> cardList;

    @FXML
    private TableColumn cardListName;

    @FXML
    private TableColumn cardListColor;

    @FXML
    private TableColumn cardListManaCost;

    @FXML
    private TableColumn cardListType;

    @FXML
    private TableView deckList;

    @FXML
    private Button searchButton;

    @FXML
    private Button saveButton;

    @FXML
    private Button openButton;

    @FXML
    private Button importButton;

    private void saveButtonPressed() {
        //TODO
    }

    private void openButtonPressed() {
        //TODO
    }

    private void importButtonPressed() {
        //TODO
    }
    @FXML
    private void searchButtonPressed() {
        System.out.println("Search button pressed");
        if(database==null){
            database = new CardDB();
        }

        ArrayList<Card> dbcopy = new ArrayList<>(database.getDb());
        String query = searchBar.getText().toLowerCase();

        if(!query.equals("")) {
            for (int i = 0; i < dbcopy.size(); i++) {
                if (dbcopy.get(i).getName().toLowerCase().contains(query) || dbcopy.get(i).getTypeString().toLowerCase().contains(query)) {
                    continue;
                } else {
                    dbcopy.remove(i);
                    i--;
                }
            }
        }
        cardList.setItems(FXCollections.observableArrayList(dbcopy));
    }



    // called by FXMLLoader to initialize the controller
    public void initialize() {
        System.out.println("Initializing Controller");
        database = new CardDB();

        final ObservableList<Card> data = FXCollections.observableArrayList(database.getDb());


        cardListName.setCellValueFactory(new PropertyValueFactory<Card,String>("name"));
        cardListColor.setCellValueFactory(new PropertyValueFactory<Card,String>("colorString"));
        cardListManaCost.setCellValueFactory(new PropertyValueFactory<Card,String>("manaCost"));
        cardListType.setCellValueFactory(new PropertyValueFactory<Card,String>("typeString"));

        cardList.setItems(data);
    }


}
