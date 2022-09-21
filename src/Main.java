import laboratoire1.RechercheTableau;
import laboratoire1.RechercheTableauComparaisons;

public class Main {

    public static void main(String[] args){
        RechercheTableauComparaisons searcher = new RechercheTableauComparaisons();
        int[] tabLongueur = new int[] {100000}; // Liste des longueurs de tableaux successives
        for (int i=0; i<tabLongueur.length; i++){
            int longueur = tabLongueur[i]; // Longueur de la liste en cours
            int[] tab = new int[longueur];
            for (int j=0; j<longueur; j++){
                tab[j] = 2 * j;
            }
            int value = tab[(int)(Math.random() * longueur - 1)];

            // Calcul du temps d'éxecution
            long startTime = System.nanoTime();
            int result = searcher.RechercheLineaire(tab, tab.length, value);
            long endTime = System.nanoTime();
            long duration = (endTime - startTime);  //diviser par 1000000 pour avoir des millisecondes.
            System.out.println(duration);

            startTime = System.nanoTime();
            result = searcher.RechercheBinaire(tab, tab.length, value);
            endTime = System.nanoTime();
            duration = (endTime - startTime);  //diviser par 1000000 pour avoir des millisecondes.
            System.out.println(duration);

            startTime = System.nanoTime();
            result = searcher.RechercheBinaireModifie(tab, tab.length, value);
            endTime = System.nanoTime();
            duration = (endTime - startTime);  //diviser par 1000000 pour avoir des millisecondes.
            System.out.println(duration);

            System.out.println(searcher.compteurLinear);
            System.out.println(searcher.compteurBinary);
            System.out.println(searcher.compteurBinaryModified);
        }

    }
}
