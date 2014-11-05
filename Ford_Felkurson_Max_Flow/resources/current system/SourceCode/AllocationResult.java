import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;


public class AllocationResult {
	private static int lastl3=PrefMain.lastl3;
	private static int lastl4=PrefMain.lastl4;
	private static int lastl5=PrefMain.lastl5;
	private int id;
	private String name;
	private int target;
	private ArrayList<Integer> projects;
	private ArrayList<Integer> ranks;
	private HashSet<Integer> originalprefs;
	private HashMap<Integer,Integer> projrank;
	private int l3;
	private int l4;
	private int l5;
	private int msc;
	private int cost;


	public AllocationResult(int id,int target,int l3, int l4, int l5, int msc){
		this.id=id;
		name="";
		projects=new ArrayList<Integer>();
		ranks=new ArrayList<Integer>();
		originalprefs=new HashSet<Integer>();
		projrank=new HashMap<Integer,Integer>();
		this.target=target;
		this.l3=l3;
		this.l4=l4;
		this.l5=l5;
		this.msc=msc;
		cost=0;
	}

	public void addOriginal(int proj){
		originalprefs.add(proj);
	}

	public void addProject(int proj,int rank){
		projects.add(proj);
		ranks.add(rank);
		projrank.put(proj, rank);
		cost+=rank;
		if(proj<=lastl3) l3++;
		else if(proj<=lastl4) l4++;
		else if(proj<lastl5) l5++;
		else msc++;
	}

	public void removeProject(int proj){
		if (projrank.containsKey(proj)){
			projects.remove((Integer)proj); 
			int rank=projrank.get(proj);
			ranks.remove((Integer) rank); 
			cost-=rank;
			projrank.remove(proj);
			if(proj<=lastl3) l3--;
			else if(proj<=lastl4) l4--;
			else if(proj<lastl5) l5--;
			else msc--;
		}
	}

	public String avgCost(){
		return(""+(float)cost/(ranks.size()==0?1:(float)ranks.size())).substring(0,3);
	}

	public int projnum() {
		return projects.size();
	}

	public String toString(){
		String res="";
		Collections.sort(projects);
		Collections.sort(ranks);
		res+=(String.format("%-5d",id) );
		String sproj="";
		for(Integer p:projects){
			if (originalprefs.contains(p))
				sproj+=p+" ";
			else sproj+=p+"* ";
		}
		res+=(String.format("%-40s",sproj));
		String srank="";
		for(Integer r:ranks)
			srank+=r+" ";
		String scost=cost+"("+avgCost()+")";
		res+=(String.format("%-22s%-7d%-6d%-12s%-4d%-4d%-4d%-4d",srank,target,ranks.size(),scost,l3,l4,l5,msc));
		return res;
	}
	
	public String toStrplus(){
		String res="";
		Collections.sort(projects);
		Collections.sort(ranks);
		res+=id+" , ";
		String sproj="";
		for(Integer p:projects){
			if (originalprefs.contains(p))
				sproj+=p+" ";
			else sproj+=p+"* ";
		}
		res+=sproj+" , ";
		String srank="";
		for(Integer r:ranks)
			srank+=r+" ";
		res+=srank+" , "+target+" , "+cost+" , "+l3+" , "+l4+" , "+l5+" , "+msc;
		return res;
	}

	/*Auto generated getters (and a few setters)*/
	public ArrayList<Integer> getProjects() {
		return projects;
	}
	public ArrayList<Integer> getRanks() {
		return ranks;
	}


	public void setProjects(ArrayList<Integer> projects) {
		this.projects = projects;
	}
	public int getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name=name;
	}
	public int getTarget() {
		return target;
	}
	public int getL3() {
		return l3;
	}
	public int getL4() {
		return l4;
	}
	public int getL5() {
		return l5;
	}
	public int getMsc() {
		return msc;
	}
	public int getCost() {
		return cost;
	}

}
