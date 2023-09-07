// Transaction.java
/*
 (provided code)
 bank.Transaction is just a dumb struct to hold
 one transaction. Supports toString.
*/
public class Transaction {
	private final int from;
	private final int to;
	private final int amount;
	
   	public Transaction(int from, int to, int amount) {
		this.from = from;
		this.to = to;
		this.amount = amount;
	}
	
	public int getFrom() {
		return from;
	}
	
	public int getTo() {
		return to;
	}
	
	public int getAmount() {
		return amount;
	}
	
	public String toString() {
		return("from:" + from + " to:" + to + " amt:" + amount);
	}
}
