package test;

public class MonIA1 implements InterfaceIA{

	private InterfacePlateau plateau;
	
	@Override
	public void setPlateau(InterfacePlateau plateau) {
		this.plateau = plateau;
	}

	@Override
	public String getCoup() {
		if(plateau.getPlateau()[5][3] == 2)
			return "Coup: (5,2)->(6,3)";
		else
			return "Coup: (4,3)->(5,4)"; 
	}

}
