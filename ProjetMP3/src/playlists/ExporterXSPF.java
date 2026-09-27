package playlists;

//importe les classes necessaires pour manipuler les playlists, fichiers MP3, metadonnées et fichiers sur disque

import playlists.Playlist;
import metadonnees.FichierMP3;
import metadonnees.Metadonnee;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * classe qui exporte une playlist au format XSPF (XML Shareable Playlist Format)
 * herite de PlaylistExporter et implemente la methode exporter().
 */

public class ExporterXSPF extends PlaylistExporter {

	// methode qui exporte la playlist dans un fichier XSPF
	
    @Override
    public boolean exporter(Playlist playlist, String cheminSortie) {
    	
    	//verifie si la playlist est vide ou nulle
        if (playlist == null || playlist.getMorceaux() == null || playlist.getMorceaux().isEmpty()) {
            System.err.println("Erreur : Playlist vide ou nulle");
            return false; //retourne false si rien à exporter
        }
        
     //cree un FileWriter pour ecrire dans le fichier de sortie
        try (FileWriter writer = new FileWriter(cheminSortie)) {

            // En-tête XML
            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            writer.write("<playlist version=\"1\" xmlns=\"http://xspf.org/ns/0/\">\n");

            //ecrit le titre de la playlist
            writer.write("  <title>" + echapperXML(playlist.getNom()) + "</title>\n");

            //debut de la liste des pistes
            writer.write("  <trackList>\n");

            for (FichierMP3 mp3 : playlist.getMorceaux()) {  //parcourt tous les morceaux de la playlist

                writer.write("    <track>\n");

               //chemin du fichier au format URI
                String location = new File(mp3.getChemin()).toURI().toString();
                writer.write("      <location>" + echapperXML(location) + "</location>\n");

               //recuperer les métadonnees du morceau
                Metadonnee meta = mp3.getMetadonnee();
                if (meta != null) {
                	
                	//titre du morceau
                    if (meta.getTitre() != null && !meta.getTitre().isEmpty()) {
                        writer.write("      <title>" + echapperXML(meta.getTitre()) + "</title>\n");
                    }
                    
                    //ertiste du morceau
                    if (meta.getArtiste() != null && !meta.getArtiste().isEmpty()) {
                        writer.write("      <creator>" + echapperXML(meta.getArtiste()) + "</creator>\n");
                    }
                    
                    //album du morceau
                    if (meta.getAlbum() != null && !meta.getAlbum().isEmpty()) {
                        writer.write("      <album>" + echapperXML(meta.getAlbum()) + "</album>\n");
                    }
                    
                    //duree du morceau en millisecondes
                    if (meta.getDuree() > 0) {
                        writer.write("      <duration>" + (meta.getDuree() * 1000) + "</duration>\n");
                    }
                }

                writer.write("    </track>\n"); //fin du morceau
            }

            writer.write("  </trackList>\n"); //fin de la liste des morceaux
            writer.write("</playlist>\n"); //fin du fichier XSPF

            System.out.println("Playlist exportée avec succès : " + cheminSortie);
            return true; //export reussi

        } catch (IOException e) { //gestion des erreurs d'ecriture
            System.err.println("Erreur lors de l'export XSPF : " + e.getMessage());
            return false; //retourne false s'il y a une erreur
        }
    }

    //methode pour remplacer les caracteres interdits en XML
    private String echapperXML(String texte) {
        if (texte == null) return "";
        return texte
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
        //pour eviter les erreurs de format XML en remplaçant les caracteres speciaux
    }

}
