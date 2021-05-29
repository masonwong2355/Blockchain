package company;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;

public class utils {

    public static String kCov(Key key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }
    public static String calculateHash(String data){
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /////////////////////////////////////////////////////////////////////
    // Merkle
    public static String generateMerkleTreeRoot(List<Transaction> transactions) {
        List<String> tempTxList = new ArrayList<String>();

        for (int i = 0; i < transactions.size(); i++) {
            tempTxList.add(transactions.get(i).id);
        }
//        System.out.println("tempTxList: " + tempTxList);

        List<String> newTxList = getNewTxList(tempTxList);
//        System.out.println("newTxList: " + newTxList);

        while (newTxList.size() != 1) {
            newTxList = getNewTxList(newTxList);
//            System.out.println("newTxList in while loop: " + newTxList);
        }

        String root = newTxList.get(0);
        return root;
    }

    public static List<String> getNewTxList(List<String> tempTxList) {

        List<String> newTxList = new ArrayList<String>();
        int index = 0;
        while (index < tempTxList.size()) {
            // left node
            String left = tempTxList.get(index);
            index++;

            // right node
            String right = "";
            if (index != tempTxList.size()) {
                right = tempTxList.get(index);
            }

            // sha2 hex value
            String sha2HexValue = getSHA2HexValue(left + right);
            newTxList.add(sha2HexValue);
            index++;

        }
        return newTxList;
    }

    public static String getSHA2HexValue(String str) {
        byte[] cipher_byte;
        try{
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(str.getBytes());
            cipher_byte = md.digest();
            StringBuilder sb = new StringBuilder(2 * cipher_byte.length);
            for(byte b: cipher_byte) {
                sb.append(String.format("%02x", b&0xff) );
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    /////////////////////////////////////////////////////////////////////
    // check block when creating
    public static boolean hashMatchesDifficulty(String hash, int difficulty) {
        String hashInBinary = hexToBinary(hash);
        String requiredPrefix = repeat("0", difficulty);
        return hashInBinary.startsWith(requiredPrefix);
    }

    public static String hexToBinary(String hex) {
        String binary = "";
        hex = hex.toUpperCase();

        // initializing the HashMap class
        HashMap<Character, String> hashMap = new HashMap<Character, String>();

        // storing the key value pairs
        hashMap.put('0', "0000");
        hashMap.put('1', "0001");
        hashMap.put('2', "0010");
        hashMap.put('3', "0011");
        hashMap.put('4', "0100");
        hashMap.put('5', "0101");
        hashMap.put('6', "0110");
        hashMap.put('7', "0111");
        hashMap.put('8', "1000");
        hashMap.put('9', "1001");
        hashMap.put('A', "1010");
        hashMap.put('B', "1011");
        hashMap.put('C', "1100");
        hashMap.put('D', "1101");
        hashMap.put('E', "1110");
        hashMap.put('F', "1111");

        int i;
        char ch;
        for (i = 0; i < hex.length(); i++) {
            ch = hex.charAt(i);
            if (hashMap.containsKey(ch))
                binary += hashMap.get(ch);
            else {
                binary = "Invalid Hexadecimal String";
                return binary;
            }
        }
        return binary;
    }

    public static String repeat(String str, int difficulty) {
        String result = "";
        for (int i = 0; i < difficulty; i++) {
            result += str;
        }
        return result;
    }

    public static boolean isValidNewBlock(Block newBlock, Block previousBlock) {
        if (previousBlock.index + 1 != newBlock.index) {
            System.out.println("invalid index");
            return false;
        } else if (previousBlock.hash != newBlock.previousHash) {
            System.out.println("invalid previoushash");
            return false;
        } else if (!generateMerkleTreeRoot(newBlock.transactions).equals(newBlock.hash)) {
            System.out.println("invalid hash");
            return false;
        } else return true;
    }
}
