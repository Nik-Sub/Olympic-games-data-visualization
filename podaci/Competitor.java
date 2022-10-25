package podaci;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

class numOfMedalsAndSports {
	int num = 0;
	// sports where he won medal
	Set<String> sports = new HashSet<>();
	// disciplines where he won medal
	Set<String> disciplines = new HashSet<>();
};
public abstract class Competitor {
	// maybe competitor was on more disciplines
	protected Set<Event> disciplines = new HashSet<>();
	protected HashMap<Medal.Type, numOfMedalsAndSports> medalsOfCompetitor = new HashMap<>();
	protected String id;
	
	// for athlete
	public Competitor(String idd, Event e, Medal m) {
		id = idd;
		// initialization for medals of competitor
		medalsOfCompetitor.put(Medal.Type.GOLD, new numOfMedalsAndSports());
		medalsOfCompetitor.put(Medal.Type.SILVER, new numOfMedalsAndSports());
		medalsOfCompetitor.put(Medal.Type.BRONZE, new numOfMedalsAndSports());
		medalsOfCompetitor.put(Medal.Type.NOMEDAL, new numOfMedalsAndSports());
		// NOMEDAL will incrementing if competitor does not have medal
		medalsOfCompetitor.get(m.getTypeOfMedal()).num++;
		// marking in which sport and discipline he won medal
		if (!m.getTypeOfMedal().equals(Medal.Type.NOMEDAL)) {
			medalsOfCompetitor.get(m.getTypeOfMedal()).sports.add(e.getSport());
			medalsOfCompetitor.get(m.getTypeOfMedal()).disciplines.add(e.getName());
		}
		disciplines.add(e);		
	}
	
	// for team, because i will know id at the end of read line from file
	public Competitor(Event e, Medal m) {
		// initialization for medals of competitor
		medalsOfCompetitor.put(Medal.Type.GOLD, new numOfMedalsAndSports());
		medalsOfCompetitor.put(Medal.Type.SILVER, new numOfMedalsAndSports());
		medalsOfCompetitor.put(Medal.Type.BRONZE, new numOfMedalsAndSports());
		medalsOfCompetitor.put(Medal.Type.NOMEDAL, new numOfMedalsAndSports());
		// NOMEDAL will incrementing if competitor does not have medal
		medalsOfCompetitor.get(m.getTypeOfMedal()).num++;
		// marking in which sport and discipline he won medal
		if (m.getTypeOfMedal() != Medal.Type.NOMEDAL) {
			medalsOfCompetitor.get(m.getTypeOfMedal()).sports.add(e.getSport());
			medalsOfCompetitor.get(m.getTypeOfMedal()).disciplines.add(e.getName());
		}
		disciplines.add(e);
	}
	
	public Set<Event> getDisciplines() {
		return disciplines;
	}

	public String getId() {
		return id;
	}

	public void setId(String idd) {
		id = idd;
	}
	
	public HashMap<Medal.Type, numOfMedalsAndSports> getMedals() {
		return medalsOfCompetitor;
	}

	public abstract boolean getIndividual();
}
