package cli;

import gui.ControleurApplication;
import playlists.Playlist;
import metadonnees.Metadonnee;
import metadonnees.FichierMP3;

public class CLI{

   private String[] arguments; //arguments passés en ligne de commande
   private ControleurApplication controleur; //controleur central
   
   //Constructeur
   public CLI(String[] args) {
       this.arguments=args;
       this.controleur=new ControleurApplication();
   }
   //methode principale qui analyse les arguments et lance le traitement approprié  
   public void executer(){
       if (arguments.length == 0) {
           afficherErreur("parametres manquants,utilisez -h ou --help pour l'aide");
           return;
       }

       if (contientOption("-h") || contientOption("--help")) {
           afficherAide();
           return;
       }

       //options exclusives: -f ou -d
       boolean cf=contientOption("-f"); //cf :contient fichier
       boolean cd=contientOption("-d"); //cd :contient dossier

       if(cd==cf) { //les deux options sont soit absentes, soit présentes en même temps
           afficherErreur("Vous devez choisir soit -f (fichier) soit -d (dossier)");
           return;
       }
       //traitement selon l'option choisie
        if(cf){
        	traiterFichier();
        }else{
            traiterDossier();
       }
    }

   private void traiterFichier() {
       String chemin=getValeurOption("-f");
       //option -f sans chemin
       if(chemin==null){
    	   afficherErreur("option -f utilisee sans nom de fichier");
           return;
       }
       try{
    	  //analyse du fichier MP3
           Metadonnee meta=controleur.analyserFichier(chemin);
           afficherMetadonnee(meta);
       }catch(Exception e){
           afficherErreur("erreur lors de l'analyse du fichier:"+ e.getMessage());
       }
   }
   //analyse un dossier contenant des fichiers MP3
    private void traiterDossier(){
        String chemin=getValeurOption("-d");
        //option -d sans chemin
        if (chemin==null) {
            afficherErreur("option -d utilisee sans nom de dossier");
            return;
        }

        //format de sortie (--xspf,--jspf,--m3u8)
        String format=detecterFormatPlaylist();
        String fichierSortie=getValeurOption("-o");
        
        //fichier de sortie sans format
        if (fichierSortie != null && format == null) {
            afficherErreur("format de playlist manquant (--xspf, --jspf ou --m3u8)");
            return;
        }

        try {
        	//analyse du dossier
            Playlist p=controleur.analyserDossier(chemin);
            //export de la playlist si c'est demandé
            if (format!=null && fichierSortie!=null) {
                boolean resultat;
                    resultat=controleur.exporterPlaylist(p,fichierSortie,format);
                if(!resultat) {
                    afficherErreur("erreur lors de l'export de la playlist");
                }
            } else{
            	//affichage simple de la playlist
                afficherPlaylist(p);
            }
        } catch(Exception e){
            afficherErreur("erreur lors de l'analyse du dossier:" + e.getMessage());
        }
    }
    //methode qui verifie si une option est présente dans les argument
    private boolean contientOption(String opt){
        for(String arg : arguments){
            if(opt.equals(arg)){
               return true;
            }
        }
        return false;
    }
    //methode qui recuperer la valeur associée à une option
    private String getValeurOption(String opt){
        for(int i=0; i<arguments.length - 1; i++){
            if(opt.equals(arguments[i])){
                return arguments[i+1];
            }
        }
        return null;
    }
    //methode qui detecte le format de la playlist demandé
    private String detecterFormatPlaylist(){
        if(contientOption("--xspf")){
           return "xspf";
        }
        if(contientOption("--jspf")){
           return "jspf";
        }
        if(contientOption("--m3u8")){
           return "m3u8";
        }
        return null;
    }

    //methode qui afficher les métadonnées d'un fichier MP3 dans la console
    private void afficherMetadonnee(Metadonnee m){
        if(m == null){
            System.out.println("aucune metadonnee trouvee");
            return;
        }
        System.out.println("titre:"+m.getTitre());
        System.out.println("artiste:"+m.getArtiste());
        System.out.println("album:"+m.getAlbum());
        System.out.println("durée:"+m.getDuree() +"s");
    }
    //methode qui affiche le contenu d'une playlist dans la console
    private void afficherPlaylist(Playlist p){
        if (p==null){
            System.out.println("Playlist vide ou introuvable");
            return;
        }
        System.out.println("playlist:"+ p.getNom());
        for(FichierMP3 f : p.getMorceaux()){
            System.out.println("-"+f.getChemin());
        }
    }
    //affiche un message d'erreur
    private void afficherErreur(String message){
    	System.err.println("Erreur : " + message);
    }
    //affiche l'aide de la ligne de commande
    public void afficherAide(){
        System.out.println("Options principales :");
        System.out.println("-h, --help : affiche cette aide");
        System.out.println("-f (fichier.mp3): analyse un fichier MP3");
        System.out.println("-d (dossier) : explore un dossier et cree la playlist par defaut");
        System.out.println("--xspf | --jspf | --m3u8 : format de la playlist en sortie");
        System.out.println("-o (fichierSortie) : fichier de sortie pour la playlist");
    }
    //point d'entree du programme en mode console
 	public static void main(String[] args) {
 	    CLI cli = new CLI(args);
 	    cli.executer();
 	}
}
