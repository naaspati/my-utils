package sam.fx.numeric.textfield;

import static java.lang.Character.isDigit;

import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

public class TextFieldInteger extends TextField {

	private ReadOnlyIntegerWrapper value;

	/**
	 * @param defaultValue value returned when textfield is empty
	 */
	public TextFieldInteger() {
		super();
	}
	/**
	 * @param defaultValue value returned when textfield is empty
	 */
	public TextFieldInteger(String text) {
		super(text);
	}

	{
		setTextFormatter(new TextFormatter<>(change -> {
			if(change.isAdded()) {
				String add = change.getText();

				boolean nochange = false;

				if(add.length() == 1) {
					nochange = !isValid(add.charAt(0), change.getAnchor() == 1 && (checkFirstChar(change.getControlText())));
				} else {
					String checkTxt = add;
					if(!isDigit(add.charAt(0)))
						checkTxt = change.getControlNewText();

					for (int i = 0; i < checkTxt.length(); i++) {
						if(!isValid(checkTxt.charAt(i), i == 0)) {
							nochange = true;
							break;
						}
					}
				}

				if(nochange)
					change.setText("");
				else if(value != null)
					value.set(valueRaw());

			}
			return change;
		}));
	}

	private boolean checkFirstChar(String s) {
		return s.isEmpty() || (s.charAt(0) != '-' && s.charAt(0) != '+');
	}

	private boolean isValid(char c, boolean zeroIndex) {
		if(zeroIndex)
			return c == '-' || c == '+' || isDigit(c);
		else
			return isDigit(c);
	}

	@SuppressWarnings("unused")
	private ReadOnlyIntegerProperty valueProperty() {
		if(value == null) {
			value = new ReadOnlyIntegerWrapper();
			value.setValue(valueRaw());	
		}

		return value.getReadOnlyProperty();
	}
	protected Integer valueRaw() {
		String s = getText();
		if(s.isEmpty())
			return null;
		
		if(s.length() == 1) {
			char c = s.charAt(0);
			if(c == '-' || c == '+')
				return null;
			else
				return c - '0';
		}
		return Integer.parseInt(s);
	}
	public Integer getValue() {
		if(value != null)
			return value.get();
		
		return valueRaw();
	}
}
