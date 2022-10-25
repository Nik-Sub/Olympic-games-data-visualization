package podaci;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;


class Filter {
	String bySport = "";
	String byYear = "";
	String byIndividual = "";
	String byMedal = "";

	public String getSport() {
		return bySport;
	}
	public String getYear() {
		return byYear;
	}
	public String getIndividual() {
		return byIndividual;
	}
	public String getMedal() {
		return byMedal;
	}
	
	
	Filter(String bySp, String byYe, String byInd, String byMed) {
		bySport = bySp;
		byYear = byYe;
		byIndividual = byInd;
		byMedal = byMed;
	}
};

class OlympicGame {
	HashMap<String, Country> countries = new HashMap<>();
	Games games;
	HashMap<String, Sport> sports = new HashMap<>();

	OlympicGame(Games g){
		games = g;
	}
	OlympicGame(){
	}

};

class KeyForOlympicGame {
	int year;
	String season;

	KeyForOlympicGame(int y, String seas) {
		year = y;
		season = seas;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
		    return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
		    return false;
		KeyForOlympicGame k2 = (KeyForOlympicGame) obj;
		return ((Integer)year).equals(k2.year) && season.equals(k2.season);
	}
	
	// i need this method and equals because this is key in HashMap
	@Override
	public int hashCode() {
		int mut;
		if (season.equals("Summer")) mut = 1;
		else mut = 2;
		return year * mut;
	}
};

public class Reader {
	
	private static HashMap<KeyForOlympicGame, OlympicGame> allGames = new HashMap<>();
	private static Filter filter = new Filter(null, "-1", null, null);
	// current oG
	private static OlympicGame oG = null;
	// SYTM - SportYearTypeMedal
	// for every olympic game for every country/discipline
	private String[][][] SYTM;
	// ind for initialazing SYTM
	private static int ind0 = 0;
	private static int ind1 = 0;
	private static int ind2 = 0;
	//private static int brojac;
	
