package com.example.batch1.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BankTransaction {
    @Id
    private Long id;
    private Long accountId;
    @Transient
    private Date transactionDate;
    private String strTransactionDate;
    private String transactionType;
    private Double amount;
}
