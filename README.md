# Minesweeper_Solver_AI
**Minesweeper Final AI Report**

**I.A. Brief description of final AI**



The algorithm consists of multiple layers. In the first layer, we keep uncovering zeros and storing neighboring tiles in a &#39;Safe to visit&#39; queue. If the queue is not empty we pop a tile from it and return uncover action on it.

If the first layer fails, we proceed to the second layer where we analyze opened tiles. If a tile has exactly as many neighbors as the tile number, then mark all those neighbors as mines and reduce the count of the neighboring tiles. If new zero tiles get discovered then mark their neighbors as &#39;safe&#39;

If the second layer fails, we proceed to the third layer where we utilize patterns. We have coded 1-2-1, 1-2-2-1, 1-2, 2-1, 1-1 patterns in our algorithm to identify mines quickly. This layer again may discover new zeros which will help accelerate our search in the first layer.

In the third layer, we model the minesweeper board state as a set of linear algebra equations and reduce the system to row-reduced echelon form. Then we perform analysis on the board using the following steps mentioned [here](https://massaioli.wordpress.com/2013/01/12/solving-minesweeper-with-matricies/comment-page-1/)

Set the maximum bound and minimum bound to zero For each column in the row (not including the augmented column of course)

if the number is positive add it to the maximum bound and if it is negative then add it to the minimum bound.

If the augmented column value is equal to the minimum bound then    All of the negative numbers in that row are mines and all of the positive values in that row are not mines else

if the augmented column value is equal to the maximum bound then    All of the negative numbers in that row are not mines and all of the positive values in that row are mines.

This layer is present in the la branch.

In the last layer, all covered/unopened tiles are candidate mines. The opened neighbors of these tiles will cast votes. We choose the mine having the minimum votes as SAFE and uncover it. This layer is less reliable in detecting mines but is used as a last resort.



**II.B Final AI algorithm's performance:**



| Board Size | Sample Size | Score | Worlds Complete |
| --- | --- | --- | --- |
| 5x5 | 1000 | 1000 | 1000 |
| 8x8 | 1000 | 1400 | 700 |
| 16x16 | 1000 | 1324 | 630 |
| 16x30 | 1000 | 374 | 134 |
| Total Summary | 4000 | 4794 | 2464 |