	private static void readEvents(String line, int y, boolean foundedYear) {
		//brojac++;
		// if arg y greater then 0 then look lines with the same year
		if (y > 0) {
			Pattern p = Pattern.compile("(\\d{4}).+");
			Matcher result = p.matcher(line);
			if (result.matches()) {
				Integer i = Integer.parseInt(result.group(1));
				if (!i.equals(y))
					return;
					/* if i found year != y, but flag is true, that means that i've finished with that y
					if (foundedYear == true) {
						//break;
					}
					else {
						//continue;
					}*/
			}
		}

		//foundedYear = true;
		Pattern p = Pattern.compile("^([0-9]+) ([^!]+)!([^!]+)!([^!]+)!([^!]+)!([^!]+)!([^!]+)!(.+)!([A-Za-z ]*\n?$)");
		Matcher m = p.matcher(line);
		if (m.matches()) {
			int year = Integer.parseInt(m.group(1));
			String season = m.group(2);
			String city = m.group(3);
			String sport = m.group(4);
			String discipline = m.group(5);
			boolean individual = (m.group(6).equals("Individual") ? true : false);
			String country = m.group(7);
			// !!!!!!!!!! 8th group will be used for parsing id from that string
			String medal = m.group(9);

			if (!allGames.containsKey(new KeyForOlympicGame(year, season))) {
				oG = new OlympicGame(new Games(city));
				allGames.put(new KeyForOlympicGame(year, season), oG);
			}
			
			
			Country nextCountry = new Country(country);
			Sport nextSport = new Sport(sport);
			
			Event e = new Event(discipline, individual, sport);
	
			// coversion from string to Type
			Medal med = null;
			if (medal.equals("Gold")) {
				med = new Medal(Medal.Type.GOLD);
			}
			else if (medal.equals("Silver")) {
				med = new Medal(Medal.Type.SILVER);
			}
			else if (medal.equals("Bronze")) {
				med = new Medal(Medal.Type.BRONZE);
			}
			else {
				med = new Medal(Medal.Type.NOMEDAL);
			}
			
			// adding sport
			if (!oG.sports.containsKey(sport))
				oG.sports.put(sport, nextSport);
			oG.sports.get(sport).addEvent(e);
			
			String id = ""; // id of Athlete or Team
			Competitor teamOrAth = null;
			if (individual) {
				id = m.group(8);

				Athlete ath = new Athlete(id, e, med);
				Details det = new Details();
				// we need this for finding the 10 youngest athletes
				det.yearOfFirstAppearance = year;
				det.wonMedalIndividual = det.wonMedalOnFirstAppearance = (medal != "" ? true : false);
				
				DetailsAboutAthlete.getInstance().add(Integer.parseInt(id), det, year, season, country);
				teamOrAth = ath;
			}
			else {
				Team team = new Team(e, med);
				line = m.group(8);
				p = Pattern.compile("([0-9]+)");
				m = p.matcher(line);
				int lastMatchPos = 0;
				List<String> forMakingIDTeam = new ArrayList<>(); // we will have id of athletas
				// we are going one by one match and checking end of the string
				while (m.find()) {
					String playerId = m.group(1);
					forMakingIDTeam.add(playerId);
					Athlete ath = new Athlete(playerId, e, med);
					Details det = new Details();
					// we need this for finding the 10 youngest athletes
					det.yearOfFirstAppearance = year;
					det.wonMedalTeam = det.wonMedalOnFirstAppearance = (medal != "" ? true : false);
					
					// i need to pass year to check first appearance
					KeyForOlympicGame keyForOG = new KeyForOlympicGame(year, season);
					DetailsAboutAthlete.getInstance().add(Integer.parseInt(playerId), det, year, season, country);
					team.addAthlete(ath);
				    lastMatchPos = m.end();
				}
				// if it is different, we have bad line in file
				//if (lastMatchPos != line.length())
				//   System.out.println("Invalid string!");
				
				// making id for team
				id += "-";
				for (String ids : forMakingIDTeam) {
					id += ids;
				}
				teamOrAth = team;
				teamOrAth.setId(id);
			}
			
			// adding country
			if (!oG.countries.containsKey(country)) {
				oG.countries.put(country, nextCountry);
			}
			// passing medal to increment number of medals if competitor already exists and sport to mark in which sport he won medal
			oG.countries.get(country).addCompetitor(teamOrAth, med, sport, discipline);
			// adding competitor to the game
			oG.games.addCompetitor(teamOrAth);
			//brojac++;
		}
	}
	
	public static void readDetailsAboutCompetitors(String line)
	{
		
		
		DetailsAboutAthlete detailsAboutAllAthletes = DetailsAboutAthlete.getInstance();
		
		// to stop reading file if we already found all athletes
		//int cnt = detailsAboutAllAthletes.getAboutCompetitors().size();
		
		// pattern to get id of athlete
		Pattern p = Pattern.compile("^([^!]+).+");
		Matcher m = p.matcher(line);
		if (m.matches()) {
			//System.out.println("usao");
			// check if we have that competitor
			String id = m.group(1);
			if (detailsAboutAllAthletes.getAboutCompetitors().containsKey(Integer.parseInt(id))) {
				// if we found id of current competitor
				p = Pattern.compile("[^!]+!([^!]+)!([^!]+)!([^!]+)!([^!]+)!([^\\n]+)\n?$");
				m.reset();
				m = p.matcher(line);
				if (m.matches()) {
					Details details = detailsAboutAllAthletes.getAboutCompetitors().get(Integer.parseInt(id));
					details.name = m.group(1);
					details.gender = m.group(2);
					details.age = m.group(3);
					details.height = m.group(4);
					details.weight = m.group(5);
				}
				//if (--cnt == 0) break;
			}
		}
	}
	
