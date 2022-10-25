package podaci;

public class Athlete extends Competitor {
	public Athlete(String idd, Event e, Medal m) {
		super(idd, e, m);
	}

	@Override
	public boolean getIndividual() {
		return true;
	}

}