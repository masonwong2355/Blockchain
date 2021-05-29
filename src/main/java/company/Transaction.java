package company;

import com.google.gson.annotations.Expose;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;

import static company.ECDSAUtils.*;
import static company.utils.*;

public class Transaction {

    @Expose
    public String id;

    // him
//    public TxIn txIns;
//    public TxOut txOuts;

    // Ben
    @Expose
    public String signature;

    @Expose(serialize = false)
    public PublicKey sender;
    @Expose(serialize = false)
    public PublicKey reciepient;

    @Expose
    public double amount;
    @Expose
    public ArrayList<TxIn> txIns = new ArrayList<TxIn>();
    @Expose
    public ArrayList<TxOut> txOuts = new ArrayList<TxOut>();

    private static int sequence = 0;

    // ben
    public Transaction(PublicKey sender, PublicKey reciepient , double amount, ArrayList<TxIn> txIns) {
        this.sender = sender;
        this.reciepient = reciepient;
        this.amount = amount;
        this.txIns = txIns;
    }

    // Ben
    public void genSignature(PrivateKey privateKey){
        try {
            String data = kCov(this.sender) + kCov(this.reciepient) + amount;
            this.signature = signECDSA(privateKey, data);
        }catch(Exception e) {
            throw new RuntimeException(e);
        }
//        return sign;
    }

    public boolean verifySignature() {
        String data = kCov(this.sender) + kCov(this.reciepient) + amount;
        return verifyECDSA(sender, signature, data);
    }

    public String getTransactionId() {
        sequence++;
        return calculateHash(kCov(sender) + kCov(reciepient) + amount + sequence);
    }

    public boolean processTransaction() {
        if(verifySignature() == false) {
            System.out.println("#Transaction Signature failed to verify");
            return false;
        }

        for(TxIn i : txIns) {
            i.UTXO = Main.UTXOs.get(i.txOutId);
        }

        double leftOver = getInputsAmount() - amount;

        id = getTransactionId();
        txOuts.add(new TxOut( this.reciepient, amount, id));
        txOuts.add(new TxOut( this.sender, leftOver, id));

        for(TxOut o : txOuts) {
            Main.UTXOs.put(o.txOutId , o);
        }

        for(TxIn i : txIns) {
            if(i.UTXO == null) continue;
            Main.UTXOs.remove(i.UTXO.txOutId);
        }

        return true;
    }

    public float getInputsAmount() {
        float total = 0;
        for(TxIn i : txIns) {
            if(i.UTXO == null) continue;
            total += i.UTXO.amount;
        }
        return total;
    }

}