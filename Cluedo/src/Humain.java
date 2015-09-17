import java.util.NoSuchElementException;

public class Humain extends Joueur
{
	public Humain(String nom, String adresse)
	{
		super(nom, adresse);
	}

	@Override
	public String[] jouerCoup()
	{
		String[] cmdComplete = null;
		String cmd = "";
		//Scanner sc = new Scanner(System.in);
		System.out.println("'help' pour plus d'informations sur les commandes disponibles");
		
		do
		{
			System.out.print(nom + " > ");
			
			try
			{
				cmd = Cluedo.sc.nextLine();
			}
			catch(NoSuchElementException e)
			{
				e.printStackTrace();
			}
			catch(IllegalStateException e)
			{
				e.printStackTrace();
			}
			
			cmdComplete = cmd.split(" ");
			cmd = cmdComplete[0];
			
			if(cmd.equals("show") && cmdComplete.length == 1)
			{
				afficherIndices();
			}
			else if(cmd.equals("help") && cmdComplete.length == 1)
			{
				afficherAide();
			}
			else if(cmd.equals("move") && cmdComplete.length == 5)
			{
				//Tester si les cartes pass�es sont correctes
				Carte[] ordreTypeCarte = testerCartes(new String[]{cmdComplete[2],cmdComplete[3],cmdComplete[4]});
				if(ordreTypeCarte == null)
				{
					System.out.println("\nMauvaises cartes. Une carte lieu, une carte arme et une carte suspect sont requises en parametres.");
				}
				else
				{
					if(cmdComplete[1].equals("suggest") || cmdComplete[1].equals("accuse"))
					{
						String[] tmp = new String[4];
						// tmp [0]option [1]arme [2]lieu [3]suspect
						tmp[0] = cmdComplete[1];
						if(ordreTypeCarte[0] instanceof Arme)
						{
							tmp[1] = cmdComplete[2];
							if(ordreTypeCarte[1] instanceof Lieu)
							{
								tmp[2] = cmdComplete[3];
								tmp[3] = cmdComplete[4];
							}
							else if(ordreTypeCarte[1] instanceof Suspect)
							{
								tmp[3] = cmdComplete[3];
								tmp[2] = cmdComplete[4];
							}
						}
						else if(ordreTypeCarte[0] instanceof Lieu)
						{
							tmp[2] = cmdComplete[2];
							if(ordreTypeCarte[1] instanceof Suspect)
							{
								tmp[3] = cmdComplete[3];
								tmp[1] = cmdComplete[4];
							}
							else if(ordreTypeCarte[1] instanceof Arme)
							{
								tmp[1] = cmdComplete[3];
								tmp[3] = cmdComplete[4];
							}
							
						}
						else if(ordreTypeCarte[0] instanceof Suspect)
						{
							tmp[3] = cmdComplete[2];
							if(ordreTypeCarte[1] instanceof Lieu)
							{
								tmp[2] = cmdComplete[3];
								tmp[1] = cmdComplete[4];
							}
							else if(ordreTypeCarte[1] instanceof Arme)
							{
								tmp[1] = cmdComplete[3];
								tmp[2] = cmdComplete[4];
							}
						}
						//sc.close();
						return tmp;
					}
					else
					{
						System.out.println("\nMauvaise option, 'suggest' ou 'accuse'");
					}	
				}	
			}
			else
			{
				System.out.println("\nMauvaise commande");
			}
		}while(!cmd.equals("exit") || cmdComplete.length != 1);
		//sc.close();
		return null;
		
	}

