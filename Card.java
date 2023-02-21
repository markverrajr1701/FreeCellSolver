
/**
 * A standard playing card
 *
 * @author Dan DiTursi
 * @version 11 February 2023
 */
public class Card
{
    private int rank;   // 1-13; 1 = A, 11 = J, 12 = Q, 13 = K
    private int suit;   // 1 = spade, 2 = heart, 3 = club, 4 = diamond
    // Use 0,0 as a joker. (Note: no jokers in FreeCell)
    
    public static final String rankString = "-A23456789TJQK";
    public static final String suitString = "-SHCD";

    /**
     * Constructor for objects of class Card
     */
    public Card() {
        rank = 0;
        suit = 0;
    }
    
    public Card(int r, int s) {
        rank = r;
        suit = s;
    }
    
    public Card(char r, char s) {
        char r1 = Character.toUpperCase(r);
        rank = rankString.indexOf(r1);
        
        char s1 = Character.toUpperCase(s);
        suit = suitString.indexOf(s1);
        
        if (rank == -1 || suit == -1) {
            rank = 0;
            suit = 0;
        }
    }

    public int getRank() { return rank; }
    public int getSuit() { return suit; }
    public int getColor() { return (suit % 2); }
    
    public boolean sameColor(Card c2) {
        return (this.getColor() == c2.getColor());
    }
    
    public static boolean sameColor(Card c1, Card c2) {
        return (c1.getColor() == c2.getColor());
    }
    
    public boolean equals(Card c2) {
        return ((rank == c2.rank) && (suit == c2.suit));
    }
    
    public String toString() {
        return ("" + rankString.charAt(rank) + suitString.charAt(suit));
    }
}
