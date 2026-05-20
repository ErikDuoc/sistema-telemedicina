package cl.duoc.fullstack.patientservice.model;

public enum UserRole {
    PATIENT("Paciente"),
    DOCTOR("Médico"),
    ADMIN("Administrador");

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
