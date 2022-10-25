package podaci;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

class OG {
	int year;
	String season;

	OG(int y, String seas) {
		year = y;
		season = seas;
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}
	@Override
	public boolean equals(Object obj) {
		OG o = (OG)obj;
		return ((Integer)year).equals(o.year) && season.equals(o.season);
	}
};

class Details {
	public String name;
	public String gender;
	//string because we can have mark NA
	public String age;
	public String height;
	public String weight;
	public boolean wonMedalOnFirstAppearance = false;
	public int yearOfFirstAppearance;
	public boolean wonMedalIndividual = false;
	// countries for which he won individualMedal
	public Set<String> countriesIndividualMedal = new HashSet<>();
	public boolean wonMedalTeam = false;
	// countires for which he won teamMedal
	public Set<String> countriesTeamMedal = new HashSet<>();
	


	
	// all OG for this athlete
	public Set<OG> allOG = new HashSet<>();

	// maybe i won't use this constructor
	public Details(String namee, String genderr, String agee, String heightt, String weightt) {
		name = namee;
		gender = genderr;
		age = agee;
		height = heightt;
		weight = weightt;
	}

	public Details(){}

	
	@Override
	public int hashCode() {
		return super.hashCode();
	}
	@Override
	public boolean equals(Object obj) {
		Details d2 = (Details)obj;
		return name.equals(d2.name) && gender.equals(d2.gender) && age.equals(d2.age) && height.equals(d2.height) && weight.equals(d2.weight);
	}
	
};

public class DetailsAboutAthlete {
	private HashMap<Integer, Details> aboutCompetitors = new HashMap<>();

	private static DetailsAboutAthlete instance;
	
	
	public DetailsAboutAthlete() {};
	
	//struct KeyForOlympicGame;
	public HashMap<Integer, Details> getAboutCompetitors(){
		return aboutCompetitors;
	}

	public static DetailsAboutAthlete getInstance() {
		if (instance == null) {
			instance = new DetailsAboutAthlete();
		}
		return instance;
	}

	// insert() won't add pair if pair with the same key already exists
	public boolean add(int id, Details det, int year, String season, String country) {
		// i need return value of this method
		boolean newAthleta = false;
		// if we first hit this athlete
		if (!aboutCompetitors.containsKey(id)) {
			newAthleta = true;
			aboutCompetitors.put(id, det);
			det.allOG.add(new OG(year, season));
			// if he won individual
			if (det.wonMedalIndividual) {
				det.countriesIndividualMedal.add(country);
			}
			// if he won team
			else if (det.wonMedalTeam) {
				det.countriesTeamMedal.add(country);
			}
			
		}
		// athlete already exists
		// check if competitor won medal with team or individual or both
		else  {

			// for 10 youngest which won medal on first appearance

			if (year < aboutCompetitors.get(id).yearOfFirstAppearance || (year <= aboutCompetitors.get(id).yearOfFirstAppearance && aboutCompetitors.get(id).wonMedalOnFirstAppearance == false)) {
				aboutCompetitors.get(id).yearOfFirstAppearance = year;
				aboutCompetitors.get(id).wonMedalOnFirstAppearance = det.wonMedalIndividual || det.wonMedalTeam;
			}
			// if he won individual
			if (det.wonMedalIndividual) {
				aboutCompetitors.get(id).wonMedalIndividual = det.wonMedalIndividual;
				aboutCompetitors.get(id).countriesIndividualMedal.add(country);
			}
			// if he won team
			else if (det.wonMedalTeam) {
				aboutCompetitors.get(id).wonMedalTeam = det.wonMedalTeam;
				aboutCompetitors.get(id).countriesTeamMedal.add(country);
			}
		}
		// returnig check.second because i need this information when i'm adding details for athletes 
		return newAthleta;
	}

	// if key doesn't exist, aboutCompetitors.end() will be returned, key won't be added
	public boolean athleteExists(int id) {
		return aboutCompetitors.containsKey(id);
	}

	public Details getDetailsAboutCompetitor(int id) {
		return aboutCompetitors.get(id);
	}
}
