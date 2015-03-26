/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ford_fulkerson;

/**
 * An exception which is thrown if a reader has insufficient amount of preferences
 * for his reader target.
 * @author Eimantas
 */
public class ReaderShortlistException extends Exception {
	
	private static final long serialVersionUID = -2203795208763946900L;
	// a boolean variable representing weather the exception has warnings or just errors
	private final boolean isError;		
    
    public ReaderShortlistException(String msg, boolean isError) {
        super(msg);
        this.isError = isError;
    }
    
    public ReaderShortlistException(String msg) {
        this(msg,false);
    }
    
    public boolean isErrorMessage(){
        return this.isError;
    }
}
