// Cracker.java
/*
 Generates SHA hashes of short strings in parallel.
*/

import java.security.*;
import java.util.concurrent.CountDownLatch;

public class Cracker {
	// Array of chars used to produce strings
	public static final char[] CHARS = "abcdefghijklmnopqrstuvwxyz0123456789.,-!".toCharArray();
	
	
	/*
	 Given a byte[] array, produces a hex String,
	 such as "234a6f". with 2 chars for each byte in the array.
	 (provided code)
	*/
	public static String hexToString(byte[] bytes) {
		StringBuffer buff = new StringBuffer();
		for (int i = 0; i < bytes.length; i++) {
			int val = bytes[i];
			val = val & 0xff;  // remove higher bits, sign
			if (val < 16) buff.append('0'); // leading 0
			buff.append(Integer.toString(val, 16));
		}
		return buff.toString();
	}
	
	/*
	 Given a string of hex byte values such as "24a26f", creates
	 a byte[] array of those values, one byte value -128..127
	 for each 2 chars.
	 (provided code)
	*/
	public static byte[] hexToArray(String hex) {
		byte[] result = new byte[hex.length() / 2];
		for (int i = 0; i < hex.length(); i += 2) {
			result[i / 2] = (byte) Integer.parseInt(hex.substring(i, i + 2), 16);
		}
		return result;
	}
	
	
	public static void main(String[] args) {
		if (args.length == 0) {
			System.out.println("Args: target length [workers]");
			System.exit(1);
		}
		// args: targ len [num]
		String targ = args[0];
		if (args.length == 1) {
			generateAndPrintHash(targ);
			return;
		}
		byte[] target = hexToArray(targ);
		int len = Integer.parseInt(args[1]);
		int num = 1;
		if (args.length > 2) {
			num = Integer.parseInt(args[2]);
		}
		num = Math.min(num, CHARS.length);
		CountDownLatch countDownLatch = new CountDownLatch(num);
		
		int part = CHARS.length / num;
		for (int i = 0; i < num; i++) {
			int start = i * part;
			int end = i == num - 1 ? CHARS.length : start + part;
			new Thread(new Worker(countDownLatch, start, end, len, target)).start();
		}
		try {
			countDownLatch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.exit(1);
		}
		System.out.println("all done");
		
		// a! 34800e15707fae815d7c90d49de44aca97e2d759
		// xyz 66b27417d37e024c46526c2f6d358a754fc552f3
		
		// YOUR CODE HERE
	}
	
	private static void generateAndPrintHash(String password) {
		MessageDigest messageDigest;
		try {
			messageDigest = MessageDigest.getInstance("SHA");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return;
		}
		messageDigest.reset();
		messageDigest.update(password.getBytes());
		byte[] hash = messageDigest.digest();
		System.out.println(hexToString(hash));
		System.out.println("all done");
	}
	
	private static class Worker implements Runnable {
		private MessageDigest messageDigest;
		private final CountDownLatch countDownLatch;
		private final int start, end, maxLength;
		private final byte[] target;
		
		public Worker(CountDownLatch countDownLatch, int start, int end, int maxLength, byte[] target) {
			this.countDownLatch = countDownLatch;
			this.start = start;
			this.end = end;
			this.maxLength = maxLength;
			this.target = target;
			try {
				messageDigest = MessageDigest.getInstance("SHA");
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
		}
		
		public void run() {
			for (int i = start; i < end; i++) {
				generate(Character.toString(CHARS[i]));
			}
			countDownLatch.countDown();
		}
		
		private void generate(String current) {
			if (current.length() > maxLength) {
				return;
			}
			messageDigest.reset();
			messageDigest.update(current.getBytes());
			byte[] hash = messageDigest.digest();
			if (MessageDigest.isEqual(hash, target)) {
				System.out.println(current);
			}
			for (char c : CHARS) {
				generate(current + c);
			}
		}
	}
}
