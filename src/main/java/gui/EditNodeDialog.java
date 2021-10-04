package gui;

import javax.swing.JDialog;
import javax.swing.JLabel;

/**
 *
 * @author alex
 */
public class EditNodeDialog extends JDialog {
    public EditNodeDialog() {
        super();
        this.setSize(300, 150);
        this.setTitle("Edit Node");
        this.add(new JLabel("Henlo"));
    }
}
