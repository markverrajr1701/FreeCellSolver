import java.util.ArrayList;
import java.util.Collections;
import java.io.File;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileWriter;

/**
 * Contains the state of a FreeCell game
 *
 * @author Dan DiTursi
 * @version 11 February 2023
 */
public class GameState
{
    // instance variables - replace the example below with your own
    private ArrayList<Card> cells; // Numbered from 0-3; empty cells will always be last
    private int numCellsFree;
    private ArrayList<ArrayList<Card>> tableau; // In actions, numbered 1-8; we adjust the -1 manually.
    private int[] foundations = {0,0,0,0,0}; // We'll ignore the first one.

    /**
     * Creates a random deal
     */
    public GameState()
    {
        cells = new ArrayList<Card>(4);
        numCellsFree = 4;
        tableau = new ArrayList<ArrayList<Card>>(8);
        
        ArrayList<Card> deck = new ArrayList<Card>(52);
        int i,j,k;
        for (i = 1; i <= 13; i++) {
            for (j = 1; j <= 4; j++) {
                deck.add(new Card(i,j));
            }
        }
        Collections.shuffle(deck);
        
        for (i = 0; i < 8; i++) {
            ArrayList<Card> current = new ArrayList<Card>();
            k = i<4 ? 7 : 6;    // first four piles get 7 cards; last four get 6 cards
            for (j = 0; j < k; j++) {
                current.add(deck.remove(0));
            }
            tableau.add(current);
        }
    }
    
    public GameState(GameState gs) {
        cells = new ArrayList<Card>(4);
        for (Card c : gs.cells) { cells.add(c); }
        numCellsFree = gs.numCellsFree;
        tableau = new ArrayList<ArrayList<Card>>(8);
        for (int i = 0; i < 8; i++) {
            ArrayList<Card> current = new ArrayList<Card>();
            ArrayList<Card> pile = gs.tableau.get(i);
            for (Card c : pile) { current.add(c); }
            tableau.add(current);
        }
        for (int i = 0; i < 5; i++) {
            foundations[i] = gs.foundations[i];
        }
    }
    
    // Note: input string must be full file path, unless file is in current working directory
    public GameState(String filename) throws FileNotFoundException {
        File f = new File(filename);
        Scanner sc = new Scanner(f);
        
        String s1 = sc.nextLine();
        String[] S = s1.split(" ");
        for (int i = 1; i <=4; i++) {
            foundations[i] = Integer.parseInt(S[i-1]);
        }
        
        cells = new ArrayList<Card>(4);
        String s2 = sc.nextLine();
        S = s2.split(" ");
        numCellsFree = Integer.parseInt(S[0]);
        for (int i = 0; i < (4 - numCellsFree); i++) {
            String s3 = S[i+1];
            cells.add(new Card(s3.charAt(0),s3.charAt(1)));
        }
        
        tableau = new ArrayList<ArrayList<Card>>(8);
        for (int i = 0; i < 8; i++) {
            ArrayList<Card> pile = new ArrayList<Card>();
            String s4 = sc.nextLine();
            S = s4.split(" ");
            if (!S[0].equals("--")) {
                for (int j = 0; j < S.length; j++) {
                    pile.add(new Card(S[j].charAt(0),S[j].charAt(1)));
                }
            }
            tableau.add(pile);
        }
    }
    
    // Note: Modifies internal state; no "undo" available
    private boolean executeAction(Action a) {
        if (!isLegalAction(a)) { return false; }
        Card c;
        if (a.fromCell()) {
            c = cells.remove(a.get_src_pile());
            numCellsFree++;
        }
        else {
            ArrayList<Card> p1 = tableau.get(a.get_src_pile()-1);
            c = p1.remove(p1.size()-1);
        }
        int d = a.get_dest_pile();
        if (d == 0) {
            cells.add(c);
            numCellsFree--;
        }
        else if (d == 9) {
            foundations[c.getSuit()] = foundations[c.getSuit()] + 1;
        }
        else {
            ArrayList<Card> p2 = tableau.get(d-1);
            p2.add(c);
        }
        return true;
    }
        
    public boolean isLegalAction(Action a) {
        int s = a.get_src_pile();
        Card c;
        ArrayList<Card> pile;
        if (a.fromCell()) {
            c = cells.get(s);
        }
        else {
            pile = tableau.get(s-1);
            c = pile.get(pile.size()-1);
        }
        if (!c.equals(a.getCard())) { return false; }    
        int d = a.get_dest_pile();
        if (d == 0 && numCellsFree > 0) { return true; }
        if (d == 9) {
            return c.getRank() == foundations[c.getSuit()] + 1;
            // is this card the next one for its suit's foundation pile?
        }
        else {
            pile = tableau.get(d-1);
            if (pile.size() == 0) { return true; }
            Card last = pile.get(pile.size()-1);
            return (last.getRank() == c.getRank() + 1) && (!last.sameColor(c));
        }
        //return false;
    }
    
