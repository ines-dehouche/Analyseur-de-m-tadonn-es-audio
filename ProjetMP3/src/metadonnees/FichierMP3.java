package metadonnees;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class FichierMP3 {
	
	   private String chemin;
	   private Metadonnee metadonnee;

	   public FichierMP3(String chemin) {
	       this.chemin = chemin;
	       this.metadonnee = null;
	   }
	   public String getChemin() {
	       return chemin;
	   }
	   public void setChemin(String chemin) {
	       this.chemin=chemin;
	   }
	   public Metadonnee getMetadonnee() {
	       return metadonnee;
	   }
	   public void setMetadonnee(Metadonnee metadonnee) {
	       this.metadonnee=metadonnee;
	   }
	   
	    public boolean estValide() {
	        return verifierExtension() && verifierMimeType();
	    }
	    
	    //verifie si le fichier est un MP3 valide (extension + type MIME)
	    private boolean verifierExtension() {
	        return chemin.toLowerCase().endsWith(".mp3");
	    }

	    private boolean verifierMimeType() {
	        try {
	            File fichier = new File(chemin);
	            String mimeType = Files.probeContentType(fichier.toPath());

	            if (mimeType == null) {
	                return false;
	            }

	            return mimeType.equals("audio/mpeg");

	        } catch (IOException e) {
	            return false;
	        }
	    }
	   
	    public void chargerMetadonnee() {
	        if (metadonnee == null) {
	            metadonnee = new Metadonnee();
	            metadonnee.extraireDepuisFichier(new File(chemin));
	        }
	    }
	    
	   @Override
	   public String toString() {
	       Metadonnee m = getMetadonnee();
	       if (m != null) {
	           return m.getArtiste() + " - " + m.getTitre();
	       }
	       return new File(chemin).getName();
	   }
}