	public void read(String fileName, String fileName2, int y) {
		
		try {
			// for events.txt file
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			Stream<String> ss = br.lines();
			
			
			boolean foundedYear = false;
				
			ss.forEach(line -> {
				Reader.readEvents(line, y, foundedYear);
			});
			
			// for athletes.txt file
			br = new BufferedReader(new FileReader(fileName2));
			ss = br.lines();
			
			ss.forEach(line -> {
				Reader.readDetailsAboutCompetitors(line);
			});
			
				
				
			} catch (FileNotFoundException e) {
				System.out.println("File not found");
				return;
			}
		
		
		
	}
	
	// SYTM - SportYearTypeMedal
	private native String[] filterFuncNumberOfContestants(Filter f, String[][][] SYTM);
	
	
	private void toMakeSYTM(Competitor c, int year) {
		String allInOne = "";
		// first adding all sports
		for (Event discipline: c.getDisciplines()) {
			allInOne += discipline.getSport() + "!";
		}
		// separator
		allInOne += "~";
		// then add year
		allInOne += year;
		// separator
		allInOne += "~";
		// add inidividual or team - as string it will be true for inidividual and false for team
		allInOne += c.getIndividual();
		// separator
		allInOne += "~";
		// add medals
		if (c.getMedals().get(Medal.Type.GOLD).num != 0) {
			allInOne += "GOLD" + c.getMedals().get(Medal.Type.GOLD).num ;
		}
		if (c.getMedals().get(Medal.Type.SILVER).num != 0) {
			allInOne += " ";
			allInOne += "SILVER" + c.getMedals().get(Medal.Type.SILVER).num;
		}
		if (c.getMedals().get(Medal.Type.BRONZE).num != 0) {
			allInOne += " ";
			allInOne += "BRONZE" + c.getMedals().get(Medal.Type.BRONZE).num;
		}
		if (c.getMedals().get(Medal.Type.NOMEDAL).num != 0) {
			allInOne += " ";
			allInOne += "NOMEDAL";
		}
		
		
		// now add allInOne on the position
		SYTM[ind0][ind1][ind2++] = allInOne;
		
	}
	public String[] NumberOfContestants() {
	//	System.out.println("Krecem");
		SYTM = new String[allGames.size()][][];
		// pairs from allGames
		for (Map.Entry<KeyForOlympicGame, OlympicGame> oGPair : allGames.entrySet()) {
			//pairs from countries
			SYTM[ind0] = new String[oGPair.getValue().countries.size()][];
			for (Map.Entry<String, Country> CountryPair : oGPair.getValue().countries.entrySet()) {
				HashMap<String, Competitor> competitors = CountryPair.getValue().getCompetitors();
				// we will have as many elements as countries
				// + 1 because on the first we will place country
				SYTM[ind0][ind1] = new String[competitors.size() + 1];
				// first element will be country
				SYTM[ind0][ind1][0] = CountryPair.getKey();
				// for every competitor from that country
				ind2 = 1;
				competitors.values().forEach(competitor -> {
					toMakeSYTM(competitor, oGPair.getKey().year);
					//System.out.println(competitor);
				});
				//System.out.println("SEPARATOR");
				ind1++;
			}
			ind0++;
			ind1 = 0;
		}
		// return to zero for another iteration
		ind0 = 0;
		//System.out.println(SYTM.length);
		//System.out.println(SYTM[0].length);
		// now we made SYTM, and can filter 
		String[] filtered = filterFuncNumberOfContestants(filter, SYTM);
		
		// now we sort it
		Arrays.sort(filtered, (String a, String b) -> {
			Pattern p = Pattern.compile("([^!]+)!([0-9]+)");
			Matcher m = p.matcher(a);
			int a1 = 0;
			if (m.matches())
				a1 = Integer.parseInt(m.group(2));
			
			m = p.matcher(b);
			int b1 = 0;
			if (m.matches())
				b1 = Integer.parseInt(m.group(2));
			return Integer.signum(b1 - a1);
		});
		return filtered;
	}
	public void setFilter(String bySp, String byYe, String byInd, String byMed) {
		filter.bySport = bySp;
		filter.byYear = byYe;
		filter.byIndividual = byInd;
		filter.byMedal = byMed;
	}
	
