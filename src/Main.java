import laboratoire1.RechercheTableau;

public class Main {

    public static void main(String[] args) {
        RechercheTableau rt = new RechercheTableau();
        int N = 100000000;
        int[] tab = new int[N];
        for (int i = 0; i < tab.length; i++) {
            tab[i] = i;
        }
        System.out.println(rt.RechercheLineaire(new int[]{1, 2, 3, 4, 5}, 5, 5));
        System.out.println(rt.RechercheBinaire(tab, N, N/4+10));
    }

}
