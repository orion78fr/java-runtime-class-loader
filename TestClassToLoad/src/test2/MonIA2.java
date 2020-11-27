package test2;

import test.InterfaceIA;
import test.InterfacePlateau;

public class MonIA2 implements InterfaceIA
{
	private UneAutreClasse test;

	@Override
	public void setPlateau(InterfacePlateau plateau)
	{
		// TODO Auto-generated method stub
		try {test = new UneAutreClasse();
		test.test();}
		catch(Exception e){e.printStackTrace();}
		
		new AutreClasse().test();
	}

	@Override
	public String getCoup()
	{
		return "Classe externe loadé avec succès";
	}
	
	private class AutreClasse{
		public void test(){
			System.out.println("Classe interne loadé avec succès");
		}
	}
}
