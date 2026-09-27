package playlists;

/**
 * classe abstraite qui ddfinit le comportement d'un exporteur de playlist.
 * Chaque exporteur concret (XSPF, JSPF, M3U8) doit heriter de cette classe
 * et implementer la methode exporter().
 */

public abstract class PlaylistExporter {
    // methode abstraite à implementer par chaque exporteur concret
    // elle permet d'exporter une playlist dans un fichier du format choisi
	
    public abstract boolean exporter(Playlist playlist, String cheminSortie);
    
    // - playlist : la playlist à sauvegarder
    // - cheminSortie : le chemin du fichier où la playlist sera enregistree
    // retourne true si l'export reussit, false sinon
}