	@Override
	public boolean refuter(String[] cartes, String nomJoueur)
	{

		String cmd = "";
		String[] cmdComplete;
		Boolean carteCorrecte = false;
		System.out.println(nomJoueur + " sugg�re : " + cartes[0] + " " + cartes[1] + " " + cartes[2]);
		System.out.println("'help' pour plus d'informations sur les commandes disponibles");
		//dans les contains, passer un string est impossible pour une collection de carte. Donc soit faire une boucle pour tester chaque carte avec le nom et le string soit trouver un autre moyen
		if(cartesJoueur.contains(cartes[0]) || cartesJoueur.contains(cartes[1]) || cartesJoueur.contains(cartes[2]))
		{
			for(Carte c : cartesJoueur)
			{
				System.out.println(c.getNom());
			}
			System.out.println("\nVous pouvez refuter la proposition. Quelle carte choisissez vous de montrer ? (show <card>)");
			do
			{
				cmd = Cluedo.sc.nextLine();
				cmdComplete = cmd.split(" ");
				cmd = cmdComplete[0];
				if(cmd.equals("help") && cmdComplete.length == 1)
				{
					afficherAideRefuter();
				}
				else if(cmdComplete.length > 1)
				{
					carteCorrecte = (cmdComplete[1].equals(cartes[0]) || cmdComplete[1].equals(cartes[1]) || cmdComplete[1].equals(cartes[2]));
				}
				//la m�me pour le contains en dessous
			}while(!cmd.equals("show") || cmdComplete.length != 2 || !cartesJoueur.contains(cmdComplete[1]) || !carteCorrecte);
			return true;
		}
		else
		{
			System.out.println("Vous n'avez aucune carte pour r�futer la proposition. (skip pour passer)");
			System.out.print(nom + " > ");
			while(!cmd.equals("skip"))
			{
				cmd = Cluedo.sc.nextLine();
				if(cmd.equals("help"))
				{
					afficherAideRefuter();
				}
			}
			return false;
		}
	}
	
	private void afficherAide()
	{
		System.out.println("\nLes commandes disponibles durant la partie :\n");
		System.out.println("show");
		System.out.println("\t Voir les informations vous concernant (Indices et accusations faites).\n");
		System.out.println("move <type> <card1> <card2> <card3>");
		System.out.println("\t <type> : Sois 'suggest' ou 'accuse'.");
		System.out.println("\t <cardN> : une carte.\n");
		System.out.println("exit");
		System.out.println("\t Quitter la partie.\n");
		System.out.println("help");
		System.out.println("\t Afficher ce message.\n");
	}
	
	private void afficherAideRefuter()
	{
		System.out.println("Les commandes disponibles :\n");
		System.out.println("show <card>");
		System.out.println("\t la carte que vous souhaiter montrer si vous les pouvez \n");
		System.out.println("skip");
		System.out.println("\t permet de passer si vous n'avez aucune carte pour r�futer \n");
		System.out.println("help");
		System.out.println("\t affiche ce message \n");
	}
	
	private void afficherIndices()
	{
		System.out.println("\n\t Indices : ");
		for(Carte c : cartesJoueur)
		{
			System.out.println(c.getNom());
		}
		if(encoreEnJeu)
		{
			System.out.println("\n Vous n'avez pas encore fait d'accusation.\n");
		}
		else
		{
			System.out.println("\n Vous avez fait une accusation fausse.\n");
		}
	}
	
