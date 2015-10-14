package principal;
import java.io.IOException;
import java.net.SocketException;
import java.util.List;


import networking.RegServer;

public class PartieServeur extends PartieHote
{
	private RegServer server;
		
	public PartieServeur(List<Joueur> joueurs) throws IOException
	{
		super(joueurs);
		server = new RegServer(12345,joueurs.size(),180000);
	}

	
	public void boucleJeu()
	{
		String[] message = null;
		int i = 0;
		int k = 0;
		
		// ouvre le serveur
		try 
		{
			server.open();
		} 
		catch(SocketException e)
		{
			System.out.println("Erreur TCP � l'ouverture du serveur.");
			return;
		}
		catch (IOException e1) 
		{
			System.out.println("Erreur, le serveur n'as pas pu �tre ouvert.");
			return;
		}
					
		try
		{
			
			if(server.getNumClients() == 0)
			{
				System.out.println("Aucun joueurs trouv�s !\n");
				server.close();
				return;
			}
			// donne le bon nom au joueur (au lieu de Joueur 1, Joueur 2, Joueur 3 ...) en fonction du nom dans les ComServer du server
			while(i < joueursPartie.size())
			{
				joueursPartie.get(i).setNom(server.getClients().get(i).getNom());
				i++;
			}
			i = 0;
						
			//envoye le message de d�but de partie en indiquant les cartes que poss�dent chaque joueur
			// msgStar =  "start joueur1,joueur2,...,joueurN carte1,carte2,carte3"
			while(i < joueursPartie.size())
			{
				String msgStart = "start";
				// ajoute le nom des joueurs
				while(k < joueursPartie.size())
				{
					if(k == 0)
					{
						msgStart += " "+joueursPartie.get(k).getNom();
					}
					else
					{
						msgStart += ","+joueursPartie.get(k).getNom();
					}
					k++;
				}
				k = 0;
				// ajoute le nom des cartes
				while(k < joueursPartie.get(i).getCartesJoueur().size())
				{
					if(k == 0)
					{
						msgStart += " "+joueursPartie.get(i).getCartesJoueur().get(k).getNom();
					}
					else
					{
						msgStart += ","+joueursPartie.get(i).getCartesJoueur().get(k).getNom();
					}
					k++;
				}
				k = 0;
				server.send(i, msgStart);
				i++;
			}
			System.out.println("\nLa partie commence !");
			i = 0;
			
			// =====================
			// === boucle de jeu ===
			// =====================
			while(!partieFinie)
			{
				//on v�rifie que le joueur actuel peut jouer sinon on passe au joueur suivant et ainsi de suite.
				while(!joueursPartie.get(joueurActuel).getEncoreEnJeu())
				{
					joueurActuel++;
					if(joueurActuel == joueursPartie.size())
					{
						joueurActuel = 0;
					}
				}
				do
				{
					server.send(joueurActuel, "play");
					System.out.println("\nLe joueur '"+joueursPartie.get(joueurActuel).getNom()+"' joue.");
					message = server.receive(joueurActuel).split(" ");
					
					if(message[0].equals("exit"))
					{
						for(i = 0; i < server.getNumClients(); i++)
						{
							server.send(i, "error exit "+joueurActuel);
						}
						System.out.println("Le joueur '"+joueursPartie.get(joueurActuel).getNom()+"' a quitt� la partie.");
					}
					else if(message[0].equals("move") && message.length == 5)
					{
						String[] cartes = {message[2],message[3],message[4]};
						// Met dans l'ordre les cartes
						Carte[] ordre = Carte.testerCartes(cartes);
						String[] cartesSuggerer = null;
						
						if(ordre != null)
						{
							cartesSuggerer = new String[]{ordre[0].getNom(), ordre[1].getNom(), ordre[2].getNom()};
						}
						// V�rifie sur le coup est valide
						if((message[1].equals("suggest") || message[1].equals("accuse")) && cartesSuggerer != null)
						{
							if(message[1].equals("suggest"))
							{
								for(i = 0; i < server.getNumClients(); i++)
								{
									server.send(i, "move "+joueurActuel+" suggest "+cartesSuggerer[0]+" "+cartesSuggerer[1]+" "+cartesSuggerer[2]);
								}
								System.out.println("Le joueur '"+joueursPartie.get(joueurActuel).getNom()+"' sugg�re '"+cartesSuggerer[0]+"' '"+cartesSuggerer[1]+"' '"+cartesSuggerer[2]+"'.");
								
								List<String> carteCommun;
								String[] carteMontre;
								i = joueurActuel;
								
								do
								{
									if(i + 1 >= joueursPartie.size())
									{
										i = 0;
									}
									else
									{
										i++;
									}
									
									if(i == joueurActuel)	
									{
										break;
									}
									
									Joueur j = joueursPartie.get(i);
									carteCommun = Carte.cartesContenuDans(j.getCartesJoueur(), cartesSuggerer);
									
									if(carteCommun.size() != 0)
									{
										do
										{
										server.send(i, "ask "+cartesSuggerer[0]+" "+cartesSuggerer[1]+" "+cartesSuggerer[2]);
										System.out.println("Le joueur '"+joueursPartie.get(i).getNom()+"' doit r�pondre � la suggestion du joueur '"+joueursPartie.get(joueurActuel).getNom()+"'.");
										carteMontre = server.receive(i).split(" ");
										} while(!carteMontre[0].equals("exit") && !(carteMontre[0].equals("respond") && carteMontre.length == 2 && Carte.contientCarte(j.getCartesJoueur(), carteMontre[1])));
									
										if(carteMontre.equals("exit"))
										{
											for(k = 0; k < server.getNumClients(); k++)
											{
												server.send(k, "error exit "+i);
											}
											System.out.println("Le joueur '"+joueursPartie.get(i).getNom()+"' a quitt� la partie.");
											server.close();
											return;
										}
										else
										{
											// Informe le joueur 'joueurActuel' de la r�ponse du joueur 'i'
											server.send(joueurActuel, "info respond "+i+" "+carteMontre[1]);
											for(k = 0; k < server.getNumClients(); k++)
											{
												server.send(k, "info show "+i+" "+joueurActuel);
											}
											System.out.println("Le joueur '"+joueursPartie.get(i).getNom()+"' montre la carte '"+carteMontre[1]+"' au joueur '"+joueursPartie.get(joueurActuel).getNom()+"'.");
										}
										break;
									}
									else
									{
										for(k = 0; k < server.getNumClients(); k++)
										{
											server.send(k, "info noshow "+i+" "+joueurActuel);
										}
										System.out.println("Le joueur '"+joueursPartie.get(i).getNom()+" ne peut montrer aucune carte au joueur '"+joueursPartie.get(joueurActuel).getNom()+"'.");
									}
								}
								while(true);
								break;
							}
							else if(message[1].equals("accuse"))
							{
								for(i = 0; i < server.getNumClients(); i++)
								{
									server.send(i, "move "+joueurActuel+" accuse "+cartesSuggerer[0]+" "+cartesSuggerer[1]+" "+cartesSuggerer[2]);
								}
								System.out.println("Le joueur '"+joueursPartie.get(joueurActuel).getNom()+"' accuse '"+cartesSuggerer[0]+"' '"+cartesSuggerer[1]+"' '"+cartesSuggerer[2]+"'.");
								// Si l'accusation est bonne
								if(cartesSuggerer[0].equals(cartesADecouvrir[0].getNom()) && cartesSuggerer[1].equals(cartesADecouvrir[1].getNom()) && cartesSuggerer[2].equals(cartesADecouvrir[2].getNom()))
								{
									// Informe � tout les joueurs que le joueur 'joueurActuel' a gagn� la partie
									for(i = 0; i < server.getNumClients(); i++)
									{
										server.send(i, "end "+joueurActuel);
									}
									System.out.println("Le joueur '"+joueursPartie.get(joueurActuel).getNom()+"' a gagn� la partie.");
									partieFinie = true;
								}
								// Si l'accusation est fausse
								else
								{
									joueursPartie.get(joueurActuel).setEncoreEnJeu(false);
									// Informe tout les joueurs que le joueur 'joueurActuel' � perdu
									for(i = 0; i < server.getNumClients(); i++)
									{
										server.send(i, "info wrong "+joueurActuel);
									}
								
									// s'il n'y a plus de joueurs en jeu
									for(Joueur j : joueursPartie)
									{
										partieFinie = partieFinie || j.getEncoreEnJeu();
									}
									partieFinie = !partieFinie;
								}
								break;
							}
						}
						else
						{
							server.send(joueurActuel, "error invalid Mauvaise commande");
							System.out.println("Erreur commande.");
						}
					}
					else
					{
						server.send(joueurActuel, "error invalid Mauvaise commande");
						System.out.println("Erreur commande.");
					}
				} while(!message[0].equals("exit"));
				if(message[0].equals("exit"))
				{
					break;
				}
				joueurActuel++;
				if(joueurActuel == joueursPartie.size())
				{
					joueurActuel = 0;
				}
			}
			for(i = 0; i < server.getNumClients(); i++)
			{
				server.send(i, "end");
			}
			System.out.println("Partie termin�e.\n");
			server.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
}
