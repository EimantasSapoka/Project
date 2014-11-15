import java.awt.Font;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import javax.swing.JLabel;


public class ResultModel {

	private ArrayList<AllocationResult> results;
	private ArrayList<PreferenceList> original;
	private ArrayList<PreferenceList> prefcopy;
	private HashMap<Integer,Integer> projToReader;
	private HashMap<Integer,Integer> projToRank;
	private int projnum=PrefMain.projnum;
	private int matchingSize;
	public String matchingtype;

	public ResultModel(ArrayList<PreferenceList> prefs,ArrayList<PreferenceList> origin,String matching) throws FileNotFoundException{
		results=new ArrayList<AllocationResult>();
		original=new ArrayList<PreferenceList>(origin);
		prefcopy=new ArrayList<PreferenceList>(prefs);
		matchingtype=matching;
		projToReader=new HashMap<Integer,Integer>();
		projToRank=new HashMap<Integer,Integer>();
		for (PreferenceList p:prefs){
			results.add(new AllocationResult(p.getId(),p.getTarget(),p.getL3(),p.getL4(),p.getL5(),p.getMsc()));
		}
		//add original preferences to result
		for (int i=0;i<results.size();i++){
			for (int k:original.get(i).getPrefs())
				results.get(i).addOriginal(k);
		}
		FileReader fin=new FileReader("trimmedout.txt");
		Scanner scan=new Scanner(fin);

		int currentindex=0;
		int lastindex=0;
		Scanner line=null;
		boolean hasnext= scan.hasNextLine();
		if (hasnext) {
			line=new Scanner(scan.nextLine());
			currentindex=line.nextInt();
		}
		for (AllocationResult res:results){
			lastindex+=res.getTarget();
			while(hasnext && currentindex<=lastindex){
				int projnum=line.nextInt();
				int rank=line.nextInt();
				res.addProject(projnum, rank);
				projToReader.put(projnum, res.getId());
				projToRank.put(projnum, rank);
				hasnext= scan.hasNextLine();
				if (hasnext) {
					line=new Scanner(scan.nextLine());
					currentindex=line.nextInt();
				}
			}
		}
		matchingSize=0;
		for (AllocationResult res : results)
			matchingSize+=res.projnum();
	}


	public ArrayList<Integer> allowedtransfer(int from, int to){
		ArrayList<Integer> res=new ArrayList<Integer>();
		if (from!=to){
			int i=0;
			while (results.get(i).getId()!=from) i++;
			AllocationResult afrom=results.get(i);
			i=0;
			while (original.get(i).getId()!=to) i++;
			PreferenceList ato=original.get(i);
			for(int k:afrom.getProjects()) res.add(k);
			for(int j:ato.getExcepts()) res.remove((Integer)j);
		}
		return res;
	}

	public void moveProject(int from, int to, int projid){
		int fromidx=0,toidx=0;
		for(int i=0;results.get(i).getId()!= from;i++) fromidx=i+1;
		for(int i=0;results.get(i).getId()!= to;i++) toidx=i+1;
		results.get(fromidx).removeProject(projid);
		int rank=prefcopy.get(toidx).getrank(projid);
		results.get(toidx).addProject(projid,rank);
		projToReader.put(projid, to);
		projToRank.put(projid,rank);
	}

	public void displayAll(){
		System.out.print(String.format("%-5s%-39s%-22s%-7s%-6s%-12s%-4s%-4s%-4s%s\n",
				"ID","Projects","Ranks","Target","Alloc","Cost(avg)","L3","L4","L5","MSc"));
		for (AllocationResult res:results)
			System.out.println(res.toString());
		System.out.println("\n\nProject   Reader");
		for(int i=1;i<=projnum;i++){
			if (projToReader.containsKey(i))
				System.out.println(String.format("%-9d %d", i,projToReader.get(i)));
		}
	}


	public void Output(File f) throws FileNotFoundException{
		FileOutputStream myoutput = new FileOutputStream(f);
		PrintWriter out= new PrintWriter(myoutput);
		out.write("id , projects , ranks , target , cost , l3 , l4 , l5 , msc\n");
		for (AllocationResult res:results){
			out.write(res.toStrplus()+"\n");
		}
		out.close();
	}

	public void OutputProjectReader(File f) throws FileNotFoundException {
		FileOutputStream myoutput = new FileOutputStream(f);
		PrintWriter out= new PrintWriter(myoutput);
		out.write("project , reader , rank \n");
		for (int i=1;i<=PrefMain.projnum;i++){
			if (projToReader.containsKey(i)) {
			out.write(String.format("%d , %d , %d \n", 
					i,projToReader.get(i),projToRank.get(i)));
			}
		}
		out.close();
	}


	/*return the profile of the matching*/
	public String profile(){
		int maxrank=1;
		HashMap<Integer,Integer> profilemap=new HashMap<Integer,Integer>();
		for (AllocationResult a:results){
			for (Integer i:a.getRanks()){
				if (i>maxrank) maxrank=i;
				int val=1;
				if (profilemap.containsKey(i))
					val+=profilemap.get(i);
				profilemap.put(i, val);
			}
		}
		String res="(";
		for (int j=1;j<maxrank;j++){
			if (profilemap.containsKey(j))
				res+=profilemap.get(j)+",";
			else res+="0,";
		}
		res+=profilemap.get(maxrank)+")";
		return res;
	}

	public int totalcost(){
		int res=0;
		for (AllocationResult a:results)
			res+=a.getCost();
		return res;
	}

	public ArrayList<AllocationResult> getResults() {
		return results;
	}

	public ArrayList<PreferenceList> getOriginal() {
		return original;
	}

	public HashMap<Integer, Integer> getProjToReader() {
		return projToReader;
	}


	public HashMap<Integer, Integer> getProjToRank() {
		return projToRank;
	}


	public int getSize(){
		return matchingSize;
	}

}
