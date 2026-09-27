# Projet MP3 – POO Java L2

## Membres du projet
- Nom Prénom – Groupe TD
- Nom Prénom – Groupe TD

## Description
Application Java permettant l’analyse des métadonnées audio (MP3) et la création
de playlists aux formats XSPF, JSPF et M3U8, avec une interface en ligne de commande
(CLI) et une interface graphique (GUI).

## Contenu de l’archive
- src/ : code source Java
- jar/ : fichiers exécutables CLI et GUI
- javadoc/ : documentation du code
- rapport/ : rapport du projet (ODT et PDF)

## Exécution
### Mode CLI
Le mode CLI permet d’analyser un fichier MP3 ou d’explorer un dossier depuis la ligne de commande.

Exemples d’utilisation :
java -jar jar/cli.jar -h
java -jar jar/cli.jar -f musique.mp3
java -jar jar/cli.jar -d ./music/
java -jar jar/cli.jar -d ./music/ --xspf -o playlist.xspf

## Mode GUI
Le mode GUI permet d’explorer des dossiers, de visualiser les métadonnées des fichiers MP3,
de gérer des playlists et d’écouter les morceaux via une interface graphique.

Lancement :
java -jar jar/gui.jar