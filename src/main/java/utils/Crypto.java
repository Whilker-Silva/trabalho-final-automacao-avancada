package utils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class Crypto extends Thread {

    private static final String ALGORITHM = "AES";
    private static final String SECRET_KEY = "1234567890123456";

    // Criptografa uma String
    public static String criptografar(String texto) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        SecretKeySpec chave = new SecretKeySpec(SECRET_KEY.getBytes(), ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, chave);
        byte[] criptografado = cipher.doFinal(texto.getBytes());
        return Base64.getEncoder().encodeToString(criptografado);
    }

    // Descriptografa uma String
    public static String descriptografar(String textoCriptografado) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        SecretKeySpec chave = new SecretKeySpec(SECRET_KEY.getBytes(), ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, chave);
        byte[] decodificado = Base64.getDecoder().decode(textoCriptografado);
        byte[] descriptografado = cipher.doFinal(decodificado);
        return new String(descriptografado);
    }
}
