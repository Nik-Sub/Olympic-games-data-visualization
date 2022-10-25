package podaci;

import java.util.HashMap;

public class Sport {
	private HashMap<String, Event> disciplines = new HashMap<>();
	private String name;

	public Sport(String namee) {
		name = namee; 
	}
	public Sport(){
	}
	
	public HashMap<String, Event> getDisciplines(){
		return disciplines;
	}
	
	public void addEvent(Event e) {
		// returns pair of iterator to object with that key and bool that indicates if key already exists (false), or dont (true) 
		disciplines.put(e.getName(), e);
	}
}
