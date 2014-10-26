package ford_fulkerson;

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
