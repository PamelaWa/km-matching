/***********************************
 * Copyright (c) 2018 Bryson J. Banks.
 * All rights reserved.
 **********************************/

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Scanner;

public class KM {
	
	private static int size;	// size of the square matrix = num of cols = num of rows
	private static int[][] w;	// weight of edges (x,y)
	
	//private static HashSet<Integer> X;	// all nodes in X partition
	private static HashSet<Integer> Y;	// all nodes in Y partition
	
	private static HashSet<Integer> freeX;	// set of free x nodes
	private static HashSet<Integer> freeY;	// set of free y nodes
	
	private static int[] xMatching;  	// mapping of (x,y) edges in matching, xMatching[x] == y
	private static int[] yMatching;  	// mapping of (x,y) edges in matching, yMatching[y] == x
	
	private static int[] xLabels;  		// labels for x nodes
	private static int[] yLabels;  		// labels for y nodes
	
	private static HashSet<Integer> S;	// working set of x nodes in alternating tree
	private static HashSet<Integer> T;	// working set of y nodes in alternating tree	

	// Takes in an input file containing a square matrix and size representing
	// a weighted bipartite graph. Using the Kuhn-Munkres algorithm, finds the
	// maximum weighted matching for the given input graph, then outputs the
	// weight of the matching followed by a sorted list of edges in the matching
	public static void main(String[] args) {
		// verify one argument: inputFile
		if (args.length != 1) {
			System.out.println("Error. Java program KM expects 1 argument specifying an input file.");
			return;
		}
		String inputFile = args[0];
				
		// parse the input file - will initialize size and w
		if (!parseInput(inputFile)) {
			return;
		}
		
		long startTime=System.nanoTime();
		
		// 1. generate initial labeling l and matching M in equality graph E
		init();
				
		// 2. If M is perfect, stop. Otherwise pick free node u from set X. Set S = {u} and T = {}
		while (!isMatchingPerfect()) {
			int u = getFreeX();
			S.clear();
			S.add(u);
			T.clear();
					
			// Go to 3. Augment matching by finding an augmenting path
			augmentMatching();
		}
				
		// print weight of matching followed by a sorted list of edges in the matching
		printMatching();
		
		long endTime=System.nanoTime();
		long totalTime=endTime-startTime;
		System.out.println("Total time taken for KM is "+totalTime);
	}
	
	// initialize variables for KM algorithm
	private static void init() {
		//X = new HashSet<>();
		Y = new HashSet<>();
		S = new HashSet<>();
		T = new HashSet<>();
		freeX = new HashSet<>();
		freeY = new HashSet<>();
		xMatching = new int[size];
		yMatching = new int[size];
		xLabels = new int[size];
		yLabels = new int[size];
		for (int x = 0; x < size; x++) {
			//X.add(x);
			Y.add(x);
			freeX.add(x);
			freeY.add(x);
			xMatching[x] = -1;
			yMatching[x] = -1;
			int maxEdgeWeight = 0;
			for (int y = 0; y < size; y++) {
				maxEdgeWeight = Math.max(w[x][y], maxEdgeWeight);
			}
			xLabels[x] = maxEdgeWeight;
			yLabels[x] = 0;
		}
	}
	
	// attempt to find augmenting path and augment M, may have
	// to update labels along the way
	private static void augmentMatching() {
		while (true) {
			// 3. If N(S) = T, update labels
			HashSet<Integer> N = getNofS();
			if (N.equals(T)) {
				updateLabels();
			}
			
			// 4. If N(S) != T, pick node y from N(S)-T
			N = getNofS();
			if (!N.equals(T)) {
				HashSet<Integer> nMinusT = N;
				nMinusT.removeAll(T);
				Iterator<Integer> it = nMinusT.iterator();
				int y = it.next();
				
				// 4a. If y is free, the path from u to y is an augmenting path. Augment M. Go to 2.
				if (freeY.contains(y)) {
					flipAugmentingTree(y);
					return;
				}
				
				// 4b. If y is matched, say to z, extend alternating tree: Set S = S U {z} and T = T U {y}. Go to 3.
				else {
					int z = yMatching[y];
					S.add(z);
					T.add(y);
				}
			}
		}
	}
	
	// from the built augmenting tree, flip the
	// augmenting path to increase size of matching
	private static void flipAugmentingTree(int y) {
		int x = getParentOfY(y);
		if (!freeX.contains(x)) {
			flipAugmentingTree(xMatching[x]);
		}
		xMatching[x] = y;
		yMatching[y] = x;
		freeX.remove(x);
		freeY.remove(y);
	}
	
