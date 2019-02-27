package sam.fx.viewswitcher;

import java.util.LinkedList;

import javafx.scene.Parent;

public class ViewHistory {
	private final LinkedList<Parent> history = new LinkedList<>();
	private final ViewSwitcher centerView;
	
	public ViewHistory(ViewSwitcher centerView) {
		this.centerView = centerView;
	}
	
	public Object set(Parent view) {
		if(centerView.current() == view)
			return view;
		
		history.add(centerView.current());
		centerView.set(view);
		
		return view;
	}
	public void back(Object marker) {
		if(marker != centerView.current())
			throw new IllegalStateException();
		
		centerView.set(history.removeLast());
	}
}
