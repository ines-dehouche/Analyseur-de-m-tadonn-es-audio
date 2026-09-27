package exploration;

import metadonnees.FichierMP3;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

  //classe qui explore un dossier et recupere tous les fichiers MP3
    public class ExplorateurDossier {
    	
    List<FichierMP3> resultat=new ArrayList<>(); //liste pour stocker les fichiers MP3 trouves
    File[] dossiers = new File[500];    //tableau pour stocker les dossiers a explorer
    int nb=0;    //nombre de dossiers actuellement dans le tableau
    
    //parcourt le dossier racine et tous ses sous-dossiers pour recuperer tous les fichiers MP3.
     public List<FichierMP3> explorer(String chemin) {
        dossiers[nb]=new File(chemin); //ajouter le dossier racine au tableau
        nb++;
        int i=0; 
        while (i < nb) {
            File dossierActuel=dossiers[i]; //prend le dossier courant
            i++;
            File[] fichiers =dossierActuel.listFiles(); //liste tous les fichiers et sous dossiers
            if (fichiers == null) {
                continue; //si le dossier est vide ou inaccessible on passe au suivant
            }
            for (File f : fichiers) {
                if (f.isDirectory()) {  
                    //si c'est un sous dossier on l'ajoute au tableau pour l'explorer plus tard
                    if (nb < dossiers.length){  
                        dossiers[nb]=f;  
                        nb++;
                    }
                }
                else{
                    //si c'est un fichier MP3 on le transforme en FichierMp3 et on l'ajoute a la liste
                    String nom=f.getName().toLowerCase();
                    if (nom.endsWith(".mp3")) { 
                        FichierMP3 mp3=new FichierMP3(f.getAbsolutePath());
                        resultat.add(mp3); 
                    }
                }
            }
        }
        return resultat; //retourne la liste finale des fichiers MP3 trouves
    }
}
