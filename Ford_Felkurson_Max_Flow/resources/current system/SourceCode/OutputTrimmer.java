import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.Scanner;


public class OutputTrimmer {

	public OutputTrimmer(String infile, String method) throws FileNotFoundException{
		 FileReader fin=new FileReader(infile);
	     Scanner scan=new Scanner(fin);
	     boolean hasnextval=true;
	     boolean skip=true;
	     FileOutputStream myoutput = new FileOutputStream("trimmedout.txt");
	     PrintWriter out= new PrintWriter(myoutput);
	     
	     /*Browse input file to the method wished*/
	     int lon=method.length();
	     
	     while (skip){
	    	 String line=scan.nextLine();
	    	 if (line.length()>=lon && line.substring(0,lon).equals(method))
	    		 skip=false;
	     }
	     /*then skip the description lines*/
	     for (int i=0;i<4;i++) scan.nextLine();
	     
	     /*copy the data*/
	     while (hasnextval) {
	    	 String line=scan.nextLine();
	    	 hasnextval= new Scanner(line).hasNextInt();
	    	 if (hasnextval) out.write(line+"\n");
	     }
	     out.close();
	}
	
}
