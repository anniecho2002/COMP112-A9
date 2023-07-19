/* Code for COMP-102-112 - 2021T1, Assignment 9
 * Name: Annie Cho
 * Username: choanni
 * ID: 300575457
 */

import ecs100.*;
import java.util.*;
import java.io.*;
import java.awt.Color;


/**
 *  Lets a player play a two-player card game (a modified version of Battle).
 *  The player takes up to 5 cards to form a hand of cards. 
 *  The player can put the leftmost card from their hand onto the table, pick up more cards from the deck of cards
 *  to fill the gaps in their "hand", replace a card in their hand with a card from the deck, and reorder the cards
 *  in their hand.
 *  For each battle, the player puts their leftmost card from their hand on the table.
 *  The other player is controlled by the computer, who will simply take the top card from the deck.
 *  The player with the highest card wins the battle. If both cards have the same value, neither player wins. 
 *  Each winning battle gives the player a point. The first player to win 7 battles is the winner. 
 *
 * See the Assignment page for description of the program design.
 */

public class BattleGame{

    // Constants for the game
    public static final int NUM_HAND = 5;      // Number of cards in hand
    public static final int TARGET_SCORE = 7;  // Number of rounds to win the game
    public static final int NUM_REPLACE = 3;   // Number of cards the player is allowed to replace per game

    // Fields for the game: deck, hand, and table
    private ArrayList<Card> deck = Card.getShuffledDeck();          // the deck (a list of Cards)
    private Card[] hand = new Card[NUM_HAND];                       // the hand (fixed size array of Cards)
    private ArrayList<Card> tableComputer = new ArrayList<Card>();  // the list of Cards that the computer has played
    private ArrayList<Card> tablePlayer = new ArrayList<Card>();    // the list of Cards that the player has played

    private int selectedPos = 0;      // selected position in the hand.
    private int compScore = 0;        // the number of points scored by the computer player
    private int playScore = 0;        // the number of points scored by the user player
    private int remainingReplaces = NUM_REPLACE;  // 

    // Constants for the layout
    public static final Color RACK_COLOR = new Color(122,61,0);

    public static final int HAND_LEFT = 60;      // x-position of the leftmost Card in the hand
    public static final int HAND_TOP = 500;      // y-Position of all the Cards in the hand 
    public static final int CARD_SPACING = 80;   // spacing is the distance from left side of a card
    // to left side of the next card in the hand
    public static final int CARD_OVERLAP = 15;   // overlap is the distance from left side of a card
    // to left side of the next card on the table
    public static final int CARD_HEIGHT = 110; 

    public static final int TABLE_LEFT = 10;                
    public static final int TABLE_TOP_COMPUTER = 80;
    public static final int TABLE_TOP_PLAYER   = TABLE_TOP_COMPUTER+CARD_HEIGHT+10;

    public static final int SCORES_TOP = 20;

    /**
     * CORE
     * 
     * Restarts the game:
     *  get a new shuffled deck,
     *  set the compScore, playScore and remainingReplaces to their initial values
     *  set the table to be empty,
     *  refill the hand from the deck
     */
    public void restart(){
        deck.clear();
        deck = Card.getShuffledDeck(); 
        tableComputer.clear();
        tablePlayer.clear();
        for (int i=0; i<NUM_HAND; i++){
            hand[i]= deck.get(i);
            deck.remove(i);
        }
        
        compScore = 0;
        playScore = 0;
        remainingReplaces = 3;

        this.redraw();
    }

    /**
     * CORE
     * 
     * If the deck is not empty and there is at least one empty position on the hand, then
     * pick up the top card from the deck and put it into the first empty position on the hand.
     * (needs to search along the array for an empty position.)
     */
    public void pickupCard(){
        // initializing values
        boolean empty = false;
        int indexFirstEmpty = 0;
        
        // checks of there is an empty space in hand

        for (int i=0; i<hand.length; i++) {
            if (hand[i]==null && empty == false) { //if so, record the place of empty    
                indexFirstEmpty = i;
                empty = true;
            } 
        }

        // if the deck is not empty and there is an empty space in hand then...
        if (deck.size()>0 && empty==true){
            // places first value in deck into the empty space and removes the card from the deck
            hand[indexFirstEmpty] = deck.get(0);
            deck.remove(0);
        }
        this.redraw();
    }
    
