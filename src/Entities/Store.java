package Entities;

import Connections.Database;

public class Store {
    private final double totalCash;
    private final double administrationFunds;
    private final double salariesFunds;
    private final double miscellaneousFunds;
    private final double developmentFunds;
    private final Database database;


    public Store(Database database) {
        this.database = database;
        double totalCash = this.database.getTotalAmount();
        this.totalCash = totalCash;
        this.administrationFunds = totalCash * 0.27;
        this.salariesFunds = totalCash * 0.54;
        this.miscellaneousFunds = totalCash * 0.07;
        this.developmentFunds = totalCash - this.administrationFunds - this.salariesFunds - this.miscellaneousFunds;
    }

    public double getTotalCash() {
        return totalCash;
    }

    public double getAdministrationFunds() {
        return administrationFunds;
    }

    public double getMiscellaneousFunds() {
        return miscellaneousFunds;
    }

    public double getSalariesFunds() {
        return salariesFunds;
    }

    public double getDevelopmentFunds() {
        return developmentFunds;
    }
}
