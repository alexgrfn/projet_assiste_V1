package com.projet_assiste_test;
import java.util.*;
import java.util.stream.Collectors;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

// ALEXANDRE GARAFFINI G5

// NOTES IMPORTANTES

// Lien vers le répositoire github : https://github.com/alexgrfn/projet_assiste_V1
// Comme vous pouvez le constater, mon fichier a une structure Maven.
// Sur certaines fonctions j'ai rajouté plusieurs fonctionnalités qui me semblaient intéressantes pour le modèle.
// J'ai ajouté le test Unit dans le fichier FlightSystemTests.java ; le test API dans FlightSearchAPI et
// le test de la connexion à la BDD dans TestConnexionBDD.java




// CLASSE PERSONNE

abstract class Personne {
    protected int identifiant;
    protected String nom;
    protected String adresse;
    protected String contact;

    public Personne(int identifiant, String nom, String adresse, String contact) {
        this.identifiant = identifiant;
        this.nom = nom;
        this.adresse = adresse;
        this.contact = contact;
    }

    public String obtenirInfos() {
        return identifiant + " - " + nom + " - " + adresse + " - " + contact;
    }
}

// CLASSE EMPLOYE


class Employe extends Personne {
    protected int numeroEmploye;
    protected Date dateEmbauche;

    public Employe(int identifiant, String nom, String adresse, String contact, int numeroEmploye, Date dateEmbauche) {
        super(identifiant, nom, adresse, contact);
        this.numeroEmploye = numeroEmploye;
        this.dateEmbauche = dateEmbauche;
    }

    public String obtenirRole() {
        return this.getClass().getSimpleName();
    }
}

// CLASSE PILOTE

class Pilote extends Employe {
    private String licence;
    private int heuresDeVol;

    public Pilote(int identifiant, String nom, String adresse, String contact, int numeroEmploye, Date dateEmbauche, String licence, int heuresDeVol) {
        super(identifiant, nom, adresse, contact, numeroEmploye, dateEmbauche);
        this.licence = licence;
        this.heuresDeVol = heuresDeVol;
    }

    public void affecterVol(Vol vol) {
        vol.setPilote(this);
    }

    public String obtenirVol() {
        return "Vol affecté au pilote: " + nom;
    }
}


// CLASSE PERSONNEL CABINE


class PersonnelCabine extends Employe {
    private String qualification;

    public PersonnelCabine(int identifiant, String nom, String adresse, String contact, int numeroEmploye, Date dateEmbauche, String qualification) {
        super(identifiant, nom, adresse, contact, numeroEmploye, dateEmbauche);
        this.qualification = qualification;
    }

    public void affecterVol(Vol vol) {
        vol.ajouterCabinCrew(this);
    }

    public String obtenirVol() {
        return "Vol affecté au personnel cabine: " + nom;
    }
}

// CLASSE PASSAGER


class Passager extends Personne {
    private String passeport;
    private List<Reservation> reservations = new ArrayList<>();

    public Passager(int identifiant, String nom, String adresse, String contact, String passeport) {
        super(identifiant, nom, adresse, contact);
        this.passeport = passeport;
    }

    public void reserverVol(Vol vol) {
        if (vol.aDesPlacesDisponibles()) {
            Reservation res = new Reservation(UUID.randomUUID().toString(), new Date(), "Confirmée", this, vol);
            reservations.add(res);
            vol.ajouterReservation(res);
        } else {
            System.out.println("❌ Le vol " + vol.getNumeroVol() + " est complet !");
        }
    }

    public void annulerReservation(String numeroReservation) {
        reservations.removeIf(r -> r.getNumeroReservation().equals(numeroReservation));
    }

    public List<Reservation> obtenirReservations() {
        return reservations;
    }

    public String getNom() {
        return nom;
    }
}


// CLASSE RESERVATION


class Reservation {
    private String numeroReservation;
    private Date dateReservation;
    private String statut;
    private Passager passager;
    private Vol vol;

