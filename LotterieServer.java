import java.io.*;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.*;

public class LotterieServer {
    private int n; //Ensemble de nombres
    private int k; //Nombre de numéro sur le billet
    private int t; //Nombre de numéro gagnant à avoir sur son ticket pour gagner un prix
    private ArrayList<Integer> chiffresGagnants; //Liste des numéros gagnants
    private int nbBilletsCat1 = 1;
    private int nbBilletsCat2 = 1;
    private int nbJoueurs = 1;
    private Lock nbJoueursLock = new ReentrantLock(); //Verrou pour les joueurs
    private Lock billetLock = new ReentrantLock(); //Verrou pour les billets
    private ExecutorService venteBillet1ThreadPool = Executors.newSingleThreadExecutor(); //Thread billet de catégorie 1
    private ExecutorService venteBillet2ThreadPool = Executors.newSingleThreadExecutor(); //Thread billet de catégorie 2



    public LotterieServer(int ni, int ki, int ti){
        this.clearBillets1File();
        this.clearBillets2File();
        this.n = ni;
        this.k = ki;
        this.t = ti;
        this.chiffresGagnants = this.generateRandomWinningNumbers();
    }

    /**
     * Fonction qui paramètre les numéros des billets de catégorie 1
     * @param nbBillets --> numéro du billet
     */
    public void setNbBilletsCat1(int nbBillets) {
        this.nbBilletsCat1 = nbBillets;
    }

    /**
     * Fonction qui récupère le nnombre de billet de catégorie 1
     * @return --> le numéro de billet de catégorie 1
     */
    public int getNbBilletsCat1() {
        return nbBilletsCat1;
    }

    /**
     * Fonction qui paramètre les numéros des billets de catégorie 2
     * @param nbBillets --> le numéro du billet de catégorie 2
     */
    public void setNbBilletsCat2(int nbBillets) {
        this.nbBilletsCat2 = nbBillets;
    }

    /**
     * Fonction qui récupère le nnombre de billet de catégorie 2
     * @return --> le nombre de billet de catégorie 2
     */
    public int getNbBilletsCat2() {
        return nbBilletsCat2;
    }

    /**
     * Fonction qui vends les billet de catégorie 1
     * @param j --> joueur qui achete un billet
     */
    public void VenteBillet1(Joueur j) {
        venteBillet1ThreadPool.submit(() -> {
            ArrayList<Integer> nombres = generateRandomNumbers(this.k);
            Billet billet = new Billet(nombres, j);
            //System.out.println(billet); enlevez le commentaire si l'enseignant veut voir les billets cat 1 acheter
            saveBillet1ToFile(billet);
        });
    } 

    /**
     * Fonction qui génère les numéros sur les billet de catégorie 1 
     * @param k --> nombre de numéro à générer 
     * @return --> la liste des numéros 
     */
    private ArrayList<Integer> generateRandomNumbers(int k) {
        ArrayList<Integer> numbers = new ArrayList<>();
        for (int i = 0; i < k; i++) {
            int random = (int) (Math.random() * (n + 1));
            numbers.add(random);
        }
        return numbers;
    }
    
    /**
     * Fonction qui vends les billet de catégorie 2
     * @param j --> joueur qui achete un billet
     */
    public void VenteBillet2(Joueur j) {
        venteBillet2ThreadPool.submit(() -> {
            ArrayList<Integer> nombres = inputNumbersFromPlayer(this.k);
            Billet billet = new Billet(nombres, j);
            System.out.println(billet);
            saveBillet2ToFile(billet);
        });
    }

