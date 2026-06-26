package de.gimik.apps.gpstracker.backend.service;

public class UniqueKeyViolationException extends Exception{

	private static final long serialVersionUID = -6978241281286446229L;

	public UniqueKeyViolationException(){
		super();
	}
	
    public UniqueKeyViolationException(String message) {
        super(message);
    }
    
    public UniqueKeyViolationException(Exception innerException) {
        super(innerException);
    }
    
    public UniqueKeyViolationException(String message, Exception innerException){
    	super(message, innerException);
    }
}
