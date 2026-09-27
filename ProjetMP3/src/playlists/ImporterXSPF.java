package playlists;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


/**
 *Classe pour importer les fichiers MP3 depuis une playlist XSPF.
 *elle implemente l'interface PlaylistImporter.
 */
public class ImporterXSPF implements PlaylistImporter{
   
	private File fichier;
	
	public ImporterXSPF (File fichier) {
		this.fichier=fichier;
	}
	
	
	/**
     *methode qui lit le fichier XSPF ligne par ligne et ajoute chaque ligne se terminant par ".mp3" a la liste de playlist.
     *retourne la liste de fichiers MP3 contenus dans le fichier
     */
	@Override
	public List<String> importer(){
		
		List<String> playlist= new ArrayList<>();
		String ligne;
		
		try(Scanner sc = new Scanner(fichier)) {
			
			while (sc.hasNextLine()) {
				ligne=sc.nextLine().trim();
				if(ligne.endsWith(".mp3")) {
					playlist.add(ligne);
				}
			}
		}
		catch (IOException e) {
			System.out.println("Erreur lors de limport XSPF");
		}
		return playlist;
	}
}
