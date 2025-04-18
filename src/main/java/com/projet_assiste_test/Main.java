package com.projet_assiste_test;
import java.util.*;
import java.util.stream.Collectors;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// ALEXANDRE GARAFFINI G5

// NOTES IMPORTANTES

// Lien vers le répositoire github : https://github.com/alexgrfn/projet_assiste_V1
// Comme vous pouvez le constater, mon fichier a une structure Maven.

// Projet : Système de Réservation de Vols



// Classe de base Personne

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

// Employe hérite de Personne

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

// Pilote hérite d'Employe

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

// PersonnelCabine hérite d'Employe
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

// Classe Passager hérite de Personne

class Passager extends Personne {
    private String passeport;
    private List<Reservation> reservations = new ArrayList<>();

    public Passager(int identifiant, String nom, String adresse, String contact, String passeport) {
        super(identifiant, nom, adresse, contact);
        this.passeport = passeport;
    }

    public void reserverVol(Vol vol) {
        Reservation res = new Reservation(UUID.randomUUID().toString(), new Date(), "Confirmée", this, vol);
        reservations.add(res);
        vol.ajouterReservation(res);
    }

    public void annulerReservation(String numeroReservation) {
        reservations.removeIf(r -> r.getNumeroReservation().equals(numeroReservation));
    }

    public List<Reservation> obtenirReservations() {
        return reservations;
    }
}

// Classe reservation

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
}

// Classe Avion

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
}

// Classe Vol

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

    public String getDestination(){
        return destination;
    }
}

// Classe Aeroport

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

// Exemple de point d'entrée Main

public class Main {
    public static void main(String[] args) {
        // Création des employés
        Pilote pilote1 = new Pilote(1, "Jean Pilote", "Paris", "0600000001", 1001, new Date(), "LIC-001", 1200);
        PersonnelCabine cabine1 = new PersonnelCabine(2, "Claire Hôtesse", "Lyon", "0600000002", 2001, new Date(), "Sécurité");

        // Création d'un avion
        Avion avion1 = new Avion("F-ABCD", "Airbus A320", 180);

        // Création de passagers
        Passager p1 = new Passager(3, "Alice", "Marseille", "0600000003", "PASS-001");
        Passager p2 = new Passager(4, "Bob", "Toulouse", "0600000004", "PASS-002");

        // Création de vols
        Date maintenant = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(maintenant);
        cal.add(Calendar.HOUR, 5);
        Date plusTard = cal.getTime();

        Vol vol1 = new Vol("AF101", "Paris", "New York", maintenant, plusTard);
        Vol vol2 = new Vol("AF102", "Paris", "Tokyo", maintenant, plusTard);

        // Affecter pilote et personnel
        pilote1.affecterVol(vol1);
        cabine1.affecterVol(vol1);

        // Affecter un avion à vol1
        avion1.affecterVol(vol1);

        // Passagers réservent
        p1.reserverVol(vol1);
        p2.reserverVol(vol1);
        p2.reserverVol(vol2);

        // Annuler une réservation
        if (!p2.obtenirReservations().isEmpty()) {
            String numRes = p2.obtenirReservations().get(0).getNumeroReservation();
            p2.annulerReservation(numRes);
        }
    }

}

// BONUS : CLASS STATISTIQUES

class Statistiques {

    // Génère un rapport simple sur les vols
    public static void genererRapportVols(List<Vol> vols, List<Reservation> reservations) {
        System.out.println("\n========= RAPPORT STATISTIQUES =========");

        System.out.println("Nombre total de vols : " + vols.size());

        long totalPassagers = reservations.stream()
                .filter(res -> res.getStatut().equalsIgnoreCase("confirmé"))
                .count();
        System.out.println("Nombre total de passagers transportés : " + totalPassagers);

        double revenus = totalPassagers * 150.0; // Supposons 150€ par billet
        System.out.println("Revenus générés (estimés) : " + revenus + " €");

        afficherDestinationsPopulaires(reservations);
    }

    // Affiche les destinations les plus populaires
    public static void afficherDestinationsPopulaires(List<Reservation> reservations) {
        Map<String, Long> compteur = reservations.stream()
                .filter(res -> res.getStatut().equalsIgnoreCase("confirmé"))
                .map(res -> res.getVol().getDestination())
                .collect(Collectors.groupingBy(dest -> dest, Collectors.counting()));

        System.out.println("\nDestinations les plus populaires :");
        compteur.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .forEach(entry -> System.out.println("- " + entry.getKey() + " : " + entry.getValue() + " réservations"));
    }
}
