package playlists;

import metadonnees.FichierMP3;

import java.util.ArrayList;
import java.util.List;

public class Playlist {

   private String nom;
   private List<FichierMP3> morceaux;

   public Playlist(String nom) {
	    this.nom=nom;
	    this.morceaux = new ArrayList<>();
	    }

   public String getNom() {
       return nom;
	 }

   public void setNom(String nom) {
	   this.nom=nom;
	    }
   public List<FichierMP3> getMorceaux() {
	   return morceaux;
	}

   public void ajouter(FichierMP3 mp3) {
	   if (mp3 != null) {
        morceaux.add(mp3);
	    }
	}
   public void supprimer(FichierMP3 mp3) {
       morceaux.remove(mp3);
   }
   public int taille() {
	   return morceaux.size();
	}
	@Override
   public String toString() {
       return nom;
   }
}