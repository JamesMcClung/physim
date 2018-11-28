package main;

import java.util.function.Supplier;

import util.ValAsStr;

public class Tester {

	public static void main(String[] args) {
		/*
		 * Change this as needed. Make a new testn() method for each test.
		 */
		final Test test = test1();
		
		/* 
		 * Runs each test twice. The first run of the first test is often longer than normal, so ignore it.
		 */
		for (int j = 0; j < 2; j++)
			for (var subject : test.subjects) {
				long startTime = System.currentTimeMillis();
				for (int i = 0; i < test.sampleSize; i++) {
					subject.run();
				}
				long endTime = System.currentTimeMillis();
				System.out.println(String.format("Subject: %s\nTime:\t %d ms\n\n", subject.getName(), endTime-startTime));
			}
	}
	
	/*
	 * Compares ValAsStr to calling String.format each time when the number is constant.
	 * 
	 * Result: ValAsStr is over 100 times faster, even after controlling for order!
	 * This code was also modified to test the case where the number is random, and they
	 * had the same performance.
	 */
	private static Test test1() {
		final double someNumber = Math.random() * 100;
		final Supplier<Object> numberGiver = () -> someNumber;
		
		Subject s1 = new Subject() {
			Supplier<String> getter = () -> String.format("x = %2.1e m", numberGiver.get());
			public void run() {
				getter.get();
			}
			public String getName() {
				return "Format";
			}
		};
		
		Subject s2 = new Subject() {
			ValAsStr getter = new ValAsStr("x = %2.1e m", numberGiver);
			public void run() {
				getter.get();
			}
			public String getName() {
				return "ValAsStr";
			}
		};
		
		return new Test(10000, s1, s2);
	}
	
	private static interface Subject {
		void run();
		String getName();
	}
	
	private static class Test {
		Test(int sampleSize, Subject...subjects) {
			this.sampleSize = sampleSize;
			this.subjects = subjects;
		}
		
		final Subject[] subjects;
		final int sampleSize;
	}
	
}
