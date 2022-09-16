package laboratoire1;

import java.util.Arrays;

public class RechercheTableau {

    // Ne pas changer ces fonctions, elles seront utilisées pour tester votre programme.
    // Elles peuvent cependant servir seulement d'interface et utiliser une méthode "helper"
    // avec des paramètres supplémentaires, au besoin.
    public int RechercheLineaire(int[] tab, int n, int x) {
        for (int i = 0; i < n; i++) {
            if (tab[i] == x) {
                return i;
            }
        }
        return -1;
    }

    public int RechercheBinaire(int[] tab, int n, int val) {
        if (n == 1)
            return tab[0] == val ? 0 : -1;
        int mid = n / 2;
        if (tab[mid] == val)
            return mid;
        if (tab[mid] > val) {
            return RechercheBinaire(tab, mid, val);
        }else {
            int res = RechercheBinaire(Arrays.copyOfRange(tab, mid, n), n - mid, val);
            return res == -1 ? -1 : mid + res;
        }
    }

    public int RechercheBinaireModifie(int[] tab, int n, int val) {
        return -1;
    }

}