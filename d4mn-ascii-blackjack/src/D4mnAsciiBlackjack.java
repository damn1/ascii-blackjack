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

    //Variabili globali costanti per i semi delle carte:
    static final char PICCHE = '♠';
    static final char CUORI = '♥';
    static final char QUADRI = '♦';
    static final char FIORI = '♣';
    //Variabili globali costanti per rappresentare le fiches:
    static final String RAPP_FICHES[] = {"1000","100","50","10","1"};
    static final int VALORI_FICHES[] = {1000,100,50,10,1};
    // variabili per le mosse utente:
    static final char HIT = 'c';
    static final char STARE = 's';
    
    
    
    // due mazzi da 52 carte. le carte sono rappresentate da interi che vanno da 0 a 51.
    static int[] mazzo = new int[104];
    // variabile che tiene a mente quante carte del mazzo sono state utilizzate
    static int cartePescate;
    
    // array contenente i nomi dei giocatori:
    static String[] giocatori;
    // array contenente le fiches dei giocatori:
    static int[] fiches;
    // arrau contenente l'informazione binaria su ogni giocatore, se è in partita o è uscito.
    static boolean[] playing;
    
    // numero di fiches che i giocatori hanno all'inizio della partita.
    static final int STARTING_FICHES = 1000;
    
    // Scanner da poter utilizzare in tutti i punti del programma.
    static Scanner scanner = new Scanner(System.in);
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        presentazione();
        initMazzo();
        shuffle();
        initPartita();
        
        boolean fine = controllaFine();
        
        while (!fine) {
            if (cartePescate >= mazzo.length/2) {
                // mischia mischia 
                shuffle();
                shuffle();
                shuffle();
            }
            boolean scoperte[] = new boolean[mazzo.length];
            for (int i = 0; i < mazzo.length; i++) scoperte[i] = true;
            stampaCarteConsecutive(mazzo, scoperte);
            giocaMano();
            fine = controllaFine();
        }
        
        fine();
    }

    private static void fine() {
        System.out.println("\nFine della partita.");
    }
    private static void giocaMano() {
        int puntate[] = betting();
        if (!controllaFine()) {
        // una matrice di due carte per ogni giocatore.
        int primeCarteGiocatori[][] = new int[giocatori.length][2];
        // un vettore di due carte per il banco.
        int primeCarteBanco[] = new int[2];
        distribuisciPrimeCarte(primeCarteGiocatori, primeCarteBanco);
        stampaStatoPartita(puntate, primeCarteGiocatori, primeCarteBanco);
        
        //variabile per contenere la somma finale delle carte dei giocatori.
        int punteggiGiocatori[] = new int[giocatori.length];
        // variabile per sapere se i giocatori hanno fatto black jack.
        boolean blackjacks[] = new boolean[giocatori.length];
        for (int giocatore = 0; giocatore < giocatori.length; giocatore++) {
            if (playing[giocatore]) {
                punteggiGiocatori[giocatore] = manoSingoloGiocatore(giocatore, primeCarteGiocatori[giocatore], blackjacks);
                if (punteggiGiocatori[giocatore] > 21) {
                        System.out.println(giocatori[giocatore]+" SBALLA.");
                }
            } else {
                punteggiGiocatori[giocatore] = -1;
            }
        }
        // dopo che hanno giocato tutti i giocatori, gioca il banco:
        boolean blackjackBanco[] = new boolean[1];
        int punteggioBanco = manoBanco(primeCarteBanco, blackjackBanco);
        // finita la mano si decide chi ha vinto, perso o pareggiato.
        controllaVittorie(puntate, blackjacks, punteggiGiocatori, blackjackBanco[0], punteggioBanco);
        }
    }

    private static void controllaVittorie(int scommesse[], boolean [] blackjacks, int [] punteggiGiocatori, boolean blackjackBanco, int punteggioBanco) {
        for (int giocatore = 0; giocatore < punteggiGiocatori.length; giocatore++) {
            if (playing[giocatore]) {
                if (blackjacks[giocatore]) {
                    if (blackjackBanco) {
                        System.out.println(giocatori[giocatore] + " pareggia con il banco. Recupera la puntata " + scommesse[giocatore] +".");
                        fiches[giocatore] += scommesse[giocatore];
                    } else {
                        System.out.println(giocatori[giocatore] + " blackjack! Punta " + scommesse[giocatore] + ", pagato 3a2 "+ scommesse[giocatore]*3/2 +".");
                        fiches[giocatore] += scommesse[giocatore]*5/2;
                    }
                } else {
                    if (!(punteggioBanco > 21)) {
                        // se il banco non sballa:
                        // nel caso in cui il giocatore non abbia fatto blackjack:
                        if (punteggiGiocatori[giocatore] > 21) {
                            // il giocatore ha sballato
                            System.out.println(giocatori[giocatore] + " sballa. Perde la puntata " + scommesse[giocatore] + ".");
                        } else if (punteggiGiocatori[giocatore] > punteggioBanco) {
                            System.out.println(giocatori[giocatore] + " batte il banco. Punta " + scommesse[giocatore] + ", pagato 1a1 " + scommesse[giocatore]*2 +".");
                            fiches[giocatore] += scommesse[giocatore]*2;
                        } else if (punteggiGiocatori[giocatore] == punteggioBanco) {
                            System.out.println(giocatori[giocatore] + " pareggia con il banco. Recupera la puntata " + scommesse[giocatore]+".");
                            fiches[giocatore] += scommesse[giocatore];
                        } else {
                            System.out.println(giocatori[giocatore] + " perde con il banco. Perde la puntata " + scommesse[giocatore]+".");
                        }
                    } else {
                        // se il banco sballa:
                        System.out.println("Il banco sballa. "+ giocatori[giocatore]+ " batte il banco. Punta " + scommesse[giocatore] + ", pagato 1a1 " + scommesse[giocatore]*2 +".");
                        fiches[giocatore] += scommesse[giocatore]*2;
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
    
    private static int manoBanco(int primeCarteBanco[], boolean blackjackBanco[]) {
        System.out.println("================================================");
        System.out.println("================================================");
        System.out.println("================================================");
        int carteBanco[] = primeCarteBanco;
        boolean scoperte[] = new boolean[carteBanco.length];
        for (int i = 0; i < scoperte.length; i++) {
            scoperte[i] = true;
        }
        int sommaFinale = sommaCarte(carteBanco);
        System.out.println("BANCO, SUM: " + sommaFinale);
        stampaCarteConsecutive(carteBanco, scoperte);
        if (sommaFinale == 21) {
            blackjackBanco[0] = true;
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
            int nuoveCarteBanco[] = new int[carteBanco.length + 1];
            for (int carta = 0; carta < carteBanco.length; carta++) {
                nuoveCarteBanco[carta] = carteBanco[carta];
            }
            nuoveCarteBanco[nuoveCarteBanco.length - 1] = nextCarta();
            carteBanco = nuoveCarteBanco;
            sommaFinale = sommaCarte(carteBanco);
            scoperte = new boolean[carteBanco.length];
            for (int i = 0; i < scoperte.length; i++) {
                scoperte[i] = true;
            }
            System.out.println("BANCO, SUM: " + sommaFinale);
            stampaCarteConsecutive(carteBanco, scoperte);
            stop = sommaFinale >= 17;
        }

        return sommaFinale;
    }
    
    
    private static int manoSingoloGiocatore(int numeroGiocatore, int primeCarteGiocatore[], boolean blackjacks[]) {
        System.out.println("******************************************");
        int carteGiocatore[] = primeCarteGiocatore;
        boolean scoperte[] = new boolean[carteGiocatore.length];
        for (int i = 0; i<scoperte.length; i++) {
            scoperte[i] = true;
        }
        int sommaFinale = sommaCarte(carteGiocatore);
        System.out.println("PLAYER " + giocatori[numeroGiocatore] + ", SUM: " + sommaFinale);
        stampaCarteConsecutive(carteGiocatore, scoperte);
        
        if (sommaFinale == 21) {
            blackjacks[numeroGiocatore] = true;
            blackjack();
            return sommaFinale;
        }
        
        boolean stop = false;
        while (!stop) {
            System.out.print("HIT ("+HIT+") or STARE ("+STARE+")? ");
            char mossa;
            do {
                mossa = scanner.next().charAt(0);
            } while (mossa != HIT && mossa != STARE);
            stop = (mossa == STARE);
            if (!stop) {
                int nuoveCarteGiocatore[] = new int[carteGiocatore.length +1];
                for (int carta = 0; carta < carteGiocatore.length; carta++) {
                    nuoveCarteGiocatore[carta] = carteGiocatore[carta];
                }
                nuoveCarteGiocatore[nuoveCarteGiocatore.length-1] = nextCarta();
                carteGiocatore = nuoveCarteGiocatore;
                sommaFinale = sommaCarte(carteGiocatore);
                stop = sommaFinale >= 21;
            }
            
            scoperte = new boolean[carteGiocatore.length];
            for (int i = 0; i < scoperte.length; i++) {
                scoperte[i] = true;
            }
            if (!(mossa == STARE)) {
                System.out.println("PLAYER " + giocatori[numeroGiocatore] + ", SUM: " + sommaFinale);
                stampaCarteConsecutive(carteGiocatore, scoperte);
            }
        }
        return sommaFinale;
    }
    
    private static int sommaCarte(int carte[]) {
        int somma = 0;
        int acesNumber = 0;
        for (int carta : carte) {
            if (carta % 13 == 0) {
                // questa carta è un asso.
                // per ora vale uno. poi alla fine dopo aver visto tutte le carte si decide se vale 11 o no.
                acesNumber++;
                somma += 1;
            } else {
                somma += (carta % 13 > 9 ? 10 : (carta % 13 +1));
            }
        }
        for (int ace = 0; ace<acesNumber; ace++) {
            somma = (somma + 10 > 21 ? somma : somma+10);
        }
        
        return somma;
    }
    
    private static void stampaStatoPartita(int[] puntate, int[][] primeCarteGiocatori, int[] primeCarteBanco) {
        for (int gioc = 0; gioc < giocatori.length; gioc++) {
            if (playing[gioc]) {
                System.out.println("================================================");
                System.out.println("\t" + giocatori[gioc] + " - status:");
                System.out.println("================================================");
                stampaPuntata(puntate[gioc]);
                boolean[] scoperte = {true, true};
                stampaCarteConsecutive(primeCarteGiocatori[gioc], scoperte);
            }
            try {
                sleep(1500);
            } catch (InterruptedException ex) {
                Logger.getLogger(D4mnAsciiBlackjack.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        System.out.println("================================================");
        System.out.println("\tBANCO - status:");
        System.out.println("================================================");
        boolean[] scoperteBanco = {true, false};
        stampaCarteConsecutive(primeCarteBanco, scoperteBanco);
    }
    
    private static void distribuisciPrimeCarte(int perGiocatori[][], int perBanco[]) {
        // per due volte distribuisce una carta a tutti i giocatori e poi al banco.
        for (int n_carta = 0; n_carta < 2; n_carta++) {
            for (int giocatore = 0; giocatore < perGiocatori.length; giocatore++) {
                if (playing[giocatore]){
                    perGiocatori[giocatore][n_carta] = nextCarta();
                } else {
                    perGiocatori[giocatore][n_carta] = -1;
                }
            }
            perBanco[n_carta] = nextCarta();
        }
    }
    
    private static int nextCarta() {
        if (cartePescate >= mazzo.length) return -1;
        cartePescate ++;
        return mazzo[cartePescate -1];
    }
    /**
     * Metodo che raccoglie le puntate dei giocatori.
     * Mette le puntate di ogni giocatore in un vettore di interi. 
     * In ogni posizione si mette la puntata corrispondente al giocatore in quella posizione.
     * Ai giocatori ritirati corrisponde una puntata di 0.
     * @return il vettore delle puntate, grande come il vettore dei giocatori.
     */
    private static int[] betting() {
        System.out.println("      ___           ___         ___     \n     /\\  \\         /\\  \\       /\\  \\    \n    /::\\  \\       /::\\  \\      \\:\\  \\   \n   /:/\\:\\  \\     /:/\\:\\  \\      \\:\\  \\  \n  /::\\~\\:\\__\\   /::\\~\\:\\  \\     /::\\  \\ \n /:/\\:\\ \\:|__| /:/\\:\\ \\:\\__\\   /:/\\:\\__\\\n \\:\\~\\:\\/:/  / \\:\\~\\:\\ \\/__/  /:/  \\/__/\n  \\:\\ \\::/  /   \\:\\ \\/__/    /:/  /     \n   \\:\\/:/  /     \\:\\__\\     /:/  /      \n    \\::/__/       \\/__/     \\/__/");
        int puntate[] = new int[giocatori.length];
        System.out.println("Puntare 0 per ritirarsi dalla partita.");
        for (int i = 0; i < giocatori.length; i ++) {
            if (playing[i]) {
                System.out.print(giocatori[i] + ", stack "+fiches[i]+", fai la tua puntata: ");
                do {
                    puntate[i] = scanner.nextInt();
                    if (puntate[i] < 0) System.out.print("Solo puntate positive. Fai la tua puntata: ");
                    if (puntate[i] > fiches[i]) System.out.print("Non puoi puntare più di quello che hai. Fai la tua puntata: ");
                    if (puntate[i] == fiches[i]) System.out.println("ALL IN, bravo.");
                } while (puntate[i] < 0 || puntate[i] > fiches[i]);
                if (puntate[i] == 0) {
                    ritiraGiocatore(i);
                } else {
                    fiches[i] -= puntate[i];
                }
            } else {
                puntate[i] = 0;
            }
        }
        return puntate;
    }
    
    
    private static void ritiraGiocatore(int numeroGiocatore) {
        playing[numeroGiocatore] = false;
        System.out.println(giocatori[numeroGiocatore] + " si ritira con "+ fiches[numeroGiocatore] + " fiches.");
        int difference = (fiches[numeroGiocatore]-STARTING_FICHES);
        System.out.print(difference > 0? "Complimenti, chiude in attivo +": (difference < 0? "Scarso, chiude in perdita -" : "Chiude in pari."));
        System.out.println(difference != 0? Math.abs(difference) : "");
    }
    
    /**
     * Metodo per controllare quando la partita è finita.
     * @return true se non sono rimasti giocatori in gioco.
     */
    private static boolean controllaFine() {
        
        int counterInGioco = 0;
        for (boolean playerPlaying : playing) {
            if (playerPlaying) counterInGioco ++;
        }
        return !(counterInGioco > 0);
    }
    
    /**
     * Presentazione del gioco e regole.
     */
    private static void presentazione() {
                                                                                                         
        String presentation[] = {"    //   ) )  / /        // | |     //   ) )  //   //          / / // | |     //   ) )  //   // ", "   //___/ /  / /        //__| |    //        //__ //          / / //__| |    //        //__ //  ", "  / __  (   / /        / ___  |   //        //__  /          / / / ___  |   //        //__  /   ", " //    ) ) / /        //    | |  //        //   \\ \\         / / //    | |  //        //   \\ \\   ","//____/ / / /____/ / //     | | ((____/ / //     \\ \\  ((___/ / //     | | ((____/ / //     \\ \\  "};
        for (String p: presentation) {
            System.out.println(p);
            try {
                sleep(200);
            } catch (InterruptedException ex) {
                Logger.getLogger(D4mnAsciiBlackjack.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        try {
                sleep(500);
            } catch (InterruptedException ex) {
                Logger.getLogger(D4mnAsciiBlackjack.class.getName()).log(Level.SEVERE, null, ex);
            }
        System.out.println("welcome to BL4CKJ4CK:");
        System.out.println("Tutti i giocatori iniziano con "+ STARTING_FICHES + " fiches.\n"
                + "Puntano contro il banco prima di ricevere le carte.\n"
                + "\tIl banco paga 1 a 1 in caso di vittoria.\n"
                + "\tIl banco paga 3 a 2 in caso di blackjack.\n"
                + "Possono abbandonare quando vogliono.\n"
                + "Goodluck.\n");
    }
    
    /**
     * Inizializza la partita.
     * Assegna i nomi a tutti i giocatori e un numero iniziale di fiches in partenza.
     */
    private static void initPartita() {
        System.out.print("Quanti giocatori presenti? ");
        int numeroGiocatori = scanner.nextInt();
        giocatori = new String[numeroGiocatori];
        fiches = new int[numeroGiocatori];
        playing = new boolean[numeroGiocatori];
        for (int i = 0; i < numeroGiocatori; i++) {
            System.out.print("Player " + (i+1) + ", name: ");
            giocatori[i] = scanner.next();
            fiches[i] = STARTING_FICHES;
            playing[i] = true;
        }
        
    }
    
    /**
     * Inizializza il mazzo.
     * Inserisce le carte nel mazzo
     */
    public static void initMazzo() {
        // assegna le carte univoche (numeri da 0 a lunghezza mazzo -1)
        for (int i = 0; i < mazzo.length; i++) {
            mazzo[i] = i;
        }
        cartePescate = 0;
    }
    
    /** 
     * Mischia le carte nel mazzo attraverso una serie di estrazioni random e spostamenti di carte.
     */
    public static void shuffle() {
        for (int estratte = 0; estratte < mazzo.length; estratte++) {
            // estrae una posizione tra 0 e lunghezza del mazzo ancora non estratto.
            int posizioneEstratta = (int) (Math.random() * (mazzo.length - estratte));
            // sposta la carta estratta in fondo al mazzo e via così
            int provv = mazzo[mazzo.length - 1 - estratte];
            mazzo[mazzo.length - 1 - estratte] = mazzo[posizioneEstratta];
            mazzo[posizioneEstratta] = provv;
        }
    }
    
    
    
    
    
    
    
    
    
    
    
    public static void stampaPuntata(int puntata) {
        // scompongo la puntata in fiches di taglio massimo:
        // stampo le fiches in fila.
        
        int numeri_fiches[] = {puntata/1000, (puntata%1000)/100, (puntata%100)/50, (puntata%50)/10, (puntata%10)/1};
        int n_tot = 0; 
        for (int nf : numeri_fiches) {
            n_tot += nf;
        }
        
        for (int val = 0; val<RAPP_FICHES.length; val++) {
            for (int f = 0; f < numeri_fiches[val]; f++) {
                System.out.print("╓");
                for (int i = 0; i < RAPP_FICHES[val].length(); i++) {
                    System.out.print("─");
                }
                System.out.print("╖");
            }
        }
        System.out.println();
        for (int val = 0; val<RAPP_FICHES.length; val++) {
            for (int f = 0; f < numeri_fiches[val]; f++) {
                System.out.print("╣");
                System.out.print(RAPP_FICHES[val]);
                System.out.print("╠");
            }
        }
        System.out.println();
        for (int val = 0; val<RAPP_FICHES.length; val++) {
            for (int f = 0; f < numeri_fiches[val]; f++) {
                System.out.print("╙");
                for (int i = 0; i < RAPP_FICHES[val].length(); i++) {
                    System.out.print("─");
                }
                System.out.print("╜");
            }
        }
        System.out.println();
            /*"╓────╖");
              "╣1000╠");
              "╙────╜");*/
        
    }
    
    /**
     * 
     * @param carte
     * @param scoperte 
     */
    public static void stampaCarteConsecutive(int carte[], boolean scoperte[]) {
        String numeriCarte[] = new String[carte.length];
        char semiCarte[] = new char[carte.length];

        for (int carta = 0; carta < carte.length; carta++) {
            switch (carte[carta] % 13) {
                case 0:
                    numeriCarte[carta] = " A";
                    break;
                case 9:
                    numeriCarte[carta] = "10";
                    break;
                case 10:
                    numeriCarte[carta] = " J";
                    break;
                case 11:
                    numeriCarte[carta] = " Q";
                    break;
                case 12:
                    numeriCarte[carta] = " K";
                    break;
                default:
                    numeriCarte[carta] = " " + Character.forDigit(carte[carta] % 13 + 1, 10);
                    break;
            }
            switch ((carte[carta]%52) / 13) {
                case 0:
                    semiCarte[carta] = PICCHE;
                    break;
                case 1:
                    semiCarte[carta] = CUORI;
                    break;
                case 2:
                    semiCarte[carta] = QUADRI;
                    break;
                case 3:
                    semiCarte[carta] = FIORI;
                    break;
                default:
                    break;
            }
        }

        // parte superiore delle carte
        for (int i = 0; i < numeriCarte.length; i++) {
            System.out.print("┌─────");
        }
        System.out.println("───┐");
        
        
        // parte centrale delle carte
        for (int i = 0; i < numeriCarte.length; i++) {
            System.out.print(scoperte[i] ? "│" + numeriCarte[i] + " " + semiCarte[i] + " " : "│▓▓▓▓▓");
        }
        System.out.println(scoperte[scoperte.length-1] ? "· ·│" : "▓▓▓│");
        for (int line = 0; line < 3; line++) {
            for (int i = 0; i < numeriCarte.length; i++) {
                System.out.print(scoperte[i] ? "│ · · " : "│▓▓▓▓▓");
            }
            System.out.println(scoperte[scoperte.length-1] ? "· ·│" : "▓▓▓│");
        }
        for (int i = 0; i < numeriCarte.length-1; i++) {
            System.out.print(scoperte[i] ? "│ · · " : "│▓▓▓▓▓");
        }
        System.out.println(scoperte[scoperte.length-1] ? "│ · ·"+ numeriCarte[scoperte.length-1]+ " " + semiCarte[scoperte.length-1] + "│" : "│▓▓▓▓▓▓▓▓│");

        
        // parte inferiore delle carte
        for (int i = 0; i < numeriCarte.length; i++) {
            System.out.print("└─────");
        }
        System.out.println("───┘");
    }
    
    private static void allin() {
        int array[] = {1,2,3};
    }
    
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
