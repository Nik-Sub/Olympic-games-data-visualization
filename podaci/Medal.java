package podaci;

public class Medal {
		
	public enum Type {BRONZE, SILVER, GOLD, NOMEDAL};


	private Type typeOfMedal;

	public Medal(Type ty) {
		typeOfMedal = ty;
	}
	public Medal() {};
	
	public Type getTypeOfMedal() {
		return typeOfMedal;
	}
}
