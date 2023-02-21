/**
 * Records a potential move in FreeCell
 *
 * @author Dan DiTursi
 * @version 11 February 2023
 */

import java.util.ArrayList;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

public final class Action
{
    // instance variables - replace the example below with your own
    private final boolean src_cell;  // TRUE = moving from free cell; FALSE = moving from tableau
    private final int src_loc; // which cell or tableau pile to move from 
    private final Card theCard;
    private final int dest; // 0 = cell; 1-8 = tableau; 9 = foundation

    private static final String digits = "0123456789";
    private static final String letters = "abcd";
    /**
     * Constructor for objects of class Action
     */
    public Action(boolean fromCell, int src_pile, Card c, int dst_pile)
    {
        src_cell = fromCell;
        src_loc = src_pile;
        theCard = c;
        dest = dst_pile;
    }

    public Action(String s) {
        if (s.charAt(0) >= '1' && s.charAt(0) <= '9') {
            src_cell = false;
            src_loc = digits.indexOf(s.charAt(0));
        }
        else {
            src_cell = true;
            src_loc = letters.indexOf(s.charAt(0));
        }
        theCard = new Card(s.charAt(1),s.charAt(2));
        dest = digits.indexOf(s.charAt(3));
    }
    
    public boolean fromCell() { return src_cell; }
    public int get_src_pile() { return src_loc; }
    public Card getCard() { return theCard; }
    public int get_dest_pile() { return dest; }
    
    public String toDisplayString() {
        String s = src_cell ? "cell #" : "tableau pile ";
        String result = "Move card " + theCard.toString() + " from " + s + src_loc + " to ";
        if (dest == 0) {
            result = result + "a free cell.";
        }
        else if (dest == 9) {
            result = result + "the foundation.";
        }
        else {
            result = result + "tableau pile " + dest + ".";
        }
        return result;
    }
    
    public String toString() {
        String result = theCard.toString() + digits.charAt(dest);
        if (src_cell) {
            result = letters.charAt(src_loc) + result;
        }
        else {
            result = digits.charAt(src_loc) + result;
        }
        return result;
    }
    
    public static void dumpToFile(ArrayList<Action> Alist, String filename) {
        try {
            FileWriter fw = new FileWriter(filename);
            for (Action a : Alist) {
                fw.write(a.toString() + "\n");
            }
            fw.close();
        }
        catch (IOException e) {
            System.out.println("file dump failed due to IOException");
        }
    }
    
    public static ArrayList<Action> readFromFile(String filename) {
        File f = new File(filename);
        Scanner sc;
        try {
            sc = new Scanner(f);
        }
        catch (FileNotFoundException e) {
            return null;
        }
        
        ArrayList<Action> result = new ArrayList<Action>();
        while (sc.hasNext()) {
            String s = sc.next();
            result.add(new Action(s));
        }
        sc.close();
        return result;
    }
}
