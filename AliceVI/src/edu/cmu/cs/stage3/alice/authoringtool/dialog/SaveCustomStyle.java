package edu.cmu.cs.stage3.alice.authoringtool.dialog;

import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class SaveCustomStyle extends JFrame{
	JLabel lblStyleName;
	JTextField tfStyleName;
	JButton btnSave;
	JButton btnCancel;
	JCheckBox cbDefault;
		
	//JPanel panel;
	public SaveCustomStyle()
	{
		//panel = new JPanel();
		
		lblStyleName = new JLabel("Name : ");
		tfStyleName = new JTextField();
		lblStyleName.setLabelFor(tfStyleName);
		
		btnSave = new JButton("Save");
		btnCancel = new JButton("Cancel");
		cbDefault = new JCheckBox("Set as default");
		
		this.setLayout(new GridLayout(0,2));
		//this.setSize(200,200);
		
		this.add(lblStyleName);
		this.add(tfStyleName);
		this.add(cbDefault);
		
		this.add(btnSave);
		this.add(btnCancel);
		
		
		this.setSize(200,200);
	
		//	Object[] objects = {tfStyleName};
		

		 //Handle window closing correctly.
		// setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);




	}

}
