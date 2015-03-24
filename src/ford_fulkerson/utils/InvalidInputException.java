package ford_fulkerson.utils;

/**
 * exception class to denote the input provided by the user is invalid
 * @author Eimantas
 *
 */
public class InvalidInputException extends Exception{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidInputException(String s){
		super(s);
	}

	public InvalidInputException(Exception e){
		super(e);
	}
	
	public InvalidInputException(String s, Exception e){
		super(s,e);
	}
}
