package laboratoire2;

import java.io.*;
import java.util.*;

public class Huffman {

    // Ne pas changer ces fonctions, elles seront utilisées pour tester votre programme
    public void Compresser(String nomFichierEntre, String nomFichierSortie) {
        HashMap<Integer, Integer> tablesFrequence = creerTableFrequences(nomFichierEntre);
        if (tablesFrequence.size() == 0) {
            System.out.println("Erreur lors de la lecture du fichier");
            return;
        }
        HashMap<Integer, Integer> tablesFrequenceSave = (HashMap<Integer, Integer>) tablesFrequence.clone();

        ArrayList<Noeud> arbreHuffman = creerArbreHuffman(tablesFrequence);
        HashMap<Integer, StringBuilder> tableCodage = creerTableCodage(arbreHuffman);
        compresseFile(nomFichierEntre, nomFichierSortie, tableCodage, tablesFrequenceSave);
    }

    private void compresseFile(String nomFichierEntre, String nomFichierSortie, HashMap<Integer, StringBuilder> tableCodage, HashMap<Integer, Integer> tablesFrequence) {
        File entre = new File(nomFichierEntre);
        File sortie = new File(nomFichierSortie);
        try (FileInputStream fileInputStream = new FileInputStream(entre);
             FileOutputStream fileOutputStream = new FileOutputStream(sortie)) {
            fileOutputStream.write(sizeTable(tablesFrequence));
            for (int i : tablesFrequence.keySet()) {
                fileOutputStream.write(i);
                fileOutputStream.write(eciture8f(tablesFrequence.get(i)));
            }

            byte bitBuffer = 0;
            int b, bitCount = 0, codeLength;
            while ((b = fileInputStream.read()) != -1) {
                StringBuilder code = tableCodage.get(b);
                codeLength = code.length();
                for (int i = 0; i < codeLength; i++) {
                    bitBuffer = (byte) ((bitBuffer << 1) | getCharDeCode(code.charAt(i)));
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

    private byte[] eciture8f(int integer) {
        byte[] bytes = new byte[4];
        bytes[0] = (byte) (integer >> 24);
        bytes[1] = (byte) (integer >> 16);
        bytes[2] = (byte) (integer >> 8);
        bytes[3] = (byte) (integer);
        return bytes;
    }

    private byte[] sizeTable(HashMap<Integer, Integer> tablesFrequence) {
        int size = 0;
        for (int i : tablesFrequence.keySet()) {
            if (i != 0) {
                size++;
            }
        }
        byte[] sizeTable = new byte[2];
        sizeTable[0] = (byte) (size >> 8);
        sizeTable[1] = (byte) (size);
        return sizeTable;
    }

    private int getCharDeCode(char charAt) {
        if (charAt == '0') {
            return 0;
        } else {
            return 1;
        }
    }

    private HashMap<Integer, StringBuilder> creerTableCodage(ArrayList<Noeud> arbreHuffman) {
        HashMap<Integer, StringBuilder> tableCodage = new HashMap<>();
        for (Noeud noeud : arbreHuffman) {
            if (noeud.getValeur() <= -1)
                continue;
            StringBuilder code = new StringBuilder();
            ArrayList<Noeud> chemin = new ArrayList<>();
            Noeud noeudCourant = noeud;
            while (noeudCourant.getParent() != null) {
                chemin.add(noeudCourant);
                noeudCourant = noeudCourant.getParent();
            }
            chemin.add(noeudCourant);
            for (int i = chemin.size() - 2; i >= 0; i--) {
                if (chemin.get(i + 1).getGauche() == chemin.get(i)) {
                    code.append("1");
                } else {
                    code.append("0");
                }
            }
            tableCodage.put(noeud.getValeur(), code);
        }
        return tableCodage;
    }

    private ArrayList<Noeud> creerArbreHuffman(HashMap<Integer, Integer> tablesFrequence) {
        ArrayList<Noeud> arbreHuffman = new ArrayList<>();
        int i = -1, key1, key2, frequence1, frequence2, somme;
        Noeud gauche, droite, parent;
        while (tablesFrequence.size() > 1) {
            key1 = trouverMin(tablesFrequence);
            frequence1 = tablesFrequence.get(key1);
            tablesFrequence.remove(key1);

            key2 = trouverMin(tablesFrequence);
            frequence2 = tablesFrequence.get(key2);
            tablesFrequence.remove(key2);

            somme = frequence1 + frequence2;
            tablesFrequence.put(i, somme);

            gauche = creationNoeud(arbreHuffman, key1, frequence1);
            droite = creationNoeud(arbreHuffman, key2, frequence2);
            parent = new Noeud(i, somme, droite, gauche);
            arbreHuffman.add(parent);
            gauche.setParent(parent);
            droite.setParent(parent);
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
        return tablesFrequence;
    }

    public void Decompresser(String nomFichierEntre, String nomFichierSortie) {
        HashMap<Integer, Integer> tablesFrequence = recupTablesFrequence(nomFichierEntre);
        if (tablesFrequence.size() == 0) {
            System.out.println("Le fichier est vide");
            return;
        }
        ArrayList<Noeud> arbreHuffman = creerArbreHuffman(tablesFrequence);
        decompresFile(nomFichierEntre, nomFichierSortie, arbreHuffman);
    }

    private HashMap<Integer, Integer> recupTablesFrequence(String nomFichierEntre) {
        HashMap<Integer, Integer> tablesFrequence = new HashMap<>();
        File file = new File(nomFichierEntre);
        int taile = 0;
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            int taille = fileInputStream.readNBytes(2)[1];
            for (int i = 0; i < taille; i++) {
                int key = fileInputStream.read();
                byte[] valueTab = fileInputStream.readNBytes(4);
                tablesFrequence.put(key, getValueTab(valueTab));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tablesFrequence;
    }

    private Integer getValueTab(byte[] valueTab) {
        int value = 0;
        for (int i = 0; i < valueTab.length; i++) {
            value += (valueTab[i] & 0xFF) << (8 * (3 - i));
        }
        return value;
    }

    private void decompresFile(String nomFichierEntre, String nomFichierSortie, ArrayList<Noeud> arbreHuffman) {
        File file = new File(nomFichierEntre);
        File file2 = new File(nomFichierSortie);
        Noeud racine = arbreHuffman.get(arbreHuffman.size() - 1);
        int nbCaractere = racine.getFrequence();
        try (FileInputStream fileInputStream = new FileInputStream(file);
             FileOutputStream fileOutputStream = new FileOutputStream(file2)) {
            //skip entete
            int taille = fileInputStream.readNBytes(2)[1] & 0xFF;
            for (int i = 0; i < taille * 5; i++) {
                fileInputStream.read();
            }

            int bitBuffer = 0;
            int bitCount = 0;
            Noeud noeudCourant = racine;
            int singleCharInt;
            while ((singleCharInt = fileInputStream.read()) != -1 && nbCaractere > 0) {
                bitBuffer = (bitBuffer << 8) | singleCharInt;
                bitCount += 8;
                while (bitCount >= 8) {
                    if (noeudCourant.getGauche() == null && noeudCourant.getDroit() == null) {
                        fileOutputStream.write(noeudCourant.getValeur());
                        noeudCourant = racine;
                        nbCaractere--;
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
            while (bitCount > 0 && nbCaractere > 0) {
                if (noeudCourant.getGauche() == null && noeudCourant.getDroit() == null) {
                    fileOutputStream.write(noeudCourant.getValeur());
                    noeudCourant = racine;
                    nbCaractere--;
                }
                int bit = (bitBuffer >> (bitCount - 1)) & 1;
                bitCount--;
                if (bit == 1) {
                    noeudCourant = noeudCourant.getGauche();
                } else {
                    noeudCourant = noeudCourant.getDroit();
                }
            }
            if (noeudCourant != null && nbCaractere > 0)
                if (noeudCourant.getGauche() == null && noeudCourant.getDroit() == null) {
                    fileOutputStream.write(noeudCourant.getValeur());
                    nbCaractere--;
                }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}