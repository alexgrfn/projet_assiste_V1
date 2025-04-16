package com.projet_assiste_test;

import java.util.*;

import java.util.*;

// Classe de base Personne
class Personne {
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
        return "ID: " + identifiant + ", Nom: " + nom + ", Adresse: " + adresse + ", Contact: " + contact;
    }
}

// Classe Employe
class Employe extends Personne {
    protected int numeroEmploye;
    protected Date dateEmbauche;

    public Employe(int identifiant, String nom, String adresse, String contact, int numeroEmploye, Date dateEmbauche) {
        super(identifiant, nom, adresse, contact);
        this.numeroEmploye = numeroEmploye;
        this.dateEmbauche = dateEmbauche;
    }

    public String obtenirRole() {
        return "Employé";
    }
}

// Classe Pilote
class Pilote extends Employe {
    private String licence;
    private int heuresDeVol;

    public Pilote(int identifiant, String nom, String adresse, String contact, int numeroEmploye, Date dateEmbauche, String licence, int heuresDeVol) {
        super(identifiant, nom, adresse, contact, numeroEmploye, dateEmbauche);
        this.licence = licence;
        this.heuresDeVol = heuresDeVol;
    }

    @Override
    public String obtenirRole() {
        return "Pilote";
    }
}

// Classe PersonnelCabine
class PersonnelCabine extends Employe {
    private String qualification;

    public PersonnelCabine(int identifiant, String nom, String adresse, String contact, int numeroEmploye, Date dateEmbauche, String qualification) {
        super(identifiant, nom, adresse, contact, numeroEmploye, dateEmbauche);
        this.qualification = qualification;
    }

    @Override
    public String obtenirRole() {
        return "Personnel de Cabine";
    }
}

// Classe Passager
class Passager extends Personne {
    private String passeport;
    private List<Reservation> reservations;

    public Passager(int identifiant, String nom, String adresse, String contact, String passeport) {
        super(identifiant, nom, adresse, contact);
        this.passeport = passeport;
        this.reservations = new ArrayList<>();
    }

    public void reserverVol(Vol vol, int numeroReservation, Date dateReservation) {
        Reservation r = new Reservation(numeroReservation, dateReservation, "Confirmée", this, vol);
        reservations.add(r);
    }

    public void annulerReservation(int numeroReservation) {
        reservations.removeIf(r -> r.getNumeroReservation() == numeroReservation);
    }

    public List<Reservation> obtenirReservations() {
        return reservations;
    }
}

// Classe Reservation
class Reservation {
    private int numeroReservation;
    private Date dateReservation;
    private String statut;
    private Passager passager;
    private Vol vol;

    public Reservation(int numeroReservation, Date dateReservation, String statut, Passager passager, Vol vol) {
        this.numeroReservation = numeroReservation;
        this.dateReservation = dateReservation;
        this.statut = statut;
        this.passager = passager;
        this.vol = vol;
    }

    public int getNumeroReservation() {
        return numeroReservation;
    }

    public String getStatut() {
        return statut;
    }

    public void confirmerReservation() {
        this.statut = "Confirmée";
    }

    public void annulerReservation() {
        this.statut = "Annulée";
    }

    public void modifierReservation(Date nouvelleDate, Vol nouveauVol) {
        this.dateReservation = nouvelleDate;
        this.vol = nouveauVol;
    }
}

// Classe Vol (placeholder simplifié)
class Vol {
    private int numeroVol;

    public Vol(int numeroVol) {
        this.numeroVol = numeroVol;
    }

    public int getNumeroVol() {
        return numeroVol;
    }
}