    public Reservation(String numeroReservation, Date dateReservation, String statut, Passager passager, Vol vol) {
        this.numeroReservation = numeroReservation;
        this.dateReservation = dateReservation;
        this.statut = statut;
        this.passager = passager;
        this.vol = vol;
    }

    public void confirmerReservation() {
        statut = "Confirmée";
    }

    public void annulerReservation() {
        statut = "Annulée";
    }

    public void modifierReservation(Date nouvelleDate) {
        this.dateReservation = nouvelleDate;
    }

    public String getNumeroReservation() {
        return numeroReservation;
    }

    public String getStatut() {
        return statut;
    }

    public Vol getVol() {
        return vol;
    }

    public Passager getPassager() {
        return passager;
    }
}


// CLASSE AVION


class Avion {
    private String immatriculation;
    private String modele;
    private int capacite;
    private List<Vol> vols = new ArrayList<>();

    public Avion(String immatriculation, String modele, int capacite) {
        this.immatriculation = immatriculation;
        this.modele = modele;
        this.capacite = capacite;
    }

    public void affecterVol(Vol vol) {
        if (verifierDisponibilite(vol)) {
            vols.add(vol);
            vol.setAvion(this);
        }
    }

    public boolean verifierDisponibilite(Vol vol) {
        return vols.stream().noneMatch(v -> v.getDateHeureDepart().equals(vol.getDateHeureDepart()));
    }

    public int getCapacite() {
        return capacite;
    }
}


// CLASSE VOL

class Vol {
    private String numeroVol;
    private String origine;
    private String destination;
    private Date dateHeureDepart;
    private Date dateHeureArrivee;
    private String etat;
    private Pilote pilote;
    private List<PersonnelCabine> cabine = new ArrayList<>();
    private List<Reservation> reservations = new ArrayList<>();
    private Avion avion;

    public Vol(String numeroVol, String origine, String destination, Date depart, Date arrivee) {
        this.numeroVol = numeroVol;
        this.origine = origine;
        this.destination = destination;
        this.dateHeureDepart = depart;
        this.dateHeureArrivee = arrivee;
        this.etat = "Planifié";
    }

    public void ajouterReservation(Reservation reservation) {
        reservations.add(reservation);
    }

    public void setPilote(Pilote pilote) {
        this.pilote = pilote;
    }

    public void ajouterCabinCrew(PersonnelCabine p) {
        cabine.add(p);
    }

    public void setAvion(Avion avion) {
        this.avion = avion;
    }

    public void annulerVol() {
        etat = "Annulé";
    }

    public void planifierVol() {
        etat = "Planifié";
    }

    public String obtenirVol() {
        return numeroVol + ": " + origine + " → " + destination + " - " + etat;
    }

    public Date getDateHeureDepart() {
        return dateHeureDepart;
    }

    public String getDestination() {
        return destination;
    }

    public String getNumeroVol() {
        return numeroVol;
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    public boolean aDesPlacesDisponibles() {
        return avion != null && reservations.size() < avion.getCapacite();
    }

    public void listerPassagers() {
        System.out.println("📋 Passagers du vol " + numeroVol + " :");
        for (Reservation res : reservations) {
            System.out.println("- " + res.getPassager().getNom());
        }
    }
}

// CLASSE AEROPORT


class Aeroport {
    private String nom;
    private String ville;
    private String description;
    private List<Vol> vols = new ArrayList<>();

    public Aeroport(String nom, String ville, String description) {
        this.nom = nom;
        this.ville = ville;
        this.description = description;
    }

    public void affecterVol(Vol vol) {
        vols.add(vol);
    }
}

// CLASSE STATISTIQUES (BONUS)


class Statistiques {
    public static void genererRapportVols(List<Vol> vols, List<Reservation> reservations) {
        System.out.println("\n========= RAPPORT STATISTIQUES =========");
        System.out.println("Nombre total de vols : " + vols.size());

        long totalPassagers = reservations.stream()
                .filter(res -> res.getStatut().equalsIgnoreCase("confirmée"))
                .count();
        System.out.println("Nombre total de passagers transportés : " + totalPassagers);

        double revenus = totalPassagers * 150.0;
        System.out.println("Revenus générés (estimés) : " + revenus + " €");

        afficherDestinationsPopulaires(reservations);
    }

