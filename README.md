# kuhn-munkres-matching
weighted bipartite matching using the Kuhn-Munkres (Hungarian) algorithm

## EE 382V Social Computing - Assignment 1 - Problem 4

```
4. (35 points) This question requires you to implement weighted bipartite matching using Kuhn-Munkres
algorithm. The goal is to understand how the algorithm works; we will not evaluate your submission on
the running time. The input to your program would be a square matrix w of non-negative integers, where
w[i, j] is the weight of the edge between the item i and the bidder j. The program should output the
weight of the maximum weighted matching followed by the sorted list of edges in the matching. The name
of your program should be KM. We will run the program as java KM “inputFileName”
A sample input and output is shown below.

input.txt
--------
3 // number of rows and columns
12 2 4
8 7 6
7 5 2

The program should produce

23 // weight of the matching
(1,1)
(2,3)
(3,2)
```
