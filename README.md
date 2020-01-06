# Word-Count
Simple JAVA program that counts words from a text document

The code follows a multithreaded architecture and with the help of JAVA thread pool size of 3, counts the no of different words from different partition of data.

Each thread:
1. Creates a csv file which contains the words in the partition.
2. Calculates the no of times it appears in that partition.
3. Calculates the total no of words in the partition

Then it creates a csv for all the words present in the text document and their count.

Some important packages used are:

com.google.common.collect - For Lists.partition() fuunction which partitions the lines

java.time - For calculating the elapsed time

