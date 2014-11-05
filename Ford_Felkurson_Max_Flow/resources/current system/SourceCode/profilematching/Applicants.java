/*
 * Applicants.java
 *
 * Created on 23 February 2007, 11:13
 *
 */

package profilematching;

/**
 * @author Rob Irving
 */

import java.util.*;

public class Applicants {
    
    private Applicant [] apps;
    private int count;
    
    /** Creates a new instance of Applicants */
    public Applicants(int n) {
        apps = new Applicant[n];
        count = 0;
    }
    
    public void reset(){
       for (Applicant a : apps)
          a.reset();
    }
    
    public int getCount(){
        return count;
    }
    
    public void setCount(int n){
        count = n;
    }
    
    public void addApplicant(Applicant a){
        apps[count++] = a;
    }
    
    public Applicant getApplicant(int i){
        return apps[i];
    }
    
    public Applicant[] getApps(){
        return apps;
    }
    
    public void permute(){
       Random rand = new Random();
       for (int i=apps.length-1; i>0;i--){
          int next = rand.nextInt(i);
          Applicant temp = apps[next];
          apps[next] = apps[i];
          apps[i] = temp;
       }
    }
    
    public void restore(){
       Applicants tempApps = new Applicants(apps.length);
       for (int i=0; i<apps.length; i++) {
          tempApps.apps[apps[i].getIndex()-1] = apps[i];
       }
       apps = tempApps.apps;
    }
    
    public String displayUnmatched(){
       String s = "";
       for (int i=0; i < apps.length; i++){
          if (!apps[i].isAssigned())
             s = s + apps[i].getIndex() + "\n";
       }
       return s;
    }
}
