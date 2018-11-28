package input_util;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.KeyStroke;


public class KeyBinder {
	
	public KeyBinder(InputMap input, ActionMap action) {
		inputMap = input;
		actionMap = action;
	}
	
	private final InputMap inputMap;
	private final ActionMap actionMap;
	
	/**
	 * Binds a key on the keyboard to a true-or-false state. When the key is pressed, the state is set to true;
	 * when the key is released, the state is set to false.
	 * @param key the key (the part after VK_, such as A or SHIFT or DOWN)
	 * @param state some boolean state that will be bound to the key
	 * @see #bindKeyToStateInverted(String, State)
	 */
	public void bindKeyToState(String key, State state) {
		bindKeyStrokeToCommand("pressed " + key, () -> state.set(true));
		bindKeyStrokeToCommand("released " + key, () -> state.set(false));
	}
	
	/**
	 * Binds a key on the keyboard to a true-or-false state. When the key is pressed, the state is set to false;
	 * when the key is released, the state is set to true.
	 * 
	 * Make sure that the state is initially set to TRUE.
	 * @param key the key (the part after VK_, such as A or SHIFT or DOWN)
	 * @param state some boolean state that will be bound to the key
	 * @see {@link #bindKeyToState(String, State)}
	 */
	public void bindKeyToStateInverted(String key, State state) {
		bindKeyStrokeToCommand("pressed " + key, () -> state.set(false));
		bindKeyStrokeToCommand("released " + key, () -> state.set(true));
	}
	
	/**
	 * Binds the specified key to the given command such that when the key is pressed,
	 * the command is invoked. This is deprecated because {@link #bindKeyStrokeToCommand(String, Command)}
	 * does the exact same thing, but with more precision.
	 * @param key the string following "VK_"
	 * @param command the command to be triggered
	 */
	@Deprecated
	public void bindKeyToCommand(String key, Command command) {
		bindKeyStrokeToCommand("pressed " + key, command);
	}
	
	/**
	 * Binds the specified key to the given command. The key stroke can be "pressed _", released _",
	 * or "typed _" (or a few other options- see {@link KeyStroke#getKeyStroke(String)} for more),
	 * where _ indicates the key. The key is whatever follows "VK_".
	 * @param keyStroke the string following "VK_"
	 * @param command the command to be triggered
	 */
	public void bindKeyStrokeToCommand(String keyStroke, Command command) {
		inputMap.put(KeyStroke.getKeyStroke(keyStroke), keyStroke);
		actionMap.put(keyStroke, new AbstractAction() {
			private static final long serialVersionUID = 1L;
			Command cmd = command;
			@Override
			public void actionPerformed(ActionEvent e) {
				cmd.invoke();
			}
		});
	}
	
	/**
	 * Binds keys to a lever. The lever is pushed up by one key and down by the other.
	 * @param keyDown the key that controls whether the lever is down (the part after VK_, such as A or SHIFT or DOWN)
	 * @param keyUp the key that controls whether the lever is up
	 * @param lever the lever to be controlled by the keys
	 */
	public void bindKeysToLever(String keyDown, String keyUp, Lever lever) {
		bindKeyToState(keyDown, lever.pushDown);
		bindKeyToState(keyUp, lever.pushUp);
	}
	
	/**
	 * Removes a keystroke from the actionmap and inputmap.
	 * @param keyStroke
	 */
	private void removeKeyStroke(String keyStroke) {
		inputMap.remove(KeyStroke.getKeyStroke(keyStroke));
		actionMap.remove(keyStroke);
	}
	
	/**
	 * Clears all interactions (pressed, released, and typed) with a given key.
	 * @param key the key (the part after VK_, such as A or SHIFT or DOWN)
	 */
	public void clearKey(String key) {
		removeKeyStroke("pressed " + key);
		removeKeyStroke("released " + key);
		removeKeyStroke("typed " + key);
	}
	
	/**
	 * Clears all interactions (pressed, released, and typed) with the given keys.
	 * @param keys the keys (the part after VK_, such as A or SHIFT or DOWN)
	 * @see #clearLetterKeys(String)
	 */
	public void clearKeys(String...keys) {
		for (var key : keys)
			clearKey(key);
	}
	
	/**
	 * Clears all interactions (pressed, released, and typed) with the given letter keys. Passing
	 * "SHIFT" to this method will clear S, H, I, F, and T; use {@link #clearKey(String)} for multi-character
	 * keys.
	 * @param letters the keys (the parts after VK_, such as A or Z)
	 */
	public void clearLetterKeys(String letters) {
		for (int i = 0; i < letters.length();)
			clearKey(letters.substring(i, ++i));
	}

	@FunctionalInterface
	public static interface State {
		void set(boolean b);
	}

	@FunctionalInterface
	public static interface Command {
		void invoke();
	}
}
