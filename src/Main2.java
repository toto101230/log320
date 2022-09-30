import laboratoire2.Huffman;

public class Main2 {
    public static void main(String[] args) {
        Huffman huffman = new Huffman();
        huffman.Compresser("src/laboratoire2/exemple.txt", "src/laboratoire2/sortie.txt");
    }
}
