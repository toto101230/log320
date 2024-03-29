/*author : Thomas PIENNE, Romain Vermande,*/

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;


class Client {

    private static final String LETTRES = "ABCDEFGH";

    static int numJoueur;
    static int numJoueurAdverse;
    static int profondeur = 3;
    static int waitTurn = 0;

    static int[][] coutPos = {
            {-80, -25, -20, -20, -20, -20, -25, -80},
            {-25, 10, 10, 10, 10, 10, 10, -25},
            {-20, 10, 25, 25, 25, 25, 10, -20},
            {-20, 10, 25, 50, 50, 25, 10, -20},
            {-20, 10, 25, 50, 50, 25, 10, -20},
            {-20, 10, 25, 25, 25, 25, 10, -20},
            {-25, 10, 10, 10, 10, 10, 10, -25},
            {-80, -25, -20, -20, -20, -20, -25, -80}};


    public static void main(String[] args) {

        if (args.length != 1) {
            System.out.println("Usage : Client.jar <adresse>");
            System.exit(1);
        }

        System.out.println(args[0]);

        Socket myClient;
        BufferedInputStream input;
        BufferedOutputStream output;
        int[][] board = new int[8][8];

        try {
            myClient = new Socket(args[0], 8888);

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
                    numJoueurAdverse = 2;
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
                    numJoueurAdverse = 4;
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
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println(e);
        }

    }

    private static void majPlateau(int[][] board, String coup) {
        int[] move = getPosition(coup);
        if (move[0] == 8) {
            return;
        }
        int nb = board[move[0]][8 - move[1]];
        board[move[0]][8 - move[1]] = 0;
        board[move[2]][8 - move[3]] = nb;
    }

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
                return LETTRES.indexOf(c);
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
        ArrayList<String> coups = generateAllCoups(board, numJoueur);
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;
        int score = Integer.MIN_VALUE;
        long startTime = System.currentTimeMillis();
        ArrayList<String> bestCoups = new ArrayList<>();

        int ancienCase, newScore;
        for (String coup : coups) {
            ancienCase = simulerCoup(board, coup);
            newScore = alphaBeta(copieDeBoard(board), alpha, beta, 0, true, startTime);
            annulerCoup(board, coup, ancienCase);
            if (newScore > score) {
                score = newScore;
                bestCoups.clear();
                bestCoups.add(coup);
            } else if (newScore == score) {
                bestCoups.add(coup);
            }
        }
        long endTime = System.currentTimeMillis();
        if (endTime - startTime > 4500) {
            profondeur--;
            waitTurn = 1;
        } else if (endTime - startTime < 3000 && waitTurn == 0) {
            profondeur++;
        }
        if (waitTurn > 0) {
            waitTurn--;
        }
//        System.out.println(score);

        if(bestCoups.size() == 0){
            return coups.get(0);
        }

