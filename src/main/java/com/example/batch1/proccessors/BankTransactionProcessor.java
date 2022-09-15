package com.example.batch1.proccessors;

import com.example.batch1.entity.BankTransaction;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;

@Component
@Qualifier("bankTransactionItemProcessor")
public class BankTransactionProcessor implements ItemProcessor<BankTransaction, BankTransaction> {
    @Override
    public BankTransaction process(BankTransaction bankTransaction) throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy-HH:mm");
        bankTransaction.setTransactionDate(dateFormat.parse(bankTransaction.getStrTransactionDate()));
        return bankTransaction;
    }
}