    /**
     * CORE
     * 
     * Draws all the Cards in the hand,
     *  This MUST use the constants:  (in order to make the selection work!)
     *   - CARD_SPACING, HAND_LEFT, HAND_TOP
     *   See the descriptions where these fields are defined.
     */
    public void drawHandCards(){
        for (int i=0; i<NUM_HAND; i++){
            if (hand[i] != null){
                hand[i].draw(HAND_LEFT + CARD_SPACING*i, 
                        HAND_TOP);
            }
        }
    }

    /**
     * CORE
     * Draws all the Cards in both the computer and player tables in two rows.
     *   See the descriptions of TABLE_LEFT, TABLE_TOP_COMPUTER, TABLE_TOP_PLAYER and CARD_OVERLAP.
     *
     * COMPLETION:
     * - The card with the highest rank in the last battle is outlined
     * 
     */
    public void drawTableCards(){
        //draw the cards in the tableComputer and tablePlayer lists.
        int lastWinner = 0;
        for (int i=0; i<tablePlayer.size(); i++){
            tablePlayer.get(i).draw(TABLE_LEFT + CARD_OVERLAP*i, TABLE_TOP_PLAYER);
            if (tablePlayer.get(tablePlayer.size()-1).getRank() > tableComputer.get(tableComputer.size()-1).getRank()){
                if (i!=0){
                    UI.setColor(Color.white);
                    UI.drawRect(TABLE_LEFT + CARD_OVERLAP*lastWinner - 3, TABLE_TOP_PLAYER - 3, CARD_OVERLAP*5 + 6, CARD_HEIGHT + 6);
                    UI.setColor(Color.green);
                    UI.drawRect(TABLE_LEFT + CARD_OVERLAP*i - 3, TABLE_TOP_PLAYER - 3, CARD_OVERLAP*5 + 6, CARD_HEIGHT + 6);
                }
                else{
                    UI.setColor(Color.green);
                    UI.drawRect(TABLE_LEFT + CARD_OVERLAP*i - 3, TABLE_TOP_PLAYER - 3, CARD_OVERLAP*5 + 6, CARD_HEIGHT + 6);
                }
                lastWinner = i;
            }
        }
        for (int i=0; i<tableComputer.size(); i++){
            tableComputer.get(i).draw(TABLE_LEFT + CARD_OVERLAP*i, TABLE_TOP_COMPUTER);
            if (tableComputer.get(tableComputer.size()-1).getRank() > tablePlayer.get(tablePlayer.size()-1).getRank()){
                if (i!=0){
                    UI.setColor(Color.white);
                    UI.drawRect(TABLE_LEFT + CARD_OVERLAP*lastWinner - 3, TABLE_TOP_COMPUTER - 3, CARD_OVERLAP*5 + 6, CARD_HEIGHT + 6);
                    UI.setColor(Color.green);
                    UI.drawRect(TABLE_LEFT + CARD_OVERLAP*i - 3, TABLE_TOP_COMPUTER - 3, CARD_OVERLAP*5 + 6, CARD_HEIGHT + 6);
                }
                else{
                    UI.setColor(Color.green);
                    UI.drawRect(TABLE_LEFT + CARD_OVERLAP*i - 3, TABLE_TOP_COMPUTER - 3, CARD_OVERLAP*5 + 6, CARD_HEIGHT + 6);
                }
                lastWinner = i;
            }
        }
    }

    /**
     * CORE
     * 
     * If there is a card in the leftmost position in the hand, then
     * - place it on the table
     * - gets the top card from the deck for the computer player and places it to the table
     * - compare the ranks of the two cards and award a point to the player with the highest card.
     * - redraw the table and hand [this.redraw()]
     * - if the player or the computer have reached the target,  end the game.
     */
    public void playBattle(){
        if (hand[0] != null){
            tablePlayer.add(hand[0]);
            hand[0] = null;
            tableComputer.add(deck.get(0));
            deck.remove(0);
            
            int pLastIndex = tablePlayer.size() - 1;
            int cLastIndex = tableComputer.size() - 1;
            if (tablePlayer.get(pLastIndex).getRank() > tableComputer.get(cLastIndex).getRank()){
                playScore = playScore + 1;
            }
            else {
                compScore = compScore + 1;
            }
            this.redraw();
            if (playScore == 7 || compScore == 7){
                this.endGame();
            }
        }
    }

