/**
 * Card class holding all the info for a magic the gathering card
 *
 * @author Creston Davison
 */

package card;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Card {
    public enum Color {
        WHITE,
        BLUE,
        BLACK,
        RED,
        GREEN,
        COLORLESS;

        @Override
        public String toString() {
            String tmp = name().toLowerCase();
            return(tmp.substring(0,1).toUpperCase() + tmp.substring(1));
        }
    }

    public enum Type {
        ARTIFACT,
        CREATURE,
        ENCHANTMENT,
        INSTANT,
        LAND,
        PLANESWALKER,
        SORCERY,
        TRIBAL;

        @Override
        public String toString() {
            String tmp = name().toLowerCase();
            return(tmp.substring(0,1).toUpperCase() + tmp.substring(1));
        }
    }

    public enum SuperType {
        NONE,
        BASIC,
        LEGENDARY,
        SNOW,
        WORLD;

        @Override
        public String toString() {
            String tmp = name().toLowerCase();
            return(tmp.substring(0,1).toUpperCase() + tmp.substring(1));
        }
    }

    private final Color[] colors; //Colors of the card
    private String colorString;
    private final ArrayList<Color> colorID; //Colors of all mana symbols on the card
    private final int manaValue;
    private final String manaCost; //as written on the card, ie {4}{b}{b} = 4 and two black
    private final String oText;
    private final Type[] types; //ie {ARTIFACT, CREATURE}
    private final SuperType[] superTypes; //ie {SNOW, BASIC}
    private final String[] subTypes; //ie ie {"snake", "elf", "druid"}
    int count = 1; //can be increased for appearances in a deck list
    String power; // Power and toughness cannot be an int due to variable P/T creatures, ie goyf
    String toughness;
    String typeString;

    String name;

    public Card(Color[] colors, String manaCost, String oText, Type[] types, SuperType[] superTypes, String[] subTypes, String name) {
        this.colors = colors;
        this.manaCost = manaCost;
        this.oText = oText;
        this.types = types;
        this.superTypes = superTypes;
        this.subTypes = subTypes;
        this.name = name;

        this.colorID = calculateColorID();
        this.manaValue = calculateManaValue();

        String colorString = "";
        for(Color i : colors) {
            colorString += i + " ";
        }
        this.colorString = colorString;
        this.typeString = generateTypeString(superTypes,types,subTypes);

    }

    public Card(Color[] colors, String manaCost, String oText, Type[] types, SuperType[] superTypes, String[] subTypes, String name, String power, String toughness) {
        this.colors = colors;
        this.manaCost = manaCost;
        this.oText = oText;
        this.types = types;
        this.superTypes = superTypes;
        this.subTypes = subTypes;
        this.name = name;

        this.colorID = calculateColorID();
        this.manaValue = calculateManaValue();

        String colorString = "";
        for(Color i : colors) {
            colorString += i + " ";
        }
        this.colorString = colorString;

        boolean isCreature = false;
        for(Type type : this.types) {
            if(type == Type.CREATURE) {
                isCreature = true;
                break;
            }
        }
        boolean isVehicle = false;
        for(String subtype : this.subTypes) {
            if(subtype.equals("Vehicle")) {
                isVehicle = true;
                break;
            }
        }
        try {
            if (isCreature || isVehicle) {
                this.power = power;
                this.toughness = toughness;
            } else {
                throw (new RuntimeException("Cannot assign power and toughness to a non-creature, non-vehicle card"));
            }
        } catch(Exception exception) {
            System.out.print("Attempted to assign power and toughness to a non creature non vehicle: " + name);
        }

        this.typeString = generateTypeString(superTypes,types,subTypes);
    }

    public Card() {
        this.colors = new Color[]{Color.COLORLESS};
        this.manaCost = "0";
        this.oText = "";
        this.types = new Type[0];
        this.superTypes = new SuperType[0];
        this.subTypes = new String[0];
        this.name = "DEFAULT_EMPTY_CARD";

        this.colorID = calculateColorID();
        this.manaValue = calculateManaValue();
    }

    @Override
    public String toString() {

        String retString = String.format("Name: %s\nCost: %s\nType: %s\nOracle Text: %s\n", name, manaCost, typeString, oText);
        if(power != null) {
            retString += String.format("Power/Toughness: %s/%s\n", power, toughness);
        }

        return(retString);
    }

    public Color[] getColors() {
        return colors;
    }

    public ArrayList<Color> getColorID() {
        return colorID;
    }

    public int getManaValue() {
        return manaValue;
    }

    public String getManaCost() {
        return manaCost;
    }

    public String getoText() {
        return oText;
    }

    public String getColorString() {
        return colorString;
    }

    public Type[] getTypes() {
        return types;
    }

    public String getTypeString(){return typeString;}

    public SuperType[] getSuperTypes() {
        return superTypes;
    }

    public String[] getSubTypes() {
        return subTypes;
    }

    public int getCount() {
        return count;
    }

    public String getPower() {
        return power;
    }

    public String getToughness() {
        return toughness;
    }

    public String getName() {
        return name;
    }

    private ArrayList<Color> calculateColorID() {
        //TODO
        return new ArrayList<>();
    }

    private int calculateManaValue() {
        //TODO
        return -1;
    }

    public String saveString() {

        String typeString="";
        String superString="";
        String subString="";
        String colorString="";

        for(Type x: this.types){
            typeString += x + " ";
        }
        for(SuperType x: this.superTypes){
            superString += x + " ";
        }
        for(String x : this.subTypes) {
            subString += x + " ";
        }
        for(Color x : this.colors) {
            colorString += x + " ";
        }

        String retString = String.format("Name:%s\nMana Cost:%s\nSupertypes:%s\nTypes:%s\nSubtypes:%s\nColors:%s\nOracleText:%s\nPower:%s\nToughness:%s",
                name, manaCost, superString, typeString, subString, colorString, oText, power, toughness);

        return(retString);

    }

    /*
     *__________________*
     *                  *
     *  HELPFUL TOOLS   *
     *                  *
     * _________________*
     */

    public static Card loadSavedCard(String savedCardName) {

        Scanner reader = null;
        try {
            reader = new Scanner(new File(savedCardName));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ArrayList<String> attributes = new ArrayList<>();

        while (reader.hasNextLine()) {
            attributes.add(reader.nextLine());
        }

        for(int i=0; i< attributes.size(); i++) {
            Pattern pattern = Pattern.compile("(.*?):(.*)");
            Matcher matcher = pattern.matcher(attributes.get(i));

            if(matcher.find()) {
               attributes.set(i, matcher.group(2));
            }
        }
        String name        = attributes.get(0);
        String manaCost    = attributes.get(1);

        String[] st        = attributes.get(2).split(" ");
        SuperType[] superTypes = new SuperType[st.length];
        for(int i=0; i<st.length; i++) {
            try {
                superTypes[i] = SuperType.valueOf(st[i].toUpperCase());
            } catch(Exception exception) {
                break;
            }
        }

        String[] ty        = attributes.get(3).split(" ");
        Type[] types = new Type[ty.length];
        for(int i=0; i<ty.length; i++) {
            try {
                types[i] = Type.valueOf(ty[i].toUpperCase());
            } catch(Exception exception) {
                break;
            }
        }

        String[] subTypes   = attributes.get(4).split(" ");

        String[] co         = attributes.get(5).split(" ");
        Color[] colors = new Color[co.length];
        for(int i=0; i<co.length; i++) {
            colors[i] = Color.valueOf(co[i].toUpperCase());
        }

        String otext        = attributes.get(6);
        String power        = attributes.get(7);
        String toughness    = attributes.get(8);

        if(power.equals("null")) {
            return(new Card(colors, manaCost, otext, types, superTypes, subTypes, name));
        } else {
            return(new Card(colors, manaCost, otext, types, superTypes, subTypes, name, power, toughness));
        }
    }

    public static String generateTypeString(SuperType[] superTypes, Type[] types, String[] subTypes) {
        String typeString = "";

        if(!Arrays.equals(superTypes, new SuperType[]{SuperType.NONE}) && superTypes.length != 0) {
            if(superTypes[0]!=null) {
                for (SuperType superType : superTypes) {
                    typeString += superType + " ";
                }
            }
        }
        for (Type type : types) {
            typeString += type + " ";
        }
        String[] empty = new String[]{""};
        if(!Arrays.equals(subTypes, empty)) {
            typeString += "- ";
            for (String subType : subTypes) {
                typeString += subType + " ";
            }
        }
        return(typeString);
    }

}
