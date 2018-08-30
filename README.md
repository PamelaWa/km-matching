# kuhn-munkres-matching
weighted bipartite matching using the Kuhn-Munkres (Hungarian) algorithm

## Weighted Bipartite Matching

Weighted bipartite graphs are bipartite graphs in which each edge (x,y) has a weight,
or value, w(x,y).

The weight of a matching M is the sum of the weights of edges in M.

The maximum weighted matching M for a graph G has a matching weight greater than or equal
to any other matching M' for graph G.

## Kuhn-Munkres (Hungarian) Algorithm

### Kuhn-Munkres Theorem

If labeling l is feasible and matching M is a perfect matching in the
equality graph E with respect to labeling l, then M is a max-weight matching.

### The Hungarian Method

1. Generate initial labeling l and matching M in equality graph E.
   Go to 2.
   
2. If M is perfect, stop. Otherwise pick free node u from set X.
   Set S = {u} and T = {}.
   Go to 3.
   
3. If N(S) = T, update labels.
   Go to 4.
   
4. If N(S) != T, pick node y from N(S)-T.

    * If y is free, there exists an augmenting path from u to y.
      Augment M.
      Go to 2.
    
    * If y is matched, say to z, extend alternating tree:
      Set S = S U {z} and T = T U {y}.
      Go to 3.
  
### Updating Labels

1. Let alpha = min{ l(x) + l(y) - w(x,y) }, for all x in S and y not in T.

2. Then:

   - Let l'(x) = l(x) - alpha, for all x in S.
   
   - Let l'(y) = l(y) + alpha, for all y in T.
   
   - Let l'(v) = l(v), otherwise.
   
## EE 382V Social Computing - Assignment 1 - #4

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
