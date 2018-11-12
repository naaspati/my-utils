package sam.io.serilizers;

import java.io.DataOutputStream;

public interface WriteableObject {
	public void write(DataOutputStream dos);
}
