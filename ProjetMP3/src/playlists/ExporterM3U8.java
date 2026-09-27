package playlists;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.File;
import java.nio.file.Path;

import metadonnees.Metadonnee;
import metadonnees.FichierMP3;

/**
 * classe qui exporte une playlist au format M3U8
 * (format texte compatible avec beaucoup de lecteurs audio)
 */

public class ExporterM3U8 extends PlaylistExporter {

    @Override
    public boolean exporter(Playlist playlist, String cheminSortie) {
    	
        //verifie si la playlist est vide ou nulle
        if (playlist == null || playlist.getMorceaux().isEmpty()) {
            System.err.println("Erreur : Playlist vide ou nulle");
            return false;
        }
        
        //represente le fichier de sortie sous forme d'objet Path
        Path fichier = Paths.get(cheminSortie);

        try (BufferedWriter writer = Files.newBufferedWriter(fichier, StandardCharsets.UTF_8)) {
            //ouvre un fichier en ecriture avec l'encodage UTF-8
        	
        	//en-tete obligatoire d'un fichier M3U8
            writer.write("#EXTM3U\n");
            
            // ecrit le nom de la playlist
            writer.write("#PLAYLIST:" + playlist.getNom() + "\n");
            
            //parcourt tous les fichiers MP3 de la playlist
            for (FichierMP3 mp3 : playlist.getMorceaux()) {
            	
                //recupere les metadonnees du fichier (titre, artiste, durée…)
                Metadonnee meta = mp3.getMetadonnee();

                if (meta != null) {
                	
                    //recupere artiste et titre, ou valeur par defaut
                    String artiste = nettoyer(meta.getArtiste(), "Inconnu");
                    String titre   = nettoyer(meta.getTitre(),   "Sans titre");

                    //ligne d'information M3U8 (#EXTINF:duree, artiste - titre)
                    writer.write(String.format("#EXTINF:%d,%s - %s\n",
                            meta.getDuree(), //duree en secondes
                            artiste,
                            titre));

                } else {
                    //si pas de metadonnees, on met juste le nom du fichier
                    String nomFichier = new File(mp3.getChemin()).getName();
                    writer.write("#EXTINF:-1," + nomFichier + "\n");
                }

                //ecrit le chemin du fichier sous forme d'URI lisible par les lecteurs audio
                String uri = Paths.get(mp3.getChemin()).toUri().toString();
                writer.write(uri + "\n");
            }
            
            System.out.println("Playlist exportée avec succès : " + cheminSortie);
            return true; //export reussi

        } catch (IOException e) {
        	//message d’erreur si probleme d’ecriture dans le fichier
            System.err.println("Erreur export M3U8 : " + e.getMessage());
            return false;
        }
    }

    //methode utilitaire qui remplace une chaine vide ou nulle par une valeur par defaut
    private String nettoyer(String texte, String defaut) {
        if (texte == null || texte.isEmpty()) return defaut;
        return texte.replace(",", " ");
    }
}

