import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import static java.lang.Thread.sleep;


class Client {

    static int numJoueur;



    public static void main(String[] args) {

        Socket myClient;
        BufferedInputStream input;
        BufferedOutputStream output;
        int[][] board = new int[8][8];

        try {
            myClient = new Socket("localhost", 8888);

            input = new BufferedInputStream(myClient.getInputStream());
            output = new BufferedOutputStream(myClient.getOutputStream());
            BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                char cmd = 0;

                cmd = (char) input.read();
                System.out.println(cmd);
                // Debut de la partie en joueur blanc
                if (cmd == '1') {
                    numJoueur = 4;
                    byte[] aBuffer = new byte[1024];

                    int size = input.available();
                    //System.out.println("size " + size);
                    input.read(aBuffer, 0, size);
                    String s = new String(aBuffer).trim();
                    System.out.println(s);
                    String[] boardValues;
                    boardValues = s.split(" ");
                    int x = 0, y = 0;
                    for (String boardValue : boardValues) {
                        board[x][y] = Integer.parseInt(boardValue);
                        x++;
                        if (x == 8) {
                            x = 0;
                            y++;
                        }
                    }

                    System.out.println("Nouvelle partie! Vous jouer blanc, entrez votre premier coup : ");
                    String move = null;
                    move = getCoup(board);
                    majPlateau(board, move);
                    output.write(move.getBytes(), 0, move.length());
                    output.flush();
                }
                // Debut de la partie en joueur Noir
                if (cmd == '2') {
                    numJoueur = 2;
                    System.out.println("Nouvelle partie! Vous jouer noir, attendez le coup des blancs");
                    byte[] aBuffer = new byte[1024];

                    int size = input.available();
                    //System.out.println("size " + size);
                    input.read(aBuffer, 0, size);
                    String s = new String(aBuffer).trim();
                    System.out.println(s);
                    String[] boardValues;
                    boardValues = s.split(" ");
                    int x = 0, y = 0;
                    for (String boardValue : boardValues) {
                        board[x][y] = Integer.parseInt(boardValue);
                        x++;
                        if (x == 8) {
                            x = 0;
                            y++;
                        }
                    }
                }


                // Le serveur demande le prochain coup
                // Le message contient aussi le dernier coup joue.
                if (cmd == '3') {
                    byte[] aBuffer = new byte[16];

                    int size = input.available();
                    System.out.println("size :" + size);
                    input.read(aBuffer, 0, size);

                    String s = new String(aBuffer);
                    System.out.println("Dernier coup :" + s);
                    majPlateau(board, s);

                    System.out.println("Entrez votre coup : ");
                    String move = null;
                    move = getCoup(board);
                    majPlateau(board, move);
                    output.write(move.getBytes(), 0, move.length());
                    output.flush();

                }
                // Le dernier coup est invalide
                if (cmd == '4') {
                    System.out.println("Coup invalide, entrez un nouveau coup : ");
                    String move = null;
                    move = console.readLine();
                    output.write(move.getBytes(), 0, move.length());
                    output.flush();

                }
                // La partie est terminée
                if (cmd == '5') {
                    byte[] aBuffer = new byte[16];
                    int size = input.available();
                    input.read(aBuffer, 0, size);
                    String s = new String(aBuffer);
                    System.out.println("Partie Terminé. Le dernier coup joué est: " + s);
                    String move = null;
                    move = console.readLine();
                    output.write(move.getBytes(), 0, move.length());
                    output.flush();

                }
            }
        } catch (IOException e) {
            System.out.println(e);
        }

    }

    private static void majPlateau(int[][] board, String s) {
        int[] move = getPosition(s);
        if (move[0] == 8) {
            return;
        }
        int nb = board[move[0]][8 - move[1]];
        board[move[0]][8 - move[1]] = 0;
        board[move[2]][8 - move[3]] = nb;
    }

    static String alpha = "ABCDEFGH";

    private static int[] getPosition(String s) {
        int[] move = new int[4];
        String[] moveString = s.split("-");
        move[0] = getLettre(moveString[0]);
        move[1] = getNumber(moveString[0]);
        move[2] = getLettre(moveString[1]);
        move[3] = getNumber(moveString[1]);
        return move;
    }

    private static int getLettre(String s) {
        for (char c : s.toCharArray()) {
            if (Character.isAlphabetic(c)) {
                return alpha.indexOf(c);
            }
        }
        return 0;
    }

    private static int getNumber(String s) {
        for (char c : s.toCharArray()) {
            if (Character.isDigit(c)) {
                return Character.getNumericValue(c);
            }
        }
        return 0;
    }

    private static String getCoup(int[][] board) {
        try {
            sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        ArrayList<String> coups = generateAllCoups(board);
        String coup = coups.get((int) (Math.random() * coups.size()));
        System.out.println("Coup joué : " + coup);
        return coup;
    }

    private static ArrayList<String> generateAllCoups(int[][] board) {
        ArrayList<String> coups = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[j][i] == numJoueur) {
                    coups.addAll(generateCoups(board, j, 7 - i));
//                    System.out.println("coups : " + coups + " en  : " + alpha.charAt(j) + "" + (8 - i));
                }
            }
        }
        return coups;

    }

    private static ArrayList<String> generateCoups(int[][] board, int j, int i) {
        ArrayList<String> coups = new ArrayList<>();
        for (int k = 0; k < 8; k++) {
            int nbPiece = calculeNbPieces(board, j, i, k);
            coups.addAll(generateCoup(board, j, 7 - i, k, nbPiece));
        }
        return coups;
    }

    public int evaluation(int[][] board){
        int boardlocal[][] = new int[8][8];
        int eval=0;
        ArrayList<Integer>valeurjoueur = new ArrayList<>();
        ArrayList<Integer>valeuradversaire = new ArrayList<>();

        ArrayList<int[]>joueur = new ArrayList<>();
        ArrayList<int[]>joueuradverse = new ArrayList<>();

        ArrayList<int[]>visitejoueur = new ArrayList<>(); //noeud deja visite par joueur
        ArrayList<int[]>visitejoueuradverse = new ArrayList<>(); //noeud deja visite par joueur adverse

        for(int i=0;i<board.length;i++){
            for(int j=0;j<board[i].length;j++){
                if (board[i][j] !=0) {
                    if(board[i][j] == numJoueur){
                            joueur.add(new int[]{i, j});
                    }
                    if(board[i][j] != numJoueur){
                        joueuradverse.add(new int[]{i,j});
                    }
                }

            }
        }
        for (int[] i : joueur) {
            if(!visitejoueur.contains(i)){
                valeurjoueur.add(trouvergroupe(i,joueur,visitejoueur));
            }
        }

        for (int[] i : joueuradverse) {
            if(!visitejoueuradverse.contains(i)){
                valeuradversaire.add(trouvergroupe(i,joueuradverse,visitejoueuradverse));
            }
        }

        Collections.sort(valeurjoueur);
        int maxjoueur = valeurjoueur.get(valeurjoueur.size()-1);

        Collections.sort(valeuradversaire);
        int maxadversaire = valeuradversaire.get(valeuradversaire.size()-1);

        if(maxjoueur > maxadversaire){
            eval = ((maxjoueur)/(valeurjoueur.size()+valeuradversaire.size()))*100;
        }
        else if(maxadversaire > maxjoueur){
            eval = ((maxadversaire)/(valeurjoueur.size()+valeuradversaire.size()))*100;
        }

        return eval;
    }
    public int trouvergroupe(int[] pos,ArrayList<int[]> joueurs,ArrayList<int[]> visitejoueur ){

        int valeur=1;
        visitejoueur.add(pos);

        for(int i=-1;i<1;i++){
            for(int j=-1;j<1;j++){
                if(!visitejoueur.contains(new int[]{pos[0]+i,pos[1]+j}) && joueurs.contains(new int[]{pos[0]+i,pos[1]+j})){

                    valeur += trouvergroupe(new int[]{pos[0]+i,pos[1]+j},joueurs,visitejoueur);
                }

            }
        }

        return valeur;
    }

    public boolean estGroupe(ArrayList<int[]> pieces){
        double somme = distancepiece(pieces);
        boolean groupe = false;

        if(somme == 0){
            groupe = true;
        }
        else{
            groupe = false;
        }

        return groupe;
    }

    public double distancepiece(ArrayList<int[]> pieces){
        double somme = 0;
        double x;
        double y;
        for(int i=0;i<pieces.size();i++){
            for(int j=0; j<i;j++){
                somme = calculdistance(pieces.get(i),pieces.get(j));
            }
        }
        return somme;
    }

    public double calculdistance(int[] p1,int[] p2){
        int x = (int) Math.pow(p1[0]-p2[0],2);
        int y = (int) Math.pow(p1[1]-p2[1],2);
        return Math.sqrt(x+y);
    }

    private static ArrayList<String> generateCoup(int[][] board, int j, int i, int k, int nbPiece) {
        ArrayList<String> coups = new ArrayList<>();
        boolean isPossible = true;
        switch (k) {
            case 0://OUEST
                if(j - nbPiece < 0 || board[j - nbPiece][i] == numJoueur){
                    break;
                }
                for (int l = 1; l <= nbPiece; l++) {
                    if (l < nbPiece && board[j - l][i] != 0 && board[j - l][i] != numJoueur) {
                        isPossible = false;
                        break;
                    }
                }
                if (isPossible) {
                    coups.add(alpha.charAt(j) + "" + (8 - i) + "-" + alpha.charAt(j - nbPiece) + "" + (8 - i));
                }
                break;
            case 4: //EST
                if(j + nbPiece > 7 || board[j + nbPiece][i] == numJoueur){
                    break;
                }
                for (int l = 1; l <= nbPiece; l++) {
                    if (l < nbPiece && board[j + l][i] != 0 && board[j + l][i] != numJoueur) {
                        isPossible = false;
                        break;
                    }
                }
                if (isPossible) {
                    coups.add(alpha.charAt(j) + "" + (8 - i) + "-" + alpha.charAt(j + nbPiece) + "" + (8 - i));
                }
                break;
            case 1: //SUD
                if(i + nbPiece > 7 || board[j][i + nbPiece] == numJoueur){
                    break;
                }
                for (int l = 1; l <= nbPiece; l++) {
                    if (l < nbPiece && board[j][i + l] != 0 && board[j][i + l] != numJoueur) {
                        isPossible = false;
                        break;
                    }
                }
                if (isPossible) {
                    coups.add(alpha.charAt(j) + "" + (8 - i) + "-" + alpha.charAt(j) + "" + (8 - i - nbPiece));
                }
                break;
            case 5: //NORD
                if(i - nbPiece < 0 || board[j][i - nbPiece] == numJoueur){
                    break;
                }
                for (int l = 1; l <= nbPiece; l++) {
                    if (l < nbPiece && board[j][i - l] != 0 && board[j][i - l] != numJoueur) {
                        isPossible = false;
                        break;
                    }
                }
                if (isPossible) {
                    coups.add(alpha.charAt(j) + "" + (8 - i) + "-" + alpha.charAt(j) + "" + (8 - i + nbPiece));
                }
                break;
            case 2: //SUD-OUEST
                if(j - nbPiece < 0 || i + nbPiece > 7 || board[j - nbPiece][i + nbPiece] == numJoueur){
                    break;
                }
                for (int l = 1; l <= nbPiece; l++) {
                    if (l < nbPiece && board[j - l][i + l] != 0 && board[j - l][i + l] != numJoueur) {
                        isPossible = false;
                        break;
                    }
                }
                if (isPossible) {
                    coups.add(alpha.charAt(j) + "" + (8 - i) + "-" + alpha.charAt(j - nbPiece) + "" + (8 - i - nbPiece));
                }
                break;
            case 6: //NORD-EST
                if(j + nbPiece > 7 || i - nbPiece < 0 || board[j + nbPiece][i - nbPiece] == numJoueur){
                    break;
                }
                for (int l = 1; l <= nbPiece; l++) {
                    if (l < nbPiece && board[j + l][i - l] != 0 && board[j + l][i - l] != numJoueur) {
                        isPossible = false;
                        break;
                    }
                }
                if (isPossible) {
                    coups.add(alpha.charAt(j) + "" + (8 - i) + "-" + alpha.charAt(j + nbPiece) + "" + (8 - i + nbPiece));
                }
                break;
            case 3: //NORD-OUEST
                if(j - nbPiece < 0 || i - nbPiece < 0 || board[j - nbPiece][i - nbPiece] == numJoueur){
                    break;
                }
                for (int l = 1; l <= nbPiece; l++) {
                    if (l < nbPiece && board[j - l][i - l] != 0 && board[j - l][i - l] != numJoueur) {
                        isPossible = false;
                        break;
                    }
                }
                if (isPossible) {
                    coups.add(alpha.charAt(j) + "" + (8 - i) + "-" + alpha.charAt(j - nbPiece) + "" + (8 - i + nbPiece));
                }
                break;
            case 7: //SUD-EST
                if(j + nbPiece > 7 || i + nbPiece > 7 || board[j + nbPiece][i + nbPiece] == numJoueur){
                    break;
                }
                for (int l = 1; l <= nbPiece; l++) {
                    if (l < nbPiece && board[j + l][i + l] != 0 && board[j + l][i + l] != numJoueur) {
                        isPossible = false;
                        break;
                    }
                }
                if (isPossible) {
                    coups.add(alpha.charAt(j) + "" + (8 - i) + "-" + alpha.charAt(j + nbPiece) + "" + (8 - i - nbPiece));
                }
                break;
        }
        return coups;
    }

    private static int calculeNbPieces(int[][] board, int j, int i, int k) {
        int nbPiece = 0, x, y;
        switch (k) {
            case 0:
            case 4:
                for (int l = 0; l < 8; l++) {
                    if (board[l][7 - i] != 0) {
                        nbPiece++;
                    }
                }
                break;
            case 1:
            case 5:
                for (int l = 0; l < 8; l++) {
                    if (board[j][l] != 0) {
                        nbPiece++;
                    }
                }
                break;
            case 2:
            case 6:
                x = j;
                y = i;
                while (x >= 0 && y >= 0) {
                    if (board[x][7 - y] != 0) {
                        nbPiece++;
                    }
                    x--;
                    y--;
                }
                x = j + 1;
                y = i + 1;
                while (x < 8 && y < 8) {
                    if (board[x][7 - y] != 0) {
                        nbPiece++;
                    }
                    x++;
                    y++;
                }
                break;

            case 3:
            case 7:
                x = j;
                y = i;
                while (x < 8 && y >= 0) {
                    if (board[x][7 - y] != 0) {
                        nbPiece++;
                    }
                    x++;
                    y--;
                }
                x = j - 1;
                y = i + 1;
                while (x >= 0 && y < 8) {
                    if (board[x][7 - y] != 0) {
                        nbPiece++;
                    }
                    x--;
                    y++;
                }
                break;
        }
        return nbPiece;
    }

    public static void afficherBoard(int[][] board) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                System.out.print(board[j][i] + " ");
            }
            System.out.println();
        }
    }

}