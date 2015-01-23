import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;
import java.util.Vector;




public class PreferenceList {
	private static int lastl3=PrefMain.lastl3;
	private static int lastl4=PrefMain.lastl4;
	private static int lastl5=PrefMain.lastl5;
	private ArrayList<Integer> prefs;
	private int target;
	private int id;
	private int l3;
	private int l4;
	private int l5;
	private int msc;
	private ArrayList<Integer> excepts;


	/*default constructor*/
	public PreferenceList(){
		prefs=new ArrayList<Integer>();
		excepts=new ArrayList<Integer>();
		target=0;
		id=0;
		l3=0;
		l4=0;
		l5=0;
		msc=0;
	}


	public PreferenceList(int id, int target){
		prefs=new ArrayList<Integer>();
		excepts=new ArrayList<Integer>();
		this.id=id;
		this.target=target;
		l3=0;
		l4=0;
		l5=0;
		msc=0;
	}


	/*Copy constructor*/
	public PreferenceList(PreferenceList p) {
		this.target = p.target;
		this.id = p.id;
		this.l3 = p.l3;
		this.l4 = p.l4;
		this.l5 = p.l5;
		this.msc = p.msc;
		this.prefs=new ArrayList<Integer>();
		for (int i:p.prefs)
			this.prefs.add(i);
		this.excepts=new ArrayList<Integer>();
		for (int i:p.excepts)
			this.excepts.add(i);
	}


	/* Add a project at the end of the preference list
       Does nothing if the project is already  supervised*/
	public boolean addPreference(int i){
		if(!excepts.contains((Integer) i) && !prefs.contains((Integer) i)){
			prefs.add(i);
			return true;
		}
		return false;
	}


	public void addEx(int i){
		excepts.add(i);
		if(i<=lastl3) l3++;
		else if(i<=lastl4) l4++;
		else if(i<lastl5) l5++;
		else msc++;
	}


	/*extend a preference list by a given set of Integer and return a set of those which have been added*/
	public HashSet<Integer> extend(Set<Integer> s, int size){
		HashSet<Integer> added=new HashSet<Integer>();
		HashSet<Integer> sprime=new HashSet<Integer>(s);  
		for(Integer i:prefs)
			sprime.remove(i);
		for(Integer j:excepts)
			sprime.remove(j);	
		ArrayList<Integer> l=new ArrayList<Integer>(sprime);
		Collections.shuffle(l);	
		for(int k=0; k<l.size() && k<size;k++) {
			this.addPreference(l.get(k));
			added.add(l.get(k));
		}
		return added;
	}

	/**
	 * return the rank of a given project, adding it if necessary
	 * @param projid Id of the project
	 * @return the rank of the project in the list
	 */
	public int getrank(int projid){
		for(int i=0;i<prefs.size();i++)
			if (prefs.get(i)==projid) return i+1;
		prefs.add(projid);
		return prefs.size();
	}

	public int getLength(){
		return prefs.size();
	}

	public ArrayList<Integer> getPrefs() {
		return prefs;
	}


	public ArrayList<Integer> getExcepts() {
		return excepts;
	}


	public int getId(){
		return this.id;
	}

	public int getTarget(){
		return this.target;
	}

	/*string of preferences*/
	public String pref2str(){
		String res="";
		for (Integer i:prefs) res+=i+" ";
		return res;
	}

	/*string of supervised projects*/
	public String exc2str(){
		String res="";
		for (Integer i:excepts) res+=i+" ";
		return res;
	} 

	public String toString(){
		String sid="Id:"+id+" ";
		String starget="Target:"+target+" ";
		String sprefs="Preferences:";
		String ssuper="Supervised:";
		for (Integer i:prefs) sprefs+=i+" ";
		for (Integer i:excepts) ssuper+=i+" ";
		return String.format("%-7s%-38s%-11s%-35s",sid,ssuper,starget,sprefs);
	}

	public String toLine(){
		//System.out.println(String.format("%-5d %-35s %-8d %-35s",id,exc2str(),target,pref2str()));
		return String.format("%-5d %-35s %-8d %-35s",id,exc2str(),target,pref2str());
	}

	public Vector<String> toVect(){
		Vector<String> row = new Vector<String>();
		row.add(""+id);
		row.add(exc2str());
		row.add(""+target);
		row.add(pref2str());
		return row;
	}

	/*getters for number of each kind of project (supervised)*/
	public int getL3(){
		return l3;
	}

	public int getL4(){
		return l4;
	}

	public int getL5(){
		return l5;
	}

	public int getMsc(){
		return msc;
	}

}