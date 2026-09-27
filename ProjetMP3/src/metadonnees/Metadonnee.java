package metadonnees;

import org.jaudiotagger.audio.AudioFile;

import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.FieldKey;

import org.jaudiotagger.tag.images.Artwork;
import java.io.File;

public class Metadonnee {
	
    private String titre;
    private String artiste;
    private String album;
    private String genre;
    private int annee;
    private int duree;
    
    private byte[] detailcover; // tableau pour stocker la cover
    
    //constructeur par défaut, il initialise les champs avec des valeurs par défaut
    public Metadonnee() {
        this.titre = "inconnu";
        this.artiste = "inconnu";
        this.album = "inconnu";
        this.genre = "inconnu";
        this.annee = 0;
        this.duree = 0;
    }
   //constructeur avec paramètres
    public Metadonnee(String titre, String artiste, String album, String genre, int annee, int duree) {
        this.titre = titre;
        this.artiste = artiste;
        this.album = album;
        this.genre = genre;
        this.annee = annee;
        this.duree = duree;
    }
    //getters et setters
    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre=titre;
    }

    public String getArtiste() {
        return artiste;
    }

    public void setArtiste(String artiste){
        this.artiste=artiste;
    }

    public String getAlbum() {
        return album;
    }
    public void setAlbum(String album){
        this.album=album;
    }
    

    public int getAnnee() {
        return annee;
    }

    public void setAnnee(int annee) {
        this.annee = annee;
    }

    public int getDuree() {
        return duree;
    }

    public void setDuree(int duree) {
        this.duree=duree;
    }
    
    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre=genre;
    }
    
    //retourne l'image de couverture de l'album (tableau d'octets représentant la cover)
    public byte[] getCover() { 
        return detailcover;
    }
    
   @Override
   public String toString() {
       return "Titre: "+titre+"\n"+"Artiste: "+artiste+"\n"+"Album: "+album+"\n"+"genre: "+genre+"\n"+"Duree: "+duree;
     }
   
   //extraire les métadonnées depuis un fichier MP3 à l'aide de la bibliothèque "jaudiotagger"
   public void extraireDepuisFichier(File fichier) {
	    try {
	        AudioFile audioFile = AudioFileIO.read(fichier);
	        Tag tag = audioFile.getTag();

	        if (tag != null) {
	        	//lecture des champs texte
	            this.titre = valeurOuInconnu(tag.getFirst(FieldKey.TITLE));
	            this.artiste = valeurOuInconnu(tag.getFirst(FieldKey.ARTIST));
	            this.album = valeurOuInconnu(tag.getFirst(FieldKey.ALBUM));
	            this.genre = valeurOuInconnu(tag.getFirst(FieldKey.GENRE));
                
	            //lecture de l'année
	            String anneeStr = tag.getFirst(FieldKey.YEAR);
	            if (anneeStr != null && !anneeStr.trim().isEmpty()) {
	                try {
	                    this.annee = Integer.parseInt(anneeStr);
	                } catch (NumberFormatException e) {
	                    this.annee = 0;
	                }
	            }
	            //extraction de l'image de couverture
	            Artwork artwork = tag.getFirstArtwork();
	            if (artwork != null) {
	                this.detailcover = artwork.getBinaryData();
	            } else {
	                this.detailcover = null;
	            }
	        }
	        //durée du morceau en secondes
	        this.duree = audioFile.getAudioHeader().getTrackLength();

	    } catch (Exception e) {
	        this.detailcover = null;
	    }
	}

   //methode utilitaire pour eviter les champs vides
   private String valeurOuInconnu(String valeur) {
	    return (valeur == null || valeur.trim().isEmpty()) ? "inconnu" : valeur;
	}
}