    /**
     * Fonction de saisie des numéros des billets de catégorie 2
     * @param k --> nombre de numéro à saisir
     * @return --> la liste des nombres saisies par l'utilisateur
     */
    private ArrayList<Integer> inputNumbersFromPlayer(int k) {
        ArrayList<Integer> numbers = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);
        int userInput=-1;
        System.out.println("Veuillez saisir " + k + " nombres pour créer votre billet (nb <= n):");
        for (int i = 0; i < k; i++) {
            while (true) {
                synchronized (scanner) {
                    try {
                        System.out.print("Nombre " + (i + 1) + ": ");
                        userInput = scanner.nextInt();
                        while(userInput<0 || userInput>n){ //Vérifie que l'entrée saisie est bien supérieur ou égale à 0 et inférieur ou égale à N
                            System.out.println("Saississez une nombre plus petit que N et supérieur à 0 !");
                            scanner.nextLine();
                            userInput = scanner.nextInt();
                        }
                        numbers.add(userInput);
                        break;  // Sort de la boucle si l'entrée est un entier valide
                    } catch (InputMismatchException e) {
                        System.out.println("Veuillez saisir un nombre entier.");
                        scanner.nextLine();  // Efface le buffer d'entrée pour éviter une boucle infinie
                    }
                }
            }
        }
        return numbers;
    }

    /**
     * Fonction qui sauvegarde les billets de catégorie 1
     * @param billet --> billet à sauvegarder
     */
    private void saveBillet1ToFile(Billet billet) {
        billetLock.lock();
        try{
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(String.valueOf("SaveBillets/Cat1/"+this.getNbBilletsCat1())))) {
                setNbBilletsCat1(this.getNbBilletsCat1()+1);
                oos.writeObject(billet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }finally {
            billetLock.unlock();
        }

    }
    
    /**
     * Fonction qui sauvegarde les billets de catégorie 2
     * @param billet --> billet à sauvegarder
     */
    private void saveBillet2ToFile(Billet billet) {
        billetLock.lock();
        try{
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(String.valueOf("SaveBillets/Cat2/"+this.getNbBilletsCat2())))) {
                setNbBilletsCat2(this.getNbBilletsCat2()+1);
                oos.writeObject(billet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }finally {
            billetLock.unlock();
        }

    }
    
    /**
     * Fonction qui récupère les billets de cat 1 
     * @param num --> numéro du billet 
     * @return --> le billet en question
     */
    public Billet recupererBillet1(String num) {
        billetLock.lock();
        try{
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("SaveBillets/Cat1/"+num))) {
                Object obj = ois.readObject();
                if (obj instanceof Billet) {
                    return (Billet) obj;
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        
            System.out.println("Aucun billet trouvé pour le numéro : " + num);
            return null;
        }finally{
            billetLock.unlock();
        }
             
    }

    /**
     * Fonction qui récupère les billets de cat 2 
     * @param num --> numéro du billet 
     * @return --> le billet en question
     */
    public Billet recupererBillet2(String num) {
        billetLock.lock();
        try{
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("SaveBillets/Cat2/"+num))) {
                Object obj = ois.readObject();
                if (obj instanceof Billet) {
                    return (Billet) obj;
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        
            System.out.println("Aucun billet trouvé pour le numéro : " + num);
            return null;
        }finally{
            billetLock.unlock();
        }
    }    
    
    /**
     *Fonction pour supprimer les fichiers dans le dossier des billets de catégorie 1
     */
    private void clearBillets1File() {
        File folder = new File("SaveBillets/Cat1");
        File[] files = folder.listFiles();
    
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    if (!file.delete()) {
                        System.err.println("Failed to delete file: " + file.getName());
                    }
                }
            }
        } else {
            System.err.println("Error listing files in the SaveBillets folder");
        }
    }

    /**
     *Fonction pour supprimer les fichiers dans le dossier des billets de catégorie 2
     */
    private void clearBillets2File() {
        File folder = new File("SaveBillets/Cat2");
        File[] files = folder.listFiles();
    
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    if (!file.delete()) {
                        System.err.println("Failed to delete file: " + file.getName());
                    }
                }
            }
        } else {
            System.err.println("Error listing files in the SaveBillets folder");
        }
    }

    /**
     * Fonction qui paramètre les chiffres gagnants
     * @param chiffresGagnants --> liste des chiffres gagnants
     */
    public void setChiffresGagnants(ArrayList<Integer> chiffresGagnants) {
        this.chiffresGagnants = chiffresGagnants;
    }

    /**
     * Fonction qui récupère les chiffres gagnants
     * @return --> la liste des chifres gagnants 
     */
    public ArrayList<Integer> getChiffresGagnants() {
        return chiffresGagnants;
    }

    /**
     * Fonction qui vérifie si le billet gagne un prix 
     * @param billet --> billet qu'on vérifie 
     * @return --> la catégorie du prix gagner 
     */
    public int getPrize(Billet billet) {
        billetLock.lock();
        try {
            ArrayList<Integer> billetNumbers = billet.getNombres();
            int countWinningNumbers = 0;

            for (Integer winningNumber : chiffresGagnants) {
                if (billetNumbers.contains(winningNumber)) {
                    countWinningNumbers++;
                }
            }

            int additionalWinningNumbers = countWinningNumbers - (t-1);

            if (additionalWinningNumbers >= 0) {
                billet.setPrix(additionalWinningNumbers);
                return additionalWinningNumbers;
            }

            return 0;
        } finally {
            billetLock.unlock();
        }
    }

    /**
     * Fonction qui genère les numéros gagnants
     * @return --> la liste des numéros gagnants 
     */
    private ArrayList<Integer> generateRandomWinningNumbers() {
        ArrayList<Integer> winningNumbers = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < k; i++) {
            int randomNumber = random.nextInt(n + 1);
            winningNumbers.add(randomNumber);
        }

        return winningNumbers;
    }

    /**
     * Fonction qui affiche les billets gagnants avec toutes les informations dessus ainsi que les numéros gagnants 
     */
    public void afficherBilletsGagnants() {
        System.out.println("Billets gagnants Catégorie 1 :");
        for (int i = 1; i <= nbBilletsCat1; i++) {
            Billet billetCat1 = recupererBillet1(Integer.toString(i));
            if (billetCat1 != null && getPrize(billetCat1) > 0) {
                System.out.println(billetCat1);
            }
        }

        System.out.println("Billets gagnants Catégorie 2 :");
        for (int i = 1; i <= nbBilletsCat2; i++) {
            Billet billetCat2 = recupererBillet2(Integer.toString(i));
            if (billetCat2 != null && getPrize(billetCat2) > 0) {
                System.out.println(billetCat2);
            }
        }
        System.out.println("Numéros gagnants : " + chiffresGagnants);
    }

    /**
     * Fonction qui incrémente le nombre de joueurs
     */
    private void incrementNbJoueurs() {
        nbJoueursLock.lock();
        try {
            nbJoueurs++;
        } finally {
            nbJoueursLock.unlock();
        }
    }

    /**
     * Fonction qui récupère le nombre de joueurs
     * @return --> le nombre de joueurs
     */
    public int getNbJoueurs() {
        nbJoueursLock.lock();
        try {
            return nbJoueurs;
        } finally {
            nbJoueursLock.unlock();
        }
    }

    public static void main(String[] args) {
        //Initialisation de la lotterie
        Scanner scanner = new Scanner(System.in);
        System.out.print("Veuillez saisir N : ");
        while (!scanner.hasNextInt()) {
            System.out.print("N doit être un entier : ");
            scanner.next();
        }
        int ni = scanner.nextInt();

        System.out.print("Veuillez saisir la longueur K des tickets : ");
        while (!scanner.hasNextInt()) {
            System.out.print("K doit être un entier : ");
            scanner.next();
        }
        int ki = scanner.nextInt();
        
        System.out.print("Veuillez saisir T le nombre minimum de chiffres gagnants : ");
        while (!scanner.hasNextInt()) {
            System.out.print("T doit être un entier : ");
            scanner.next();
        }
        int ti = scanner.nextInt();

        //Initialisation du Timer
        System.out.print("Veuillez saisir D la durée en seconde voulu : ");
        while (!scanner.hasNextInt()) {
            System.out.print("D doit être un entier : ");
            scanner.next();
        }
        int di = scanner.nextInt();
        LotterieServer lotterieServer = new LotterieServer(ni, ki, ti);
        Joueur j = new Joueur(0);
        long dureeEnMillisecondes = di*1000;
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        //Vente des billets de catégorie 1 et 2
        scheduler.scheduleAtFixedRate(() -> {
            for (int i = 0; i < 4; i++) {
                lotterieServer.VenteBillet1(new Joueur(lotterieServer.getNbJoueurs()));
                lotterieServer.incrementNbJoueurs();
            } 
            lotterieServer.VenteBillet2(j);
        }, 0, 100, TimeUnit.MILLISECONDS);


        scheduler.schedule(() -> {
            //Récupération et vérification de tous les billets de la catégorie 1
            String dossierPath = "SaveBillets/Cat1";
            File dossier = new File(dossierPath);
            int totalPrizeCat1 = 0;
            int totalWinningTicketsCat1 = 0;

            if (dossier.isDirectory()) {
                File[] fichiers = dossier.listFiles();
                if (fichiers != null) {
                    for (File fichier : fichiers) {
                        Billet billetCat1 = lotterieServer.recupererBillet1(fichier.getName());
            
                        if (billetCat1 != null) {
                            int prizeCat1 = lotterieServer.getPrize(billetCat1);
            
                            if (prizeCat1 > 0) {
                                totalWinningTicketsCat1++;
                                totalPrizeCat1 += prizeCat1;
                            }
                        }
                    }
                } else {
                    System.out.println("Erreur lors de la récupération de la liste des fichiers.");
                }
            } else {
                System.out.println("Le chemin spécifié ne correspond pas à un dossier.");
            }

            //Récupération et vérification de tous les billets de la catégorie 2
            String dossierPathCat2 = "SaveBillets/Cat2";
            File dossierCat2 = new File(dossierPathCat2);
            int totalPrizeCat2 = 0;
            int totalWinningTicketsCat2 = 0;
            if (dossierCat2.isDirectory()) {
                File[] fichiersCat2 = dossierCat2.listFiles();
            
                if (fichiersCat2 != null) {
                    for (File fichierCat2 : fichiersCat2) {
                        Billet billetCat2 = lotterieServer.recupererBillet2(fichierCat2.getName());
            
                        if (billetCat2 != null) {
                            int prizeCat2 = lotterieServer.getPrize(billetCat2);
            
                            if (prizeCat2 > 0) {
                                totalWinningTicketsCat2++;
                                totalPrizeCat2 += prizeCat2;
                            }
                        }
                    }
                } else {
                    System.out.println("Erreur lors de la récupération de la liste des fichiers.");
                }
            }
            else {
                System.out.println("Le chemin spécifié ne correspond pas à un dossier.");
            }

            // Affiche les statistiques
            System.out.println("Nombre total de billets gagnants Catégorie 1 : " + totalWinningTicketsCat1);
            System.out.println("Prix total attribué Catégorie 1 : " + totalPrizeCat1);

            System.out.println("Nombre total de billets gagnants Catégorie 2 : " + totalWinningTicketsCat2);
            System.out.println("Prix total attribué Catégorie 2 : " + totalPrizeCat2);

            // Affiche tout les billets gagnants de chaque catégories à la fin
            System.out.println("Billets gagnants Catégorie 1 :");
            String dossierPathCat1 = "SaveBillets/Cat1";
            File dossierCat1 = new File(dossierPathCat1);
            if (dossierCat1.isDirectory()) {
                File[] fichiersCat1 = dossierCat1.listFiles();
                if (fichiersCat1 != null) {
                    for (File fichierCat1 : fichiersCat1) {
                        Billet billetCat1 = lotterieServer.recupererBillet1(fichierCat1.getName());
                        if (billetCat1 != null && lotterieServer.getPrize(billetCat1) > 0) {
                            System.out.println(billetCat1);
                        }
                    }
                } else {
                    System.out.println("Erreur lors de la récupération de la liste des fichiers.");
                }
            } else {
                System.out.println("Le chemin spécifié ne correspond pas à un dossier.");
            }

            System.out.println("Billets gagnants Catégorie 2 :");
            dossierPathCat2 = "SaveBillets/Cat2";
            dossierCat2 = new File(dossierPathCat2);
            if (dossierCat2.isDirectory()) {
                File[] fichiersCat2 = dossierCat2.listFiles();
                if (fichiersCat2 != null) {
                    for (File fichierCat2 : fichiersCat2) {
                        Billet billetCat2 = lotterieServer.recupererBillet2(fichierCat2.getName());
                        if (billetCat2 != null && lotterieServer.getPrize(billetCat2) > 0) {
                            System.out.println(billetCat2);
                        }
                    }
                } else {
                    System.out.println("Erreur lors de la récupération de la liste des fichiers.");
                }
            } else {
                System.out.println("Le chemin spécifié ne correspond pas à un dossier.");
            }

            // Affiche les numéros gagnants
            System.out.println("Numéros gagnants : " + lotterieServer.getChiffresGagnants());
            scheduler.shutdown();
            System. exit(0);
        }, dureeEnMillisecondes, TimeUnit.MILLISECONDS);
    }

}