/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import static java.lang.Thread.sleep;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author damiano
 */
public class D4mnAsciiBlackjack {

    //global constants for the seeds (UI variables):
    static final char SPADES = '♠';
    static final char HEARTS = '♥';
    static final char DIAMONDS = '♦';
    static final char CLUBS = '♣';
    
    //global costants for fiches rapresentation (UI variables):
    static final String[] FICHES_RAPRESENTATION = {"1000","100","50","10","1"};
    static final int[] FICHES_VALUES = {1000,100,50,10,1};
    
    //global constants for user inputs (UI variables):
    static final char HIT = 'c';
    static final char STARE = 's';
    
    
    //global variable for the deck: 2 decks of 52 cards. Cards are integer numbers:
    static int[] deck = new int[104];
    //global variable to keep information about how many cards have been used:
    static int usedCards;
    
    //array with players names:
    static String[] players;
    //array with playrs fiches:
    static int[] fiches;
    //array saying if a player (actually a position) is playing or is retired:
    static boolean[] playing;
    
    //constants with starting fiches:
    static final int STARTING_FICHES = 1000;
    
    //global scanner for user inputs:
    static Scanner scanner = new Scanner(System.in);
    
    
    /**
     * Main mwthod.
     * Play hand until all players are retired.
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        presentation();
        initDeck();
        shuffle();
        initMatch();
        
        boolean fine = endControl();
        
        while (!fine) {
            if (usedCards >= deck.length/2) {
                // mischia mischia 
                shuffle();
                shuffle();
                shuffle();
            }
            /* IF U WANT TO PRINT DECK TO CHECK IF CARDS ARE CORRECTLY DISTRIBUTED:
            boolean visible[] = new boolean[deck.length];
            for (int i = 0; i < deck.length; i++) visible[i] = true;
            printHand(deck, visible);
            */
            playRound();
            fine = endControl();
        }
        
        endGame();
    }

    private static void endGame() {
        System.out.println("\nFine della partita.");
    }
    /**
     * Method for playing a round.
     * Bets for each player are collected.
     * Cards are distributed to players and dealer.
     * Each player plays his hand against the dealer.
     * Dealer plays his hand.
     * Cards of each player are evaluated against the dealer, and bets are paid.
     */
    private static void playRound() {
        // collect bets:
        int[] bets = betting();
        // if players are in the match:
        if (!endControl()) {
            // matrix of two cards for each player:
            int[][] playersTwoCards = new int[players.length][2];
            // two cards for the dealer:
            int[] dealerTwoCards = new int[2];
            // cards are passed by reference and modified by the method:
            distributeTwoCards(playersTwoCards, dealerTwoCards);
            printMatchStatus(bets, playersTwoCards, dealerTwoCards);

            // variable with values of the hands of the players:
            int[] playersValues = new int[players.length];
            // variable with information about players having a served blackjack
            boolean blackjacks[] = new boolean[players.length];
            
            // each player plays his hand:
            for (int giocatore = 0; giocatore < players.length; giocatore++) {
                if (playing[giocatore]) {
                    playersValues[giocatore] = singlePlayerHand(giocatore, playersTwoCards[giocatore], blackjacks);
                    if (playersValues[giocatore] > 21) {
                        System.out.println(players[giocatore] + " SBALLA.");
                    }
                } else {
                    playersValues[giocatore] = -1;
                }
                // wait a moment before passing to next player hand
                try {
                    sleep(2000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(D4mnAsciiBlackjack.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            // the dealer plays:
            boolean blackjackBanco[] = new boolean[1];
            int punteggioBanco = dealerHand(dealerTwoCards, blackjackBanco);
            // check the results:
            finalEvaluation(bets, blackjacks, playersValues, blackjackBanco[0], punteggioBanco);
        }
    }

    /**
     * Method to decide who is winning against the dealer, and who is loosing.
     * Winners are paid what they deserve. Loosers loose their bets.
     * Blackjacks are paid 3:2.
     * @param bets the array with the entity of the bet for each player
     * @param blackjacks the array saying for each player if he has a blackjack
     * @param playersHandsValues array with final values of the hands for each player
     * @param dealerBlackjack true if the dealer has a blackjack
     * @param dealerHandValue the value of the dealer hand
     */
    private static void finalEvaluation(int[] bets, boolean[] blackjacks, int[] playersHandsValues, boolean dealerBlackjack, int dealerHandValue) {
        // check all possible conditions for each player against the dealer:
        for (int giocatore = 0; giocatore < playersHandsValues.length; giocatore++) {
            if (playing[giocatore]) {
                if (blackjacks[giocatore]) {
                    if (dealerBlackjack) {
                        System.out.println(players[giocatore] + " ties with the dealer. Get back the bet: " + bets[giocatore] +".");
                        fiches[giocatore] += bets[giocatore];
                    } else {
                        System.out.println(players[giocatore] + " blackjack! Bets " + bets[giocatore] + ", paid 3:2 "+ bets[giocatore]*5/2 +".");
                        fiches[giocatore] += bets[giocatore]*5/2;
                    }
                } else {
                    if (!(dealerHandValue > 21)) {
                        // se il banco non sballa:
                        // nel caso in cui il giocatore non abbia fatto blackjack:
                        if (playersHandsValues[giocatore] > 21) {
                            // il giocatore ha sballato
                            System.out.println(players[giocatore] + " goes bust. Loose the bet: " + bets[giocatore] + ".");
                        } else if (playersHandsValues[giocatore] > dealerHandValue) {
                            System.out.println(players[giocatore] + " wins. Bets " + bets[giocatore] + ", paid 1:1 " + bets[giocatore]*2 +".");
                            fiches[giocatore] += bets[giocatore]*2;
                        } else if (playersHandsValues[giocatore] == dealerHandValue) {
                            System.out.println(players[giocatore] + " ties with the dealer. Get back the bet:  " + bets[giocatore]+".");
                            fiches[giocatore] += bets[giocatore];
                        } else {
                            System.out.println(players[giocatore] + " looses. Looses the bet: " + bets[giocatore]+".");
                        }
                    } else {
                        // se il banco sballa:
                        System.out.println("Dealer goes bust. "+ players[giocatore]+ " bets " + bets[giocatore] + ", paid 1:1 " + bets[giocatore]*2 +".");
                        fiches[giocatore] += bets[giocatore]*2;
                    }
                }
            }
            try {
                sleep(1500);
            } catch (InterruptedException ex) {
                Logger.getLogger(D4mnAsciiBlackjack.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    /**
     * Method to play the dealer hand.
     * works exactly the same as the player hand. The difference is there are no user choices.
     * The dealer stares with a >=17 hand.
     * @param dealerTwoCards int array with starting two cards of the dealer.
     * @param dealerBlackjack boolean array with single cell, a simple way to pass a boolean by reference. it will contain true if dealer has a blackjack
     * @return 
     */
    private static int dealerHand(int[] dealerTwoCards, boolean[] dealerBlackjack) {
        System.out.println("================================================");
        System.out.println("================================================");
        System.out.println("================================================");
        int[] dealerCards = dealerTwoCards;
        boolean[] visible = new boolean[dealerCards.length];
        for (int i = 0; i < visible.length; i++) {
            visible[i] = true;
        }
        int sommaFinale = computeHandValue(dealerCards);
        System.out.println("DEALER, SUM: " + sommaFinale);
        printHand(dealerCards, visible);
        if (sommaFinale == 21) {
            dealerBlackjack[0] = true;
            blackjack();
            return sommaFinale;
        }
        boolean stop = sommaFinale >= 17;
        while (!stop) {
            try {
                sleep(2000);
            } catch (InterruptedException ex) {
                Logger.getLogger(D4mnAsciiBlackjack.class.getName()).log(Level.SEVERE, null, ex);
            }
            int nuoveCarteBanco[] = new int[dealerCards.length + 1];
            for (int carta = 0; carta < dealerCards.length; carta++) {
                nuoveCarteBanco[carta] = dealerCards[carta];
            }
            nuoveCarteBanco[nuoveCarteBanco.length - 1] = nextCard();
            dealerCards = nuoveCarteBanco;
            sommaFinale = computeHandValue(dealerCards);
            visible = new boolean[dealerCards.length];
            for (int i = 0; i < visible.length; i++) {
                visible[i] = true;
            }
            System.out.println("DEALER, SUM: " + sommaFinale);
            printHand(dealerCards, visible);
            stop = sommaFinale >= 17;
        }

        return sommaFinale;
    }
    
    /**
     * Method to play an hand for a single player.
     * If the player has a blackjack hand is ended.
     * And the array of blackjacks is setted to true in the position of this player.
     * Otherwise he can hit or stare until he stares or he goes bust.
     * @param playerNumber the position identifying the player in the global arrays
     * @param playerTwoCards the array with starting cards of this player.
     * @param blackjacks the array of boolean keeping information about players with a blackjack.
     * @return final value of the hand.
     */
    private static int singlePlayerHand(int playerNumber, int[] playerTwoCards, boolean blackjacks[]) {
        System.out.println("================================================");
        System.out.println("================================================");
        System.out.println("================================================");        
        
        // player cards initially are the two he starts with.
        // this should be a dynamic array, but this lecture doues not consider this kind of structure.
        // so this variable will be re-initialized with a bigger dimension each time the player hits a new card.
        int[] playerCards = playerTwoCards;
        
        // this array is useful to print the hand. It means that all player cards are visible:
        boolean[] visible = new boolean[playerCards.length];
        for (int i = 0; i<visible.length; i++) {
            visible[i] = true;
        }
        
        // the value of the hand:
        int finalValue = computeHandValue(playerCards);
        System.out.println("PLAYER " + players[playerNumber] + ", SUM: " + finalValue);
        printHand(playerCards, visible);
        
        // in case of blackjack:
        if (finalValue == 21) {
            blackjacks[playerNumber] = true;
            blackjack();
            return finalValue;
        }
        
        // otherwise:
        boolean stop = false;
        while (!stop) {
            char mossa;
            do {
                System.out.print("HIT ("+HIT+") or STARE ("+STARE+")? ");
                mossa = scanner.next().charAt(0);
            } while (mossa != HIT && mossa != STARE);
            stop = (mossa == STARE);
            
            if (!stop) {
                // array with one more card, the hitted one:
                int[] newPlayerCards = new int[playerCards.length +1];
                for (int carta = 0; carta < playerCards.length; carta++) {
                    newPlayerCards[carta] = playerCards[carta];
                }
                // hit the card:
                newPlayerCards[newPlayerCards.length-1] = nextCard();
                playerCards = newPlayerCards;
                // re-compute value and check the stop:
                finalValue = computeHandValue(playerCards);
                stop = finalValue >= 21;
            }
            
            visible = new boolean[playerCards.length];
            for (int i = 0; i < visible.length; i++) {
                visible[i] = true;
            }
            if (!(mossa == STARE)) {
                System.out.println("PLAYER " + players[playerNumber] + ", SUM: " + finalValue);
                printHand(playerCards, visible);
            }
        }
        return finalValue;
    }
    
    /**
     * Method to compute points of a hand.
     * Given an array of cards compute the sum following the game rules:
     * Ace value: 1 or 11 depending on the case.
     * Figures: 10
     * Others: the number of the card.
     * @param cards the array of cards representing the hand.
     * @return the value of the hand.
     */
    private static int computeHandValue(int[] cards) {
        // variable for the final value of the hand:
        int somma = 0;
        // variable to count how many aces are in the hand:
        int acesNumber = 0;
        // add values of cards to the sum 
        for (int carta : cards) {
            if (carta % 13 == 0) {
                // for now, each ace has value 1. 
                acesNumber++;
                somma += 1;
            } else {
                // each card is considered with its value, Jack Queen and King are 10s.
                somma += (carta % 13 > 9 ? 10 : (carta % 13 +1));
            }
        }
        // now decide if aces can have value 11.
        for (int ace = 0; ace<acesNumber; ace++) {
            somma = (somma + 10 > 21 ? somma : somma+10);
        }
        // return the value of the hand.
        return somma;
    }
    
    /**
     * Method to distribute cards among players.
     * As in the real world, pick card from the deck and assign it to players from first to dealer and again.
     * The method has no output because arrays are passed by reference.
     * The method is modifying the arrays in input to it.
     * @param forPlayers a int matrix that Nx2 to keep 2 cards for each players.
     * @param forDealer a int array of 2 cells to keep 2 cards for the dealer.
     */
    private static void distributeTwoCards(int[][] forPlayers, int[] forDealer) {
        // For two times, distribute cards to the players that are not retired and dealer.
        for (int n_carta = 0; n_carta < 2; n_carta++) {
            for (int giocatore = 0; giocatore < forPlayers.length; giocatore++) {
                if (playing[giocatore]){
                    forPlayers[giocatore][n_carta] = nextCard();
                } else {
                    forPlayers[giocatore][n_carta] = -1;
                }
            }
            forDealer[n_carta] = nextCard();
        }
    }
    
    /**
     * Method to obtain the next card in the deck.
     * Simply pick the next card following the deck. The deck is assumed to be already ordered in a random fashion (shuffle method).
     * @return the number of the card.
     */
    private static int nextCard() {
        if (usedCards >= deck.length) return -1;
        usedCards ++;
        return deck[usedCards -1];
    }
    
    /**
     * Method to collect players bets.
     * Each bet is put in an array in the position correspondent to the number of the player.
     * Retired players have a 0 bet.
     * @return the array of the bets.
     */
    private static int[] betting() {
        System.out.println("      ___           ___         ___     \n     /\\  \\         /\\  \\       /\\  \\    \n    /::\\  \\       /::\\  \\      \\:\\  \\   \n   /:/\\:\\  \\     /:/\\:\\  \\      \\:\\  \\  \n  /::\\~\\:\\__\\   /::\\~\\:\\  \\     /::\\  \\ \n /:/\\:\\ \\:|__| /:/\\:\\ \\:\\__\\   /:/\\:\\__\\\n \\:\\~\\:\\/:/  / \\:\\~\\:\\ \\/__/  /:/  \\/__/\n  \\:\\ \\::/  /   \\:\\ \\/__/    /:/  /     \n   \\:\\/:/  /     \\:\\__\\     /:/  /      \n    \\::/__/       \\/__/     \\/__/");
        // array for the bets:
        int puntate[] = new int[players.length];
        System.out.println("Bet 0 to quit the match.");
        
        // collect bets for each player:
        for (int i = 0; i < players.length; i ++) {
            if (playing[i]) {
                System.out.print(players[i] + ", stack: "+fiches[i]+", do your bet: ");
                do {
                    puntate[i] = scanner.nextInt();
                    if (puntate[i] < 0) System.out.print("Only positive numbers allowed. Bet: ");
                    if (puntate[i] > fiches[i]) System.out.print("Can't bet more then you have, genius. Bet: ");
                    if (puntate[i] == fiches[i]) allin(); 
                } while (puntate[i] < 0 || puntate[i] > fiches[i]);
                if (puntate[i] == 0) {
                    playerQuit(i);
                } else {
                    fiches[i] -= puntate[i];
                }
            } else {
                puntate[i] = 0;
            }
        }
        return puntate;
    }
    
    /**
     * Method for retiring a player.
     * He is setted as not playing anymore, and an interface shows loss or gain.
     * @param playerNumber the number (i.e. position) of the player that is retiring.
     */
    private static void playerQuit(int playerNumber) {
        playing[playerNumber] = false;
        System.out.println(players[playerNumber] + " is leaving the table with "+ fiches[playerNumber] + " fiches.");
        int difference = (fiches[playerNumber]-STARTING_FICHES);
        System.out.print(difference > 0? "Nice, gain +": (difference < 0? "Bad, loss -" : "No risk, no loss, no gain. So sad..."));
        System.out.println(difference != 0? Math.abs(difference) : "");
    }
    
    /**
     * Method for check the end of the game.
     * The match is ending when all players quit the table.
     * @return true if no players are playing.
     */
    private static boolean endControl() {
        int counterInGioco = 0;
        for (boolean playerPlaying : playing) {
            if (playerPlaying) counterInGioco ++;
        }
        return !(counterInGioco > 0);
    }
        
    /**
     * Initialize the match.
     * Assign names to the players, initialize their fiches and their status "playing" to true.
     */
    private static void initMatch() {
        System.out.print("How many players are there? ");
        int numeroGiocatori = scanner.nextInt();
        players = new String[numeroGiocatori];
        fiches = new int[numeroGiocatori];
        playing = new boolean[numeroGiocatori];
        for (int i = 0; i < numeroGiocatori; i++) {
            System.out.print("Player " + (i+1) + ", name: ");
            players[i] = scanner.next();
            fiches[i] = STARTING_FICHES;
            playing[i] = true;
        }
    }
    
    /**
     * Initialize the deck.
     * Insert values in the deck array. 
     * Each position contains as value itself.
     * Ordered deck.
     */
    public static void initDeck() {
        // assegna le carte univoche (numeri da 0 a lunghezza mazzo -1)
        for (int i = 0; i < deck.length; i++) {
            deck[i] = i;
        }
        usedCards = 0;
    }
    
    /** 
     * Shuffle the deck with a series of random extractions.
     * Changes the order of the cards in the deck.
     */
    public static void shuffle() {
        for (int estratte = 0; estratte < deck.length; estratte++) {
            // etract a position:
            int posizioneEstratta = (int) (Math.random() * (deck.length - estratte));
            // substitute card in that position with the last not exctracted at the end of the deck:
            int provv = deck[deck.length - 1 - estratte];
            deck[deck.length - 1 - estratte] = deck[posizioneEstratta];
            deck[posizioneEstratta] = provv;
        }
    }
    
    
    
    
    
    
    /************************** UI METHODS **************************/
    
    
    /**
     * Presentation of the game (UI METHOD).
     * Brief introduction with the rules of the game.
     */
    private static void presentation() {
        // print an ascii-art introduction to the game:
        String presentation[] = {"    //   ) )  / /        // | |     //   ) )  //   //          / / // | |     //   ) )  //   // ", "   //___/ /  / /        //__| |    //        //__ //          / / //__| |    //        //__ //  ", "  / __  (   / /        / ___  |   //        //__  /          / / / ___  |   //        //__  /   ", " //    ) ) / /        //    | |  //        //   \\ \\         / / //    | |  //        //   \\ \\   ", "//____/ / / /____/ / //     | | ((____/ / //     \\ \\  ((___/ / //     | | ((____/ / //     \\ \\  "};
        for (String p : presentation) {
            System.out.println(p);
            try {
                sleep(400);
            } catch (InterruptedException ex) {
                Logger.getLogger(D4mnAsciiBlackjack.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        try {
            sleep(500);
        } catch (InterruptedException ex) {
            Logger.getLogger(D4mnAsciiBlackjack.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("welcome to D4MN-ASCII-BL4CKJ4CK:");
        System.out.println("Players strat with " + STARTING_FICHES + " fiches.\n"
                + "They play against the dealer before they see their cards.\n"
                + "\tThe dealer pays 1:1 the winning players.\n"
                + "\tthe dealer pays 3:2 the blackjack (21).\n"
                + "They can leave betting 0 when they want to quit the game.\n"
                + "Goodluck.\n");
    }
    
    /**
     * Interface method to print the bets of the players.
     * The idea is to represent bets as a line of fiches of different values.
     * The bet is decomposed into the least number of fiches possible.
     * Bigger fiches are considered in decreasing values.
     * @param bet the integer representing the bet
     */
    public static void printBet(int bet) {
        // the bet is decomposed into maxim value fiches.
        // this variable contains for each fiches value the number of fiches with that value to make the bet
        int numeri_fiches[] = {bet/1000, (bet%1000)/100, (bet%100)/50, (bet%50)/10, (bet%10)/1};
        /* this should be the result:   ╓────╖
                                        ╣1000╠
                                        ╙────╜ */
        // print the stack of fiches:
        // first line of each fiche:
        for (int val = 0; val<FICHES_RAPRESENTATION.length; val++) {
            for (int f = 0; f < numeri_fiches[val]; f++) {
                System.out.print("╓");
                for (int i = 0; i < FICHES_RAPRESENTATION[val].length(); i++) {
                    System.out.print("─");
                }
                System.out.print("╖");
            }
        }
        System.out.println();
        // central part of each fiche:
        for (int val = 0; val<FICHES_RAPRESENTATION.length; val++) {
            for (int f = 0; f < numeri_fiches[val]; f++) {
                System.out.print("╣");
                System.out.print(FICHES_RAPRESENTATION[val]);
                System.out.print("╠");
            }
        }
        System.out.println();
        // last line of each fiche:
        for (int val = 0; val<FICHES_RAPRESENTATION.length; val++) {
            for (int f = 0; f < numeri_fiches[val]; f++) {
                System.out.print("╙");
                for (int i = 0; i < FICHES_RAPRESENTATION[val].length(); i++) {
                    System.out.print("─");
                }
                System.out.print("╜");
            }
        }
        System.out.println();
    }
    
    /**
     * Interface method to print a series of cards.
     * This is a general method. Allows to print any number of cards flipped up or down.
     * @param cards the int array with the cards to print
     * @param visible the boolean array with the side of the card to print: true if the card is visible.
     */
    public static void printHand(int[] cards, boolean[] visible) {
        // to each card is assigned a string rapresentig the value:
        String numeriCarte[] = new String[cards.length];
        // to each card is assigned a char rapresentig the seed:
        char semiCarte[] = new char[cards.length];
        
        // assigning the value:
        for (int carta = 0; carta < cards.length; carta++) {
            switch (cards[carta] % 13) {
                case 0: // ace
                    numeriCarte[carta] = " A";
                    break;
                case 9: // ten
                    numeriCarte[carta] = "10";
                    break;
                case 10: // jack
                    numeriCarte[carta] = " J";
                    break;
                case 11: // queen
                    numeriCarte[carta] = " Q";
                    break;
                case 12: // king
                    numeriCarte[carta] = " K";
                    break;
                default: // other values:
                    numeriCarte[carta] = " " + Character.forDigit(cards[carta] % 13 + 1, 10);
                    break;
            }
            
            // assigning the seed
            // order convention: spades - hearts - diamonds - clubs - spades again...
            switch ((cards[carta]%52) / 13) {
                case 0: 
                    semiCarte[carta] = SPADES;
                    break;
                case 1:
                    semiCarte[carta] = HEARTS;
                    break;
                case 2:
                    semiCarte[carta] = DIAMONDS;
                    break;
                case 3:
                    semiCarte[carta] = CLUBS;
                    break;
                default:
                    break;
            }
        }

        // PRINT CARDS:
        
        // parte superiore delle carte
        for (int i = 0; i < numeriCarte.length; i++) {
            System.out.print("┌─────");
        }
        System.out.println("───┐");
        
        
        // parte centrale delle carte
        for (int i = 0; i < numeriCarte.length; i++) {
            System.out.print(visible[i] ? "│" + numeriCarte[i] + " " + semiCarte[i] + " " : "│▓▓▓▓▓");
        }
        System.out.println(visible[visible.length-1] ? "· ·│" : "▓▓▓│");
        for (int line = 0; line < 3; line++) {
            for (int i = 0; i < numeriCarte.length; i++) {
                System.out.print(visible[i] ? "│ · · " : "│▓▓▓▓▓");
            }
            System.out.println(visible[visible.length-1] ? "· ·│" : "▓▓▓│");
        }
        for (int i = 0; i < numeriCarte.length-1; i++) {
            System.out.print(visible[i] ? "│ · · " : "│▓▓▓▓▓");
        }
        System.out.println(visible[visible.length-1] ? "│ · ·"+ numeriCarte[visible.length-1]+ " " + semiCarte[visible.length-1] + "│" : "│▓▓▓▓▓▓▓▓│");

        
        // parte inferiore delle carte
        for (int i = 0; i < numeriCarte.length; i++) {
            System.out.print("└─────");
        }
        System.out.println("───┘");
    }
    
    /**
     * Interface method to print the match status.
     * When the cards have been distributed to each player and dealer, this method shows the situation.
     * For each player print the bet and the received cards.
     * For the dealer shows the visible card and the other flipped.
     * @param bets the collection of bets of the players
     * @param playersTwoCards the int matrix containing first two cards for each player
     * @param dealerTwoCards the int array of two cards for the dealer
     */
    private static void printMatchStatus(int[] bets, int[][] playersTwoCards, int[] dealerTwoCards) {
        for (int gioc = 0; gioc < players.length; gioc++) {
            if (playing[gioc]) {
                System.out.println("================================================");
                System.out.println("\t" + players[gioc] + ":");
                System.out.println("================================================");
                printBet(bets[gioc]);
                boolean[] scoperte = {true, true};
                printHand(playersTwoCards[gioc], scoperte);
            }
            try {
                sleep(1500);
            } catch (InterruptedException ex) {
                Logger.getLogger(D4mnAsciiBlackjack.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        System.out.println("================================================");
        System.out.println("\tDealer:");
        System.out.println("================================================");
        boolean[] scoperteBanco = {true, false};
        printHand(dealerTwoCards, scoperteBanco);
    }
    /**
     * Interface method for the all-in situation.
     * Prints an ascii art meaning the player bets all of his money.
     */
    private static void allin() {
        // TODO - interface for the allin+
        System.out.println("ALL IN, good man.");
    }
    
    /**
     * Interface method for the blackjack situation.
     * Prints an ascii art meaning the player has a blackjack.
     */
    private static void blackjack() {
        System.out.println("BLACKJACK BAAAAAABY");
        System.out.println(""
                + "\t██╗    ██████╗  ██╗    ██╗\n"
                + "\t██║    ╚════██╗███║    ██║\n"
                + "\t██║     █████╔╝╚██║    ██║\n"
                + "\t╚═╝    ██╔═══╝  ██║    ╚═╝\n"
                + "\t██╗    ███████╗ ██║    ██╗\n"
                + "\t╚═╝    ╚══════╝ ╚═╝    ╚═╝\n");

    }
}
