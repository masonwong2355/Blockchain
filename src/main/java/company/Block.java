package company;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

import static  company.utils.*;
import static company.utils.generateMerkleTreeRoot;

public class Block {
    @Expose
    public int index;
    @Expose
    public double timestamp;
    @Expose
    public String hash;
    @Expose
    public String merkleRootHash;
    @Expose
    public String previousHash;
    @Expose
    public String data;
    @Expose
    public int difficulty;
    @Expose
    public int nonce;

    @Expose
    public ArrayList<Transaction> transactions;
    @Expose
    public String minerAddress;

    // Ben
    public Block(String minerAddress, int index, String hash, String previousHash, double timestamp, String data) {
        this.minerAddress = minerAddress;
        this.index = index;
        this.timestamp = timestamp;
        this.hash = hash;
        this.previousHash = previousHash;
        this.data = data;

        this.transactions = new ArrayList<Transaction>();
    }

    // Ben
    public Block(String minerAddress,int index, String hash, String previousHash, double timestamp, String data, int difficulty, int nonce) {
        this.minerAddress = minerAddress;
        this.index = index;
        this.timestamp = timestamp;
        this.hash = hash;
        this.previousHash = previousHash;
        this.data = data;
        this.difficulty = difficulty;
        this.nonce = nonce;

        this.transactions = new ArrayList<Transaction>();
    }

    public void setMerkleRootHash(){
        this.merkleRootHash = generateMerkleTreeRoot(transactions);
    }

    public boolean addTransaction(Transaction transaction) {

        if(transaction == null) return false;
        if((!"0".equals(previousHash)) && transaction.txIns != null ) {
            if((transaction.processTransaction() != true)) {
                System.out.println("Transaction failed to process. Discarded.");
                return false;
            }
        }
        transactions.add(transaction);
        return true;
    }
}
