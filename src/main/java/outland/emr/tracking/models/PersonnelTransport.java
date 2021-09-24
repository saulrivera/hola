package outland.emr.tracking.models;

public class PersonnelTransport {
    private int nurses;
    private int providers;
    private int patients;
    private int hospitality;
    private int assets;

    public PersonnelTransport() {}

    public PersonnelTransport(int nurses, int providers, int patients, int hospitality, int assets) {
        this.nurses = nurses;
        this.providers = providers;
        this.patients = patients;
        this.hospitality = hospitality;
        this.assets = assets;
    }

    public int getNurses() {
        return nurses;
    }

    public void setNurses(int nurses) {
        this.nurses = nurses;
    }

    public int getProviders() {
        return providers;
    }

    public void setProviders(int providers) {
        this.providers = providers;
    }

    public int getPatients() {
        return patients;
    }

    public void setPatients(int patients) {
        this.patients = patients;
    }

    public int getHospitality() {
        return hospitality;
    }

    public void setHospitality(int hospitality) {
        this.hospitality = hospitality;
    }

    public int getAssets() {
        return assets;
    }

    public void setAssets(int assets) {
        this.assets = assets;
    }
}
