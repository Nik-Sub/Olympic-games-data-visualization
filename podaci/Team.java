package podaci;

import java.util.ArrayList;
import java.util.List;

public class Team extends Competitor{
	private List<Athlete> team = new ArrayList<>();

	
	public Team(Event e, Medal m){
		super(e, m);
	}
	public void addAthlete(Athlete ath) {
		team.add(ath);
	}
	
	public List<Athlete> getTeam() {
		return team;
	}
	
	@Override
	public boolean getIndividual() {
		return false;
	}
}
