package company;


public class TxIn {
    public String txOutId;
    public TxOut UTXO;

    public TxIn(String txOutId) {
        this.txOutId = txOutId;
    }
}