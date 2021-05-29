package company;

import javax.xml.bind.DatatypeConverter;
import java.security.*;

public class ECDSAUtils {
    public static KeyPair getKeyPair() throws Exception {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("EC");
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        keyPairGen.initialize(256, random);

        return keyPairGen.generateKeyPair();
    }

    public static String signECDSA(PrivateKey privateKey, String message) {
        String result = "";
        try {
            Signature signature = Signature.getInstance("SHA256withECDSA");
            signature.initSign(privateKey);
            signature.update(message.getBytes());

            byte[] sign = signature.sign();

            return DatatypeConverter.printHexBinary(sign);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static boolean verifyECDSA(PublicKey publicKey, String signed, String message) {
        try {
            Signature signature = Signature.getInstance("SHA256withECDSA");
            signature.initVerify(publicKey);
            signature.update(message.getBytes());

            byte[] hex = DatatypeConverter.parseHexBinary(signed);
            boolean bool = signature.verify(hex);

            System.out.println("verify：" + bool);
            return bool;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
