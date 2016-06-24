package at.htl.medassistant.entity;

public class Medicine {
    private long id;
    private String name;
    private String activeSubstance;
    private MedType medType;
    private int periodicityInDays;


    //region Getter and Setter
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getActiveSubstance() {
        return activeSubstance;
    }

    public void setActiveSubstance(String activeSubstance) {
        this.activeSubstance = activeSubstance;
    }

    public MedType getMedType() {
        return medType;
    }

    public void setMedType(MedType medType) {
        this.medType = medType;
    }

    public void setMedType(String medTypeString) {
        this.medType = MedType.valueOf(medTypeString);
    }

    public int getPeriodicityInDays() {
        return periodicityInDays;
    }

    public void setPeriodicityInDays(int periodicityInDays) {
        this.periodicityInDays = periodicityInDays;
    }
//endregion


    public Medicine() {
    }

    public Medicine(String name, String activeSubstance, MedType medType, int periodicityInDays) {
        this.name = name;
        this.activeSubstance = activeSubstance;
        this.medType = medType;
        this.periodicityInDays = periodicityInDays;
    }

    public Medicine(String name, String activeSubstance, MedType medType) {
        this.name = name;
        this.activeSubstance = activeSubstance;
        this.medType = medType;
    }

    public Medicine(String name, String activeSubstance, String medType, int periodicityInDays) {
        this.name = name;
        this.activeSubstance = activeSubstance;
        if (medType != "") {
            this.medType = MedType.valueOf(medType);
        }
        this.periodicityInDays = periodicityInDays;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name);
        sb.append(" / ");
        sb.append(medType.name());

        if (activeSubstance != null) {
            sb.append(" (" + activeSubstance + ")");
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Medicine medicine = (Medicine) o;

        if (id != medicine.id) return false;
        if (periodicityInDays != medicine.periodicityInDays) return false;
        if (name != null ? !name.equals(medicine.name) : medicine.name != null) return false;
        if (activeSubstance != null ? !activeSubstance.equals(medicine.activeSubstance) : medicine.activeSubstance != null)
            return false;
        return medType == medicine.medType;

    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (activeSubstance != null ? activeSubstance.hashCode() : 0);
        result = 31 * result + (medType != null ? medType.hashCode() : 0);
        result = 31 * result + periodicityInDays;
        return result;
    }
}
