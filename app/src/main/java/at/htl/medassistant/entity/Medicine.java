package at.htl.medassistant.entity;

public class Medicine {
    private long id;
    private String name;
    private String activeSubstance;
    private MedType medType;
    private int periodicityInDays;

    //region Constructors
    public Medicine(long id, String name, String activeSubstance, MedType medType, int periodicityInDays) {
        this.id = id;
        this.name = name;
        this.activeSubstance = activeSubstance;
        this.medType = medType;
        this.periodicityInDays = periodicityInDays;
    }

    public Medicine(String name, String activeSubstance, MedType medType, int periodicityInDays) {
        this.name = name;
        this.activeSubstance = activeSubstance;
        this.medType = medType;
        this.periodicityInDays = periodicityInDays;
    }

    public Medicine(String name, String activeSubstance, String medTypeString, int periodicityInDays) {
        this.name = name;
        this.activeSubstance = activeSubstance;
        setMedType(medTypeString);
        this.periodicityInDays = periodicityInDays;
    }
    //endregion

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
        if (!name.equals(medicine.name)) return false;
        return medType == medicine.medType;

    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + name.hashCode();
        result = 31 * result + medType.hashCode();
        return result;
    }
}
