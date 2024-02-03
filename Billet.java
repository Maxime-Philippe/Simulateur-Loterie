import java.io.Serializable;
import java.security.SecureRandom;
import java.util.ArrayList;

public class Billet implements Serializable{
    private static final long serialVersionUID = 1L;

    private String numeroSerie; //Numéro de série du billet
    private ArrayList<Integer> nombres; //Nombres sur le billet
    private Joueur joueur; //Variable des joueurs 
    private int prix = 0; //Catégorie des prix gagnés

    //Toutes les lettres et chiffres utilisables pour le numéro de serie du billet
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    public Billet(ArrayList<Integer> n, Joueur j){
        this.numeroSerie = generateSerialNumber();
        this.nombres = n;
        this.joueur = j;
        
    }

    /**
     * Fonction pour générer un numéro de serie
     * @return --> le numéro de série 
     */
    public static String generateSerialNumber() {
        SecureRandom random = new SecureRandom();
        StringBuilder serialNumber = new StringBuilder();
        int SERIAL_LENGTH = 20;
        for (int i = 0; i < SERIAL_LENGTH; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            char randomChar = CHARACTERS.charAt(randomIndex);
            serialNumber.append(randomChar);
        }

        return serialNumber.toString();
    }

    /**
     * Fonction qui retourne la liste des nombres du billet
     * @return --> les nombres du billet
     */
    public ArrayList<Integer> getNombres() {
        return this.nombres;
    }

    /**
     * Fonction qui récupère le numéro de série
     * @return --> le numéro de série
     */
    public String getNumeroSerie(){
        return this.numeroSerie;
    }

    /**
     * Fonction qui paramètre le numéro de serie
     * @param numeroSerie --> numéro de série
     */
    public void setNumeroSerie(String numeroSerie) {
        this.numeroSerie = numeroSerie;
    }

    /**
     * Fonction qui récupère le joueur
     * @return --> le joueur
     */
    public Joueur getJoueur() {
        return joueur;
    }

    /**
     * Fonction qui récupère la catégorie du prix gagner
     * @return --> la catégorie du prix gagner
     */
    public int getPrix() {
        return prix;
    }

    /**
     * Fonction pour paramétrer le prix gagner
     * @param prix --> valeur de la catégorie du prix gagner 
     */
    public void setPrix(int prix) {
        this.prix = prix;
    }

    /**
     * Fonction d'affichage toString des billets
     */
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("Numéro de série : ").append(this.numeroSerie).append(this.getJoueur()).append(" ; Catégorie prix gagné : ").append(this.getPrix()).append("; Nombres : ");
        for (int nombre : this.nombres) {
            result.append(nombre).append(", ");
        }
        result.delete(result.length() - 2, result.length());
        return result.toString();
    }
}