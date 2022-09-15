package com.example.batch1.writers;

import com.example.batch1.repositories.BankTransactionRepository;
import com.example.batch1.entity.BankTransaction;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BankTransactionItemWriter implements ItemWriter<BankTransaction> {
    private final BankTransactionRepository bankTransactionRepository;

    public BankTransactionItemWriter(BankTransactionRepository bankTransactionRepository) {
        this.bankTransactionRepository = bankTransactionRepository;
    }

    @Override
    public void write(List<? extends BankTransaction> list) throws Exception {
        bankTransactionRepository.saveAll(list);
    }
}
