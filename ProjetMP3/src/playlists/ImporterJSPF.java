package playlists;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;


/**
 *Classe pour importer les fichiers MP3 depuis une playlist JSPF
 *elle implemente l'interface PlaylistImporter
 *le fonctionnement est similaire a ImporteurXSPF
 */

public class ImporterJSPF implements PlaylistImporter {
	   
		private File fichier;
		
		public ImporterJSPF (File fichier) {
			this.fichier=fichier;
		}
		
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
				System.out.println("Erreur lors de limport JSPF");
			}
			return playlist;
		}
}
