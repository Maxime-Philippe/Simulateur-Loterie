import java.io.Serializable;

public class Joueur implements Serializable{
    private int id;

    public Joueur(int i){
        this.id = i;
    }

    /**
     * Fonction pour récuperer l'ID d'un joueur
     * @return l'ID du joueur
     */
    public int getId() {
        return id;
    }

    /**
     * Fonction pour paramétrer l'ID du joueur
     * @param id --> ID du joueur
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Fonction d'affichage toString des joueurs 
     */
    public String toString() {
        return "; Joueur : " + this.getId();
    }
    
}
