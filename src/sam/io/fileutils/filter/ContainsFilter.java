package sam.io.fileutils.filter;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ContainsFilter extends StringValuesFilter {
	private final Set<Path> set;
	
	public ContainsFilter(String[] array) {
		super(array);
		if(array == null || array.length == 0)
			this.set = Collections.emptySet();
		else if(array.length == 1)
			this.set = Collections.singleton(Paths.get(array[1]));
		else {
			this.set = new HashSet<>();
			for (String s : array) 
				set.add(Paths.get(s));
		}
	}

	@Override
	public boolean test(Path t) {
		return set.contains(t);
	}

}