	// given node y in augmenting tree, find parent x
	// on its tight edge that isn't currently matched to it
	private static int getParentOfY(int y) {
		for (int x : S) {
			if (isEdgeTight(x,y) && xMatching[x] != y) {
				return x;
			}
		}
		return -1;
	}

	// improve the node labelling in order to increase
	// tight edges in the equality graph
	private static void updateLabels() {
		// let alpha = min{ l(x) + l(y) - w(x,y) }, for all x in S and y not in T
		int alpha = Integer.MAX_VALUE;
		for (int x : S) {
			HashSet<Integer> yMinusT = new HashSet<>(Y);
			yMinusT.removeAll(T);
			for (int y : yMinusT) {
				int slack = getSlack(x, y);
				if (slack < alpha) {
					alpha = slack;
				}
			}
		}
		// update all nodes in S
		for (int x : S) {
			xLabels[x] -= alpha;
		}
		// update all nodes in T
		for (int y : T) {
			yLabels[y] += alpha;
		}
	}
	
	// get the neighbor set of set S
	private static HashSet<Integer> getNofS() {
		HashSet<Integer> N = new HashSet<>();
		for (int x : S) {
			for (int y = 0; y < size; y++) {
				if (isEdgeTight(x, y)) {
					N.add(y);
				}
			}
		}
		return N;
	}
	
	// returns true if given edge is tight, false if not
	private static boolean isEdgeTight(int x, int y) {
		return (getSlack(x, y) == 0);
	}
	
	// returns the calculated slack for the given edge
	private static int getSlack(int x, int y) {
		return (xLabels[x] + yLabels[y]) - w[x][y];
	}
	
	// return the size of the current matching
	private static int getMatchingSize() {
		int mSize = 0;
		for (int i = 0; i < size; i++) {
			if (xMatching[i] != -1) {
				mSize++;
			}
		}
		return mSize;
	}
	
	// returns true if current matching is perfect, false if not
	private static boolean isMatchingPerfect() {
		return (getMatchingSize() == size);
	}
	
	// returns the weight of the current matching
	private static int getMatchingWeight() {
		int weight = 0;
		for (int i = 0; i < size; i++) {
			if (xMatching[i] != -1) {
				weight += w[i][xMatching[i]];
			}
		}
		return weight;
	}
	
	// gets a free x node from X
	private static int getFreeX() {
		for (int x : freeX) {
			return x;
		}
		return -1;
	}
	
	// prints the weight of the matching followed by a sorted
	// list of the edges in the matching
	private static void printMatching() {
		System.out.println(getMatchingWeight());
		for (int i = 0; i < size; i++) {
			System.out.println("(" + (i+1) + "," + (xMatching[i]+1) + ")");
		}
	}

	// parse given input file
	// returns true if success, or false if parsing fails
	private static boolean parseInput(String inputFile) {
		// create file scanner object
		Scanner scanner;
		try {
			scanner = new Scanner(new File(inputFile));
		} catch (FileNotFoundException e) {
			System.out.println("Error. File not found: " + inputFile);
			return false;
		}
					
		// parse the matrix size from first line
		size = parseSize(scanner);
		if (size < 0) {
			System.out.println("Error. Failed to parse matrix size. Input file is incorrectly formatted.");
			scanner.close();
			return false;
		}

		// parse the square matrix values
		w = parseMatrix(scanner, size);
		if (w == null) {
			System.out.println("Error. Failed to parse weight matrix. Input file is incorrectly formatted.");
			scanner.close();
			return false;
		}
			
		// done parsing file, success
		scanner.close();
		return true;
	}

	// parse input file to retrieve matrix size
	// returns size, or -1 if parsing fails
	private static int parseSize(Scanner scanner) {
		if (scanner.hasNextInt()) {
			return scanner.nextInt();
		} else {
			return -1;
		}
	}		

	// parse input file to retrieve square matrix
	// returns matrix, or null if parsing fails
	private static int[][] parseMatrix(Scanner scanner, int size) {
		int[][] matrix = new int[size][size];
		for (int rowIndex = 0; rowIndex < size; rowIndex++) {
			for (int colIndex = 0; colIndex < size; colIndex++) {
				if (scanner.hasNextInt()) {
					matrix[rowIndex][colIndex] = scanner.nextInt();
				} else {
					return null;
				}
			}
		}
		return matrix;
	}
}
