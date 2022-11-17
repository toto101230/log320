package laboratoire4;

import java.util.Scanner;

public class TicTacToe {

    public static void main(String[] args) {
        TicTacToe ticTacToe = new TicTacToe();
        ticTacToe.jouer();
    }

    private void jouer() {
        int debut = lireEntier("Qui commence? (1 = joueur, 2 = ordinateur)");
        int tour = 0;
        String[][] grille = new String[3][3];
        String joueur = (debut == 1) ? "X" : "O";
        boolean gagne = false;

        while (tour < 9 && !gagne) {
            afficherGrille(grille);
            System.out.println("Au tour de " + joueur);
            if (joueur.equals("X")) {
                int ligne = lireEntier("Entrez la ligne");
                int colonne = lireEntier("Entrez la colonne");
                placerJeton(grille, ligne, colonne, joueur);
            } else {
                joueurIA(grille, joueur);
            }
            gagne = gagne(grille, joueur);
            joueur = joueur.equals("X") ? "O" : "X";
            tour++;
        }

        afficherGrille(grille);
        if (gagne) {
            System.out.println("Le joueur " + (joueur.equals("X") ? "O" : "X") + " a gagné");
        } else {
            System.out.println("Match nul");
        }
    }

    private void joueurIA(String[][] grille, String joueur) {
        int ligne = 0;
        int colonne = 0;
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;
        int score = Integer.MIN_VALUE;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (grille[i][j] == null) {
                    grille[i][j] = "O";
//                    afficherGrille(grille);
                    int scoreTemp = alphaBeta(grille, "O", 0, alpha, beta, true);
                    grille[i][j] = null;
                    //todo : si score égal, choisir au hasard ou choisir le meilleur
                    if (scoreTemp > score) {
                        score = scoreTemp;
                        ligne = i;
                        colonne = j;
                    }
//                    System.out.println("Score pour " + i + ", " + j + " : " + scoreTemp);
                }
            }
        }
        placerJeton(grille, ligne, colonne, joueur);
    }

    private int alphaBeta(String[][] grille, String x, int i, int alpha, int beta, boolean b) {
//        System.out.println(i +" "+ gagne(grille, "X"));
//        afficherGrille(grille);
        if (gagne(grille, "O") && b) {
            return 10-i;
        } else if (gagne(grille, "X") && !b) {
            return -10+i;
        } else if (i == 9) {
            return 0;
        } else {
            if (b) {
                int score = Integer.MAX_VALUE;
                for (int j = 0; j < 3; j++) {
                    for (int k = 0; k < 3; k++) {
                        if (grille[j][k] == null) {
                            grille[j][k] = "X";
                            score = Math.min(score, alphaBeta(grille, "X", i + 1, alpha, beta, false));
                            grille[j][k] = null;
                            alpha = Math.min(alpha, score);
                            if (beta <= alpha) {
                                break;
                            }
                        }
                    }
                }
                return score;
            } else {
                int score = Integer.MIN_VALUE;
                for (int j = 0; j < 3; j++) {
                    for (int k = 0; k < 3; k++) {
                        if (grille[j][k] == null) {
                            grille[j][k] = "O";
                            score = Math.max(score, alphaBeta(grille,  "O", i + 1, alpha, beta, true));
                            grille[j][k] = null;
                            beta = Math.max(beta, score);
                            if (beta <= alpha) {
                                break;
                            }
                        }
                    }
                }
                return score;
            }
        }
    }


    private void afficherGrille(String[][] grille) {
        for (int i = 0; i < grille.length; i++) {

            for (int j = 0; j < grille[i].length; j++) {
                System.out.print((grille[i][j] == null ? " " : grille[i][j]) + " ");
                if (j < grille[i].length - 1) {
                    System.out.print("| ");
                }
            }
            System.out.println("\n---------");
        }
    }

    private int lireEntier(String message) {
        System.out.println(message);
        Scanner scanner = new Scanner(System.in);
        return scanner.nextInt();
    }

    private void placerJeton(String[][] grille, int ligne, int colonne, String joueur) {
        grille[ligne][colonne] = joueur;
    }

    private boolean gagne(String[][] grille, String joueur) {
        if (grille[0][0] == joueur && grille[0][1] == joueur && grille[0][2] == joueur) {
            return true;
        }
        if (grille[1][0] == joueur && grille[1][1] == joueur && grille[1][2] == joueur) {
            return true;
        }
        if (grille[2][0] == joueur && grille[2][1] == joueur && grille[2][2] == joueur) {
            return true;
        }
        if (grille[0][0] == joueur && grille[1][0] == joueur && grille[2][0] == joueur) {
            return true;
        }
        if (grille[0][1] == joueur && grille[1][1] == joueur && grille[2][1] == joueur) {
            return true;
        }
        if (grille[0][2] == joueur && grille[1][2] == joueur && grille[2][2] == joueur) {
            return true;
        }
        if (grille[0][0] == joueur && grille[1][1] == joueur && grille[2][2] == joueur) {
            return true;
        }
        if (grille[0][2] == joueur && grille[1][1] == joueur && grille[2][0] == joueur) {
            return true;
        }
        return false;
    }
}