	private Carte[] testerCartes(String[] tabCarte)
	{
		//Tester si les cartes pass�es sont correctes
		//si elles sont correctes, renvoie un tableau de carte
		//sinon renvoie null;
		
		Carte[] cartes = null;
		Carte a = null;
		Carte b = null;
		Carte c = null;
		
		//on test la premi�re carte
		for(Armes arme : Armes.values())
		{
			if(arme.toString().equals(tabCarte[0]))
			{
				a = new Arme(arme.toString(), arme.getImage());
			}
		}
		if(a == null)
		{
			for(Lieux lieu : Lieux.values())
			{
				if(lieu.toString().equals(tabCarte[0]))
				{
					a = new Lieu(lieu.toString(), lieu.getImage());
				}
			}
		}
		if(a == null)
		{
			for(Suspects suspect : Suspects.values())
			{
				if(suspect.toString().equals(tabCarte[0]))
				{
					a = new Suspect(suspect.toString(), suspect.getImage());
				}
			}
		}
		
		//on test la 2�me carte (plus de conditions..)
		if(a != null)
		{
			// si la premi�re carte est une arme
			if(a instanceof Arme)
			{
				for(Lieux lieu : Lieux.values())
				{
					if(lieu.toString().equals(tabCarte[1]))
					{
						b = new Lieu(lieu.toString(), lieu.getImage());
					}
				}
				if(b == null)
				{
					for(Suspects suspect : Suspects.values())
					{
						if(suspect.toString().equals(tabCarte[1]))
						{
							b = new Suspect(suspect.toString(), suspect.getImage());
						}
					}
				}
			}
			// si la premi�re carte est un lieu
			if(a instanceof Lieu)
			{
				for(Armes arme : Armes.values())
				{
					if(arme.toString().equals(tabCarte[1]))
					{
						b = new Arme(arme.toString(), arme.getImage());
					}
				}
				if(b == null)
				{
					for(Suspects suspect : Suspects.values())
					{
						if(suspect.toString().equals(tabCarte[1]))
						{
							b = new Suspect(suspect.toString(), suspect.getImage());
						}
					}
				}
			}
			// si la premi�re carte est un suspect
			if(a instanceof Suspect)
			{
				for(Armes arme : Armes.values())
				{
					if(arme.toString().equals(tabCarte[1]))
					{
						b = new Arme(arme.toString(), arme.getImage());
					}
				}
				if(b == null)
				{
					for(Lieux lieu : Lieux.values())
					{
						if(lieu.toString().equals(tabCarte[1]))
						{
							b = new Lieu(lieu.toString(), lieu.getImage());
						}
					}
				}
			}
		}
		// on test la 3�me carte (encore plus de test...)
		if(b != null)
		{
			//si la premir�e carte est une arme
			if(a instanceof Arme)
			{
				//si la deuxi�me carte est un lieu
				if(b instanceof Lieu)
				{
					for(Suspects suspect : Suspects.values())
					{
						if(suspect.toString().equals(tabCarte[2]))
						{
							c = new Suspect(suspect.toString(), suspect.getImage());
						}
					}
				}
				//si la deuxi�me carte est un suspect
				else
				{
					for(Lieux lieu : Lieux.values())
					{
						if(lieu.toString().equals(tabCarte[2]))
						{
							c = new Lieu(lieu.toString(), lieu.getImage());
						}
					}
				}
			}
			//si la premi�re carte est un lieu
			if(a instanceof Lieu)
			{
				//si la deuxi�me carte est une arme
				if(b instanceof Arme)
				{
					for(Suspects suspect : Suspects.values())
					{
						if(suspect.toString().equals(tabCarte[2]))
						{
							c = new Suspect(suspect.toString(), suspect.getImage());
						}
					}
				}
				//si la deuxi�me carte est un suspect
				else
				{
					for(Armes arme : Armes.values())
					{
						if(arme.toString().equals(tabCarte[2]))
						{
							c = new Arme(arme.toString(), arme.getImage());
						}
					}
				}
			}
			//si la premi�re carte est un suspect
			if(a instanceof Suspect)
			{
				//si la deuxi�me carte est une arme
				if(b instanceof Arme)
				{
					for(Lieux lieu : Lieux.values())
					{
						if(lieu.toString().equals(tabCarte[2]))
						{
							c = new Lieu(lieu.toString(), lieu.getImage());
						}
					}
				}
				//si la deuxi�me carte est un lieu
				else
				{
					for(Armes arme : Armes.values())
					{
						if(arme.toString().equals(tabCarte[2]))
						{
							c = new Arme(arme.toString(), arme.getImage());
						}
					}
				}
			}
		}
		if(c != null)
		{
			cartes = new Carte[]{a,b,c};
		}
		return cartes;
	}

}