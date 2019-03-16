This application computes the frequent item sets from a transaction file using the Apriori Algorithm.

Run the program as follows :

1. Git clone the repository in your local machine.
2. cd into frequent-itemsSets
3. Run ./gradlew clean --info
4. Run ./gradlew build --info
5. Run ./gradlew run --info
6. The program does the following :

   a. Read the default transaction file [test.dat under src/test/resources]
   
   b. Create files with .serialized extension [intermediate files that store the item sets required for the next iteration.
      does not store these files in memory to save heap space]
      
   c. Write the output file to src/test/resources/output.txt [item sets of size ranging from 1 upto the default max size : 4
      would be written to the output file. The default SIGMA value is also 4.]

7. To provide your own inputs use the following command :

   ./gradlew run --args='src/test/resources/test.dat SIGMA MAX_ITEM_SETS_SIZE' --info
   
   for example : ./gradlew run --args='src/test/resources/test.dat 5 3' --info
   
   
The application was tested using Java 11. With Java 8 streams ran into a performance issue related to dividing the buffered reader
input into chunks for parallel computation using streams. More details are outlined in this blog : 
https://bytefish.de/blog/jdk8_files_lines_parallel_stream/ . 

The app was monitored using the VisualVM tool . Some monitoring data about CPU, heap sizes and parallel threads can be found under the folder named “monitoring”.
