package company;


import java.security.*;

public class RSASignUtils {
    private static final String ALGORITHM = "RSA";
    private static final int KEY_SIZE = 2048;
    private static final String SIGNATURE_ALGORITHM = "Sha1WithRSA";

    public static KeyPair generateKeyPair() throws Exception {
        KeyPairGenerator gen = KeyPairGenerator.getInstance(ALGORITHM);

        gen.initialize(KEY_SIZE);
        return gen.generateKeyPair();
    }

    public static byte[] sign(byte[] data, PrivateKey priKey) throws Exception {
        Signature sign = Signature.getInstance(SIGNATURE_ALGORITHM);
        byte[] signInfo;

        sign.initSign(priKey);
        sign.update(data);

        signInfo = sign.sign();
        return signInfo;
    }

    public static boolean verify(byte[] data, byte[] signInfo, PublicKey pubKey) throws Exception {
        Signature sign = Signature.getInstance(SIGNATURE_ALGORITHM);
        boolean verify;

        sign.initVerify(pubKey);
        sign.update(data);

        verify = sign.verify(signInfo);
        return verify;
    }



}