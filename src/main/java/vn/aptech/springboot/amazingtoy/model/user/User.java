package vn.aptech.springboot.amazingtoy.model.user;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.sql.Date;
import java.util.Set;

@Getter
@Setter
@Accessors(chain = true)
@Entity
@Table(name = "users")
public class User extends BaseEntity {

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "email_confirmed")
    private boolean emailConfirmed;

    @Column(name = "password")
    private String password;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "phone_confirmed")
    private boolean phoneConfirmed;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "gender")
    public GenderType gender;

    @Column(name = "date_of_birth")
    public Date dateOfBirth;

    @Column(name = "profile_picture")
    public String profilePicture;

    @OneToOne()
    @JoinColumn(name = "address_id")
    public Address address;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;

    public User() {
    }

    public User(String email, boolean emailConfirmed, String password, String phoneNumber, boolean phoneConfirmed, String firstName, String lastName, GenderType gender, Date dateOfBirth, String profilePicture, boolean status, Address address, Set<Role> roles) {
        this.email = email;
        this.emailConfirmed = emailConfirmed;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.phoneConfirmed = phoneConfirmed;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.profilePicture = profilePicture;
        this.status = status;
        this.address = address;
        this.roles = roles;
    }

    public String getFullName() {
        return firstName != null ? firstName.concat(" ").concat(lastName) : "";
    }

    public enum GenderType {
        Male,
        Female,
        Other
    }
}
