package laboratoire3;

import laboratoire3.DocDist;

public class Main3 {
    public static void main(String[] args) {
        DocDist docDist = new DocDist();
//        System.out.println(docDist.docDistance("src/laboratoire3/test.txt", "src/laboratoire3/test2.txt"));
        double distance = docDist.docDistance("src/laboratoire3/adventures_of_Sherlock_Holmes-Doyle.txt", "src/laboratoire3/monte_cristo_1-Dumas.txt");
        System.out.println("La distance cosinus entre les deux documents est de " + distance +" radians.");
//        System.out.println(docDist.docDistance("src/laboratoire3/fables_Lafontaine.txt", "src/laboratoire3/fables_Lafontaine.txt"));
    }
}
