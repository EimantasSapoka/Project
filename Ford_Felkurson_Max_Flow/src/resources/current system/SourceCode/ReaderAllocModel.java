import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import profilematching.Main;

public class ReaderAllocModel {
	private ArrayList<PreferenceList> preferences=new ArrayList<PreferenceList>();
	private ArrayList<PreferenceList> originalprefs=new ArrayList<PreferenceList>();
	private HashSet<Integer> lefts=new HashSet<Integer>();
	int projectnum = PrefMain.projnum;
	int []projectfreq=new int[projectnum];
	int targetsum = 0;
	private ArrayList<PreferenceList> shortlists=new ArrayList<PreferenceList>();
	private HashSet<Integer> extensionset=new HashSet<Integer>();


	/**
	 * Class constructor :
	 * Initialize all attributes
	 * @param fin Stream from file containing preferences
	 * 
	 */
	public ReaderAllocModel(FileInputStream fin){
		try{
			Scanner infile=new Scanner(fin);
			for (int k=1;k<=14;k++)
				extensionset.add(k);
			for (int k=65;k<=114;k++)
				extensionset.add(k);
			while (infile.hasNextLine()) {
				String line=infile.nextLine();
				String []params=line.split(" [|]");
				//remove spaces 
				for (int i=0;i<params.length;i++)
					if (params[i]!= null && !params[i].equals("")  && params[i].charAt(0)==' ')
						params[i]=params[i].substring(1);
				int target=Integer.parseInt(params[2]);
				targetsum+=target;
				if (target!=0) {
					int id=Integer.parseInt(params[0]);
					PreferenceList pref=new PreferenceList(id,target);
					String []superv=params[1].split(" ");
					for(String s:superv) {
						if (! s.equals("")) {
							int projectnumber=Integer.parseInt(s);
							pref.addEx(projectnumber);
						}
					}
					String []prefs=params[3].split(" ");
					for(String s:prefs) {
						/*the addPreference method will check that
		      the project is not supervised*/ 
						if (! s.equals("")) {
							int projectnumber=Integer.parseInt(s);
							if (pref.addPreference(projectnumber))
								projectfreq[projectnumber - 1]++;
						}
					}
					preferences.add(pref);
				}
			} //endwhile
			int i=1;
			//not bid for projects
			for(int k: projectfreq){
				if (k==0) {
					lefts.add(i);
				}
				i++;
			}
			//short lists & creation of a copy of original preferences
			for(PreferenceList p:preferences) {
				originalprefs.add(new PreferenceList(p));
				if  (p.getLength()< 2*p.getTarget())
					shortlists.add(p);
			} 
		}catch (Exception e){
			System.err.println("Incorrect format input: should be \"<ID> | <supervised projects> | <target> | <preferences>\"");
			System.exit(-1);
		}
	}


	/**
	 * 
	 * @param id : Id of the list to extend
	 * @param s : Extension set
	 * extend the list with a random subset of s (to the size 2*target)
	 */
	private void extend(int id, Set<Integer> s){
		HashSet<Integer> added=new HashSet<Integer>();
		int k=0; 
		int toremove=-1;
		for (PreferenceList p:shortlists){
			if (p.getId()==id){
				added=p.extend(s, 2*p.getTarget()- p.getLength());
				if  (p.getLength()>= 2*p.getTarget())
					toremove=k;	
			}
			k++;
		}
		if(toremove>=0)
			shortlists.remove(toremove);
		lefts.removeAll(added);
	}


	/*extend all shorts*/
	public void extendshorts(Set<Integer> s){
		HashSet<Integer> added=new HashSet<Integer>();
		List<PreferenceList> toremove=new ArrayList<PreferenceList>();
		for (PreferenceList p:shortlists){
			added.addAll(p.extend(s, 2*p.getTarget()- p.getLength()));
			if  (p.getLength()>= 2*p.getTarget())
				toremove.add(p);
		}
		lefts.removeAll(added);
		for (PreferenceList pref : toremove)
			shortlists.remove(pref);
	}

	/*different extension methods*/
	public void extend1(int id){
		extend(id,extensionset);
	}

	public void extendshorts1(){
		extendshorts(extensionset);
	}

	public void extend2(int id){
		extend(id,lefts);
	}

	public void extendshorts2(){
		extendshorts(lefts);
	}

	public void extend3(int id, String entry){
		Scanner projIds=new Scanner(entry);
		HashSet<Integer> customExt=new HashSet<Integer>();
		while (projIds.hasNextInt()) {
			int projid=projIds.nextInt();
			if (projid>0 && projid<=projectnum)
				customExt.add(projid);
		}
		extend(id,customExt);
	}

	public void extendshorts3( String entry){
		Scanner projIds=new Scanner(entry);
		HashSet<Integer> customExt=new HashSet<Integer>();
		while (projIds.hasNextInt()) {
			int projid=projIds.nextInt();
			if (projid>0 && projid<=projectnum)
				customExt.add(projid);
		}
		extendshorts(customExt);
	}

	public void extend4(int id,String entry){
		Scanner projids=new Scanner(entry);
		for (PreferenceList p: preferences){
			if (p.getId()==id){
				while (projids.hasNextInt()) {
					int projid=projids.nextInt();
					if (projid>0 && projid<=projectnum);
					if(p.addPreference(projid))
						lefts.remove((Integer) projid);
				}
				if  (p.getLength()>= 2*p.getTarget())
					shortlists.remove(p);		
			}
		}
	}


	public ArrayList<PreferenceList> getPreferences() {
		return preferences;
	}


	public HashSet<Integer> getLefts() {
		return lefts;
	}


	public ArrayList<PreferenceList> getShortlists() {
		return shortlists;
	}

	public String notbid(){
		String res="";
		ArrayList<Integer> tosort=new ArrayList<Integer>(lefts);
		Collections.sort(tosort);
		for (Integer k: tosort)
			res+=k+"  ";
		return res;
	}

	public void runMatching() throws FileNotFoundException{
		FileOutputStream myoutput=null;
		try {
			myoutput = new FileOutputStream("matchinginput.txt");
		} catch (FileNotFoundException e) {
			System.out.println("Error creating file !");
			e.printStackTrace();
		}
		PrintWriter out= new PrintWriter(myoutput);
		out.write(targetsum +"\n");
		out.write(projectnum +"\n");
		for(PreferenceList p : preferences)
			for (int k=0 ; k<p.getTarget() ; k++)
				out.write(p.pref2str() + "\n");
		for(int l=0;l<projectnum;l++)
			out.write("1\n");
		out.close();
		try {
			Main.main(null);
		} catch (IOException e1) {
			System.out.println("Error running matching algorithm! ");
			e1.printStackTrace();
		}
	}


	public void showRes(String matchingmethod) throws FileNotFoundException {
		new OutputTrimmer("matchingoutput.txt",matchingmethod);
		ResultModel model=new ResultModel(preferences, originalprefs,matchingmethod);
		ResultController controller = new ResultController(model);
		ResultView view = new ResultView(model, controller);
		controller.setView(view);
		view.setVisible(true);
	}

}
