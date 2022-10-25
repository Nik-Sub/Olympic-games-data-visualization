package podaci;

import java.util.HashMap;

public class Country {
	private String name;
	private HashMap<String, Competitor> competitors = new HashMap<>();

	
	public Country(String namee){
		name = namee;
	}
	public Country() {}
	
	public void addCompetitor(Competitor comp, Medal m, String sport, String eve) {
	    if (competitors.containsKey(comp.getId())) {
	        // NOMEDAL will incrementing if competitor does not have medal
	        ((competitors.get(comp.getId())).getMedals()).get(m.getTypeOfMedal()).num++;
	        // marking in which sport he won medal
	        if (m.getTypeOfMedal() != Medal.Type.NOMEDAL) {
	        	((competitors.get(comp.getId())).getMedals()).get(m.getTypeOfMedal()).sports.add(sport);
	        	((competitors.get(comp.getId())).getMedals()).get(m.getTypeOfMedal()).disciplines.add(eve);
	        }
	        // if we have that competitor, just adding his discipline to the set if he changed discipline
	        // comp will have only one discipline in his set!!!!
	        (competitors.get(comp.getId())).getDisciplines().add(comp.getDisciplines().iterator().next());
	    }
	    else {
	    	competitors.put(comp.getId(), comp);
	    }
	}

	public HashMap<String, Competitor> getCompetitors() {
		return competitors;
	}

	public String getName() {
		return name;
	}

	//public boolean countryHasCompetitorsFromDiscipline(std::string nameOfDiscipline) const;


	//friend bool operator==(const Country& c1, const Country& c2) {
	//	return c1.name == c2.name;
	//}
}
