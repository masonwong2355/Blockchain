package company;

import java.security.PublicKey;

import static company.utils.calculateHash;
import static company.utils.kCov;

public class TxOut {
    public String txOutId;
    public PublicKey reciepient;
    public double amount;
    public String parentTransactionId;

    public TxOut(PublicKey reciepient, double amount, String parentTransactionId) {
        this.reciepient = reciepient;
        this.amount = amount;
        this.parentTransactionId = parentTransactionId;
        this.txOutId = calculateHash(kCov(reciepient) + amount + parentTransactionId);
    }

    public boolean isMine(PublicKey publicKey) {
        return (publicKey == reciepient);
    }
}