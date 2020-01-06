
import java.util.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.time.*;

import com.google.common.collect.*;

public class WordCount2 implements Runnable{
	
	static Map<String,Integer> map= new HashMap<String,Integer>();
	private Map<String,Integer> submap= new HashMap<String,Integer>();
	private List<String>partition;
	
	public WordCount2(List<String>partition) {		
		this.partition = partition;
	}
	
	@Override
	public void run() {
		
		String[] words= null;
		
		/*Iterate through the lines
		 * split each line into words
		 * then find the word in the map
		 * if found, increment the value
		 * else put the word in the map
		 * */

		for(String str:partition) {
			words = str.split(" ");
			for(String word: words) {
				if(word.trim().isEmpty())
					continue;
				if(submap.containsKey(word)){
					submap.put(word, submap.get(word)+1);
				}
				else {
					submap.put(word, 1);
				}	
				synchronized (map) {
					if(map.containsKey(word)){
						map.put(word, map.get(word)+1);
					}
					else {
						map.put(word, 1);
					}
				}
			}
			words = null;
		}
		
		//Sort the map by the value
		submap = sortByValue(submap);
				
		wordList(submap);
		top5words(submap);		
	}
	
	public static void wordList(Map<String,Integer> map) {
		System.out.println("Summary of the words and their counts by "+Thread.currentThread());
		Set set = map.entrySet();
		Iterator i = set.iterator();
		int count = 0;
		try {
			File outputFile = new File("file/wordCount_"+Thread.currentThread()+".csv");
			FileOutputStream fos = new FileOutputStream(outputFile);
			OutputStreamWriter out = new OutputStreamWriter(fos,StandardCharsets.UTF_8);
			BufferedWriter bw= new BufferedWriter(out);
			bw.append("WORDS");
			bw.append(",");
			bw.append("COUNT");
			bw.append("\n");
			while(i.hasNext()) {
				Map.Entry me= (Map.Entry)i.next();
				System.out.println(me.getKey()+" "+me.getValue());
				count+=(Integer)me.getValue();
				bw.append((String)me.getKey());
				bw.append(",");
				bw.append(me.getValue().toString());
				bw.append("\n");
			}
			bw.write("Total no of words : "+count);
			bw.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		System.out.println("Total no of words by "+Thread.currentThread()+" : "+count);
	}

	public static void top5words(Map<String,Integer> map) {

		//find top 5 most existing words whose length is greater than 4
		List keys = new ArrayList(map.keySet());
		int index = keys.size()-1;
		int num= 0;
		System.out.println("Top 5 most appearing words processed by "+Thread.currentThread());
		System.out.println("Word "+"Count");
		while(num<=5 && index>=0) {
		    String obj = (String)keys.get(index);
		    if(!(obj.length()<=5)) {
		    	System.out.println(obj+" "+map.get(obj));
			    num++;
		    }
		    index--;
		}
	}
	
	//Creating method for sorting the map by value
	public static HashMap<String, Integer> sortByValue(Map<String, Integer> map) 
    { 
        // Create a list from elements of HashMap 
        List<Map.Entry<String, Integer> > list = 
               new LinkedList<Map.Entry<String, Integer> >(map.entrySet()); 
  
        // Sort the list 
        Collections.sort(list, new Comparator<Map.Entry<String, Integer> >() { 
            public int compare(Map.Entry<String, Integer> o1,  
                               Map.Entry<String, Integer> o2) 
            { 
                return (o1.getValue()).compareTo(o2.getValue()); 
            } 
        }); 
          
        // put data from sorted list to hashmap  
        HashMap<String, Integer> hmap = new LinkedHashMap<String, Integer>(); 
        for (Map.Entry<String, Integer> aa : list) { 
            hmap.put(aa.getKey(), aa.getValue()); 
        } 
        return hmap; 
    } 
	
	public static void main(String[] args) {
		
		Instant start = Instant.now();
		Instant startRead = Instant.now();
		
		List<String> lines = new ArrayList<String>();
		String fileName="file/hello.txt";
		
		/*Read the file from the URL/document
		 * Read it line by line
		 *replace all the punctuations
		 *convert all the words to the lower case
		 */
		try {
			//URL url = new URL("http://www.gutenberg.org/files/2600/2600-0.txt");
			//BufferedInputStream bis = new BufferedInputStream(url.openStream());
			File file = new File(fileName);
			FileInputStream fis = new FileInputStream(file);
			InputStreamReader isr = new InputStreamReader(fis,StandardCharsets.UTF_8);
			BufferedReader br = new BufferedReader(isr);
			
			String line = br.readLine();
			if(line.length() == 1) {
				line = br.readLine();
			}			
			while(line != null) {
				if(!line.isEmpty()) {
					line = line.replaceAll("\\p{P}","").toLowerCase();
					lines.add(line);
				}
				line = br.readLine();
			}
			br.close();			
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		Instant stopRead = Instant.now();
		
		/*Partition the list lines
		 * pass each partition to each thread
		*/		
		//Thread poolSize
		int poolSize = 3;
		
		List<List<String>> partition = Lists.partition(lines, lines.size()/poolSize);
		List<WordCount2> wcList = new ArrayList<WordCount2>();
		List<Thread> threads = new ArrayList<Thread>();
		
		for(int n=0;n<partition.size();n++) {
			wcList.add(n, new WordCount2(partition.get(n)));
			threads.add(n, new Thread(wcList.get(n),"Thread "+n+""));
		}
		
		//starting the execution of threads
		Instant startThreads = Instant.now();
		for(int n=0;n<poolSize;n++ ) {
			threads.get(n).start();
		}
		
		//waiting for threads to complete the execution
		for(int n=0;n<poolSize;n++ ) {
			try {
				threads.get(n).join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Instant stopThreads = Instant.now();
		
		//Printing the words in the map and their count
		//Printing top 10 most occurring words whose length is greater than 4	
		map= sortByValue(map);
		wordList(WordCount2.map);
		top5words(WordCount2.map);
				
		Instant finish = Instant.now();
		
		//Printing the elapsed time for the execution
		long timeElapsed = Duration.between(startRead, stopRead).toMillis(); 
		System.out.println("Time Lapsed in Reading = "+timeElapsed);
		timeElapsed = Duration.between(startThreads, stopThreads).toMillis();
		System.out.println("Time Lapsed in Thread Execution = "+timeElapsed);
		timeElapsed = Duration.between(start, finish).toMillis(); 
		System.out.println("Total Execution Time = "+timeElapsed);
	}
}
