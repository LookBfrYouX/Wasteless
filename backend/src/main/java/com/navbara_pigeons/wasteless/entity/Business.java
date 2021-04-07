package com.navbara_pigeons.wasteless.entity;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Data;
import lombok.ToString;

@Data
@Entity
@Table(name = "BUSINESS")
public class Business {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID")
  private long id;

  @Column(name = "PRIMARY_ADMINISTRATOR_ID")
  private long primaryAdministratorId;

  @Column(name = "NAME")
  private String name;

  @Column(name = "DESCRIPTION")
  private String description;

  @OneToOne(fetch = FetchType.EAGER, optional = false)
  @JoinColumn(name = "ADDRESS_ID", referencedColumnName = "ID")
  private Address address;

  @Column(name = "BUSINESS_TYPE")
  private String businessType;

  @Column(name = "CREATED")
  private String created;

  @ManyToMany(
      fetch = FetchType.EAGER,
      cascade = {
          CascadeType.DETACH,
          CascadeType.MERGE,
          CascadeType.PERSIST,
          CascadeType.REFRESH
      }
  )
  @JoinTable(
      name = "USER_BUSINESS",
      joinColumns = @JoinColumn(name = "BUSINESS_ID"),
      inverseJoinColumns = @JoinColumn(name = "USER_ID")
  )
  private List<User> administrators;

  /**
   * This is a helper method for adding a user to the business.
   *
   * @param administrator The user to be added.
   */
  public void addAdministrator(User administrator) {
    if (this.administrators == null) {
      this.administrators = new ArrayList<>();
    }
    this.administrators.add(administrator);
  }

}