        String bestCoup = bestCoups.remove(0);
        if (bestCoups.size() > 0) {
            score = -10000;
            int scoreTemp;
            for (String coup : bestCoups) {
                if (System.currentTimeMillis() - startTime > 4900) {
                    break;
                }
                ancienCase = simulerCoup(board, coup);
                scoreTemp = pieceSquare(board);
                if (scoreTemp > score) {
                    score = scoreTemp;
                    bestCoup = coup;
                } else if (scoreTemp == score) {
                    if (Math.random() > 0.5) {
                        bestCoup = coup;
                    }
                }
                annulerCoup(board, coup, ancienCase);
            }
        }
        return bestCoup;
    }

    private static int simulerCoup(int[][] board, String coup) {
        int[] move = getPosition(coup);
        int ancienCase = board[move[2]][8 - move[3]];
        board[move[2]][8 - move[3]] = board[move[0]][8 - move[1]];
        board[move[0]][8 - move[1]] = 0;
        return ancienCase;
    }

    private static void annulerCoup(int[][] board, String coup, int ancienCase) {
        int[] move = getPosition(coup);
        int nb = board[move[2]][8 - move[3]];
        board[move[2]][8 - move[3]] = ancienCase;
        board[move[0]][8 - move[1]] = nb;
    }

    private static int[][] copieDeBoard(int[][] board) {
        int[][] newBoard = new int[8][8];
        for (int i = 0; i < 8; i++) {
            System.arraycopy(board[i], 0, newBoard[i], 0, 8);
        }
        return newBoard;
    }

    //Speudo code venant de wikipedia
    private static int alphaBeta(int[][] board, int alpha, int beta, int i, boolean b, long startTime) {
        int gagne = evaluation(board, i, false);
        if (gagne == 10000 || gagne == -10000) {
            return gagne;
        } else if (i == profondeur || System.currentTimeMillis() - startTime > 4500) {
            return evaluation(board, i, true);
        } else {
            int score;
            if (b) {
                score = Integer.MAX_VALUE;
                ArrayList<String> coups = generateAllCoups(board, numJoueurAdverse);
                for (String coup : coups) {
                    int ancienCase = simulerCoup(board, coup);
                    int val = alphaBeta(copieDeBoard(board), alpha, beta, i + 1, false, startTime);
                    annulerCoup(board, coup, ancienCase);
                    if (score > val) {
                        score = val;
                        beta = Math.min(beta, score);
                        if (alpha >= beta) {
                            return score;
                        }
                    }
                }
            } else {
                score = Integer.MIN_VALUE;
                ArrayList<String> coups = generateAllCoups(board, numJoueur);
                for (String coup : coups) {
                    int ancienCase = simulerCoup(board, coup);
                    int val = alphaBeta(copieDeBoard(board), alpha, beta, i + 1, true, startTime);
                    annulerCoup(board, coup, ancienCase);
                    if (score < val) {
                        score = val;
                        alpha = Math.max(alpha, score);
                        if (alpha >= beta) {
                            return score;
                        }
                    }
                }
            }
            return score;
        }
    }

    private static ArrayList<String> generateAllCoups(int[][] board, int joueur) {
        ArrayList<String> coups = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[j][i] == joueur) {
                    coups.addAll(generateCoups(board, j, 7 - i, joueur));
                }
            }
        }
        return coups;

    }

    private static ArrayList<String> generateCoups(int[][] board, int j, int i, int joueur) {
        ArrayList<String> coups = new ArrayList<>();
        int nbPiece;
        for (int k = 0; k < 4; k++) {
            nbPiece = calculeNbPieces(board, j, i, k);
            coups.addAll(generateCoup(board, j, 7 - i, k, nbPiece, joueur));
            coups.addAll(generateCoup(board, j, 7 - i, k + 4, nbPiece, joueur));
        }
        return coups;
    }

    public static int evaluation(int[][] board, int deep, boolean b) {
        double eval = 0;
        ArrayList<ArrayList<int[]>> valeurjoueur = new ArrayList<>();
        ArrayList<ArrayList<int[]>> valeuradversaire = new ArrayList<>();

        ArrayList<int[]> joueur = new ArrayList<>();
        ArrayList<int[]> joueuradverse = new ArrayList<>();

        ArrayList<int[]> visitejoueur = new ArrayList<>(); //noeud deja visite par joueur
        ArrayList<int[]> visitejoueuradverse = new ArrayList<>(); //noeud deja visite par joueur adverse

        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j] != 0) {
                    if (board[i][j] == numJoueur) {
                        joueur.add(new int[]{i, j});
                    }
                    if (board[i][j] != numJoueur) {
                        joueuradverse.add(new int[]{i, j});
                    }
                }

            }
        }
        for (int[] i : joueur) {
            if (!contiens(visitejoueur, i)) {
                valeurjoueur.add(trouvergroupe(i, joueur, visitejoueur));
            }
        }

        for (int[] i : joueuradverse) {
            if (!contiens(visitejoueuradverse, i)) {
                valeuradversaire.add(trouvergroupe(i, joueuradverse, visitejoueuradverse));
            }
        }

        if (valeurjoueur.size() <= 1) {
            return 10000 - deep;
        }
        if (valeuradversaire.size() <= 1) {
            return -10000 + deep;
        }
        if (!b) return 0;

        //calculer le nombre de piece du joueur
        int nbpiecejoueur = 0;
        for (ArrayList<int[]> i : valeurjoueur) {
            nbpiecejoueur += i.size();
        }
        //calculer le nombre de piece du joueur adverse
        int nbpiecejoueuradverse = 0;
        for (ArrayList<int[]> i : valeuradversaire) {
            nbpiecejoueuradverse += i.size();
        }

        eval += (nbpiecejoueur - nbpiecejoueuradverse) * 10;

        //cacul de la zone la plus grande pour le joueur
        int[] coinHauteGauche = new int[]{7, 7};
        int[] coinBasDroite = new int[]{0, 0};

        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j] == numJoueur) {
                    if (i < coinHauteGauche[0]) {
                        coinHauteGauche[0] = i;
                    }
                    if (j < coinHauteGauche[1]) {
                        coinHauteGauche[1] = j;
                    }
                    if (i > coinBasDroite[0]) {
                        coinBasDroite[0] = i;
                    }
                    if (j > coinBasDroite[1]) {
                        coinBasDroite[1] = j;
                    }
                }
            }
        }

        //calcul de la zone la plus grande pour l'adversaire
        int[] coinHauteGaucheAdverse = new int[]{7, 7};
        int[] coinBasDroiteAdverse = new int[]{0, 0};

        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j] == numJoueurAdverse) {
                    if (i < coinHauteGaucheAdverse[0]) {
                        coinHauteGaucheAdverse[0] = i;
                    }
                    if (j < coinHauteGaucheAdverse[1]) {
                        coinHauteGaucheAdverse[1] = j;
                    }
                    if (i > coinBasDroiteAdverse[0]) {
                        coinBasDroiteAdverse[0] = i;
                    }
                    if (j > coinBasDroiteAdverse[1]) {
                        coinBasDroiteAdverse[1] = j;
                    }
                }
            }
        }

        int areaJoueur = (coinBasDroite[0] - coinHauteGauche[0]) * (coinBasDroite[1] - coinHauteGauche[1]);
        int areaAdversaire = (coinBasDroiteAdverse[0] - coinHauteGaucheAdverse[0]) * (coinBasDroiteAdverse[1] - coinHauteGaucheAdverse[1]);

        eval += (areaAdversaire - areaJoueur) * 10;

        // calcul de la densité de la zone du joueur
        double densite = 0;
        double densiteMax = 0;
        for (int i = coinHauteGauche[0]; i <= coinBasDroite[0]; i++) {
            for (int j = coinHauteGauche[1]; j <= coinBasDroite[1]; j++) {
                if (board[i][j] == numJoueur) {
                    densite++;
                }
                densiteMax++;
            }
        }

        //calcul de la densité de la zone de l'adversaire
        double densiteAdverse = 0;
        double densiteMaxAdverse = 0;
        for (int i = coinHauteGaucheAdverse[0]; i <= coinBasDroiteAdverse[0]; i++) {
            for (int j = coinHauteGaucheAdverse[1]; j <= coinBasDroiteAdverse[1]; j++) {
                if (board[i][j] == numJoueurAdverse) {
                    densiteAdverse++;
                }
                densiteMaxAdverse++;
            }
        }

        int densiteJoueur = (int) (densite / densiteMax * nbpiecejoueur);
        int densiteAdversaire = (int) (densiteAdverse / densiteMaxAdverse * nbpiecejoueuradverse);

        eval += (densiteAdversaire - densiteJoueur) * 10;


        eval += pieceSquare(board) * 2;
        return (int) eval;
    }

    //Inspiré de https://github.com/abrar-fahim/lines-of-action
    private static int pieceSquare(int[][] board) {
        int totalScore = 0;

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j] == numJoueur) {
                    totalScore += coutPos[i][j];
                } else if (board[i][j] == numJoueurAdverse) {
                    totalScore -= coutPos[i][j];
                }
            }
        }

        return totalScore;
    }

    private static boolean contiens(ArrayList<int[]> visitejoueur, int[] i) {
        for (int[] ints : visitejoueur) {
            if (ints[0] == i[0] && ints[1] == i[1]) {
                return true;
            }
        }
        return false;
    }

    public static ArrayList<int[]> trouvergroupe(int[] pos, ArrayList<int[]> joueurs, ArrayList<int[]> visitejoueur) {

        ArrayList<int[]> groupe = new ArrayList<>();
        visitejoueur.add(pos);
        groupe.add(pos);

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (!contiens(visitejoueur, new int[]{pos[0] + i, pos[1] + j}) && contiens(joueurs, new int[]{pos[0] + i, pos[1] + j})) {
                    groupe.addAll(trouvergroupe(new int[]{pos[0] + i, pos[1] + j}, joueurs, visitejoueur));
                }
            }
        }

        return groupe;
    }

    private static ArrayList<String> generateCoup(int[][] board, int j, int i, int k, int nbPiece, int joueur) {
        ArrayList<String> coups = new ArrayList<>();
        boolean isPossible = true;
        switch (k) {
            case 0://OUEST
                if (j - nbPiece < 0 || board[j - nbPiece][i] == joueur) {
                    break;
                }
                for (int l = 1; l <= nbPiece; l++) {
                    if (l < nbPiece && board[j - l][i] != 0 && board[j - l][i] != joueur) {
                        isPossible = false;
                        break;
                    }
                }
                if (isPossible) {
                    coups.add(LETTRES.charAt(j) + "" + (8 - i) + "-" + LETTRES.charAt(j - nbPiece) + "" + (8 - i));
                }
                break;
            case 4: //EST
                if (j + nbPiece > 7 || board[j + nbPiece][i] == joueur) {
                    break;
                }
                for (int l = 1; l <= nbPiece; l++) {
                    if (l < nbPiece && board[j + l][i] != 0 && board[j + l][i] != joueur) {
                        isPossible = false;
                        break;
                    }
                }
                if (isPossible) {
                    coups.add(LETTRES.charAt(j) + "" + (8 - i) + "-" + LETTRES.charAt(j + nbPiece) + "" + (8 - i));
                }
                break;
            case 1: //SUD
                if (i + nbPiece > 7 || board[j][i + nbPiece] == joueur) {
                    break;
                }
                for (int l = 1; l <= nbPiece; l++) {
                    if (l < nbPiece && board[j][i + l] != 0 && board[j][i + l] != joueur) {
                        isPossible = false;
                        break;
                    }
                }
                if (isPossible) {
                    coups.add(LETTRES.charAt(j) + "" + (8 - i) + "-" + LETTRES.charAt(j) + "" + (8 - i - nbPiece));
                }
                break;
            case 5: //NORD
                if (i - nbPiece < 0 || board[j][i - nbPiece] == joueur) {
                    break;
                }
                for (int l = 1; l <= nbPiece; l++) {
                    if (l < nbPiece && board[j][i - l] != 0 && board[j][i - l] != joueur) {
                        isPossible = false;
                        break;
                    }
                }
                if (isPossible) {
                    coups.add(LETTRES.charAt(j) + "" + (8 - i) + "-" + LETTRES.charAt(j) + "" + (8 - i + nbPiece));
                }
                break;
            case 2: //SUD-OUEST
                if (j - nbPiece < 0 || i + nbPiece > 7 || board[j - nbPiece][i + nbPiece] == joueur) {
                    break;
                }
                for (int l = 1; l <= nbPiece; l++) {
                    if (l < nbPiece && board[j - l][i + l] != 0 && board[j - l][i + l] != joueur) {
                        isPossible = false;
                        break;
                    }
                }
                if (isPossible) {
                    coups.add(LETTRES.charAt(j) + "" + (8 - i) + "-" + LETTRES.charAt(j - nbPiece) + "" + (8 - i - nbPiece));
                }
                break;
            case 6: //NORD-EST
                if (j + nbPiece > 7 || i - nbPiece < 0 || board[j + nbPiece][i - nbPiece] == joueur) {
                    break;
                }
                for (int l = 1; l <= nbPiece; l++) {
                    if (l < nbPiece && board[j + l][i - l] != 0 && board[j + l][i - l] != joueur) {
                        isPossible = false;
                        break;
                    }
                }
                if (isPossible) {
                    coups.add(LETTRES.charAt(j) + "" + (8 - i) + "-" + LETTRES.charAt(j + nbPiece) + "" + (8 - i + nbPiece));
                }
                break;
            case 3: //NORD-OUEST
                if (j - nbPiece < 0 || i - nbPiece < 0 || board[j - nbPiece][i - nbPiece] == joueur) {
                    break;
                }
                for (int l = 1; l <= nbPiece; l++) {
                    if (l < nbPiece && board[j - l][i - l] != 0 && board[j - l][i - l] != joueur) {
                        isPossible = false;
                        break;
                    }
                }
                if (isPossible) {
                    coups.add(LETTRES.charAt(j) + "" + (8 - i) + "-" + LETTRES.charAt(j - nbPiece) + "" + (8 - i + nbPiece));
                }
                break;
            case 7: //SUD-EST
                if (j + nbPiece > 7 || i + nbPiece > 7 || board[j + nbPiece][i + nbPiece] == joueur) {
                    break;
                }
                for (int l = 1; l <= nbPiece; l++) {
                    if (l < nbPiece && board[j + l][i + l] != 0 && board[j + l][i + l] != joueur) {
                        isPossible = false;
                        break;
                    }
                }
                if (isPossible) {
                    coups.add(LETTRES.charAt(j) + "" + (8 - i) + "-" + LETTRES.charAt(j + nbPiece) + "" + (8 - i - nbPiece));
                }
                break;
        }
        return coups;
    }

    private static int calculeNbPieces(int[][] board, int j, int i, int k) {
        int nbPiece = 0, x, y;
        switch (k) {
            case 0: //OUEST et EST
                for (int l = 0; l < 8; l++) {
                    if (board[l][7 - i] != 0) {
                        nbPiece++;
                    }
                }
                break;
            case 1: //SUD et NORD
                for (int l = 0; l < 8; l++) {
                    if (board[j][l] != 0) {
                        nbPiece++;
                    }
                }
                break;
            case 2: //SUD-OUEST et NORD-EST
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

            case 3: //NORD-OUEST et SUD-EST
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

}