import java.util.Scanner;

public class Cluedo
{
	public static void main(String[] args)
	{
		String cmd = "";
		Scanner sc = new Scanner(System.in);
		
		
		
		System.out.println("Cluedo 0.1");
		System.out.println("Taper 'help' pour plus d'informations");
		
		while(cmd.equalsIgnoreCase("exit"))
		{
			cmd = sc.next();
			switch(cmd)
			{
			case "solo" :
				System.out.println("yolo");
				System.out.println("Sacha est un noob");
				System.out.println("Sacha est un noob22222");
				System.out.println("Hannnnnnnnnnnnnnnnnnn");
				break;
			case "help" :
				afficherAide();
				break;
			case "exit" :
				break;
			default :
				System.out.println("Mauvaise commande");
			}
		}
		sc.close();
		System.exit(0);
	}
	
	private static void afficherAide()
	{
		System.out.println("solo");
		System.out.println("\t Commencer une partie solo (Humain + Ordinateur)\n");
		System.out.println("hote");
		System.out.println("\t Commencer une partie en tant qu'h�te\n");
		System.out.println("multi");
	}

}
