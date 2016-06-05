package at.htl.medassistant.entity;


import android.util.Log;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import static junit.framework.Assert.fail;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class EntityUnitTest {


    private static SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

    @Test
    public void t010CreateUser() throws Exception {
        User dagobert = new User("Dagobert", "Duck");
        assertThat(dagobert.toString(),is("Duck, Dagobert"));
    }

    @Test
    public void t020CreateMedicine() {
        Medicine fsme = new Medicine("FSME-IMMUN Inject", "FSME-Virus-Antigen", MedType.VACCINE, 365*5);
        assertThat(fsme.toString(),is("FSME-IMMUN Inject / VACCINE (FSME-Virus-Antigen)"));
    }

    @Test
    public void t025SetMedTypeWithString() {
        Medicine fsme = new Medicine("FSME-IMMUN Inject", "FSME-Virus-Antigen", MedType.VACCINE, 365*5);
        fsme.setMedType("PHARMACEUTICAL");
        assertThat(fsme.getMedType(),is(MedType.PHARMACEUTICAL));
    }

    @Test
    public void t026ConstructorWithMedTypeWithString() {
        Medicine fsme = new Medicine("FSME-IMMUN Inject", "FSME-Virus-Antigen", "VACCINE", 365*5);
        assertThat(fsme.getMedType(),is(MedType.VACCINE));
    }

    @Test
    public void t027ConstructorWithMedTypeWithStringFail() {
        Medicine fsme = null;
        try {
            fsme = new Medicine("FSME-IMMUN Inject", "FSME-Virus-Antigen", "", 365*5);
        } catch (Exception e) {
            System.out.println("t027ConstructorWithMedTypeWithStringFail: " + e.getMessage() );
            assertThat(e.getMessage(),is("No enum constant at.htl.medassistant.entity.MedType."));
            assertThat(e.getClass().toString(),is("class java.lang.IllegalArgumentException"));
            return;
        }
        fail("Bei fehlerhaften MedType muss ein Fehler geworfen werden");
    }

    @Test
    public void t030CreateTreatmentForDagobert() throws ParseException {
        User dagobert = new User("Dagobert", "Duck");
        Medicine fsme = new Medicine("FSME-IMMUN Inject", "FSME-Virus-Antigen", MedType.VACCINE, 365*5);
        Treatment treatment = new Treatment(
                dagobert,
                fsme, sdf.parse("06.05.2016"),
                sdf.parse("31.12.2030"),
                Time.valueOf("12:00:00")
        );

        assertThat(
                treatment.toString(),
                is("Duck, Dagobert: FSME-IMMUN Inject, vom 06.05.2016 bis 31.12.2030, Time of Taking: 12:00")
        );
    }

    @Test
    public void t040CreateTreatmentForMickey() throws ParseException {
        User mickey = new User("Mickey", "Mouse");
        Medicine aspirin = new Medicine("Aspirin", "Acetylsalicylsäure", MedType.PHARMACEUTICAL, 7);
        Treatment treatment = new Treatment(
                mickey,
                aspirin, sdf.parse("07.05.2016"),
                sdf.parse("07.06.2016"),
                Time.valueOf("08:00:00")
        );

        assertThat(
                treatment.toString(),
                is("Mouse, Mickey: Aspirin, vom 07.05.2016 bis 07.06.2016, Time of Taking: 08:00")
        );
    }

    @Test
    public void t050SetTimeOfTakingOk() throws ParseException {
        User mickey = new User("Mickey", "Mouse");
        Medicine aspirin = new Medicine("Aspirin", "Acetylsalicylsäure", MedType.PHARMACEUTICAL, 7);
        Treatment treatment = new Treatment(
                mickey,
                aspirin, sdf.parse("07.05.2016"),
                sdf.parse("07.06.2016"),
                Time.valueOf("08:00:00")
        );

        boolean parsingOk = treatment.parseTimeOfTaking("09:15");
        assertThat(treatment.getTimeOfTakingToString(), is("09:15"));
        assertThat(parsingOk, is(true));
    }

    @Test
    public void t060SetTimeOfTakingFail() throws ParseException {
        User mickey = new User("Mickey", "Mouse");
        Medicine fsme = new Medicine("Aspirin", "Acetylsalicylsäure", MedType.PHARMACEUTICAL, 7);
        Treatment treatment = new Treatment(
                mickey,
                fsme, sdf.parse("07.05.2016"),
                sdf.parse("07.06.2016"),
                Time.valueOf("08:00:00")
        );

        boolean parsingOk = treatment.parseTimeOfTaking("25:69");
        assertThat(treatment.getTimeOfTakingToString(), is("08:00"));
        assertThat(parsingOk, is(false));
    }

    /**
     * zu prüfen wären noch
     *
     * - startDate liegt vor Enddate
     * - compareTo()-Methode
     *
     */

}