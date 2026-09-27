package gui;

import playlists.*;
import metadonnees.*;
import exploration.ExplorateurDossier;
import java.io.File;
import java.util.List;

//controleur central de l'application,Il fait le lien entre l'interface (GUI ou CLI)
public class ControleurApplication{

    private ExplorateurDossier explorateur; //permet d'explorer un dossier pour trouver des fichiers MP3
    private PlaylistExporter exporter; //exporteur de playlists (XSPF, JSPF, M3U8)
    private PlaylistImporter importer; //importeur de playlists

    public ControleurApplication(){
        explorateur=new ExplorateurDossier();
        exporter=null;
        importer=null;
    }

    public Metadonnee analyserFichier(String chemin){
        File f=new File(chemin);
     //verifier si le fichier existe
        if(!f.exists()){
            System.err.println("Erreur : Le fichier n'existe pas : "+chemin);
            return null;
        }
        FichierMP3 mp3=new FichierMP3(chemin);
        mp3.chargerMetadonnee();
        return mp3.getMetadonnee();
    }

    public Playlist analyserDossier(String chemin){
        File d=new File(chemin);
      //verifier si le dossier existe
        if(!d.exists() || !d.isDirectory()){
            System.err.println("Erreur : Le dossier n'existe pas : "+chemin);
            return null;
        }
      //exploration du dossier
        List<FichierMP3> fichiers=explorateur.explorer(chemin);

        if(fichiers==null || fichiers.isEmpty()){
            System.out.println("Aucun fichier MP3 trouvé dans : "+chemin);
            return null;
        }
        //creation de la playlist
        String nom=d.getName();
        Playlist p=new Playlist(nom);

        for(FichierMP3 f : fichiers){
            p.ajouter(f);
        }

        return p;
    }

    public boolean exporterPlaylist(Playlist p,String sortie,String format){
    	//verification de la playlist
        if(p==null || p.getMorceaux().isEmpty()){
            System.err.println("Erreur : playlist vide");
            return false;
        }
        //choix de l'exporteur selon le format
        switch(format.toLowerCase()){
            case "xspf": exporter=new ExporterXSPF(); break;
            case "jspf": exporter=new ExporterJSPF(); break;
            case "m3u8": exporter=new ExporterM3U8(); break;
            default:
                System.err.println("Format non supporté : "+format);
                return false;
        }

        return exporter.exporter(p,sortie);
    }

    public Playlist importerPlaylist(String chemin,String format){
        File f=new File(chemin);
        if(!f.exists()){
            System.err.println("Erreur : fichier introuvable : "+chemin);
            return null;
        }

        if(format==null){
            System.err.println("Erreur : format null");
            return null;
        }
      //choix de l'importeur selon le format
        switch(format.toLowerCase()){
            case "xspf": importer=new ImporterXSPF(f); break;
            case "jspf": importer=new ImporterJSPF(f); break;
            case "m3u8": importer=new ImporterM3U8(f); break;
            default:
                System.err.println("Format non supporté : "+format);
                return null;
        }

        return (Playlist) importer.importer();
    }
    //affiche les métadonnées d'un fichier MP3 dans la console
    public void afficherMetadonnee(Metadonnee m){
        if(m==null){
            System.out.println("Aucune métadonnée");
            return;
        }
        System.out.println("Titre : "+m.getTitre());
        System.out.println("Artiste : "+m.getArtiste());
        System.out.println("Album : "+m.getAlbum());
        System.out.println("Durée : "+m.getDuree()+"s");
    }
   //affiche le contenu d'une playlist dans la console
    public void afficherPlaylist(Playlist p){
        if(p==null || p.taille()==0){
            System.out.println("Playlist vide");
            return;
        }

        System.out.println("Playlist : "+p.getNom());
        System.out.println("Nb morceaux : "+p.taille());

        int i=1;
        for(FichierMP3 f : p.getMorceaux()){
            Metadonnee m=f.getMetadonnee();
            if(m!=null){
                System.out.println(i+++". "+m.getArtiste()+" - "+m.getTitre()+" ("+f.getChemin()+")");
            }else{
                System.out.println(i+++". "+f.getChemin());
            }
        }
    }
}
