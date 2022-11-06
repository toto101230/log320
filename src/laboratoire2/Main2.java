package laboratoire2;

import laboratoire2.Comparateur;
import laboratoire2.Huffman;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main2 {
    public static void main(String[] args) throws IOException {
        Huffman huffman = new Huffman();
        long startTime = System.currentTimeMillis();
        huffman.Compresser("src/laboratoire2/exemple.txt", "src/laboratoire2/sortie.txt");
        long endCompressionTime = System.currentTimeMillis();
        huffman.Decompresser("src/laboratoire2/sortie.txt", "src/laboratoire2/exemple2.txt");
        long endDecompressionTime = System.currentTimeMillis();
        Comparateur comparateur = new Comparateur();
        if (comparateur.Comparer("src/laboratoire2/exemple.txt", "src/laboratoire2/exemple2.txt")) {
            System.out.println("Les fichiers sont identiques");
        } else {
            System.out.println("Les fichiers sont différents");
        }
        long bytes1 = Files.size(Paths.get("src/laboratoire2/exemple.txt"));
        long bytes2 = Files.size(Paths.get("src/laboratoire2/sortie.txt"));
        System.out.println("Taille du fichier initial : " + bytes1/1024 + " Ko");
        System.out.println("Taille du fichier compressé : " + bytes2/1024 + " Ko");
        System.out.println("Temps de compression : " + (endCompressionTime - startTime) + " ms");
        System.out.println("Temps de décompression : " + (endDecompressionTime - endCompressionTime) + " ms");

    }

//    public static void main(String[] args) {
//        Huffman huffman = new Huffman();
//        huffman.Compresser("src/laboratoire2/test.txt", "src/laboratoire2/test2.txt");
//        huffman.Decompresser("src/laboratoire2/test2.txt", "src/laboratoire2/testD.txt");
//    }

//    public static void main(String[] args) {
//        byte b = -128;
//        System.out.println(b&0xFF);
//    }
}
