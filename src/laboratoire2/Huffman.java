package laboratoire2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class Huffman {

    // Ne pas changer ces fonctions, elles seront utilisées pour tester votre programme
    public void Compresser(String nomFichierEntre, String nomFichierSortie) {
        HashMap<Integer, Integer> tablesFrequence = creerTableFrequences(nomFichierEntre);
        for(Map.Entry<Integer, Integer> entry : tablesFrequence.entrySet()) {
            System.out.println(entry.getKey() + " " + entry.getValue());
        }
        HashMap<Integer, Integer> tablesFrequenceSave = (HashMap<Integer, Integer>) tablesFrequence.clone();
        ArrayList<Noeud> arbreHuffman = creerArbreHuffman(tablesFrequence);
        TreeMap<Integer, StringBuilder> tableCodage = creerTableCodage(arbreHuffman);
        for (int i : tableCodage.keySet()) {
            System.out.println(i + " : " + tableCodage.get(i));
        }
        afficherDoublon(tableCodage);
        compresseFile(nomFichierEntre, nomFichierSortie, tableCodage, tablesFrequenceSave);
    }

    private void afficherDoublon(TreeMap<Integer, StringBuilder> arbreHuffman) {
        ArrayList<StringBuilder> doublon = new ArrayList<>();
        for (int i : arbreHuffman.keySet()) {
            if (doublon.contains(arbreHuffman.get(i))) {
                System.out.println("Doublon : " + i + " : " + arbreHuffman.get(i));
            }
            doublon.add(arbreHuffman.get(i));
        }
    }

    private void compresseFile(String nomFichierEntre, String nomFichierSortie, TreeMap<Integer, StringBuilder> tableCodage, HashMap<Integer, Integer> tablesFrequence) {
        File entre = new File(nomFichierEntre);
        File sortie = new File(nomFichierSortie);
        try (FileInputStream fileInputStream = new FileInputStream(entre);
             FileOutputStream fileOutputStream = new FileOutputStream(sortie)) {
            fileOutputStream.write(sizeTable(tablesFrequence));
            for (int i : tablesFrequence.keySet()) {
                fileOutputStream.write(i);
                fileOutputStream.write(eciture8f(tablesFrequence.get(i)));
            }

            int b;
            byte bitBuffer = 0;
            int bitCount = 0;
            while ((b = fileInputStream.read()) != -1) {
                StringBuilder code = tableCodage.get(b);
                int codeLength = code.length();
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

    private void writeString(StringBuilder stringBuilder, FileOutputStream fileOutputStream) throws IOException {
        for(int i = 0; i < stringBuilder.length(); i += 8) {
            int b = 0;
            for(int j = 0; j < 8; j++) {
                if(i + j < stringBuilder.length()) {
                    b = (b << 1) | (stringBuilder.charAt(i + j) - '0');
                }
            }
            fileOutputStream.write(b);
        }
    }

    private TreeMap<Integer, StringBuilder> creerTableCodage(ArrayList<Noeud> arbreHuffman) {
        TreeMap<Integer, StringBuilder> tableCodage = new TreeMap<>();
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
                }
                else {
                    code.append("0");
                }
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
        HashMap<Integer, Integer> tablesFrequence = creerTableFrequencesRecu(nomFichierEntre);
        ArrayList<Noeud> arbreHuffman = creerArbreHuffman(tablesFrequence);
        for (Noeud noeud : arbreHuffman) {
            System.out.println(noeud);
        }
        decompresFile(nomFichierEntre, nomFichierSortie, arbreHuffman);
    }

    private HashMap<Integer, Integer> creerTableFrequencesRecu(String nomFichierEntre) {
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
        System.out.println("Taille du fichier : " + taile);
        return tablesFrequence;
    }

    private Integer getValueTab(byte[] valueTab) {
        System.out.println(valueTab[0] + " " + valueTab[1] + " " + valueTab[2] + " " + valueTab[3]);
        int value = 0;
        for (int i = 0; i < valueTab.length; i++) {
            value += (valueTab[i] & 0xFF) << (8 * (3-i));
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
            int taille = fileInputStream.readNBytes(2)[1];
            System.out.println("Taille de l'entete : " + taille);
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
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}