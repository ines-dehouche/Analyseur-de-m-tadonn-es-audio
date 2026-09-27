package playlists;

import playlists.Playlist;
import metadonnees.FichierMP3;
import metadonnees.Metadonnee;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ExporterJSPF extends PlaylistExporter {
    // classe qui exporte une playlist au format JSPF (format JSON pour les playlists)

    @Override
    public boolean exporter(Playlist playlist, String cheminSortie) {

    	 //verifie si la playlist est vide ou nulle
        if (playlist == null || playlist.getMorceaux().isEmpty()) {
            return false;
        }

        try (FileWriter writer = new FileWriter(cheminSortie)) {
            //ouvre un fichier en ecriture

            writer.write("{\n"); //debut du JSON
            writer.write("  \"playlist\": {\n"); //debut de l’objet playlist

           //ecrit le titre de la playlist
            writer.write("    \"title\": \"" + echapperJSON(playlist.getNom()) + "\"");

           //si la playlist contient des morceaux
            if (!playlist.getMorceaux().isEmpty()) {
                writer.write(",\n");
                writer.write("    \"track\": [\n"); //debut du tableau des pistes

                int size = playlist.getMorceaux().size(); //nombre total de morceaux
                int index = 0;                            // pour savoir si on doit mettre une virgule
                
               //parcourt chaque fichier MP3 de la playlist
                for (FichierMP3 mp3 : playlist.getMorceaux()) {

                    writer.write("      {\n"); //debut d’un morceau

                    //chemin du fichier converti en URI
                    String location = new File(mp3.getChemin()).toURI().toString();
                    writer.write("        \"location\": [\"" + echapperJSON(location) + "\"]");

                    //recupere les metadonnees du fichier
                    Metadonnee meta = mp3.getMetadonnee();

                    if (meta != null) {
                    	
                        //titre si present
                        if (meta.getTitre() != null && !meta.getTitre().isEmpty()) {
                            writer.write(",\n        \"title\": \"" + echapperJSON(meta.getTitre()) + "\"");
                        }
                        
                        //artiste si present
                        if (meta.getArtiste() != null && !meta.getArtiste().isEmpty()) {
                            writer.write(",\n        \"creator\": \"" + echapperJSON(meta.getArtiste()) + "\"");
                        }
                        
                        //album si present
                        if (meta.getAlbum() != null && !meta.getAlbum().isEmpty()) {
                            writer.write(",\n        \"album\": \"" + echapperJSON(meta.getAlbum()) + "\"");
                        }


                        //duree en millisecondes
                        if (meta.getDuree() > 0) {
                            writer.write(",\n        \"duration\": " + (meta.getDuree() * 1000));
                        }
                    }

                    writer.write("\n      }");  //fin d’un morceau
                    
                    //ajoute une virgule si ce n’est pas le dernier morceau
                    if (index < size - 1) writer.write(",");

                    writer.write("\n");
                    index++;
                }

                writer.write("    ]\n"); //fin du tableau des pistes
            } else {
                writer.write("\n");
            }

            writer.write("  }\n"); //fin de l’objet playlist
            writer.write("}\n"); //fin du JSON
            
            System.out.println("Playlist exportée avec succès : " + cheminSortie);
            return true; //export reussi

        } catch (IOException e) {
            //si une erreur se produit pendant l’ecriture
            System.err.println("Erreur lors de l'export JSPF : " + e.getMessage());
            return false;
        }
    }

    //methode qui echappe les caracteres interdits dans un fichier JSON
    private String echapperJSON(String texte) {
        if (texte == null) return "";
        return texte
                .replace("\\", "\\\\") //remplace "\" par "\\"
                .replace("\"", "\\\"") //remplace " par \"
                .replace("\n", "\\n") //retour a la ligne en version JSON
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
