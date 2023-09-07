// Bank.java

/*
 Creates a bunch of accounts and uses threads
 to post transactions to the accounts concurrently.
*/

import java.io.*;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

public class Bank {
	public static final int ACCOUNTS = 20;     // number of accounts
	public static final int ACCOUNT_BALANCE = 1000;
	private final List<Account> accounts;
	private final BlockingQueue<Transaction> blockingDeque;
	private final Queue<Transaction> transactions;
	private final CountDownLatch countDownLatch;
	
	public Bank(int numWorkers) {
		this.accounts = new ArrayList<>();
		this.blockingDeque = new ArrayBlockingQueue<>(ACCOUNTS);
		this.transactions = new LinkedList<>();
		this.countDownLatch = new CountDownLatch(numWorkers);
	}
	
	/*
		 Reads transaction data (from/to/amt) from a file for processing.
		 (provided code)
		 */
	public void readFile(String file) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			
			// Use stream tokenizer to get successive words from file
			StreamTokenizer tokenizer = new StreamTokenizer(reader);
			
			while (true) {
				int read = tokenizer.nextToken();
				if (read == StreamTokenizer.TT_EOF) break;  // detect EOF
				int from = (int) tokenizer.nval;
				
				tokenizer.nextToken();
				int to = (int) tokenizer.nval;
				
				tokenizer.nextToken();
				int amount = (int) tokenizer.nval;
				
				this.transactions.add(new Transaction(from, to, amount));
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	/*
	 Processes one file of transaction data
	 -fork off workers
	 -read file into the buffer
	 -wait for the workers to finish
	*/
	public void processFile(String file, int numWorkers) throws InterruptedException {
		readFile(file);
		Runnable worker = getWorker();
		for (int i = 0; i < numWorkers; i++) {
			new Worker(worker, "Worker" + i).start();
		}
		
		while (!transactions.isEmpty()) {
			blockingDeque.put(transactions.remove());
		}
		blockingDeque.put(new Transaction(-1, 0, 0));
		countDownLatch.await();
	}
	
	private Runnable getWorker() {
		return () -> {
			while (true) {
				try {
					Transaction t = blockingDeque.take();
					if (t.getFrom() < 0) {
						blockingDeque.put(t);
						countDownLatch.countDown();
						break;
					}
					accounts.get(t.getFrom()).withdraw(t.getAmount());
					accounts.get(t.getTo()).deposit(t.getAmount());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};
	}
	
	public void initAccounts() {
		for (int i = 0; i < ACCOUNTS; i++) {
			accounts.add(new Account(i, ACCOUNT_BALANCE));
		}
	}
	
	public void printResults() {
		for (Account account : accounts) {
			System.out.println(account.toString());
		}
	}
	
	
	/*
	 Looks at commandline args and calls bank.Bank processing.
	*/
	public static void main(String[] args) {
		// deal with command-lines args
		if (args.length == 0) {
			System.out.println("Args: transaction-file [num-workers [limit]]");
			System.exit(1);
		}
		
		String file = args[0];
		
		int numWorkers = 1;
		if (args.length >= 2) {
			numWorkers = Integer.parseInt(args[1]);
		}
		
		Bank bank = new Bank(numWorkers);
		bank.initAccounts();
		try {
			bank.processFile(file, numWorkers);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		bank.printResults();
	}
	
	private static class Worker extends Thread {
		public Worker(Runnable target, String name) {
			super(target, name);
		}
		
		@Override
		public void run() {
			super.run();
		}
	}
}

