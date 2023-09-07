// Account.java

/*
 Simple, thread-safe bank.Account class encapsulates
 a balance and a transaction count.
*/
public class Account {
	private final int id;
	private int balance;
	private int transactions;
	
	
	public Account(int id, int balance) {
		this.id = id;
		this.balance = balance;
		transactions = 0;
	}
	
	public int getId() {
		return id;
	}
	
	public synchronized int getBalance() {
		return balance;
	}
	
	public synchronized int getTransactions() {
		return transactions;
	}
	
	public synchronized void deposit(int amount) {
		if (amount < 0) {
			throw new RuntimeException("Invalid amount");
		}
		this.balance += amount;
		this.transactions++;
	}
	
	public synchronized void withdraw(int amount) {
		if (amount < 0) {
			throw new RuntimeException("Invalid amount");
		}
		this.balance -= amount;
		this.transactions++;
	}
	
	@Override
	public String toString() {
		return "bank.Account{" +
				"id=" + id +
				", balance=" + balance +
				", transactions=" + transactions +
				'}';
	}
}
