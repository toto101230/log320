import laboratoire1.RechercheTableau;

public class Main {

    public static void main(String[] args){
        RechercheTableau searcher = new RechercheTableau();
        int[] tabLongueur = new int[] {1, 10, 100, 1000, 10000, 100000, 1000000}; // Liste des longueurs de tableaux successives
        for (int i=0; i<tabLongueur.length; i++){
            int longueur = tabLongueur[i]; // Longueur de la liste en cours
            int[] tab = new int[longueur];
            for (int j=0; j<longueur; j++){
                tab[j] = 2 * j;
            }
            int value = tab[(int)(Math.random() * longueur - 1)];

            long startTime = System.nanoTime();
            int linear = searcher.RechercheLineaire(tab, tab.length, value);
            long endTime = System.nanoTime();

            long duration = (endTime - startTime);  //diviser par 1000000 pour avoir des millisecondes.
            System.out.println(duration);
        }

        /*
        int linear = searcher.RechercheLineaire(tab, tab.length, 11);
        int binary = searcher.RechercheBinaire(tab, tab.length, 11);
        int binaryMod = searcher.RechercheBinaireModifie(tab, tab.length, 11);


        System.out.println(linear);
        System.out.println(binary);
        System.out.println(binaryMod);

         */
    }
}
