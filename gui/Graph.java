package gui;



import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;


public abstract class Graph extends JPanel{
	
	// data which we will draw on graph
	protected String[] data;
	
	
	public Graph(String[] dataa) {
		data = dataa;
	}
	
	public void setData(String[] d) {
		data = d;
	}
	
}
