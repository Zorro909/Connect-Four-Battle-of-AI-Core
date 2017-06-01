import java.util.Random;

import de.Zorro.VierGewinnt.API.AI;
import de.Zorro.VierGewinnt.Board.Board;

public class JavaTemplateAI implements AI {

    @Override
    public int turn(Board b, String symbol) {
        int x = new Random().nextInt(b.getWidth());
        while(!b.getCell(x, b.getHeight()-1).equalsIgnoreCase("u")){
            x = new Random().nextInt(b.getWidth());
        }
        return x;
    }

}
