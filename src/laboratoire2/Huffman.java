package laboratoire2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

public class Huffman {

    // Ne pas changer ces fonctions, elles seront utilisées pour tester votre programme
    public void Compresser(String nomFichierEntre, String nomFichierSortie) {
        HashMap<Integer, Integer> tablesFrequence = creerTableFrequences(nomFichierEntre);
        ArrayList<Noeud> arbreHuffman = creerArbreHuffman(tablesFrequence);
        for(Noeud n : arbreHuffman){
            System.out.println(n.getValeur() + " " + n.getFrequence());
        }
        TreeMap<Integer, Byte> tableCodage = creerTableCodage(arbreHuffman);
        compresseFile(nomFichierEntre, nomFichierSortie, tableCodage);
    }

    private void compresseFile(String nomFichierEntre, String nomFichierSortie, TreeMap<Integer, Byte> tableCodage) {
        File entre = new File(nomFichierEntre);
        File sortie = new File(nomFichierSortie);
        try (FileInputStream fileInputStream = new FileInputStream(entre)) {
            FileOutputStream fos = new FileOutputStream(sortie);
            int singleCharInt;
            while ((singleCharInt = fileInputStream.read()) != -1) {
                byte val = tableCodage.get(singleCharInt);
                fos.write(val);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private TreeMap<Integer, Byte> creerTableCodage(ArrayList<Noeud> arbreHuffman) {
        TreeMap<Integer, Byte> tableCodage = new TreeMap<>();
        for (Noeud noeud : arbreHuffman) {
            if (noeud.getValeur() == -1)
                continue;
            byte code = 0;
            byte un = 1;
            ArrayList<Noeud> chemin = new ArrayList<>();
            Noeud noeudCourant = noeud;
            while (noeudCourant.getParent() != null) {
                chemin.add(noeudCourant);
                noeudCourant = noeudCourant.getParent();
            }
            chemin.add(noeudCourant);
            for (int i = chemin.size() - 2; i >= 0; i--) {
                if (chemin.get(i + 1).getGauche() == chemin.get(i)) {
                    code = (byte) (code + un);
                }
                if (chemin.get(i) != noeud)
                    code = (byte) (code << 1);
            }
            tableCodage.put(noeud.getValeur(), code);
        }
        return tableCodage;
    }

    private ArrayList<Noeud> creerArbreHuffman(HashMap<Integer, Integer> tablesFrequence) {
        ArrayList<Noeud> arbreHuffman = new ArrayList<>();
        while (tablesFrequence.size() > 1) {
            int key1 = trouverMin(tablesFrequence);
            int frequence1 = tablesFrequence.get(key1);
            tablesFrequence.remove(key1);
            int key2 = trouverMin(tablesFrequence);
            int frequence2 = tablesFrequence.get(key2);
            tablesFrequence.remove(key2);

            int somme = frequence1 + frequence2;
            tablesFrequence.put(-1, somme);

            Noeud gauche = creationNoeud(arbreHuffman, key1, frequence1);
            Noeud droit = creationNoeud(arbreHuffman, key2, frequence2);
            Noeud sommeN = new Noeud(-1, somme, gauche, droit);
            arbreHuffman.add(sommeN);
            gauche.setParent(sommeN);
            droit.setParent(sommeN);
        }
        return arbreHuffman;
    }

    private Noeud creationNoeud(ArrayList<Noeud> arbreHuffman, int key, int frequence) {
        for (Noeud noeud : arbreHuffman) {
            if (noeud.getValeur() == key && noeud.getFrequence() == frequence) {
                return noeud;
            }
        }
        Noeud noeud = new Noeud(key, frequence, null, null);
        arbreHuffman.add(noeud);
        return noeud;
    }

    private Noeud rechercheNoeud(ArrayList<Noeud> arbreHuffman, int key1) {
        for (Noeud noeud : arbreHuffman) {
            if (noeud.getValeur() == key1) {
                return noeud;
            }
        }
        return null;
    }

    private int trouverMin(HashMap<Integer, Integer> tablesFrequence) {
        int min = Integer.MAX_VALUE;
        int index = 0;
        for (int i : tablesFrequence.keySet()) {
            if (tablesFrequence.get(i) < min && tablesFrequence.get(i) != 0) {
                min = tablesFrequence.get(i);
                index = i;
            }
        }
        return index;
    }

    private HashMap<Integer, Integer> creerTableFrequences(String nomFichierEntre) {
        HashMap<Integer, Integer> tablesFrequence = new HashMap<>();
        File file = new File(nomFichierEntre);
        int taile = 0;
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            int singleCharInt;
            while ((singleCharInt = fileInputStream.read()) != -1) {
                if (tablesFrequence.containsKey(singleCharInt)) {
                    tablesFrequence.put(singleCharInt, tablesFrequence.get(singleCharInt) + 1);
                } else {
                    tablesFrequence.put(singleCharInt, 1);
                }
                taile++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Taille du fichier : " + taile);
        return tablesFrequence;
    }

    public void Decompresser(String nomFichierEntre, String nomFichierSortie) {

    }

}