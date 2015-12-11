<?php
	function chargerClasse($classe)
	{
		require $classe . '.php';
	}
	spl_autoload_register('chargerClasse');
	session_start();
	
	$user = $_SESSION['user'];
	
	if(isset($_POST['hiddenUserSuppr']))
	{
		Utilisateur::deleteFollowing($_SESSION,['Connexion'], $user, $_POST['hiddenUserSuppr']);
	}
	
	if(isset($_POST['hiddenUserRest']))
	{
		Utilisateur::restrictFollower($_SESSION,['Connexion'], $user, $_POST['hiddenUserSuppr']);
	}
?>

<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" lang="fr">
	<head>
		<meta charset="utf-8">
		<meta http-equiv="Content-Language" content="fr" />
	    <link rel="Stylesheet" type="text/css" href="interfaceAvecMenu.css" />
		<title>Informations personnelles</title>
	</head>
	
	<body>
		<?php 
			include_once('menuPrincipal.php');
		?>
	
		<div id="titre">
			Profil personnel
		</div>
		
		<table id="principal">
			<tr>
				<td>
					<div id="sousMenu">
						<ul>
							<li><a href="infosPerso.php">Informations</a></li>
							<li><a href="listeCadeau.php">Listes</a></li>
							<li><a href="follow.php">Follow</a></li>
						</ul>
					</div>
				</td>
				<td>
					<div id="contentAvecMenu">
						<table>
							<tr><th>Following</th></tr>
							<?php 
								$tabFollowing = Utilisateur::getFollowing($_SESSION['Connexion'], $user);
								$nbFollowing = 0;
								foreach ($tabFollowing as $k=>$v)
								{
									$nbFollowing++;
									echo 
									"<tr>
										<td><a href=searchUser.php?user=$v>$v</a></td>
										<form method='post' action='userFollow.php'>
											<input type='hidden' name='hiddenUserSuppr' value=$k>
											<td><input type='submit' name='submitSuppr' value='Supprimer'></td>
										</form>
									</tr>";
								}
								echo"<tr><td>$nbFollowing</td></tr>";
							?>
							<tr><th>Followers</th></tr>
							<?php
								$tabFollowers = Utilisateur::getFollowers($_SESSION['Connexion'], $user);
								$nbFollowers = 0;
								foreach($tabFollowers as $k=>$v)
								{
									$nbFollowers++;
									echo 
									"<tr>
										<td><a href=searchUser.php?user=$v>$v</a></td>
										<form method='post' action='userFollow.php'>
											<input type='hidden' name='hiddenUserRest' value=$k>
											<td><input type='submit' name='submitRest' value='Restreindre acces'></td>
										</form>
									</tr>";
								}
								echo"<tr><td>$nbFollowers</td></tr>";
							?>
						</table>
					</div>
				</td>
			</tr>
		</table>
			
	</body>
</html>