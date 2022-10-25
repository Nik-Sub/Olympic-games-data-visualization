package gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.image.RescaleOp;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XYGraph extends Graph {
	// i will need only max for y-axis(number of disciplines, average height, average weight)
	private int maxY;
	private int minY;
	// i will need both min and max for x-axis(year)
	private int minYear = 1950;
	private int maxYear = 2020;

	private int divisionsY;
	private int divisionsX;
	
	private int mousePressX;
	private int mousePressY;
	private int mouseReleX;
	private int mouseReleY;
	
	boolean flag = false;
	
	
	public XYGraph(String[] dataa) {
		super(dataa);
		
		addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				mouseReleX = e.getX();
				mouseReleY = e.getY();
				
				//+ (mousePressX - 50) % divisionsX
				int oldMinYear = minYear;
				minYear = minYear + ((mousePressX - 50) / 50 ) * divisionsX;
				maxYear = oldMinYear + ((mouseReleX - 50) / 50 ) * divisionsX;
				
				//-  (mousePressY % divisionsY)
				int oldMaxY = maxY;
				maxY = maxY - ((mousePressY - 50) / 50) * divisionsY;
				minY = oldMaxY - ((mouseReleY - 50) / 50) * divisionsY;
				
				flag = true;
				repaint();
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				mousePressX = e.getX();
				mousePressY = e.getY();
				
			}
			
			
			
			
		});
	}
	
	public void setMaxY(int maxY) {
		this.maxY = maxY;
	}

	public void setMinYear(int minYear) {
		this.minYear = minYear;
	}
	public int getMinYear() {
		return minYear;
	}

	public void setMaxYear(int maxYear) {
		this.maxYear = maxYear;
	}
	public int getMaxYear() {
		return maxYear;
	}

	

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		
		if (data != null) {
			// y axis
			// looking for max value on y axes
			if (!flag) {
				String line = data[0];
				// string is like year~season~number
				Pattern p = Pattern.compile("([^~]+)~([^~]+)~(.+)");
				Matcher m = p.matcher(line);
				if (m.matches())
					maxY = (int)Double.parseDouble(m.group(3));
			}
			// i will divade Y-axis on 7 parts
			// first find the nearest number which num%7 = 0
			g.drawRect(50, 50, 400, 350);
			int nearestNumDivisibleWithSeven = maxY + 7 - maxY % 7 ;
			divisionsY = (nearestNumDivisibleWithSeven - minY) / 7;
			// height is 350 so ind is incrementing with 50 (we have 7 parts)
			System.out.println(minY + " " + maxY);
			System.out.println("N" + nearestNumDivisibleWithSeven + " D" + divisionsY);
			for (int i = nearestNumDivisibleWithSeven, ind = 0; i >= minY; i -= divisionsY, ind += 50) {
				g.drawLine(45, 50 + ind, 55, 50 + ind);
				g.drawString(((Integer)i).toString(), 20, 50 + ind);
			}
			
			// x axis
			// i will divade Y-axis on 8 parts
			// first find the nearest number which num%8 = 0
			int nearestNumDivisibleWithEight = maxYear + 8 - maxYear % 8; 
			// rounding on first smaller number which is divsible with 8
			int tmpMinYear = minYear - minYear % 8;
			divisionsX = (nearestNumDivisibleWithEight - tmpMinYear) / 8;
			// width is 400 so ind is incrementing with 50 (we have 8 parts)
			for (int i = tmpMinYear, ind = 0; i <= nearestNumDivisibleWithEight; i += divisionsX, ind += 50) {
				g.drawLine(50 + ind, 395, 50 + ind, 405);
				g.drawString(((Integer)i).toString(), 50 + ind, 420);
			}
			
			// drawing points
			for (String tmp : data) {
				Pattern pp = Pattern.compile("([^~]+)~([^~]+)~(.+)");
				Matcher mm = pp.matcher(tmp);
				int year;
				String season;
				int number;
				if (mm.matches()) {
					year = Integer.parseInt(mm.group(1));
					if (year < minYear || year > maxYear) 
						continue;
					season = mm.group(2);
					number = (int)Double.parseDouble(mm.group(3));
					
					// find coordinates
					int x = 50 + (year - tmpMinYear) / divisionsX * 50 + (year - tmpMinYear) % divisionsX * (50 / divisionsX);
					int y = 400 - number / divisionsY * 50 - number % divisionsY * (50 / divisionsY);
					//System.out.println(year - minYear);
					//System.out.println(x + " " + y);
					Color oldColor = g.getColor();
					if (season.equals("Summer"))
						g.setColor(Color.RED);
					else
						g.setColor(Color.BLUE);
					
					g.fillOval(x, y, 7, 7);
				
					g.setColor(oldColor);
				
				}
				
			}
		}
		
		
		
		
	}
}
