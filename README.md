# Word-Count in JAVA
Simple JAVA program that counts words from a text document

The code follows a multithreaded architecture and with the help of JAVA thread pool size of 3, counts the no of different words from different partition of data.

Each thread: 
1. Finds all the words and the no of times it appears in particular partition.
2. Creates a csv file which contains the words in the partition and its count.
3. Calculates the total no of words in the partition
4. Finds top 5 most occuring words whose length is greater than 5

Then Main thread creates a csv for all the words present in the text document and their count.

For reading the data, BufferedReader and single thread is used as it is faster to read data sequentially. First whole data is read and lines are stored in an ArrayList. After that the list is partitioned and different threads are created for evaluating each partition of the data.

However if the data is very large, it can be read in chunks and each chunk can be processed immediately after it is read without waiting for the remaining data to be read.

Some important packages used are:

com.google.common.collect - For Lists.partition() fuunction which partitions the list of lines

java.time - For calculating the elapsed time for reading the text file, execution of threads and total execution time.

To execute the code manually (without IDE):
1. Download the java file WordCount2.java
2. In the same folder create a new folder named "file" which will contain the document to be read and the output documents
3. Open command prompt in the folder where java file is stored and run the following command to compile the code:
   javac WordCount2.java
   (You might need google-collections-1.0.jar if not present)
4. Type "java WordCount2" in command prompt to run the code.




