/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ford_fulkerson;

/**
 *
 * @author Eimantas
 */
public class ReaderShortlistException extends Exception {
	
	private static final long serialVersionUID = -2203795208763946900L;
	private boolean isError;
    
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
