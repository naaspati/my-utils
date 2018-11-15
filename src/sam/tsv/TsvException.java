package sam.tsv;

public class TsvException extends RuntimeException {
	private static final long serialVersionUID = -1126358682707005719L;
	public TsvException(String string) {
		super(string);
	}
	public TsvException(String string, Exception e) {
		super(string, e);
	}
	

}