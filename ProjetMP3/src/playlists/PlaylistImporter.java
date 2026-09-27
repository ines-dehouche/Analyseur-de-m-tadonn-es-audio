package playlists;

import java.util.List;

/**
 *Interface qui definit la methode pour importer une playlist.
 *Toutes les classes qui implementent cette interface doivent fournir une implementation de la methode "importer" qui retourne la liste des fichiers MP3.
 */
public interface PlaylistImporter {
	
	/**
     *methode pour importer une playlist depuis un fichier.
     */
	List<String> importer();
	
}
