package laboratoire3;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

public class DocDist {

    // Ne pas changer cette fonction, elle sera utilisée pour tester votre programme
    public double docDistance(String nomFichier1, String nomFichier2) {
        HashMap<String, Integer> listMots1 = lireFichier1(nomFichier1);
        HashMap<String, Integer> listMots2 = lireFichier2(nomFichier2, listMots1);

        float produitScalaire = calculerProduitScalaire(listMots1, listMots2);
        float norme1 = calculerNorme(listMots1);
        float norme2 = calculerNorme(listMots2);

        double distance = Math.acos(produitScalaire / (norme1 * norme2));
        distance = Math.round(distance * 1000.0) / 1000.0;
        return (Double.isNaN(distance))?0:distance;
    }

    private float calculerNorme(HashMap<String, Integer> listMots1) {
        float norme = 0;
        for (String mot : listMots1.keySet()) {
            norme += Math.pow(listMots1.get(mot), 2);
        }
        return (float) Math.sqrt(norme);
    }

    private float calculerProduitScalaire(HashMap<String, Integer> listMots1, HashMap<String, Integer> listMots2) {
        float produitScalaire = 0;
        for (String mot : listMots1.keySet()) {
            produitScalaire += listMots1.get(mot) * listMots2.get(mot);
        }
        return produitScalaire;
    }

    private HashMap<String, Integer> lireFichier1(String nomFichier) {
        int motCount = 0;
        int motDistinctCount = 0;
        HashMap<String, Integer> listMots = new HashMap<>();
        File file = new File(nomFichier);
        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] mots = line.toLowerCase().split("[^a-z0-9œàáâãäåæçèéêëìíîïðòóôõöùúûüýÿ]");
                for (String mot : mots) {
                    if(!mot.isEmpty()) {
                        motCount++;
                        if (listMots.containsKey(mot)) {
                            listMots.put(mot, listMots.get(mot) + 1);
                        } else {
                            listMots.put(mot, 1);
                            motDistinctCount++;
                        }
                    }
                }
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println("Fichier " + nomFichier + " : " + motCount + " mots, " + motDistinctCount + " mots distincts");
        return listMots;
    }



    private HashMap<String, Integer> lireFichier2(String nomFichier, HashMap<String, Integer> listMots1) {
        int motCount = 0;
        int motDistinctCount = 0;
        HashMap<String, Integer> listMots = cloneListeMot(listMots1);
        File file = new File(nomFichier);
        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] mots = line.toLowerCase().split("[^a-z0-9œàáâãäåæçèéêëìíîïðòóôõöùúûüýÿ]");
                for (String mot : mots) {
                    if(!mot.isEmpty()) {
                        motCount++;
                        if (listMots.containsKey(mot)) {
                            if(listMots.get(mot) == 0) {
                                motDistinctCount++;
                            }
                            listMots.put(mot, listMots.get(mot) + 1);
                        } else {
                            listMots.put(mot, 1);
                            listMots1.put(mot, 0);
                            motDistinctCount++;
                        }
                    }
                }
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println("Fichier " + nomFichier + " : " + motCount + " mots, " + motDistinctCount + " mots distincts");
        return listMots;
    }

    private HashMap<String, Integer> cloneListeMot(HashMap<String, Integer> listMots1) {
        HashMap<String, Integer> listMots2 = new HashMap<>();
        for (String mot : listMots1.keySet()) {
            listMots2.put(mot, 0);
        }
        return listMots2;
    }
}