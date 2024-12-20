package org.cyberrealm.tech.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@Setter
@SQLDelete(sql = "UPDATE addresses SET is_deleted = true WHERE id=?")
@SQLRestriction("is_deleted=false")
@Table(name = "addresses", uniqueConstraints = {@UniqueConstraint(
        columnNames = {"country", "city", "state", "street", "house_number", "postal_code"})
})
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String country;
    @Column(nullable = false)
    private String city;
    @Column(nullable = false)
    private String state;
    @Column(nullable = false)
    private String street;
    @Column(nullable = false)
    private String houseNumber;
    @Column(nullable = false)
    private String postalCode;
    @Column(nullable = false)
    private boolean isDeleted = false;
}
