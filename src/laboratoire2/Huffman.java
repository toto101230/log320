package laboratoire2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.*;

public class Huffman {

    // Ne pas changer ces fonctions, elles seront utilisées pour tester votre programme
    public void Compresser(String nomFichierEntre, String nomFichierSortie) {
        HashMap<Integer, Integer> tablesFrequence = creerTableFrequences(nomFichierEntre);
        ArrayList<Noeud> arbreHuffman = creerArbreHuffman(tablesFrequence);
        TreeMap<Integer, Integer> tableCodage = creerTableCodage(arbreHuffman);
        for (int i : tableCodage.keySet()) {
            System.out.println(i + " : " + Integer.toBinaryString(tableCodage.get(i)));
        }
        compresseFile(nomFichierEntre, nomFichierSortie, tableCodage);
    }

    private void compresseFile(String nomFichierEntre, String nomFichierSortie, TreeMap<Integer, Integer> tableCodage) {
        File entre = new File(nomFichierEntre);
        File sortie = new File(nomFichierSortie);
        try (FileInputStream fileInputStream = new FileInputStream(entre);
             FileOutputStream fileOutputStream = new FileOutputStream(sortie)) {
            fileOutputStream.write(tableCodage.size());
            fileOutputStream.write(0);
            for (int i : tableCodage.keySet()) {
                fileOutputStream.write(i);
                fileOutputStream.write(tableCodage.get(i));
            }

            int b;
            byte bitBuffer = 0;
            int bitCount = 0;
            while ((b = fileInputStream.read()) != -1) {
                int code = tableCodage.get(b);
                int codeLength = Integer.toBinaryString(code).length();
                for (int i = codeLength - 1; i >= 0; i--) {
                    bitBuffer = (byte) ((bitBuffer << 1) | (code >> i & 1));
                    bitCount++;
                    if (bitCount == 8) {
                        fileOutputStream.write(bitBuffer);
                        bitBuffer = 0;
                        bitCount = 0;
                    }
                }
            }
            while (bitCount != 0) {
                bitBuffer = (byte) (bitBuffer << 1);
                bitCount++;
                if (bitCount == 8) {
                    fileOutputStream.write(bitBuffer);
                    bitBuffer = 0;
                    bitCount = 0;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private TreeMap<Integer, Integer> creerTableCodage(ArrayList<Noeud> arbreHuffman) {
        TreeMap<Integer, Integer> tableCodage = new TreeMap<>();
        for (Noeud noeud : arbreHuffman) {
            if (noeud.getValeur() <= -1)
                continue;
            int code = 0;
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
        int i = -1;
        while (tablesFrequence.size() > 1) {
            int key1 = trouverMin(tablesFrequence);
            int frequence1 = tablesFrequence.get(key1);
            tablesFrequence.remove(key1);

            int key2 = trouverMin(tablesFrequence);
            int frequence2 = tablesFrequence.get(key2);
            tablesFrequence.remove(key2);

            int somme = frequence1 + frequence2;
            tablesFrequence.put(i, somme);

            Noeud gauche = creationNoeud(arbreHuffman, key1, frequence1);
            Noeud droit = creationNoeud(arbreHuffman, key2, frequence2);
            Noeud sommeN;
            if (gauche.getValeur() < 0)
                sommeN = new Noeud(i, somme, gauche, droit);
            else
                sommeN = new Noeud(i, somme, droit, gauche);
            arbreHuffman.add(sommeN);
            gauche.setParent(sommeN);
            droit.setParent(sommeN);
            i--;
        }
        return arbreHuffman;
    }

    private Noeud creationNoeud(ArrayList<Noeud> arbreHuffman, int key, int frequence) {
        for (Noeud noeud : arbreHuffman) {
            if (noeud.getValeur() == key && noeud.getFrequence() == frequence && noeud.getParent() == null) {
                return noeud;
            }
        }
        Noeud noeud = new Noeud(key, frequence, null, null);
        arbreHuffman.add(noeud);
        return noeud;
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
        ArrayList<Noeud> arbreHuffman = recreerArbreHuffman(nomFichierEntre);
        completionArbreHuffman(arbreHuffman);
        for (Noeud noeud : arbreHuffman) {
            System.out.println(noeud);
        }
        decompresFile(nomFichierEntre, nomFichierSortie, arbreHuffman);
    }

    private void decompresFile(String nomFichierEntre, String nomFichierSortie, ArrayList<Noeud> arbreHuffman) {
        File file = new File(nomFichierEntre);
        File file2 = new File(nomFichierSortie);
        Noeud racine = arbreHuffman.get(arbreHuffman.size() - 1);
        try (FileInputStream fileInputStream = new FileInputStream(file);
             FileOutputStream fileOutputStream = new FileOutputStream(file2)) {
            //skip entete
            int taille = fileInputStream.read();
            fileInputStream.read();
            for (int i = 0; i < taille * 2; i++) {
                fileInputStream.read();
            }

            int bitBuffer = 0;
            int bitCount = 0;
            Noeud noeudCourant = racine;
            int singleCharInt;
            while ((singleCharInt = fileInputStream.read()) != -1) {
                bitBuffer = (bitBuffer << 8) | singleCharInt;
                bitCount += 8;
                while (bitCount >= 8) {
                    if (noeudCourant.getGauche() == null && noeudCourant.getDroit() == null) {
                        fileOutputStream.write(noeudCourant.getValeur());
                        noeudCourant = racine;
                    }
                    int bit = (bitBuffer >> (bitCount - 1)) & 1;
                    bitCount--;
                    if (bit == 1) {
                        noeudCourant = noeudCourant.getGauche();
                    } else {
                        noeudCourant = noeudCourant.getDroit();
                    }
                }
            }
            while (bitCount > 0) {
                if (noeudCourant.getGauche() == null && noeudCourant.getDroit() == null) {
                    fileOutputStream.write(noeudCourant.getValeur());
                    noeudCourant = racine;
                }
                int bit = (bitBuffer >> (bitCount - 1)) & 1;
                bitCount--;
                if (bit == 1) {
                    noeudCourant = noeudCourant.getGauche();
                } else {
                    noeudCourant = noeudCourant.getDroit();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void completionArbreHuffman(ArrayList<Noeud> arbreHuffman) {
        ArrayList<Noeud> noeuds = (ArrayList<Noeud>) arbreHuffman.clone();

        Noeud droite = getNoeud(noeuds, (byte) 0);
        Noeud racine = new Noeud(-1, -1, null, droite);
        droite.setParent(racine);
        noeuds.remove(droite);
        racine.setGauche(recuFindNoeud(noeuds, (byte) 1, arbreHuffman));
        arbreHuffman.add(racine);
    }

    private Noeud recuFindNoeud(ArrayList<Noeud> noeuds, byte i, ArrayList<Noeud> arbreHuffman) {
        if (noeuds.size() <= 1) {
            return noeuds.get(0);
        }
        Noeud droit = getNoeud(noeuds, (byte) (i << 1));
        Noeud noeud = new Noeud(-1, -1, null, droit);
        noeuds.remove(droit);
        droit.setParent(noeud);
        Noeud gauche = recuFindNoeud(noeuds, (byte) ((i << 1) + 1), arbreHuffman);
        noeud.setGauche(gauche);
        arbreHuffman.add(noeud);
        return noeud;
    }

    private Noeud getNoeud(ArrayList<Noeud> noeuds, byte i) {
        for (Noeud noeud : noeuds) {
            if (noeud.getFrequence() == i) {
                return noeud;
            }
        }
        return null;
    }

    private ArrayList<Noeud> recreerArbreHuffman(String nomFichierEntre) {
        ArrayList<Noeud> arbreHuffman = new ArrayList<>();
        File file = new File(nomFichierEntre);
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            int taille = fileInputStream.read();
            fileInputStream.skip(1);
            for (int i = 0; i < taille; i++) {
                int valeur = fileInputStream.read();
                int frequence = fileInputStream.read();
                Noeud noeud = new Noeud(valeur, frequence, null, null);
                arbreHuffman.add(noeud);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return arbreHuffman;
    }

}