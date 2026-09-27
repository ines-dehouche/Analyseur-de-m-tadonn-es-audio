package gui;

import org.jaudiotagger.audio.AudioFile; //bibliothèque utilisée pour lire les fichiers audio (métadonnées)

//imports JavaFX
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

//imports Java
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.io.ByteArrayInputStream;

//imports du projet
import playlists.Playlist;
import metadonnees.FichierMP3;
import metadonnees.Metadonnee;
import gui.ControleurApplication;

public class GUI extends Application {

	//controleur principal de l'application
    private ControleurApplication controleur;

    // playlist actuellement chargée
    private Playlist playlistActuelle;
    
    private List<Playlist> playlists = new ArrayList<>(); //liste de toutes les playlists
    private ChoiceBox<Playlist> choixPlaylist; //menu pour choisir une playlist

    // composants graphiques
    private ListView<FichierMP3> listeMp3;
    private ChoiceBox<String> choixFormat; //choix du format d'export (XSPF, JSPF, M3U8)

    //labels affichant les métadonnées du fichier sélectionné
    private Label titre;
    private Label artiste;
    private Label album;
    private Label duree;
    
    private ImageView coverImage; //image de couverture de l'album
    
    private MediaPlayer mediaPlayer; //lecteur audio JavaFX
    private int indexCourant = -1;  //index du morceau actuellement joué
    
    private Slider sliderProgression; //bare de progression pour afficher et modifier la progression de la musique
    
    private Playlist playlistBibliotheque; //playlist contenant tous les fichiers MP3 du dossier sélectionné

    /**
     * methode appelée automatiquement au lancement de l'application JavaFX
     * elle crée et organise tous les composants graphiques
     */
    @Override
    public void start(Stage stage) {

        controleur = new ControleurApplication(); //initialisation du contrôleur

        BorderPane root = new BorderPane(); //conteneur principal de la fenêtre

        //zone du haut
        Button btnChoisirDossier = new Button("Choisir un dossier"); //bouton pour choisir un dossier contenant des fichiers MP3
        btnChoisirDossier.setOnAction(e -> choisirDossier());
        
        choixPlaylist = new ChoiceBox<>();
        //menu déroulant pour choisir la playlist active
        choixPlaylist.setOnAction(e -> {
            playlistActuelle = choixPlaylist.getValue();

            if (playlistActuelle != null) {
                listeMp3.getItems().setAll(playlistActuelle.getMorceaux());
            }
        });
        //bouton pour créer une nouvelle playlist utilisateur
        Button btnNouvellePlaylist = new Button("➕ Nouvelle playlist");
        btnNouvellePlaylist.setOnAction(e -> creerPlaylistUtilisateur());
        //conteneur horizontal du haut
        HBox haut = new HBox(10);
        haut.setPadding(new Insets(10));
        haut.getChildren().addAll(
            btnChoisirDossier,
            choixPlaylist,
            btnNouvellePlaylist
        );
        root.setTop(haut);

        //zone gauche: licte mp3
        Label lblListe = new Label("🎵 Fichiers MP3");

        listeMp3 = new ListView<>(); //liste graphique des fichiers MP3
        listeMp3.setOnMouseClicked(e -> afficherMetadonnee());

        VBox gauche = new VBox(5, lblListe, listeMp3);
        gauche.setPadding(new Insets(10));
        gauche.setPrefWidth(300);
        root.setLeft(gauche);

        //zone centrale : metadonnees
        Label lblMeta = new Label("Métadonnées");

        titre = new Label("Titre : ");
        artiste = new Label("Artiste : ");
        album = new Label("Album : ");
        duree = new Label("Durée : ");
        
       //slider de progression de la musique
        sliderProgression = new Slider();
        sliderProgression.setMin(0);
        sliderProgression.setMax(100);
        sliderProgression.setValue(0);
        sliderProgression.setPrefWidth(250); // largeur du slider
        
     // Initialisation de la cover
        coverImage = new ImageView();
        coverImage.setFitWidth(150);   // largeur
        coverImage.setFitHeight(150);  // hauteur
        coverImage.setPreserveRatio(true);

        // Ajouter coverImage dans le VBox central avant les labels
        VBox centre = new VBox(8,lblMeta,coverImage,titre,artiste,album,duree,sliderProgression);
        centre.setPadding(new Insets(10));
        root.setCenter(centre);

        //zone basse : export
        choixFormat = new ChoiceBox<>();
        choixFormat.getItems().addAll("XSPF", "JSPF", "M3U8");
        choixFormat.setValue("XSPF");

        Button btnExporter = new Button("Exporter la playlist");
        btnExporter.setOnAction(e -> exporterPlaylist());
        
       //boutons lecteur
        Button btnPlay = new Button("▶");
        Button btnPause = new Button("⏸");
        Button btnNext = new Button("⏭");
        
        Button btnPrev = new Button("⏮");
        Button btnAjouterPlaylist = new Button("➕ Ajouter à une playlist");
        btnAjouterPlaylist.setOnAction(e -> ajouterMusiquePlaylist());
        
        Button btnRetirer = new Button("➖ Retirer"); //boutton pour retirer une musique d'une playlist (pas la supprimer)
        btnRetirer.setOnAction(e -> retirerMusiquePlaylist());

        btnPlay.setOnAction(e -> { //jouer la musique sélectionnée dans la liste
            int index = listeMp3.getSelectionModel().getSelectedIndex();
            if (index == -1) index = 0; //si aucune musique n'est sélectionnée, on joue la première
            jouerMusique(index);
        });

        btnPause.setOnAction(e -> { //action du bouton Pause (mettre la musique en pose)
            if (mediaPlayer != null) {
                mediaPlayer.pause();
            }
        });

        btnNext.setOnAction(e -> jouerMusique(indexCourant + 1)); //bouton suivant (jouer la musique suivante dans la playlist)
        btnPrev.setOnAction(e -> jouerMusique(indexCourant - 1)); //bouton précédent (jouer la musique précédente dans la playlist)

        //barre du bas avec les boutons du lecteur et l’export
        HBox bas = new HBox(10,btnPrev,btnPlay,btnPause,btnNext,btnAjouterPlaylist,btnRetirer,choixFormat,btnExporter);
        bas.setPadding(new Insets(10));
        root.setBottom(bas);

        //creation et affichage de la fenetre principale
        Scene scene = new Scene(root, 800, 500);
        stage.setTitle("Projet MP3 – Mode Graphique");
        stage.setScene(scene);
        stage.show();
    }

