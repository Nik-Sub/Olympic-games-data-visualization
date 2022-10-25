package podaci;

import java.util.HashMap;

public class Games {
	private HashMap<String, Competitor> competitors = new HashMap<>();
	private String city;

	public Games(String cityy) {
		city = cityy;
	}
	public Games(){};
	
	public HashMap<String, Competitor> getCompetitors(){
		return competitors;
	}
	
	public void addCompetitor(Competitor comp) {
		if (!competitors.containsKey(comp))
			competitors.put(comp.getId(), comp);
		//System.out.println(competitors.size());
	}
}
