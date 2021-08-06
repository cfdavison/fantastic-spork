package card;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

public class CardDB {
    ArrayList<Card> db;

    public void populateDB(){
        ArrayList<String> filenames;
        String path = new File("").getAbsolutePath();
        //path += "/res/cards";
// TODO
        path += "/res/cards2";

        // Create res and cards directories if they do not exist
        try {
            Files.createDirectories(new File(path).toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Populate the directories with cards if they do not exist
        String[] dirs = new File(path).list();
        if (dirs.length == 0){
            JsonToCardObjects.main(new String[]{});
            dirs = new File(path).list();
        }
        for(String dir : dirs) {
            String[] cardFiles = new File(path + "/" + dir).list();
            for(String cardFile : cardFiles) {
                try {
                    Card card = Card.loadSavedCard(path + "/" + dir + "/" + cardFile);
                    System.out.println("adding " + card.name);
                    this.db.add(card);
                } catch(Exception exception) {
                    System.out.println("Failed to load card: " + cardFile);
                }
            }
        }
    }

    public CardDB() {
        this.db = new ArrayList<>();
        populateDB();
    }

    public ArrayList<Card> getDb() {
        return db;
    }
}
