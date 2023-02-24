import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Comparator;

/**
 * Driver class for the Freecell game.
 *
 * @author Dan DiTursi
 * @version 4 February 2023
 */
public class FreeCell
{
    public FreeCell() {}

    public ArrayList<Action> solve(GameState gs) 
    {
        ArrayList<Action> solutionMoves = new ArrayList<Action>();

        PriorityQueue<GameState> pq = new PriorityQueue<GameState>(new Comparator<GameState>() {
            @Override
            public int compare(GameState gs1, GameState gs2) {
                return gs1.h(gs1) - gs2.h(gs2);
            }
        });

        return solutionMoves;
    }

    public static void main() {
        GameState game = new GameState();
        game.display();
        ArrayList<Action> moves = game.getLegalActions();
        for (Action a : moves) {
            System.out.println(a.toDisplayString());
        }
        Action a1 = moves.get(moves.size()-2);
        GameState g2 = game.nextState(a1);
        System.out.println();
        g2.display();
        moves = g2.getLegalActions();
        for (Action a : moves) {
            System.out.println(a.toDisplayString());
        }
        Action a2 = moves.get(moves.size()-3);
        GameState g3 = g2.nextState(a2);
        System.out.println();
        g3.display();
        moves = g3.getLegalActions();
        for (Action a : moves) {
            System.out.println(a.toDisplayString());
        }
        
    }
}
