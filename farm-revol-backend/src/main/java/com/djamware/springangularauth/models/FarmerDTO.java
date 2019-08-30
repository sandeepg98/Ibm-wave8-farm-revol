package com.djamware.springangularauth.models;

public class FarmerDTO {
    private String email;
    private String password;
    private String role;

    @Override
    public String toString() {
        return "FarmerDTO{" +
                "email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", role='" + role + '\'' +
                '}';
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = "farmer";
    }

    public FarmerDTO(String email, String password, String role) {
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public FarmerDTO() {

    }
}