    public void choisirDossier() {
        try {
            DirectoryChooser chooser = new DirectoryChooser();
            File dossier = chooser.showDialog(null);

            if (dossier != null) {
            	//analyse du dossier et creation de la playlist principale
                playlistBibliotheque = controleur.analyserDossier(dossier.getAbsolutePath());

                playlists.clear();
                choixPlaylist.getItems().clear();

                playlists.add(playlistBibliotheque);
                choixPlaylist.getItems().add(playlistBibliotheque);

                playlistActuelle = playlistBibliotheque;
                choixPlaylist.setValue(playlistBibliotheque);
                //affichage des fichiers MP3 dans la liste
                listeMp3.getItems().setAll(playlistBibliotheque.getMorceaux());
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public void afficherMetadonnee() {
        try {
            FichierMP3 fichier = listeMp3.getSelectionModel().getSelectedItem();
            if (fichier != null) {
                fichier.chargerMetadonnee(); 
                Metadonnee m = fichier.getMetadonnee();

                titre.setText("Titre : " + m.getTitre());
                artiste.setText("Artiste : " + m.getArtiste());
                album.setText("Album : " + m.getAlbum());
                duree.setText("Durée : " + m.getDuree());

               //affichage de la cover si elle existe
                byte[] imageBytes = m.getCover(); 
                if (imageBytes != null) {
                    Image img = new Image(new ByteArrayInputStream(imageBytes));
                    coverImage.setImage(img);
                } else { //image par défaut si aucune aucune n'est trouvée
                    File defaultCover = new File("C:/Users/dell/OneDrive/cover_default.png");
                    if (defaultCover.exists()) {
                        coverImage.setImage(new Image(defaultCover.toURI().toString()));
                    } else {
                        coverImage.setImage(null);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur affichage métadonnées : " + e.getMessage());
        }
    }
   //exporte la playlist courante dans le format choisi par l'utilisateur
    public void exporterPlaylist() {
        try {
            if (playlistActuelle == null) return;

            FileChooser chooser = new FileChooser();
            File fichier = chooser.showSaveDialog(null);

            if (fichier != null) {
                String chemin = fichier.getAbsolutePath();
                String format = choixFormat.getValue().toLowerCase();

                //ajout automatique de l'extension si elle est manquante
                if (!chemin.endsWith("." + format)) {
                    chemin = chemin + "." + format;
                }
                //appel du controleur pour l’export
                controleur.exporterPlaylist(playlistActuelle,chemin,choixFormat.getValue());
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
   
    private void jouerMusique(int index) {  //joue la musique à l’index donné et gere la barre de progression
        if (playlistActuelle == null) return;
        if (index < 0 || index >= listeMp3.getItems().size()) return;
       //arret de la musique en cours
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
        FichierMP3 mp3 = listeMp3.getItems().get(index);
        File fichier = new File(mp3.getChemin());
        Media media = new Media(fichier.toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.play();
        indexCourant = index;
        listeMp3.getSelectionModel().select(index);
        //mise à jour du slider en fonction de l'avancement de la musique
        mediaPlayer.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
            if (!sliderProgression.isValueChanging()) { //eviter les conflits quand l’utilisateur bouge le slider
                double progress = newTime.toSeconds() / mediaPlayer.getTotalDuration().toSeconds() * 100;
                sliderProgression.setValue(progress);
            }
        });
        //deplacement de la musique quand l'utilisateur bouge le slider
        sliderProgression.valueChangingProperty().addListener((obs, wasChanging, isChanging) -> {
            if (!isChanging) { // l'utilisateur a fini de bouger le slider
                double position = sliderProgression.getValue() / 100 * mediaPlayer.getTotalDuration().toSeconds();
                mediaPlayer.seek(javafx.util.Duration.seconds(position));
            }
        });
        sliderProgression.setOnMouseReleased(event -> { // pour un clic direct sur le slider
            double position = sliderProgression.getValue() / 100 * mediaPlayer.getTotalDuration().toSeconds();
            mediaPlayer.seek(javafx.util.Duration.seconds(position));
        });
    }
    //ajouter la musique sélectionnée à une playlist utilisateur
    private void ajouterMusiquePlaylist() {
        FichierMP3 mp3 = listeMp3.getSelectionModel().getSelectedItem();
        if (mp3 == null || playlists.isEmpty()) return;

        //liste des playlists utilisateur
        List<Playlist> playlistsUtilisateur = new ArrayList<>();
        for (Playlist p : playlists) {
            if (p != playlistBibliotheque) {
                playlistsUtilisateur.add(p);
            }
        }
        //message si aucune playlist utilisateur n'existe
        if (playlistsUtilisateur.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Aucune playlist");
            alert.setHeaderText(null);
            alert.setContentText("Crée une playlist avant d'ajouter une musique.");
            alert.show();
            return;
        }
        //fenetre de choix de la playlist
        ChoiceDialog<Playlist> dialog = new ChoiceDialog<>(playlistsUtilisateur.get(0), playlistsUtilisateur);
        dialog.setTitle("Ajouter à une playlist");
        dialog.setHeaderText("Choisis la playlist");
        dialog.setContentText("Playlist :");

        dialog.showAndWait().ifPresent(playlistChoisie -> {
          if (!playlistChoisie.getMorceaux().contains(mp3)) {
            playlistChoisie.ajouter(mp3);
          }
        });
    }
    //cree une nouvelle playlist utilisateur
    private void creerPlaylistUtilisateur() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Nouvelle playlist");
        dialog.setHeaderText("Créer une playlist");
        dialog.setContentText("Nom de la playlist :");

        dialog.showAndWait().ifPresent(nom -> {
            Playlist nouvelle = new Playlist(nom);
            playlists.add(nouvelle);
            choixPlaylist.getItems().add(nouvelle);
            choixPlaylist.setValue(nouvelle);
            playlistActuelle = nouvelle;
           //affichage des musiques de la bibliothèque
            listeMp3.getItems().setAll(playlistBibliotheque.getMorceaux());
        });
    }
   //retirer la musique sélectionnée de la playlist courante
    private void retirerMusiquePlaylist() {
        if (playlistActuelle == playlistBibliotheque) return;

        FichierMP3 mp3 = listeMp3.getSelectionModel().getSelectedItem();

        if (mp3 != null) {
            playlistActuelle.supprimer(mp3);
            listeMp3.getItems().remove(mp3);
        }
    }
   //point d'entrée de l'application graphique
    public static void main(String[] args) {
        launch(args);
    }
}
