import laboratoire2.Huffman;

public class Main2 {
    public static void main(String[] args) {
        Huffman huffman = new Huffman();
        huffman.Compresser("src/laboratoire2/exemple.txt", "src/laboratoire2/sortie.txt");
        huffman.Decompresser("src/laboratoire2/sortie.txt", "src/laboratoire2/exemple2.txt");
    }

//    public static void main(String[] args) {
//        Huffman huffman = new Huffman();
//        huffman.Compresser("src/laboratoire2/test.txt", "src/laboratoire2/test2.txt");
//        huffman.Decompresser("src/laboratoire2/test2.txt", "src/laboratoire2/testD.txt");
//    }
}
