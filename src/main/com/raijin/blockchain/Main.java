package com.raijin.blockchain;

import com.raijin.blockchain.mining.Miner;
import com.raijin.blockchain.storage.Blockchain;
import com.raijin.blockchain.transactions.Client;
import com.raijin.blockchain.transactions.ClientFactory;
import com.raijin.blockchain.utils.IOUtils;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    /**
     *
     */

    private static final Blockchain bc = Blockchain.getInstance();
    private static final List<Miner> miners = new ArrayList<>();
    private static final Map<String, Client> clients = new HashMap<>();

    private static final ExecutorService service = Executors.newCachedThreadPool();

    private static boolean isRunning = false;

    public static void main(String[] args) throws Exception {

        System.out.println("RUNNING");
        System.out.print("Please set path for keys: ");
        Scanner scanner = new Scanner(System.in);

        setPath(scanner);

        System.out.println("Path is correct. Please check out the commands using help.");

        String command;
        while (!(command = scanner.nextLine()).contains("exit")) {

            handler(command.toLowerCase().trim());

        }

        System.out.println(miners);
        System.out.println("Stopped by user");
        System.exit(-1);

    }

    static void setPath(Scanner scanner) {
        String pattern = "([a-zA-Z]:)?(\\\\\\\\?[a-zA-Z0-9_.-]+)*\\\\?\\\\?";
        String path;
        while (!(path = scanner.nextLine()).matches(pattern)) {
            System.err.println("Incorrect path! Try again.");
        }
        ClientFactory.setPath(path);
    }

    static void handler(String line) {
        if (line.equals("clients"))
            infoHandler();
        else if (line.startsWith("create"))
            createHandler(line);
        else if (line.startsWith("transaction"))
            transactionHandler(line);
        else if (line.startsWith("remove"))
            removeHandler(line);
        else if (line.equals("run"))
            runHandler();
        else if (line.startsWith("balance"))
            balanceHandler(line);
        else if (line.equals("help"))
            helpInfo();
        else if (line.equals("blocks"))
            bc.blockchain().forEach(IOUtils::printBlockState);
        else
            System.err.println("Unknown input");

    }

    static void helpInfo() {
        String info = "~~WELCOME~~!\n" +
                "Here is some commands to run:\n" +
                "create - creates new miner, require 3 parameters:\n" +
                "example: create miner Tom 10\n" +
                "type - [miner, default miner];\n" +
                "default miner is just a miner without ability to perform transactions\n" +
                "miner %name% - miner, which is also may act like a client - gains reward for each generated block and can run transaction between other clients;\n" +
                "last parameter is number of blocks to generate;" +
                "\n\ntransaction %sender% %quantity% %receiver% - performs a transaction between clients;\n" +
                "example: transaction Tom 10 Anna\n" +
                "only works while service is running. Clients should be created as well.\n" +
                "\n\nclient %name% - creates a client, which cannot mine and have no coins - but can perform transactions! (if someone gifted him some coins)\n" +
                "example: client Tom\n" +
                "\n\nrun - just run" +
                "\n\nremove %name% - removes specified client if exists\n" +
                "example: remove Tom" +
                "\n\nbalance %name% - shows balance for client, example: balance Tom" +
                "\n\nblocks - shows current blocks info" +
                "\n\nexit - stop awaiting commands, just mining rest of blocks" +
                "\n\nrealexit - terminating all";

        System.out.println(info);

    }

    static void balanceHandler(String line) {
        String[] commands = line.split(" ");

        String name = commands[1];

        if (!clients.containsKey(name)) {
            System.err.println("Client " + name + " does not exist!");
            return;
        }

        System.out.println(name + clients.get(name).getBalance());
    }

    static void createHandler(String line) {
        if (line.contains("miner")) {
            if (validate(line, 4))
                minerHandler(line);
        }
        else if (line.contains("client"))
            if (validate(line, 3))
                clientHandler(line);
    }

    static void minerHandler(String line) {
        if (line.contains("default miner")) {
            String[] commandsChain = line.split(" ");
            try {
                handleMiner(new Miner(bc, Integer.parseInt(commandsChain[3])));
            } catch (IOException | NumberFormatException exception) {
                System.err.println("Invalid number of blocks: " + commandsChain[3]);
                return;
            }
        } else if (line.contains("miner")) {
            String[] commandsChain = line.split(" ");

            try {
                String clientName = commandsChain[2];
                if (clients.containsKey(clientName)) {
                    System.err.println("Client " + clientName + " already exists!");
                    return;
                }
                int blockNum = Integer.parseInt(commandsChain[3]);
                Client cli = ClientFactory.getFactory().create(clientName);
                bc.createClient(cli);
                clients.put(clientName, cli);

                handleMiner(new Miner(bc, blockNum, cli));

            } catch (NumberFormatException x) {
                System.err.println("Invalid number of blocks: " + commandsChain[3]);
                return;
            } catch (Exception x) {
                System.err.println("Unable to create new client...");
                return;
            }
        } else return;
        System.out.println("Command executed successfully");

    }

    static void handleMiner(Miner miner) {
        miners.add(miner);
        if (isRunning) service.submit(miner);
    }

    static void clientHandler(String line) {
        String[] commandsChain = line.split(" ");
        String name = commandsChain[2];
        if (clients.containsKey(name)) {
            System.err.println("Client " + name + " already exists! Run \"clients\" command to checkout active clients");
        }
        try {
            Client cli = ClientFactory.getFactory().create(commandsChain[2]);
            bc.createClient(cli);
            clients.put(name, cli);
        } catch (Exception x) {
            System.err.println("Unable to create new message client...");
            return;
        }
        System.out.println("Message client created successfully");
    }

    static void removeHandler(String line) {
        String name = line.split(" ")[1];
        if (clients.containsKey(name)) {
            bc.removeClient(clients.get(name));
            clients.remove(name);
        }
        else System.err.println("Client " + name + " does not exist.");
    }

    static void transactionHandler(String line) {
        String[] commandsChain = line.split(" ");
        if (!isRunning) {
            System.err.println("Service is not running!");
            return;
        }
        if (!validate(line, 4)) {
            System.err.println("Invalid command format - required 4 parameters!");
            return;
        }
        try {

            String sender = commandsChain[1];
            int quantity = Integer.parseInt(commandsChain[2]);
            String receiver = commandsChain[3];

            Client s = clients.get(sender);
            Client r = clients.get(receiver);

            if (s == null) {
                System.err.println(String.format("Client %s does not exist!", sender));
                return;
            }
            if (r == null) {
                System.err.println(String.format("Client %s does not exist!", receiver));
                return;
            }

            bc.executeTransaction(s, r, quantity);

        } catch (NumberFormatException x) {
            System.err.println("Ïnvalid quantity: " + commandsChain[2] + "!");
        }
    }

    static boolean validate(String line, int num) {
        if (line.split(" ").length != num) {
            System.err.println("Invalid command format: for creating operations 4 keywords required!");
            return false;
        }
        return true;
    }

    static void infoHandler() {
        System.out.println(clients.keySet());
    }

    static void runHandler() {
        if (miners.isEmpty()) {
            System.err.println("No miners.");
            return;
        }
        isRunning = true;
        miners.forEach(service::submit);
    }
}