	private native String[] filterForDisciplines(String[] numberOfDisciplinesPerYear, int yearFrom, int yearTo);
	private native String[] filterForHeightOrWeight(String[][] competitorsForAllGames, int yearFrom, int yearTo);
	
	
	public String[] numberOfDisciplinesPerYears(int yearFrom, int yearTo) {
		int ind = 0;
		String[] numberOfDisciplinesPerYear = new String[allGames.size()];
		for (Map.Entry<KeyForOlympicGame, OlympicGame> oGPair : allGames.entrySet()) {
			String allInOne = "";
			//year
			allInOne += oGPair.getKey().year;
			// separator
			allInOne += "~";
			// season
			allInOne += oGPair.getKey().season;
			// separator
			allInOne += "~";
			// number of disciplines
			int sum = 0;
			for (Sport sport : oGPair.getValue().sports.values()) {
				sum += sport.getDisciplines().size();
			}
			allInOne += sum;
			numberOfDisciplinesPerYear[ind++] = allInOne;
		}
		
		String[] filtered = filterForDisciplines(numberOfDisciplinesPerYear, yearFrom, yearTo);
		// sort so we can use first value as max Y-axes value
		Arrays.sort(filtered, (String a, String b) -> {
			Pattern p = Pattern.compile("([^~]+)~([^~]+)~(.+)");
			Matcher m = p.matcher(a);
			int a1 = 0;
			if (m.matches())
				a1 = Integer.parseInt(m.group(3));
			
			m = p.matcher(b);
			int b1 = 0;
			if (m.matches())
				b1 = Integer.parseInt(m.group(3));
			return Integer.signum(b1 - a1);
		});
		return filtered;
	}
	
	public String[] averageHeightPerYears(int yearFrom, int yearTo) {
		String[][] competitorsPerYear = new String[allGames.size()][];
		for (Map.Entry<KeyForOlympicGame, OlympicGame> oGPair : allGames.entrySet()) {
			// tmp arrayList
			List<String> tmp = new ArrayList<>();
			// first element will be Year~Season
			tmp.add(oGPair.getKey().year + "~" + oGPair.getKey().season);
			// passing all heights of competitors for that olympic games
			for (Map.Entry<String, Competitor> competitor : oGPair.getValue().games.getCompetitors().entrySet()) {
				if (competitor.getValue().getIndividual() == false) {
					Team team = (Team)competitor.getValue();
					for (Athlete a : team.getTeam()) {
						if (DetailsAboutAthlete.getInstance().getDetailsAboutCompetitor(Integer.parseInt(a.getId())).height.equals("NA"))
							continue;
						tmp.add(DetailsAboutAthlete.getInstance().getDetailsAboutCompetitor(Integer.parseInt(a.getId())).height);
					}
				}
				else {
					if (DetailsAboutAthlete.getInstance().getDetailsAboutCompetitor(Integer.parseInt(competitor.getKey())).height.equals("NA"))
						continue;
					tmp.add(DetailsAboutAthlete.getInstance().getDetailsAboutCompetitor(Integer.parseInt(competitor.getKey())).height);
				}
			}
			
			// cast List to Array
			String[] heights = new String[tmp.size()];
			int ind = 0;
			for (String s: tmp) {
				heights[ind++] = s;
			}
			competitorsPerYear[ind0++] = heights;
			
		}
		ind0 = 0;
		String[] filtered = filterForHeightOrWeight(competitorsPerYear, yearFrom, yearTo);
		// sort so we can use first value as max Y-axes value
		Arrays.sort(filtered, (String a, String b) -> {
			Pattern p = Pattern.compile("([^~]+)~([^~]+)~(.+)");
			Matcher m = p.matcher(a);
			double a1 = 0;
			if (m.matches())
				a1 = Double.parseDouble(m.group(3));
			
			m = p.matcher(b);
			double b1 = 0;
			if (m.matches())
				b1 = Double.parseDouble(m.group(3));
			return (b1-a1 < 0? -1: 1);
		});
		return filtered;
	}
	
