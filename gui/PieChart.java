package gui;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class PieChart extends Graph {

	// will need this to find percents for first 10 countries
	private int sum = 0;
	private int radius = 140;
	
	public PieChart(String[] dataa) {
		super(dataa);
		setBackground(Color.GRAY);
	}

	private void addNumber(int num) {
		sum += num;
	}
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		if (data != null) {
			if (data.length == 0)
				return;
			// how many competitors we have
			Stream<String> competitors = Arrays.stream(data);
			// line is like Country!numOfComp
			competitors.forEach((line) -> {
				Pattern p = Pattern.compile("([^!]+)!([0-9]+)");
				Matcher m = p.matcher(line);
				if (m.matches())
					addNumber(Integer.parseInt(m.group(2)));
			});
			// calculating % of all Competitors just for first 10
			// % will represent angle
			int percents[] = new int[10];
			for (int i = 0; i < 10; i++) {
				Pattern p = Pattern.compile("([^!]+)!([0-9]+)");
				Matcher m = p.matcher(data[i]);
				if (m.matches())
					if (sum != 0)
						percents[i] = Integer.parseInt(m.group(2)) * 100 / sum;
			}
			int startAngle = 0;
			Color oldColor = g.getColor();
			HashSet<Integer> usedColors = new HashSet<>();
			for (int i = 0; i < 11; i++) {
				Color c;
				do {
					c = new Color(new Random().nextInt() & (int)(Math.pow(2, 25) - 1));
				}while(usedColors.contains(c));
				g.setColor(c);
				// for drawing name of the country
				int halfAngle;
				if (i == 10) {
					halfAngle = (360 + startAngle) / 2;
				}
				else {
					halfAngle = (2 * startAngle + percents[i] * 360 / 100 ) / 2;
				}
				int xm = (int)(Math.cos( halfAngle * Math.PI / 180) * (radius + 30));
				int ym = (int)(Math.sin( halfAngle * Math.PI / 180) * (radius + 30));
				if (i == 10) {
					g.fillArc(getWidth() / 2 - radius, getHeight() / 2 - radius, 2 * radius, 2 * radius, startAngle, 360 - startAngle);
					g.drawString("Others",getWidth() / 2 + xm + (halfAngle > 90 && halfAngle < 270?-4 * "Others".length():-20), getHeight() / 2 - ym + (halfAngle >= 0 && halfAngle <= 180?10:-10));
					continue;
				}
				g.fillArc(getWidth() / 2 - radius, getHeight() / 2 - radius, 2 * radius, 2 * radius, startAngle, percents[i] * 360 / 100);
				
				// for drawing name of the country
				//int halfAngle = (2 * startAngle + percents[i] * 360 / 100 ) / 2;
				//int xm = (int)(Math.cos( halfAngle * Math.PI / 180) * (radius + 30));
				//int ym = (int)(Math.sin( halfAngle * Math.PI / 180) * (radius + 30));

				Pattern p = Pattern.compile("([^!]+)!([0-9]+)");
				Matcher m = p.matcher(data[i]);
				if (m.matches())
					g.drawString(m.group(1),getWidth() / 2 + xm + (halfAngle > 90 && halfAngle < 270?-4 * m.group(1).length():-20), getHeight() / 2 - ym + (halfAngle >= 0 && halfAngle <= 180?10:0));
				
				startAngle += percents[i] * 360 / 100;
			}
			g.setColor(oldColor);
		}
		sum = 0;
	}

	
	
}
