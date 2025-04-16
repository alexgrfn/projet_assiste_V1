package com.projet_assiste_test;

import java.util.*;

// Lien vers le répositoire github.
// Comme vous pouvez le constater, mon fichier a une structure Maven.
// https://github.com/alexgrfn/projet_assiste_V1

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
}

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
}

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
        Passager p1 = new Passager(1, "Alice", "Paris", "0600000000", "P12345");
        Vol vol1 = new Vol("AF001", "Paris", "Tokyo", new Date(), new Date());

        p1.reserverVol(vol1);
        p1.obtenirReservations().forEach(r -> System.out.println(r.getNumeroReservation()));
    }
}

