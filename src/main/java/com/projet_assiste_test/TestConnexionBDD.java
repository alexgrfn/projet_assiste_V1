package com.projet_assiste_test;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// POUR LA CONNEXION AVEC LA BDD, DANS NOTRE CAS J'UTILISE POSTEGRESQL
// LA FONCTION MARCHE, NOUS DEVONS JUSTE SAISIR LE USERNAME ET MDP DE l'HOTE SUR SON APPLICATION POSTEGRE

public class TestConnexionBDD {
    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5432/projet_assiste_db";
        String user = ""; // METTRE SON USERNAME DE POSTEGRE
        String password = ""; // METTRE SON MDP DE POSTEGRE

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            System.out.println("Connexion réussie à la base PostgreSQL");
        } catch (SQLException e) {
            System.err.println("Erreur de connexion : " + e.getMessage());
        }
    }
}