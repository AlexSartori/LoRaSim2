package gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToolBar;

/**
 *
 * @author alex
 */
public class CanvasWindow extends javax.swing.JFrame implements ActionListener {
    private JToolBar toolbar;
    private CanvasPanel canvas;
    
    public CanvasWindow() {
        super();
        this.initUI();
    }
    
    private void initUI() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(800, 600);
        this.setTitle("LoRaSim2");
        this.setLayout(new BorderLayout());
        
        /* Toolbar ---------------------------------------------------------- */
        JPanel tb_panel = new JPanel(new FlowLayout());
        
        JButton tb_add = new JButton("Add Node");
        JButton tb_edit = new JButton("Edit Node");
        JButton tb_del = new JButton("Delete Node");
        
        tb_add.addActionListener(this);
        tb_edit.addActionListener(this);
        tb_del.addActionListener(this);
        
        tb_panel.add(tb_add);
        tb_panel.add(tb_edit);
        tb_panel.add(tb_del);
        
        toolbar = new JToolBar();
        toolbar.add(tb_panel);
        
        this.add(toolbar, BorderLayout.NORTH);
        
        /* Canvas ----------------------------------------------------------- */
        canvas = new CanvasPanel();
        this.add(canvas, BorderLayout.CENTER);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        String cmd = ae.getActionCommand();
        
        switch (cmd) {
            case "Add Node":
                canvas.beginAddNode();
                break;
            case "Edit Node":
                canvas.beginEditNode();
                break;
            case "Delete Node":
                canvas.beginDelNode();
                break;
            default:
                System.err.println("Unexpected action command in CanvasWindow listener: " + cmd);
                break;
        }
    }
}