    /**
     * COMPLETION
     * 
     * If there is a card at the selected position in the hand, 
     * replace it by a card from the deck.
     */
    public void replaceCard(){
        if (remainingReplaces > 0 ){
            hand[selectedPos] = deck.get(0);
            deck.remove(0);
            remainingReplaces = remainingReplaces - 1;
        }
        this.redraw();
    }

    /**
     * COMPLETION
     *
     * Swap the contents of the selected position on hand with the
     * position on its left (if there is such a position)
     * and also decrement the selected position to follow the card 
     */
    public void moveLeft(){
        Card placeholder = hand[selectedPos];
        if(selectedPos-1 >= 0){
            hand[selectedPos] = null;
            hand[selectedPos] = hand[selectedPos - 1];
            hand[selectedPos - 1] = placeholder;
        }
        this.redraw();
    }

    /** ---------- The code below is already written for you ---------- **/

    /** 
     * Allows the user to select a position in the hand using the mouse.
     * If the mouse is released over the hand, then sets  selectedPos
     * to be the index into the hand array.
     * Redraws the hand and table 
     */
    public void doMouse(String action, double x, double y){
        if (action.equals("released")){
            if (y >= HAND_TOP && y <= HAND_TOP+CARD_HEIGHT && 
            x >= HAND_LEFT && x <= HAND_LEFT + NUM_HAND*CARD_SPACING) {
                this.selectedPos = (int) ((x-HAND_LEFT)/CARD_SPACING);
                //UI.clearText();UI.println("selected "+this.selectedPos);
                this.redraw();
            }
        }
    }

    /**
     * Displays a win/lose message
     */
    public void endGame(){
        UI.setFontSize(40);
        UI.setColor(Color.red);
        if (this.playScore > this.compScore){
            UI.drawString("YOU WIN!!!", 500, HAND_TOP-80);
        }
        else{
            UI.drawString("YOU LOSE", 500, HAND_TOP-80);
        }
        UI.sleep(3000);
        this.restart();
    }

    /**
     *  Redraw the table and the hand.
     */
    public void redraw(){
        UI.clearGraphics();
        UI.setFontSize(20);
        UI.setColor(Color.black);
        UI.drawString("Player: " + playScore + " Computer: " + compScore, TABLE_LEFT+150, SCORES_TOP);
        UI.drawString("Remaining replaces: " + remainingReplaces, TABLE_LEFT+600, SCORES_TOP);

        // outline the hand and the selected position
        UI.setLineWidth(2);
        UI.setColor(Color.black);
        UI.drawRect(HAND_LEFT-4, HAND_TOP-4, (CARD_SPACING)*NUM_HAND+4, CARD_HEIGHT+8);

        UI.setColor(Color.green);
        int selLeft = HAND_LEFT + (this.selectedPos * (CARD_SPACING)) - 2;
        UI.drawRect(selLeft, HAND_TOP - 2, CARD_SPACING, CARD_HEIGHT+4);

        // draw the rack top
        UI.setColor(RACK_COLOR);
        UI.fillRect(HAND_LEFT-10, HAND_TOP-28, (CARD_SPACING)*NUM_HAND+20, 20);

        this.drawHandCards();
        this.drawTableCards();
    }

    /**
     * Set up the user interface
     */
    public void setupGUI(){
        UI.setMouseListener( this::doMouse );
        UI.addButton("Battle", this::playBattle);
        UI.addButton("Pickup", this::pickupCard);
        UI.addButton("Replace",  this::replaceCard); 
        UI.addButton("Left", this::moveLeft);
        UI.addButton("Restart", this::restart);
        UI.addButton("Quit", UI::quit);
        UI.setWindowSize(1100,650);
        UI.setDivider(0.0);

    }

    public static void main(String[] args){
        BattleGame bg = new BattleGame();
        bg.setupGUI();
        bg.restart();
    }   
}