    public ArrayList<Action> getLegalActions() {
        ArrayList<Action> result = new ArrayList<Action>();
        
        // Moves from tableau to cells
        if (numCellsFree > 0) {
            for (int i = 0; i < 8; i++) {
                ArrayList<Card> pile = tableau.get(i);
                if (pile.size() > 0) {
                    Card c = pile.get(pile.size()-1);
                    result.add(new Action(false,i+1,c,0));
                }
            }
        }
        
        // Moves to tableau
        boolean foundEmpty = false;
        for (int d = 0; d < 8; d++) {
            ArrayList<Card> pile = tableau.get(d);
            // non-empty pile - check all movable cards to see if they can go here.
            if (pile.size() > 0) {
                Card top = pile.get(pile.size() - 1);
                for (int s = 0; s<cells.size(); s++) {
                    Card c2 = cells.get(s);
                    if (!top.sameColor(c2) && (top.getRank() == c2.getRank()+1)) {
                        result.add(new Action(true,s,c2,d+1));
                    }
                }
                for (int s = 0; s < 8; s++) {
                    if (s == d) { continue; }
                    ArrayList<Card> p2 = tableau.get(s);
                    if (p2.size() == 0) { continue; }
                    Card c2 = p2.get(p2.size()-1);
                    if (!top.sameColor(c2) && (top.getRank() == c2.getRank()+1)) {
                        result.add(new Action(false,s+1,c2,d+1));
                    }                    
                }
            }
            else {  // empty pile - any card can go here
                if (!foundEmpty) {
                    foundEmpty = true;
                    for (int s = 0; s<cells.size(); s++) {
                        result.add(new Action(true,s,cells.get(s),d+1));
                    }
                    for (int s = 0; s < 8; s++) {
                        if (s == d) { continue; }
                        ArrayList<Card> p2 = tableau.get(s);
                        // No point in moving a single card from one tableau pile to an empty space
                        if (p2.size() >= 2) {
                            result.add(new Action(false,s+1,p2.get(p2.size()-1),d+1));
                        }
                    }
                }
            }
        }
        
        // Moves to foundation
        for (int s = 0; s<cells.size(); s++) {
            Card c2 = cells.get(s);
            if (c2.getRank() == foundations[c2.getSuit()] + 1) {
                result.add(new Action(true,s,c2,9));
            }
        }
        for (int s = 0; s < 8; s++) {
            ArrayList<Card> p2 = tableau.get(s);
            if (p2.size() > 0) {
                Card c2 = p2.get(p2.size()-1);
                if (c2.getRank() == foundations[c2.getSuit()] + 1) {
                    result.add(new Action(false,s+1,c2,9));
                }
            }
        }
        
        return result;
    }

    public GameState nextState(Action a) {
        GameState result = new GameState(this);
        if (!result.executeAction(a)) { return null; }
        return result;
    }
    
    public GameState resultState(ArrayList<Action> Alist) {
        GameState result = new GameState(this);
        for (Action a : Alist) {
            if (!result.executeAction(a)) { return null; }
        }
        return result;
    }
    
    public String toDisplayString() {
        String s1 = "Foundations:";
        String s2 = "";
        for (int i = 1; i <= 4; i++) {
            s2 = s2 + " " + Card.rankString.charAt(foundations[i]) + Card.suitString.charAt(i);
        }
        String s3 = "Free cells:";
        for (Card c : cells) {
            s3 = s3 + " " + c.toString();
        }
        for (int i = 0; i < numCellsFree; i++) {
            s3 = s3 + " --";
        }
        String s4 = "Tableau (piles go left to right, right is top):";
        for (int j = 0; j < 8; j++) {
            ArrayList<Card> pile = tableau.get(j);
            s4 = s4 + System.lineSeparator() + " " + (j+1) + ":";
            if (pile.size() == 0) {
                s4 = s4 + " --";
            }
            else {
                for (Card c : pile) {
                    s4 = s4 + " " + c.toString();
                }
            }
        }
        return s1 + s2 + System.lineSeparator() + s3 + System.lineSeparator() + s4;
    }
    
    // The string format for a GameState is as follows:
    //   First line: four integers representing the foundations
    //   Second line: one integer for number of free cells, followed by list of cards in cells (if any)
    //   Lines 3-10: List of cards in each tableau pile. Empty piles are represented by "--"
    public String toString() {
        String result = "";
        for (int i = 1; i <= 4; i++) {
            result = result + foundations[i] + " ";
        }
        result = result + "\n";
        
        result = result + numCellsFree + " ";
        for (int i = 0; i < cells.size(); i++) {
            result = result + cells.get(i).toString() + " ";
        }
        result = result + "\n";
                
        for (int i = 0; i < 8; i++) {
            ArrayList<Card> pile = tableau.get(i);
            if (pile.size() == 0) {
                result = result + "--";
            }
            else {
                for (Card c : pile) {
                    result = result + c.toString() + " ";
                }
            }
            result = result + "\n";
        }
        return result;
    }
    
    public void dumpToFile(String filename) {
        try {
            FileWriter fw = new FileWriter(filename);
            fw.write(toString());
            fw.close();
        }
        catch (IOException e) {
            System.out.println("file dump failed, IOException");
        }
    }
    
    public void display() {
        System.out.println(toDisplayString());
    }
    
    public boolean isWin() {
        for (int i = 1; i <=4; i++) {
            if (foundations[i] < 13) { return false; }
        }
        return true;
    }
    
    public boolean gameover() {
        return (getLegalActions().size()) == 0;
    }
    
    public boolean isLoss() {
        return !isWin() && gameover();
    }
}