    public static void afficherDestinationsPopulaires(List<Reservation> reservations) {
        Map<String, Long> compteur = reservations.stream()
                .filter(res -> res.getStatut().equalsIgnoreCase("confirmée"))
                .map(res -> res.getVol().getDestination())
                .collect(Collectors.groupingBy(dest -> dest, Collectors.counting()));

        System.out.println("\nDestinations les plus populaires :");
        compteur.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .forEach(entry -> System.out.println("- " + entry.getKey() + " : " + entry.getValue() + " réservations"));
    }

    public static void annulerVolsSansReservations(List<Vol> vols) {
        Date maintenant = new Date();
        for (Vol vol : vols) {
            long heuresRestantes = (vol.getDateHeureDepart().getTime() - maintenant.getTime()) / (1000 * 60 * 60);
            if (heuresRestantes <= 24 && vol.getReservations().isEmpty()) {
                vol.annulerVol();
                System.out.println("⚠️ Vol " + vol.getNumeroVol() + " annulé (aucune réservation à H-24)");
            }
        }
    }

    // POUR METTRE DES VALEURS DANS UN FICHIER CSV PUIS DANS UNE BDD
    class FichierCSV {

        public static List<Vol> importerVolsDepuisCSV(String cheminFichier) {
            List<Vol> vols = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new FileReader(cheminFichier))) {
                String ligne;
                while ((ligne = reader.readLine()) != null) {
                    String[] elements = ligne.split(",");
                    String code = elements[0];
                    String dep = elements[1];
                    String arr = elements[2];
                    Date date = new Date(); // simpliﬁcation
                    vols.add(new Vol(code, dep, arr, date, date));
                }
            } catch (IOException e) {
                System.err.println("Erreur de lecture du fichier : " + e.getMessage());
            }
            return vols;
        }
    }

}


// CLASS MAIN POUR FAIRE LES TESTS DE FONCTIONS

public class Main {
    public static void main(String[] args) {
        Pilote pilote1 = new Pilote(1, "Jean Pilote", "Paris", "0600000001", 1001, new Date(), "LIC-001", 1200);
        PersonnelCabine cabine1 = new PersonnelCabine(2, "Claire Hôtesse", "Lyon", "0600000002", 2001, new Date(), "Sécurité");
        Avion avion1 = new Avion("F-ABCD", "Airbus A320", 2);

        Passager p1 = new Passager(3, "Alice", "Marseille", "0600000003", "PASS-001");
        Passager p2 = new Passager(4, "Bob", "Toulouse", "0600000004", "PASS-002");

        Date maintenant = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(maintenant);
        cal.add(Calendar.HOUR, 5);
        Date plusTard = cal.getTime();

        Vol vol1 = new Vol("AF101", "Paris", "New York", maintenant, plusTard);
        Vol vol2 = new Vol("AF102", "Paris", "Tokyo", maintenant, plusTard);

        pilote1.affecterVol(vol1);
        cabine1.affecterVol(vol1);
        avion1.affecterVol(vol1);

        p1.reserverVol(vol1);
        p2.reserverVol(vol1);
        p2.reserverVol(vol2);

        if (!p2.obtenirReservations().isEmpty()) {
            String numRes = p2.obtenirReservations().get(0).getNumeroReservation();
            p2.annulerReservation(numRes);
        }

        System.out.println("\n=== Passagers du vol ===");
        vol1.listerPassagers();

        System.out.println("\n=== Statistiques ===");
        List<Vol> tousLesVols = Arrays.asList(vol1, vol2);
        List<Reservation> toutesRes = new ArrayList<>();
        toutesRes.addAll(p1.obtenirReservations());
        toutesRes.addAll(p2.obtenirReservations());
        Statistiques.genererRapportVols(tousLesVols, toutesRes);

        System.out.println("\n=== Annulation automatique ===");
        Statistiques.annulerVolsSansReservations(tousLesVols);
    }
}




