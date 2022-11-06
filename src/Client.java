import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

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
            while (1 == 1) {
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

                    for (int i = 0; i < 8; i++) {
                        for (int j = 0; j < 8; j++) {
                            System.out.print(board[j][i] + " ");
                        }
                        System.out.println();
                    }

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
            coups.addAll(generateCoup(board, j, 7-i, k, nbPiece));
        }
        return coups;
    }

    private static ArrayList<String> generateCoup(int[][] board, int j, int i, int k, int nbPiece) {
        ArrayList<String> coups = new ArrayList<>();
        boolean isPossible = true;
        switch (k) {
            case 0://OUEST
                for (int l = 1; l <= nbPiece; l++) {
                    if (j - l < 0) {
                        isPossible = false;
                        break;
                    }
                    if (l < nbPiece && board[j - l][i] != 0 && board[j - l][i] != numJoueur) {
                        isPossible = false;
                        break;
                    }
                    if (l == nbPiece && board[j - l][i] == numJoueur) {
                        isPossible = false;
                        break;
                    }
                }
                if (isPossible) {
                    coups.add(alpha.charAt(j) + "" + (8-i) + "-" + alpha.charAt(j - nbPiece) + "" + (8-i));
                }
                break;
            case 4 : //EST
                for (int l = 1; l <= nbPiece; l++) {
                    if (j + l > 7) {
                        isPossible = false;
                        break;
                    }
                    if (l < nbPiece && board[j + l][i] != 0 && board[j + l][i] != numJoueur) {
                        isPossible = false;
                        break;
                    }
                    if (l == nbPiece && board[j + l][i] == numJoueur) {
                        isPossible = false;
                        break;
                    }
                }
                if (isPossible) {
                    coups.add(alpha.charAt(j) + "" + (8-i) + "-" + alpha.charAt(j + nbPiece) + "" + (8-i));
                }
                break;
            case 1 : //SUD
                for (int l = 1; l <= nbPiece; l++) {
                    if (i + l > 7) {
                        isPossible = false;
                        break;
                    }
                    if (l < nbPiece && board[j][i + l] != 0 && board[j][i + l] != numJoueur) {
                        isPossible = false;
                        break;
                    }
                    if (l == nbPiece && board[j][i + l] == numJoueur) {
                        isPossible = false;
                        break;
                    }
                }
                if (isPossible) {
                    coups.add(alpha.charAt(j) + "" + (8-i) + "-" + alpha.charAt(j) + "" + (8-i - nbPiece));
                }
                break;
            case 5 : //NORD
                for (int l = 1; l <= nbPiece; l++) {
                    if (i - l < 0) {
                        isPossible = false;
                        break;
                    }
                    if (l < nbPiece && board[j][i - l] != 0 && board[j][i - l] != numJoueur) {
                        isPossible = false;
                        break;
                    }
                    if (l == nbPiece && board[j][i - l] == numJoueur) {
                        isPossible = false;
                        break;
                    }
                }
                if (isPossible) {
                    coups.add(alpha.charAt(j) + "" + (8-i) + "-" + alpha.charAt(j) + "" + (8-i + nbPiece));
                }
                break;

        }
        return coups;
    }

    private static int calculeNbPieces(int[][] board, int j, int i, int k) {
        int nbPiece = 0;
        switch (k) {
            case 0:
            case 4:
                for (int l = 0; l < 8; l++) {
                    if (board[l][7 - i] != 0) {
                        nbPiece++;
//                        System.out.println("nbPiece : " + board[l][i] + " l : " + l + " i : " + i);
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
//            case 2:
//            case 6:
//                for (int l = 0; l < 8; l++) {
//                    if (board[i][i] != 0) {
//                        nbPiece++;
//                    }
//                }
//                break;
//            case 3:
//            case 7:
//                for (int l = 0; l < 8; l++) {
//                    if (board[i][7 - i] != 0) {
//                        nbPiece++;
//                    }
//                }
//                break;
        }
        return nbPiece;
    }


}
