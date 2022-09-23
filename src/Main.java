import laboratoire1.RechercheTableauComparaisons;

public class Main {

    public static void main(String[] args) {
        RechercheTableauComparaisons searcher = new RechercheTableauComparaisons();
        int[] tabLongueur = new int[]{10, 100, 1000, 10000, 100000, 1000000, 10000000, 100000000}; // Liste des longueurs de tableaux successives
        int[][] tabTps = new int[3][tabLongueur.length]; // Tableau de temps
        int[][] tabCmp = new int[3][tabLongueur.length]; // Tableau de comparaisons
        for (int i = 0; i < tabLongueur.length; i++) {
            int longueur = tabLongueur[i]; // Longueur de la liste en cours
            int[] tab = new int[longueur];
            for (int j = 0; j < longueur; j++) {
                tab[j] = 2 * j;
            }
            int value = tab[(int) (Math.random() * longueur - 1)];

            // Calcul du temps d'éxecution
            long startTime = System.nanoTime();
            int result = searcher.RechercheLineaire(tab, tab.length, value);
            long endTime = System.nanoTime();
            long duration = (endTime - startTime);  //diviser par 1000000 pour avoir des millisecondes.
//            System.out.println("tps algo1: " + duration);
            tabTps[0][i] = (int) duration;

            startTime = System.nanoTime();
            result = searcher.RechercheBinaire(tab, tab.length, value);
            endTime = System.nanoTime();
            duration = (endTime - startTime);  //diviser par 1000000 pour avoir des millisecondes.
//            System.out.println("tps algo2: " + duration);
            tabTps[1][i] = (int) duration;

            startTime = System.nanoTime();
            result = searcher.RechercheBinaireModifie(tab, tab.length, value);
            endTime = System.nanoTime();
            duration = (endTime - startTime);  //diviser par 1000000 pour avoir des millisecondes.
//            System.out.println("tps algo3: " + duration);
            tabTps[2][i] = (int) duration;

//            System.out.println("compteur algo1: " + searcher.compteurLinear);
//            System.out.println("compteur algo2: " + searcher.compteurBinary);
//            System.out.println("compteur algo3: " + searcher.compteurBinaryModified);
            tabCmp[0][i] = searcher.compteurLinear;
            tabCmp[1][i] = searcher.compteurBinary;
            tabCmp[2][i] = searcher.compteurBinaryModified;
        }

        // Affichage des résultats
        System.out.println("Temps d'éxecution");
        System.out.println("Algo1\tAlgo2\tAlgo3");
        for (int i = 0; i < tabLongueur.length; i++) {
            System.out.println(tabTps[0][i] + "\t" + tabTps[1][i] + "\t" + tabTps[2][i]);
        }
        System.out.println("\nNombre de comparaisons");
        System.out.println("Algo1\tAlgo2\tAlgo3");
        for (int i = 0; i < tabLongueur.length; i++) {
            System.out.println(tabCmp[0][i] + "\t" + tabCmp[1][i] + "\t" + tabCmp[2][i]);
        }
    }
}
