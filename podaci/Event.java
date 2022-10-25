package podaci;

public class Event {
	private String name;
	// if it's false then it's team sport
	private boolean individual;
	private String sport;

	public Event(String namee, boolean individuall, String sp){
		name = namee; 
		individual = individuall; 
		sport = sp;
	}
	
	public String getName() {
		return name;
	}

	public String getSport() {
		return sport;
	}
}
