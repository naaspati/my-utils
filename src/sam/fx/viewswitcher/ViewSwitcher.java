package sam.fx.viewswitcher;

import java.util.LinkedList;

import javafx.scene.Parent;

public class ViewSwitcher {
	private final LinkedList<Parent> history = new LinkedList<>();
	private final CenterView centerView;
	
	public ViewSwitcher(CenterView centerView) {
		this.centerView = centerView;
	}
	
	public Object set(Parent view) {
		if(centerView.get() == view)
			return view;
		
		history.add(centerView.get());
		centerView.set(view);
		
		return view;
	}
	public void back(Object marker) {
		if(marker != centerView.get())
			throw new IllegalStateException();
		
		centerView.set(history.removeLast());
	}
}
