package company;

import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static company.utils.*;
import static java.lang.System.exit;

public class Main {
        static final int BLOCK_GENERATION_INTERVAL = 4;
        static final int DIFFICULTY_ADJUSTMENT_INTERVAL = 2;
        static final int COINBASE_AMOUNT = 50;
        static final int TOTAL_COIN_SUPPLY = 21000000;
        static ArrayList<Block> chain = new ArrayList<Block>();
        public static HashMap<String, TxOut> UTXOs = new HashMap<String, TxOut>();
        public static HashMap<String, User> Users = new HashMap<String, User>();

        static User coinbase;


    public static void main(String[] args) throws Exception {
        // write your code here
        System.out.println("Block Chain Start!");

        coinbase = new User();
        String[] userName = {"Arthur", "Ben", "Him", "Sugar"};
        for (int i = 0; i < userName.length; i++) {
            User user = new User();
            Users.put(userName[i], user);
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        int operation = 0;
        while (operation != 6) {
            System.out.println("Please input operation :");
            System.out.println("1 : Create Block Chain");
            System.out.println("2 : Mining Block ");
            System.out.println("3 : Create Transaction ");
            System.out.println("4 : Print User State ");
            System.out.println("5 : Print Block Chain ");
            System.out.println("6 : Exit ");

            operation = Integer.parseInt(reader.readLine());
            switch (operation) {
                case 1:
                    User user = Users.get("Arthur");
                    Transaction genesisTransaction = new Transaction(coinbase.publicKey, user.publicKey, 100, null);
                    genesisTransaction.genSignature(coinbase.privateKey);

                    genesisTransaction.id = "0";
                    genesisTransaction.txOuts.add(new TxOut(genesisTransaction.reciepient, genesisTransaction.amount, genesisTransaction.id));
                    UTXOs.put(genesisTransaction.txOuts.get(0).txOutId, genesisTransaction.txOuts.get(0));

                    System.out.println("Creating and Mining Genesis block... ");
                    Block genesisBlock = createGenesisBlock();
                    genesisBlock.addTransaction(genesisTransaction);
                    chain.add(genesisBlock);
                    break;
                case 2:
                    for (int i = 0; i < userName.length; i++) {
                        System.out.print(i + ": " + userName[i] + "\t");
                    }
                    System.out.println();
                    int minerName = -1;
                    do {
                        System.out.print("Input miner number : ");
                        minerName = Integer.parseInt(reader.readLine());
                    } while(minerName < 0 || minerName > userName.length - 1);
                    try {
                        User miner = Users.get(userName[minerName]);
                        chain.add(findBlock(miner.publicKey, userName[minerName] + " find a block"));
                    } catch (Exception e) {
                        System.out.print(e);
                        System.out.print("User not find");
                    }
                    break;
                case 3:
                    for (int i = 0; i < userName.length; i++) {
                        System.out.print(i + ": " + userName[i] + "\t\t");
                    }
                    System.out.println();
                    int senderName = -1;
                    do {
                        System.out.print("Input sender number : ");
                        senderName = Integer.parseInt(reader.readLine());
                    } while(senderName < 0 || senderName > userName.length - 1);

                    int recipientName = -1;
                    do {
                        System.out.print("Input recipient number : ");
                        recipientName = Integer.parseInt(reader.readLine());
                    } while(recipientName < 0 || recipientName > userName.length - 1);

                    System.out.print("Input Amount : ");
                    double amount = Integer.parseInt(reader.readLine());

                    try {
                        User sender = Users.get(userName[senderName]);
                        User recipient = Users.get(userName[recipientName]);

                        Block previousBlock = chain.get(chain.size() - 1);
                        previousBlock.addTransaction(sender.sendFunds(recipient.publicKey, amount));

                        chain.set(chain.size() - 1, previousBlock);
                    } catch (Exception e) {
                        System.out.print("User not find");
                    }

                    break;
                case 4:
                    for (int i = 0; i < userName.length; i++) {
                        User u = Users.get(userName[i]);
                        System.out.println(userName[i] + " Balance : " + u.getBalance() + " public key : " + kCov(u.publicKey));
                    }
                    break;
                case 5:
                    printBlockChain();
                    break;
                case 6:
                    exit(0);
                case 7:
                    User arthur = Users.get("Arthur");
                    User ben = Users.get("Ben");
                    arthur.sendFunds(ben.publicKey, 20);
                    break;
                default:
                    System.out.println("Unknown operation, please input again");
            }
            System.out.println();
        }


        // Testing
        for (int i = 0; i < userName.length; i++) {
            User user = Users.get(userName[i]);
            System.out.println(userName[i] + " Amount : " + user.getBalance());
        }
        printBlockChain();
    }

    public static int getAdjustedDifficulty(Block latestBlock, ArrayList<Block> chain) {
        Block prevAdjustmentBlock = chain.get(chain.size() - DIFFICULTY_ADJUSTMENT_INTERVAL);
        int timeExpected = BLOCK_GENERATION_INTERVAL * DIFFICULTY_ADJUSTMENT_INTERVAL;
        double timeTaken = latestBlock.timestamp - prevAdjustmentBlock.timestamp;
        if (timeTaken < timeExpected / 2) {
            return prevAdjustmentBlock.difficulty + 1;
        } else if (timeTaken > timeExpected * 2) {
            return prevAdjustmentBlock.difficulty - 1;
        } else {
            return prevAdjustmentBlock.difficulty;
        }
    }

    public static int getDifficulty(ArrayList<Block> chain) {
        Block lastBlock = chain.get(chain.size() - 1);
        if (lastBlock.index % DIFFICULTY_ADJUSTMENT_INTERVAL == 0 && lastBlock.index != 0) {
            return getAdjustedDifficulty(lastBlock, chain);
        } else {
            return lastBlock.difficulty;
        }
    }

    // Ben
    public static Block createGenesisBlock() {
        int index = 0;
        double timestamp = new Date().getTime() / 1000;
        String previousBlockHash = "0";
        String blockData = "This is first block";
        String hash = calculateHash(index + previousBlockHash + timestamp + blockData);
        System.out.println("Genesis Block is created wish hash : " + hash);

        return new Block(kCov(coinbase.publicKey), index, hash, previousBlockHash, timestamp, blockData, DIFFICULTY_ADJUSTMENT_INTERVAL, 0);
    }

    // Ben
    public static Block generateNextBlock(String miner, String blockData, int difficulty, int nonce) {
        Block previousBlock = chain.get(chain.size() - 1);
        int nextIndex = previousBlock.index + 1;
        previousBlock.setMerkleRootHash();
        double nextTimestamp = new Date().getTime() / 1000;
        String nextHash = calculateHash(nextIndex + previousBlock.hash + nextTimestamp + blockData + nonce);
        Block newBlock = new Block(miner, nextIndex, nextHash, previousBlock.hash, nextTimestamp, blockData, difficulty, nonce);

        return newBlock;
    }

    // Ben
    public static Block findBlock(PublicKey miner, String data) {
        int nonce = 0;
        while (true) {
            int difficulty = getDifficulty(chain);
//            System.out.println(difficulty);
            Block newBlock = generateNextBlock(kCov(miner), data, difficulty, nonce);
            if (hashMatchesDifficulty(newBlock.hash, difficulty)) {
                // create Coinbase Transaction for block
                newBlock.addTransaction(createCoinBaseTransaction(miner));
                System.out.println("Mined A Block wish hash : " + newBlock.hash + " new block difficulty :" + newBlock.difficulty);
                return newBlock;
            }
            nonce++;
        }
    }

    public static String hashForBlock(Block block) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String blockInformation = block.index + block.previousHash + block.timestamp + block.data + block.nonce;
            byte[] hash = digest.digest(blockInformation.getBytes(StandardCharsets.UTF_8));
            StringBuffer hexString = new StringBuffer();

            for (int i = 0; i < hash.length; ++i) {
                String hex = Integer.toHexString(255 & hash[i]);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (Exception var7) {
            throw new RuntimeException(var7);
        }
    }


    public static void printBlockChain() {
        String blockchainJson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create().toJson(chain);
        System.out.println(blockchainJson);
        double blockHeight = chain.size();
        System.out.println("Block Height : " + blockHeight);

        System.out.println("UTXOs as follow : ");
        for (Map.Entry<String, TxOut> item : UTXOs.entrySet()) {
            TxOut UTXO = item.getValue();
            System.out.println("id : " + UTXO.txOutId);
            System.out.println("Amount : " + UTXO.amount);
            System.out.println("Recipient public key : " + kCov(UTXO.reciepient) + "\n");
        }
    }

    public static Transaction createCoinBaseTransaction(PublicKey miner) {
        try {
            Transaction coinBasetransaction = new Transaction(coinbase.publicKey, miner, COINBASE_AMOUNT, null);
            coinBasetransaction.genSignature(coinbase.privateKey);
            coinBasetransaction.id = "0";
            coinBasetransaction.txOuts.add(new TxOut(coinBasetransaction.reciepient, coinBasetransaction.amount, coinBasetransaction.id));
            UTXOs.put(coinBasetransaction.txOuts.get(0).txOutId, coinBasetransaction.txOuts.get(0));

            return coinBasetransaction;
        } catch (Exception e) {
            return null;
        }
    }



}
