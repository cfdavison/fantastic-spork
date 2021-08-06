package card;

import java.util.*;

public class CardTest {

    public static void main(String args[]) {



        Card testCard = new Card(new Card.Color[]{Card.Color.GREEN}, "{1}{G}", "", new Card.Type[]{Card.Type.CREATURE},
                new Card.SuperType[]{Card.SuperType.NONE}, new String[]{"Bear"}, "Grizzly Bears", "2", "2");

        System.out.println(testCard);
        System.out.println(new Card());


    }

}
