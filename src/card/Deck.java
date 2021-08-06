package card;

import rules.Format;

import java.util.*;

public class Deck {
    ArrayList<Card> mainList;
    ArrayList<Card> sideboard;
    public String name;
    public int mainCardCount;
    public int sideCardCount;
    public Format format;

    public Deck(String name, Format format) {
        this.name = name;
        this.format = format;

        this.mainList = new ArrayList<Card>();
        this.sideboard = new ArrayList<Card>();
        this.mainCardCount = 0;
        this.sideCardCount = 0;
    }

    @Override
    public String toString() {
        String title = String.format("Name: %s | Format: %s | Card Count: %d\n", this.name, this.format.name, this.mainCardCount);
        String cards = "";
        String sideCards = "";

        for(int i=0; i<this.mainList.size(); i++) {
            Card tmp = this.mainList.get(i);
            cards += String.format("%d %s\n", tmp.count, tmp.name);
        }

        for(int i=0; i<sideboard.size(); i++) {
            Card tmp = this.sideboard.get(i);
            cards += String.format("%d %s\n", tmp.count, tmp.name);
        }

        return(title + cards + "\n" + sideCards);
    }

    public void addToMain(){

    }

    public void addToSide(){

    }

    

}
