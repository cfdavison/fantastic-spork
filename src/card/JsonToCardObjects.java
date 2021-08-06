package card;

import java.io.*;
import java.nio.file.Files;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonToCardObjects {
    // There is a bunch of other info in the json objects as well, this is the potentially important info

    // {"name":"Fury Sliver","mana_cost":"{5}{R}","cmc":6.0,"type_line":"Creature — Sliver","oracle_text":"All Sliver creatures have double strike.","power":"3","toughness":"3","colors":["R"],"color_identity":["R"],"legalities":{"standard":"not_legal","future":"not_legal","historic":"not_legal","gladiator":"not_legal","pioneer":"not_legal", "modern":"legal","legacy":"legal","pauper":"not_legal","vintage":"legal","penny":"legal","commander":"legal","brawl":"not_legal","duel":"legal", "oldschool":"not_legal","premodern":"not_legal"},"set":"tsp","set_name":"Time Spiral","collector_number":"157","digital":false,"rarity":"uncommon"}
    private static Card.Color stringToColor(String colorString) {
        switch (colorString){
            case "\"W\"": return Card.Color.WHITE;
            case "\"U\"": return Card.Color.BLUE;
            case "\"B\"": return Card.Color.BLACK;
            case "\"R\"": return Card.Color.RED;
            case "\"G\"": return Card.Color.GREEN;
        }
        return Card.Color.COLORLESS;
    }

    private static Card jsonToCard(String json) {
        Pattern pattern;
        Matcher matcher;

        /*
         *  Get Name
         */
        String name;
        pattern = Pattern.compile("\"name\":\"(.*?)\"");
        matcher = pattern.matcher(json);

        if(matcher.find()) {
            name = matcher.group(1);
        } else {
            name = "";
        }

        System.out.println("Creating card object for: " + name);

        /*
         *  Get color array
         */
        String[] colorString;
        Card.Color[] color;

        pattern = Pattern.compile("\"colors\":.(.*?)]");
        matcher = pattern.matcher(json);
        if(matcher.find()) {
            String tmp = matcher.group(1);
            colorString = tmp.split(",");

            color = new Card.Color[colorString.length];
            for(int i = 0; i<color.length; i++) {
                color[i] = stringToColor(colorString[i]);
            }

        } else {
            color = new Card.Color[]{Card.Color.COLORLESS};
        }

        /*
         *  Get cost string
         */
        String cost;

        pattern = Pattern.compile("\"mana_cost\":\"(.*?)\"");
        matcher = pattern.matcher(json);

        if(matcher.find()) {
            cost = matcher.group(1);
        } else {
            cost = "";
        }

        /*
         *  Get otext
         */

        String otext;

        pattern = Pattern.compile("\"oracle_text\":\"(.*?)\"");
        matcher = pattern.matcher(json);

        if(matcher.find()) {
            otext = matcher.group(1);
        } else {
            otext = "";
        }

        /*
         *  Get types array
         *  Buckle up, this one gets long
         *      the JSON does not differentiate supertype, type, and subtype
         *      The only way to know if something is a supertype or type is to check as there are multiple permutations
         *      of Supertype + Type (ie. 2 supers, 1 regular Legendary Snow Creature)
         */
        String typeline;
        Card.SuperType[] superTypes = {};
        Card.Type[] types = {};
        String[] subTypes = {};

        pattern = Pattern.compile("\"type_line\":\"(.*?)\"");
        matcher = pattern.matcher(json);

        if(matcher.find()) {
            typeline = matcher.group(1);
        } else {
            typeline = "";
        }

        String[] typeLineArray = typeline.split(" ");
        for(int i=0; i<typeLineArray.length; i++) {
            //Large Try-Catch Block to test typing
            try {
                //Test the elements as supertypes
                Card.SuperType tmp = Card.SuperType.valueOf(typeLineArray[i].toUpperCase());
                //Extend the supertype array by 1 and add the new type to it
                Card.SuperType[] superTypesCopy = superTypes;
                superTypes = new Card.SuperType[superTypes.length + 1];
                for(int j=0; j<superTypesCopy.length; j++) {
                    superTypes[j] = superTypesCopy[j];
                }
                superTypes[superTypes.length-1] = tmp;

            } catch(Exception e) {
                try {
                    //test the elements as a type
                    Card.Type tmp = Card.Type.valueOf(typeLineArray[i].toUpperCase());
                    //Extend the type array by 1 and add the new type to it
                    Card.Type[] typesCopy = types;
                    types = new Card.Type[types.length + 1];
                    int j=0;
                    while(!typeLineArray[j].equals("—") && j < typesCopy.length){
                        types[j] = typesCopy[j];
                        j++;
                    }

                    types[types.length-1] = tmp;

                } catch (Exception exception) {

                }
            }
        }
        //All other types after the "-" are subtypes
        Boolean hasSub = false;
        for(String x : typeLineArray) {
            if(x.equals("—")) {
                hasSub = true;
                break;
            }
        }

        if(hasSub) {
            int j = 0;
            while (!typeLineArray[j].equals("—")) {
                j++;
            }
            subTypes = new String[typeLineArray.length - j - 1];
            for (int k = 0; k < subTypes.length; k++) {
                subTypes[k] = typeLineArray[k + j + 1];
            }
        } else {
            subTypes = new String[]{};
        }

        /*
         * Get Power/Toughness
         */
        Boolean isCreature = false;
        String power="";

        pattern = Pattern.compile("\"power\":\"(.*?)\"");
        matcher = pattern.matcher(json);

        if(matcher.find()) {
            power = matcher.group(1);
            isCreature = true;
        }

        String toughness="";
        pattern = Pattern.compile("\"toughness\":\"(.*?)\"");
        matcher = pattern.matcher(json);

        if(matcher.find()) {
            toughness = matcher.group(1);
        }

        /*
        System.out.println("Name: " + name);
        System.out.println("Colors: " + Arrays.toString(color));
        System.out.println("Cost: " + cost);
        System.out.println("Oracle Text: " + otext);
        System.out.println("Supers: " + Arrays.toString(superTypes));
        System.out.println("Types: " + Arrays.toString(types));
        System.out.println("Subs: " + Arrays.toString(subTypes));
        */
        if(isCreature) {
            return(new Card(color, cost, otext, types, superTypes, subTypes, name, power, toughness));
        } else {
            return (new Card(color, cost, otext, types, superTypes, subTypes, name));
        }
    }

    public static void main(String[] args) {
        String path = new File("").getAbsolutePath();
        String cardDir = path.concat("/res/cards2");
        try {
            /*
             *  "/default-cards-20210727210340.json"
             *  "/training.json"
             */
            File jsonFile = new File(path + "/res/training.json");
            Scanner reader = new Scanner(jsonFile);


            //Loop over each line of the file
            while(reader.hasNextLine()) {
                String line = reader.nextLine();
                if(line.substring(0).equals("[") || line.substring(0).equals("]")) {
                    continue;
                }

                String dirname;

                Card card = jsonToCard(line);
                // Modal dual cards are denoted with a double slash, but this cannot be in a file name so it is weeded out
                Pattern pattern = Pattern.compile("(.*?) // (.*)");

                Matcher matcher = pattern.matcher(card.name);

                if(matcher.find()) {

                    if(matcher.group(1).equals(matcher.group(2))) {
                        continue;
                    }
                }

                dirname = card.name.substring(0,1);
                Files.createDirectories(new File(cardDir + "/" + dirname).toPath());

                String cardNameCleaned = card.name.replace(" // ", "_");
                String filename = cardDir + "/" + dirname + "/" + cardNameCleaned + ".txt";

                try {
                    File newCardFile = new File(filename);
                    if (newCardFile.createNewFile()) {
                        System.out.println("File Created: " + newCardFile.getName());
                    }
                } catch (IOException e) {
                    System.out.println("An error occurred when creating file for " + card.name);
                }
                try {
                    FileWriter writer = new FileWriter(filename, false);
                    writer.write(card.saveString());
                    writer.close();
                    System.out.println("File written: " + card.name);

                } catch (IOException e) {
                    System.out.println("An error occurred when writing file for " + card.name);

                }
            }
            reader.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
