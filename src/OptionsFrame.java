/*
 * John McCrummen
 * CS 3160
 * OO Project
 * 
 * -OptionsFrame.java-
 * frame that pops up when you click 'options' on game screen that
 * allows you to change the size of the field and number of mines
 * (starts new game when player hits 'OK')
 */
import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.Font;
import java.awt.event.*;


public class OptionsFrame extends JFrame implements ChangeListener, ActionListener{
	
	//global variables
	private int rows;
	private int mines;
	
	JLabel lblNumMines = new JLabel("16");
	JSlider slider = new JSlider();
	
	public JFrame gameWindow; // refers to current open window of game. gets closed and new one is opened if options are changed


	public OptionsFrame() {
		initialize();
	}//end constructor
	
	private void initialize() {
		rows = mines = 8;
		this.setBounds(100, 100, 444, 207);
		this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		this.setResizable(false);
		getContentPane().setLayout(null);
		
		JLabel lblGridSize = new JLabel("Grid Size");
		lblGridSize.setHorizontalAlignment(SwingConstants.CENTER);
		lblGridSize.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lblGridSize.setBounds(10, 11, 190, 14);
		getContentPane().add(lblGridSize);
		
		JLabel lblNumberOfMines = new JLabel("Number of Mines");
		lblNumberOfMines.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lblNumberOfMines.setBounds(234, 11, 190, 14);
		getContentPane().add(lblNumberOfMines);
		
		JRadioButton rdbtnX = new JRadioButton("8 x 8");
		rdbtnX.setSelected(true);
		rdbtnX.setBounds(76, 32, 109, 23);
		getContentPane().add(rdbtnX);
		rdbtnX.addActionListener(this);
		rdbtnX.setActionCommand("8");
		
		JRadioButton rdbtnX_1 = new JRadioButton("16 x 16");
		rdbtnX_1.setBounds(76, 58, 109, 23);
		getContentPane().add(rdbtnX_1);
		rdbtnX_1.addActionListener(this);
		rdbtnX_1.setActionCommand("16");
		
		JRadioButton rdbtnX_2 = new JRadioButton("24 x 24");
		rdbtnX_2.setBounds(76, 84, 109, 23);
		getContentPane().add(rdbtnX_2);
		rdbtnX_2.addActionListener(this);
		rdbtnX_2.setActionCommand("24");
		
		/*// Way too big
		JRadioButton rdbtnX_3 = new JRadioButton("32 x 32");
		rdbtnX_3.setBounds(76, 110, 109, 23);
		getContentPane().add(rdbtnX_3);
		rdbtnX_3.addActionListener(this);
		rdbtnX_3.setActionCommand("32");
		*/
		
		ButtonGroup group = new ButtonGroup();
		group.add(rdbtnX);
		group.add(rdbtnX_1);
		group.add(rdbtnX_2);
		//group.add(rdbtnX_3);
		
		slider.setMinimum(1);
		slider.setMaximum(32);
		slider.setValue(mines);
		slider.setBounds(204, 84, 200, 26);
		getContentPane().add(slider);
		slider.addChangeListener(this);
		
		lblNumMines.setFont(new Font("Tahoma", Font.ITALIC, 22));
		lblNumMines.setHorizontalAlignment(SwingConstants.CENTER);
		lblNumMines.setBounds(264, 36, 79, 45);
		getContentPane().add(lblNumMines);
		
		JButton btnOk = new JButton("OK");
		btnOk.setBounds(169, 140, 89, 23);
		getContentPane().add(btnOk);
		btnOk.addActionListener(new ButtonActionListener());
	}//end initialize
	
	public void stateChanged(ChangeEvent e){
		// change text of label to represent slider value
		JSlider src = (JSlider)e.getSource();
		lblNumMines.setText(String.valueOf(src.getValue()));
		mines = Integer.parseInt(lblNumMines.getText());
	}//end stateChanged
	
	public void actionPerformed(ActionEvent e){
		JRadioButton radiosrc = (JRadioButton)e.getSource();
		rows = Integer.parseInt(radiosrc.getActionCommand());
		slider.setMaximum(rows*rows/2); // change value of max # of mines based on grid size
		slider.setValue(slider.getMaximum()/4); // reset slider so doesn't accidentally hold too large of value based on grid size
		lblNumMines.setText(String.valueOf(slider.getValue()));
	}//end actionPerformed
	
	public class ButtonActionListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			JButton buttonsrc = (JButton)e.getSource();
			if (buttonsrc.getText() == "OK"){
				new GameFrame(rows,mines); // open new game window with new settings
				OptionsFrame.this.dispose(); //close options
				gameWindow.dispose(); //close old game window
			}//end if
		}//end actionPerformed
	}//end ButtonActionListener (inner class)
	
}//end class
