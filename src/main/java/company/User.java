package company;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static company.ECDSAUtils.*;
import static company.ECDSAUtils.getKeyPair;


public class User {

    public  PublicKey publicKey;
    public  PrivateKey privateKey;
    public  KeyPair keyPair;

    public HashMap<String, TxOut> UTXOs = new HashMap<String, TxOut>();

    public User() throws Exception {
        keyPair = getKeyPair();
        publicKey = keyPair.getPublic();
        privateKey = keyPair.getPrivate();
    }

    public double getBalance() {
        double total = 0;
        for (Map.Entry<String, TxOut> item: Main.UTXOs.entrySet()){
            TxOut UTXO = item.getValue();
            if(UTXO.isMine(publicKey)) {
                UTXOs.put(UTXO.txOutId, UTXO);
                total += UTXO.amount ;
            }
        }
        return total;
    }

    public Transaction sendFunds(PublicKey recipient, double amount) {
        if(getBalance() < amount) {
            System.out.println("#Not Enough funds to send transaction. Transaction Discarded.");
            return null;
        }
        ArrayList<TxIn> inputs = new ArrayList<TxIn>();

        float total = 0;
        for (Map.Entry<String, TxOut> item: UTXOs.entrySet()){
            TxOut UTXO = item.getValue();
            total += UTXO.amount;
            inputs.add(new TxIn(UTXO.txOutId));
            if(total > amount) break;
        }

        Transaction newTransaction = new Transaction(publicKey, recipient , amount, inputs);
        newTransaction.genSignature(privateKey);

        for(TxIn input: inputs){
            UTXOs.remove(input.txOutId);
        }

        return newTransaction;
    }

}