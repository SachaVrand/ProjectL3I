<?php
	function chargerClasse($classe)
	{
		require $classe . '.php';
	}
	spl_autoload_register('chargerClasse');
	session_start();
	
	if(!isset($_SESSION['Connexion']))
	{
		$db = new ConnexionBase();
		$_SESSION['Connexion'] = $db;
	}
	
	if(isset($_POST['loginConnexion']) && isset($_POST['passwordConnexion']))
	{
		$user = Utilisateur::getUser($_SESSION['Connexion'], $_POST['loginConnexion'], $_POST['passwordConnexion']);
		if($user == null)
		{
			$erreur = 'Mot de passe ou identifiant incorrect';
		}
		else if(Utilisateur::isEmailBanned($_SESSION['Connexion'], $user->mail))
		{
			$erreur = 'Vous avez été banni';
		}
		else
		{
			$_SESSION['user'] = $user;
			if($user->getPermission() == 1)
			{
				header("Location: creerEvenementAdmin.php");
			}
			else
			{
				header("Location: fluxActiviteFollowing.php");
			}
			exit();
		}
	}
	
	if(isset($_POST['pseudo']))
	{
		$containsAt = false;
		for($i = 0; $i < strlen($_POST['pseudo']); $i++)
		{
			if($_POST['pseudo'][$i] === '@')
			{
				$containsAt = true;
			}
		}
		if($containsAt)
		{
			$erreurMdp = "Le pseudo ne doit pas contenir de '@'";
		}
		else
		{
			if($_POST['password'] === $_POST['cpassword'])
			{
				$photo = 'images/userIcon.png';
				$motDePasse = $_POST['password'];
				if(!empty($_POST['photo']))
				{
					$photo = $_POST['photo'];
				}
				$user = new Utilisateur(Utilisateur::getNewId($_SESSION['Connexion']),$_POST['pseudo'], $_POST['nom'], $_POST['prenom'], $_POST['ville'], $_POST['mail'],0, $_POST['annee'].'-'.$_POST['mois'].'-'.$_POST['jour'], $photo,$_POST['confidentialite']);
				$res = Utilisateur::addUserToDataBase($_SESSION['Connexion'], $user, $motDePasse);
				if(!$res)
				{
					$existingLoginMail = 'Il existe deja un utilisateur avec le même login ou adresse mail';
				}
				else 
				{
					$notif = 'Votre inscription a bien été pris en compte.';
				}
			}
			else
			{
				$erreurMdp = 'Les deux mots de passe ne correspondent pas';
			}
		}
	}
?>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" lang="fr">
	<head>
		<meta name="author" content="page de connexion">
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<link rel="Stylesheet" type="text/css" href="pageConnexion.css" />
		<title>
		</title>
	</head>
	<body>
		<header class="headerConnexion">
			<!-- Connexion -->
			<div class="divCo">
				<form action="pageConnexion.php" method="post">
					<ul>
						<li><label class="label">Login : </label></li>
						<li><input type="text" name="loginConnexion" required="required" class="text"></li>
						<li>
						<?php
							if(isset($erreur))
							{
								echo "$erreur";
							}
						?>
						</li>
					</ul>
					<ul>
						<li><label class="label">Mot de passe : </label></li>
						<li><input type="password" name="passwordConnexion" required="required" class="text"></li>
						<li><a href="formLostPassword.php">Mot de passe oublié ?</a></li>
					</ul>
					<ul>
						<li><label class="label" style="visibility: hidden">Hmmmmm</label> </li>
						<li><input type="submit" name="Connexion" value="Connexion"></li>
					</ul>
				</form>
			</div>
		</header>
		<div class="sectionInscription">
			<form method="post" action="pageConnexion.php">
				<?php
					if(isset($notif))
					{
						echo "<tr><td colspan='2'><h4 style='color:green;font-family: helvetica;'>$notif</h4></td></tr>";
					}
					else if(isset($erreurMdp))
					{
						echo "<tr><td colspan='2'><h4 style='color:red;font-family: helvetica;'>$erreurMdp</h4></td></tr>";
					}
					else if(isset($existingLoginMail))
					{
						echo "<tr><td colspan='2'><h4 style='color:red;font-family: helvetica;'>$existingLoginMail</h4></td></tr>";
					}
				?>
				<table>
					<tr>
						<th colspan="2" align="left">Inscription</th>
					</tr>
					<tr>
						<td colspan="2" class="tdInput"><input type="text" name="pseudo" required="required" placeholder="Pseudonyme" class="inputFull"></td>
					</tr>
					<tr>
						<td colspan="2" class="tdInput"><input type="password" name="password" title="password" required="required" placeholder="Mot de passe" class="inputFull"></td>
					</tr>
					<tr>
						<td colspan="2" class="tdInput"><input type="password" name="cpassword" title="confirm password" required="required" placeholder="Entrez a nouveau" class="inputFull"></td>
					</tr>
					<tr>
						<td colspan="1" class="tdInput"><input type="text" name="prenom" required="required" placeholder="Prénom" class="inputFull"></td>
						<td colspan="1" class="tdInput"><input type="text" name="nom" required="required" placeholder="Nom" class="inputFull"></td>
					</tr>
					<tr>
						<td colspan="2" class="tdInput"><input type="text" name="ville" required="required" placeholder="Ville" class="inputFull"></td>
					</tr>
					<tr>
						<td colspan="2" class="tdInput"><input type="email" name="mail" required="required" placeholder="Adresse mail" class="inputFull"></td>
					</tr>
					<tr>
					<th align="left" colspan="2">Profil confidentiel ?</th>
					</tr>
					<tr>
						<td class="tdInput"><input type="radio" name="confidentialite" value="0" id="non" checked="checked" /> <label for="non">Non</label></td>
						<td class="tdInput"><input type="radio" name="confidentialite" value="1" id="oui" /> <label for="oui">Oui</label></td>
					</tr>
					<tr>
						<th align="left" colspan="2">Date de naissance : </th>
					</tr>
					<tr>
						<td colspan="2" class="tdInput">
							<select name="jour">
								<option value="1" selected="selected">1</option>
								<?php
									for($i = 2; $i <= 31; $i++)
									{
										echo "<option value=$i> $i </option>";
									}
								?>
							</select>
							<select name="mois">
								<option value="1" selected="selected">1</option>
								<?php 
									for($i=2;$i<=12;$i++)
									{
										echo "<option value=$i> $i </option>";
									}
								?>
							</select>
							<select name="annee">
								<?php
									$y = date('Y');
									echo "<option value=$y selected=selected> $y </option>";
									for($i=$y-1 ; $i>$y-100 ; $i--)
									{
										echo "<option value=$i> $i </option>";
									}
								?>
							</select>
						</td>
					</tr>
					<tr>
						<td colspan="2" class="tdInput"><input type="text" name="photo" placeholder="URL de la photo de profil (non obligatoire)" class="inputFull"></td>
					</tr>
					<tr>
						<td colspan="2"><input type="submit" name="Inscription" value="Inscription"></td>
					<tr>
				</table>
			</form>
		</div>
	</body>
</html>