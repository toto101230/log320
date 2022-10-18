package laboratoire2;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Comparateur {

    // Function which compare two files with file checksum
    public Boolean Comparer(String nomFichier1, String nomFichier2){
        boolean identiques = true;

        try {
            //Create the files
            File fichier1 = new File(nomFichier1);
            File fichier2 = new File(nomFichier2);

            // MD5 digest is used to compare the files
            String hash1 = this.getFileChecksum(MessageDigest.getInstance("MD5"), fichier1);
            String hash2 = this.getFileChecksum(MessageDigest.getInstance("MD5"), fichier2);
            System.out.println("Hash MD5 du fichier initial : " + hash1);
            System.out.println("Hash MD5 du fichier décompressé : " + hash2);

            identiques = hash1.equals(hash2);
        }
        catch (FileNotFoundException e) {
            System.out.println("Le fichier n'existe pas");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return identiques;
    }

    // Function which return the checksum of a file
    private String getFileChecksum(MessageDigest digest, File file) throws IOException {
        byte[] byteArray = new byte[1024];
        int bytesCount = 0;

        FileInputStream fis = new FileInputStream(file);
        while ((bytesCount = fis.read(byteArray)) != -1) {
            digest.update(byteArray, 0, bytesCount);
        };
        fis.close();

        byte[] bytes = digest.digest();

        StringBuilder sb = new StringBuilder();
        for (byte aByte : bytes) {
            sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
        }

        return sb.toString();
    }

}
