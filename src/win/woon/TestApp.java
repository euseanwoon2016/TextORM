package win.woon;

import java.util.ArrayList;

public class TestApp {
    public static void main(String[] args) {
        // seedVaccines();

        ArrayList<Vaccine> vaccines = TextORM.getAll(Vaccine.class, stringObjectHashMap -> Integer.parseInt(stringObjectHashMap.get("daysBetweenDoses")) <= 30);

        if (vaccines != null) {
            for (Vaccine vaccine : vaccines) {
                System.out.printf("[%d] %s, %d day(s) between doses. Finished: %b%n", vaccine.id, vaccine.vaccineName, vaccine.daysBetweenDoses, vaccine.isFinished);
            }
        }
    }

    static void seedVaccines() {
        new Vaccine(1, "Sinovac", 21, false).save();
        new Vaccine(2, "Moderna", 14, true).save();
        new Vaccine(3, "Pfizer", 14, false).save();
        new Vaccine(4, "Sinopharm", 14, false).save();
        new Vaccine(5, "Johnson&Johnson", 21, false).save();
    }
}