	public String[] averageWeightPerYears(int yearFrom, int yearTo) {
		String[][] competitorsPerYear = new String[allGames.size()][];
		for (Map.Entry<KeyForOlympicGame, OlympicGame> oGPair : allGames.entrySet()) {
			// tmp arrayList
			List<String> tmp = new ArrayList<>();
			// first element will be Year~Season
			tmp.add(oGPair.getKey().year + "~" + oGPair.getKey().season);
			// passing all weights of competitors for that olympic games
			for (Map.Entry<String, Competitor> competitor : oGPair.getValue().games.getCompetitors().entrySet()) {
				if (competitor.getValue().getIndividual() == false) {
					Team team = (Team)competitor.getValue();
					for (Athlete a : team.getTeam()) {
						if (DetailsAboutAthlete.getInstance().getDetailsAboutCompetitor(Integer.parseInt(a.getId())).weight.equals("NA"))
							continue;
						tmp.add(DetailsAboutAthlete.getInstance().getDetailsAboutCompetitor(Integer.parseInt(a.getId())).weight);
					}
				}
				else {
					if (DetailsAboutAthlete.getInstance().getDetailsAboutCompetitor(Integer.parseInt(competitor.getKey())).weight.equals("NA"))
						continue;
					tmp.add(DetailsAboutAthlete.getInstance().getDetailsAboutCompetitor(Integer.parseInt(competitor.getKey())).weight);
				}
			}
			
			// cast List to Array
			String[] weights = new String[tmp.size()];
			int ind = 0;
			for (String s: tmp) {
				weights[ind++] = s;
			}
			competitorsPerYear[ind0++] = weights;
			
		}
		ind0 = 0;
		
		String[] filtered = filterForHeightOrWeight(competitorsPerYear, yearFrom, yearTo);
		// sort so we can use first value as max Y-axes value
		Arrays.sort(filtered, (String a, String b) -> {
			Pattern p = Pattern.compile("([^~]+)~([^~]+)~(.+)");
			Matcher m = p.matcher(a);
			double a1 = 0;
			if (m.matches())
				a1 = Double.parseDouble(m.group(3));
			
			m = p.matcher(b);
			double b1 = 0;
			if (m.matches())
				b1 = Double.parseDouble(m.group(3));
			return (b1-a1 < 0? -1: 1);
		});
		return filtered;
	}
	
	
	public static void main(String[] args) {
		
		System.loadLibrary("JNI_Projekat2POOP");
		
		Reader r = new Reader();
		r.read("events.txt", "athletes.txt", -1);
		//System.out.println(r.allGames.size());
		
		//filter = new Filter("", "2016", "", "");
		//r.NumberOfContestants();
		//System.out.println(r.numberOfContestants(null));
		//System.out.println(r.brojac);
		int br = 0;
		
			OlympicGame co = r.allGames.get(new KeyForOlympicGame(2016, "Summer"));
			for (Country c : co.countries.values()) {
				if (c.getName().equals("United States")) {
					for (Competitor comp: c.getCompetitors().values()) {
						if (comp.getMedals().get(Medal.Type.GOLD).num != 0) {
							br += comp.getMedals().get(Medal.Type.GOLD).num;
						}
					}
				}
			}
		System.out.println(br);
		
		//System.out.println(DetailsAboutAthlete.getInstance().getAboutCompetitors().get(135481).age);
	}
}
