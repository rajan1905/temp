package entity.dao;

import lombok.Setter;

import javax.persistence.*;

@Table(name = "Transactions")
@Entity
@Setter
public class Transactions {

    @Id
    @GeneratedValue
    @Column(name="transactionNo",nullable=false)
    int transactionNo;

    @Column(name="TRNX_MSG",nullable=false)
    String transactionMessage;

    @Column(name="CR_FROM")
    Long crFrom;

    @Column(name="CR_TO")
    Long crTo;

    @Column(name="amount")
    Double amount;

}
