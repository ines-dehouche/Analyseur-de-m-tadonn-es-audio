package playlists;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *Classe pour importer les fichiers MP3 depuis une playlist M3U8.
 *Elle ignore les lignes qui commencent par "#" car ce sont des commentaires
 */

public class ImporterM3U8 implements PlaylistImporter {
	
    private File fichier;  //le fichier M3U8 à lire
	
	public ImporterM3U8 (File fichier) {
		this.fichier=fichier;
	}
	
	@Override
	public List<String> importer(){
		
		List<String> playlist=new ArrayList<>();
		String ligne;
		
		try(Scanner sc=new Scanner(fichier)) {
			
			while (sc.hasNextLine()) {
				ligne=sc.nextLine().trim();
				  // On ignore les commentaires et on ne garde que les fichiers .mp3
				if(!ligne.startsWith("#") && ligne.endsWith(".mp3")) {
					playlist.add(ligne);
				}
			}
		}
		catch (IOException e) {
			System.out.println("Erreur lors de limport M3U8");
		}
		return playlist;
	}
}
