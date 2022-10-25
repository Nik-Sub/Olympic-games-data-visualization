package gui;

import podaci.*; 

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog.ModalityType;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class Application {
	
	static {
		System.loadLibrary("JNI_Projekat2POOP");
	}
	
	private JFrame content = new JFrame();
	private Reader r = new Reader();
	private Graph graph;
	
	private JTextField events = new JTextField(15);
	private JTextField athletes = new JTextField(15);
	private JTextField yearForRead = new JTextField(15);
	private JButton apply = new JButton("Apply");
	
	// for PieChart
	private JPanel filterPanel = new JPanel();
	private JButton filter = new JButton("Use filter!");
	private JTextField sport = new JTextField(15);
	private JTextField year = new JTextField(15);
	private JRadioButton individual = new JRadioButton("Individual");
	private JRadioButton team = new JRadioButton("Team");
	private JRadioButton gold = new JRadioButton("GOLD");
	private JRadioButton silver = new JRadioButton("SILVER");
	private JRadioButton bronze = new JRadioButton("BRONZE");
	private JRadioButton noMedal = new JRadioButton("NOMEDAL");
	// for type
	private ButtonGroup g1 = new ButtonGroup();
	// for medal
	private ButtonGroup g2 = new ButtonGroup();
	// for discipline, height and weight
	private ButtonGroup g3 = new ButtonGroup();
	
	private JButton drawPieChart = new JButton("Draw graph!");
	
	// for XYGraph
	//from year
	private JSlider sliderF = new JSlider(1800, 2020);
	private JSpinner spinnerF = new JSpinner();
	//to Year
	private JSlider sliderT = new JSlider(1800, 2020); // we will make this after chose from year
	private JSpinner spinnerT = new JSpinner();
	private JRadioButton disciplines = new JRadioButton("Disciplines");
	private JRadioButton avrgHeight = new JRadioButton("Average height");
	private JRadioButton avrgWeight = new JRadioButton("Average weight");
	private JButton drawXYGraph = new JButton("Draw graph!");
	
	
	private GridBagConstraints gbc = new GridBagConstraints();
	
	
	private void populateWindowForXYGraph() {
		content.getContentPane().removeAll();
		filterPanel.removeAll();
		content.repaint();
		// for changing options for filtering
		filterPanel.setBackground(Color.YELLOW);
		JMenuItem pieChart = new JMenuItem("PieChart");
		JMenuItem XYGraph = new JMenuItem("XYGraph");
		JMenu option = new JMenu("Option");
		option.add(pieChart);
		option.add(XYGraph);
		JMenuBar menuBar = new JMenuBar();
		menuBar.add(option);
		content.setJMenuBar(menuBar);
		
		// listeners for changing mode
		pieChart.addActionListener((ae) -> {
			populateWindowForPieChart();
		});
		XYGraph.addActionListener((ae)-> {
			populateWindowForXYGraph();
		});
		
		filterPanel.setLayout(new GridBagLayout());
		
		gbc.gridx = 0;
		gbc.gridy = 0;
		filterPanel.add(new JLabel("Year from:"), gbc);
		gbc.gridx = 1;
		gbc.gridy = 0;
		filterPanel.add(sliderF, gbc);
		gbc.gridx = 2;
		gbc.gridy = 0;
		filterPanel.add(spinnerF, gbc);
        sliderF.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider s = (JSlider) e.getSource();
                spinnerF.setValue(s.getValue());
				sliderT.setMinimum((Integer) s.getValue());
                disciplines.setEnabled(true);
                avrgHeight.setEnabled(true);
                avrgWeight.setEnabled(true);
                
            }
        });
		spinnerF.setModel(new SpinnerNumberModel(1950, 1800, 2020, 1));
        spinnerF.setEditor(new JSpinner.NumberEditor(spinnerF, "0"));
        spinnerF.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JSpinner s = (JSpinner) e.getSource();
                sliderF.setValue((Integer) s.getValue());
				sliderT.setMinimum((Integer) s.getValue());
				((XYGraph)graph).setMinYear((Integer) s.getValue());
			}
		});
        
        gbc.gridx = 0;
		gbc.gridy = 1;
		filterPanel.add(new JLabel("Year to:"), gbc);
		gbc.gridx = 1;
		gbc.gridy = 1;
		filterPanel.add(sliderT, gbc);
		sliderT.setMinimum(sliderT.getValue());
		gbc.gridx = 2;
		gbc.gridy = 1;
		filterPanel.add(spinnerT, gbc);
		spinnerT.setValue(sliderT.getValue());
        sliderT.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider s = (JSlider) e.getSource();
                spinnerT.setValue(s.getValue());
                sliderF.setMaximum(s.getValue());
                disciplines.setEnabled(true);
                avrgHeight.setEnabled(true);
                avrgWeight.setEnabled(true);
            }
        });
        

		spinnerT.setModel(new SpinnerNumberModel(1951, 1800, 2020, 1));
        spinnerT.setEditor(new JSpinner.NumberEditor(spinnerT, "0"));
        spinnerT.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JSpinner s = (JSpinner) e.getSource();
                sliderT.setValue((Integer) s.getValue());
                ((XYGraph)graph).setMaxYear((Integer) s.getValue());
                sliderF.setMaximum((Integer) s.getValue());
			}
		});
		
        g3.add(disciplines);
        g3.add(avrgHeight);
        g3.add(avrgWeight);
        
        gbc.gridx = 0;
		gbc.gridy = 2;
		drawXYGraph.setEnabled(false);
		filterPanel.add(drawXYGraph, gbc);
		drawXYGraph.addActionListener((ae) -> {
			g3.clearSelection();
			sliderF.setEnabled(true);
			spinnerF.setEnabled(true);
			sliderT.setEnabled(true);
			spinnerT.setEnabled(true);
			disciplines.setEnabled(false);
            avrgHeight.setEnabled(false);
            avrgWeight.setEnabled(false);
            drawXYGraph.setEnabled(false);
            
			content.repaint();
		});
		
		gbc.gridx = 1;
		gbc.gridy = 2;
		filterPanel.add(disciplines, gbc);
		disciplines.setEnabled(false);
		disciplines.addActionListener((ae)->{
			drawXYGraph.setEnabled(true);
			sliderF.setEnabled(false);
			spinnerF.setEnabled(false);
			sliderT.setEnabled(false);
			spinnerT.setEnabled(false);
			graph.setData(r.numberOfDisciplinesPerYears(((XYGraph)graph).getMinYear(), ((XYGraph)graph).getMaxYear()));
		});
		gbc.gridx = 2;
		gbc.gridy = 2;
		filterPanel.add(avrgHeight, gbc);
		avrgHeight.setEnabled(false);
		avrgHeight.addActionListener((ae)->{
			drawXYGraph.setEnabled(true);
			sliderF.setEnabled(false);
			spinnerF.setEnabled(false);
			sliderT.setEnabled(false);
			spinnerT.setEnabled(false);
			graph.setData(r.averageHeightPerYears(((XYGraph)graph).getMinYear(), ((XYGraph)graph).getMaxYear()));
		});
		gbc.gridx = 3;
		gbc.gridy = 2;
		filterPanel.add(avrgWeight, gbc);
		avrgWeight.setEnabled(false);
		avrgWeight.addActionListener((ae)->{
			drawXYGraph.setEnabled(true);
			sliderF.setEnabled(false);
			spinnerF.setEnabled(false);
			sliderT.setEnabled(false);
			spinnerT.setEnabled(false);
			graph.setData(r.averageWeightPerYears(((XYGraph)graph).getMinYear(), ((XYGraph)graph).getMaxYear()));
		});
        
		content.add(filterPanel, BorderLayout.PAGE_START);
		graph = new XYGraph(null);
		graph.setSize(400, 600);
		content.add(graph, BorderLayout.CENTER);
		content.revalidate();
	}
	
	private void populateWindowForPieChart() {
		// for changing options for filtering
		content.getContentPane().removeAll();
		filterPanel.removeAll();
		content.repaint();
		g1.add(individual);
		g1.add(team);
		
		g2.add(gold);
		g2.add(silver);
		g2.add(bronze);
		g2.add(noMedal);
		
		JMenuItem pieChart = new JMenuItem("PieChart");
		JMenuItem XYGraph = new JMenuItem("XYGraph");
		JMenu option = new JMenu("Option");
		option.add(pieChart);
		option.add(XYGraph);
		JMenuBar menuBar = new JMenuBar();
		menuBar.add(option);
		content.setJMenuBar(menuBar);
		
		// listeners for changing mode
		pieChart.addActionListener((ae) -> {
			populateWindowForPieChart();
		});
		XYGraph.addActionListener((ae)-> {
			populateWindowForXYGraph();
		});
		
		filterPanel.setLayout(new GridBagLayout());
		
		gbc.insets = new Insets(5, 5, 5, 5);
		
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		filterPanel.add(new JLabel("FILTERS:"), gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		filterPanel.add(new JLabel("Sport:"), gbc);
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		filterPanel.add(sport, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridwidth = 1;
		filterPanel.add(new JLabel("Year:"), gbc);
		gbc.gridx = 1;
		gbc.gridy = 2;
		gbc.gridwidth = 1;
		filterPanel.add(year, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.gridwidth = 1;
		filterPanel.add(new JLabel("Type:"), gbc);
		gbc.gridx = 1;
		gbc.gridy = 3;
		gbc.gridwidth = 1;
		filterPanel.add(individual, gbc);
		gbc.gridx = 2;
		gbc.gridy = 3;
		gbc.gridwidth = 1;
		filterPanel.add(team, gbc);
		gbc.gridx = 0;
		gbc.gridy = 4;
		gbc.gridwidth = 1;
		filterPanel.add(new JLabel("Medal:"), gbc);
		gbc.gridx = 1;
		gbc.gridy = 4;
		gbc.gridwidth = 1;
		filterPanel.add(gold, gbc);
		gbc.gridx = 2;
		gbc.gridy = 4;
		gbc.gridwidth = 1;
		filterPanel.add(silver, gbc);
		gbc.gridx = 3;
		gbc.gridy = 4;
		gbc.gridwidth = 1;
		filterPanel.add(bronze, gbc);
		gbc.gridx = 4;
		gbc.gridy = 4;
		gbc.gridwidth = 1;
		filterPanel.add(noMedal, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 5;
		gbc.gridwidth = 1;
		filterPanel.add(filter, gbc);
		
		filter.addActionListener((ae) -> {
			// for type
			String type = "";
			if (individual.isSelected()) {
				type += true;
			}
			else if (team.isSelected()) {
				type += false;
			}
			else {
				type = "";
			}
			// for medal
			String medal;
			if (gold.isSelected()) {
				medal = gold.getLabel();
			}
			else if (silver.isSelected()) {
				medal = silver.getLabel();
			}
			else if (bronze.isSelected()) {
				medal = bronze.getLabel();
			}
			else {
				medal = "";
			}
			r.setFilter(sport.getText(), year.getText(), type, medal);
			
			graph.setData(r.NumberOfContestants());
			drawPieChart.setEnabled(true);
		});
		
		drawPieChart.addActionListener((ae) -> {
			content.repaint();
			drawPieChart.setEnabled(false);
		});
		
		gbc.gridx = 1;
		gbc.gridy = 5;
		gbc.gridwidth = 1;
		filterPanel.add(drawPieChart, gbc);
		drawPieChart.setEnabled(false);
		filterPanel.setBackground(Color.YELLOW);
		content.add(filterPanel, BorderLayout.PAGE_START);
		
		graph = new PieChart(null);
		graph.setSize(400, 600);
		content.add(graph, BorderLayout.CENTER);
		content.revalidate();
	}
	
	void dialogWindowForFiles() {
		JDialog toChoseFiles = new JDialog(content, ModalityType.APPLICATION_MODAL);
		toChoseFiles.setBounds(500, 200, 400, 300);
		toChoseFiles.setLayout(new GridBagLayout());
		
		// events
		gbc.gridx = 0;
		gbc.gridy = 0;
		toChoseFiles.add(new JLabel("File for events: "), gbc);
		gbc.gridx = 1;
		gbc.gridy = 0;
		toChoseFiles.add(events, gbc);
		
		// athletes
		gbc.gridx = 0;
		gbc.gridy = 1;
		toChoseFiles.add(new JLabel("File for athletes: "), gbc);
		gbc.gridx = 1;
		gbc.gridy = 1;
		toChoseFiles.add(athletes, gbc);
		
		// year
		gbc.gridx = 0;
		gbc.gridy = 2;
		toChoseFiles.add(new JLabel("Special year (0 if u dont want): "), gbc);
		gbc.gridx = 1;
		gbc.gridy = 2;
		toChoseFiles.add(yearForRead, gbc);
		
		// button apply
		gbc.gridx = 0;
		gbc.gridy = 3;
		toChoseFiles.add(apply, gbc);
		
		apply.addActionListener((ae) -> {
			toChoseFiles.dispose();
		});
		
		toChoseFiles.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
		
		toChoseFiles.setVisible(true);
	}
	
	public Application() throws Exception {
		content.setTitle("Olympic Games");
		content.setBounds(500, 200, 600, 600);
		// let's set for default PieChart
		populateWindowForPieChart();
		//populateWindowForXYGraph();
		dialogWindowForFiles();
		try {
			Pattern p = Pattern.compile("[a-zA-Z0-9_]+.txt");
			Matcher m = p.matcher(events.getText());
			if (!m.matches())
				throw new Exception();
			m = p.matcher(athletes.getText());
			if (!m.matches())
				throw new Exception();
			p = Pattern.compile("[0-9]+");
			if (!m.matches()) {
				throw new Exception();
			}
			m = p.matcher(yearForRead.getText());
			r.read(events.getText(), athletes.getText(), Integer.parseInt(yearForRead.getText()));
		}
		catch(Exception e) {
			content.dispose();
			throw new Exception();
		}
		content.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		content.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				JDialog dialog = new JDialog(content, ModalityType.APPLICATION_MODAL);
				dialog.setBounds(500 + content.getWidth() / 2,200 + content.getHeight() / 2, 200, 100);
				dialog.setLayout(new GridLayout(2, 1));
				
				JPanel panelLabel = new JPanel();
				JLabel label = new JLabel("Are you sure you want to exit?");
				panelLabel.add(label);
				
				JPanel buttonsPanel = new JPanel();
				JButton yes = new JButton("Yes");
				JButton close = new JButton("Close");
				buttonsPanel.add(yes);
				buttonsPanel.add(close);
				dialog.add(panelLabel);
				dialog.add(buttonsPanel);
				
				yes.addActionListener((ae) -> {
					content.dispose();
				});
				
				close.addActionListener((ae) -> {
					dialog.dispose();
				});
				
				dialog.setVisible(true);
				
				
			}
		});
		content.setVisible(true);
	}
	
	public static void main(String[] args) {
		
		try {
			new Application();
		}
		catch(Exception e) {
			System.out.println("Error with entry files!");
		}
	}
}
