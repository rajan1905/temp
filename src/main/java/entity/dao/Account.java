package entity.dao;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SelectBeforeUpdate;

import javax.persistence.*;
import javax.transaction.Transactional;
import java.io.Serializable;

@Table(name = "Account")
@Entity
@Getter
@Setter
@NoArgsConstructor
@Transactional
@SelectBeforeUpdate
public class Account implements Serializable {

    @Column(name="FNAME",length=100,nullable=false)
    String firstName;

    @Column(name="MNAME",length=100)
    String middleName;

    @Column(name="LNAME",length=100,nullable=false)
    String lastName;

    @Id
    @Column(name="ACCOUNTNO",nullable=false)
    Long accountNo;

    @Column(name="BALANCE",nullable=false)
    Double balance;
}
