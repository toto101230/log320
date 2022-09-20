package laboratoire1;

public class RechercheTableau {

    public int RechercheLineaire(int[] tab, int n, int x){
        for (int i=0; i < n; i++){
            if (tab[i] == x){
                return i;
            }
        }
        return -1;
    }

    public int RechercheBinaire(int[] tab, int n, int val){
        return HelperBinaire(tab, 0, n - 1, val);
    }

    public int RechercheBinaireModifie(int[] tab, int n, int val){
        return HelperBinaireModifie(tab, 0, n - 1, val);
    }

    private int HelperBinaire(int[] tab, int firstIndex, int lastIndex, int val){
        int n = lastIndex - firstIndex + 1;
        if (n == 1){
            if (tab[lastIndex] == val){
                return lastIndex;
            }
            return -1;
        }
        int middle = firstIndex + n / 2;
        if (tab[middle] == val){
            return middle;
        }

        if (tab[middle] > val){
            return HelperBinaire(tab, firstIndex, middle - 1, val);
        }
        return HelperBinaire(tab, middle + 1, lastIndex, val);
    }

    private int HelperBinaireModifie(int[] tab, int firstIndex, int lastIndex, int val){
        int n = lastIndex - firstIndex + 1;
        if (n == 1){
            if (tab[lastIndex] == val){
                return lastIndex;
            }
            return -1;
        }
        int gauche = firstIndex + n / 3;
        int droite = lastIndex - n / 3;
        if (tab[gauche] == val){
            return gauche;
        }
        if (tab[gauche] > val){
            return HelperBinaireModifie(tab, firstIndex, gauche - 1, val);
        }
        if (tab[droite] == val){
            return droite;
        }
        if (tab[droite] < val){
            return HelperBinaireModifie(tab, droite + 1, lastIndex, val);
        }
        return HelperBinaireModifie(tab, gauche - 1, droite + 1, val);
    }

}