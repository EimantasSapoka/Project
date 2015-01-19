package augustineMCMF;


/**
 * Represents the profile of a matching or a path
 * Shows the number of students matched to their ith choice project
 * Where 1 <= i <= R and R is the maximum length of any students' preference list.
 * @author augustine
 */
public class Profile 
{
	private int[] profile;
	private boolean isPosInfinity, isNegInfinity;

	
	public Profile(int size)
	{
		profile = new int[size];
		isPosInfinity = isNegInfinity = false;
	}
	
	public int getCountAtRank(int index) throws Exception
	{
		if(index < profile.length)
			return profile[index];
		else
			throw new Exception("The profile is not big enough to handle the rank "+index);
	}
	
	/**
	 * Adds a new student's rank to the profile
	 * @param index
	 * @throws Exception
	 */
	public void add(int index) throws Exception
	{
		// no addition can affect an infinity profile
		if(isPosInfinity || isNegInfinity)
			return;
		
		if(index <= profile.length)
			profile[index-1]++;
		else
			throw new Exception("The profile is not big enough to handle the rank "+index);
	}
	
	/**
	 * Removes a student's rank from the profile
	 * @param index
	 * @throws Exception
	 */
	public void remove(int index) throws Exception
	{
		// no removal can affect an infinity profile
		if(isPosInfinity || isNegInfinity)
			return;
		
		if(index <= profile.length)
			profile[index-1]--;
		else
			throw new Exception("The profile is not big enough to handle the rank "+index);
	}
	
	
	/**
	 * Determines if this profile left dominates the input profile
	 * @param p
	 * @return
	 */
	public boolean leftDominates(Profile p) throws Exception
	{
		// consider the case where this profile is negative infinity
		if(isNegInfinity)
			return false;
		
		// consider the case where the input profile is negative infinity
		if(p.isNegInfinity())
			return true;
		
		for(int i=0; i<profile.length; i++)
		{
			if(profile[i] == p.getCountAtRank(i))
				continue;
			
			return profile[i] > p.getCountAtRank(i);
			
		}
		return false;
	}
	
	/**
	 * Determines if this profile left dominates the input profile
	 * @param p
	 * @return
	 */
	public boolean rightDominates(Profile p) throws Exception
	{
		// consider the case where this profile is positive infinity
		if(isPosInfinity)
			return false;
		
		// consider the case where the input profile is positive infinity
		if(p.isPosInfinity())
			return true;
		
		for(int i=profile.length-1; i>=0; i--)
		{
			if(profile[i] == p.getCountAtRank(i))
				continue;
			
			return profile[i] < p.getCountAtRank(i);
		}
		return false;
	}
	
	/**
	 * Makes this profile a + or - infinity profile
	 */
	public void makeInfinity(int value)
	{
		if(value>0)
		{
			isPosInfinity = true;
			isNegInfinity = false;
		}
		else
		{
			isNegInfinity = true;
			isPosInfinity = false;
		}
	}
	
	@Override
	public String toString() 
	{
		if(isPosInfinity)
			return "(+infinity)";
		if(isNegInfinity)
			return "(-infinity)";
		
		StringBuilder output = new StringBuilder("(");
		for(int i=0; i<profile.length-1; i++)
			output.append(profile[i]+",");
		output.append(profile[profile.length-1]+")");
		return output.toString();
	}

	public boolean isPosInfinity() 
	{
		return isPosInfinity;
	}

	public boolean isNegInfinity() 
	{
		return isNegInfinity;
	}
	
	public void setProfileValue(int value, int index) throws Exception
	{
		if(index <= profile.length)
			profile[index] = value;
		else
			throw new Exception("The profile is not big enough to handle the rank "+index);
	}
	
	/**
	 * Clones the current profile
	 */
	public Profile cloneProfile() throws Exception
	{
		Profile p = new Profile(profile.length);
		for(int i=0; i<profile.length; i++)
			p.setProfileValue(profile[i], i);
		p.isNegInfinity = isNegInfinity;
		p.isPosInfinity = isPosInfinity;
		return p;
	}
}
