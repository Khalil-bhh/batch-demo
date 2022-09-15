package com.example.batch1.config;

import com.example.batch1.entity.BankTransaction;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.util.ArrayList;
import java.util.List;


@Configuration
@EnableBatchProcessing
public class SpringBatchConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final ItemWriter<BankTransaction> bankTransactionItemWriter;
    private final ItemProcessor<BankTransaction,BankTransaction> bankTransactionItemProcessor;
    private final ItemProcessor<BankTransaction,BankTransaction> bankTransactionAnalyticsProcessor;
    @Value("${inputFile}")
    private String file;

    public SpringBatchConfig(JobBuilderFactory jobBuilderFactory,
                             StepBuilderFactory stepBuilderFactory,
                             ItemWriter<BankTransaction> bankTransactionItemWriter,
                             @Qualifier("bankTransactionItemProcessor") ItemProcessor<BankTransaction, BankTransaction> bankTransactionItemProcessor,
                             @Qualifier("bankTransactionAnalyticsProcessor") ItemProcessor<BankTransaction, BankTransaction> bankTransactionAnalyticsProcessor) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.bankTransactionItemWriter = bankTransactionItemWriter;
        this.bankTransactionItemProcessor = bankTransactionItemProcessor;
        this.bankTransactionAnalyticsProcessor = bankTransactionAnalyticsProcessor;
    }

    @Bean
    public Job bankJob(){
        Step step = stepBuilderFactory.get("step1")
                .<BankTransaction,BankTransaction>chunk(100)
                .reader(fileItemReader())
                .writer(bankTransactionItemWriter)
                .processor(compositeItemProcessor())
                .build();
        return jobBuilderFactory.get("bank-data-loader-job")
                .incrementer(new RunIdIncrementer())
                .start(step)
                .build();
    }

    @Bean
    public ItemProcessor<? super BankTransaction,? extends BankTransaction> compositeItemProcessor() {
        List<ItemProcessor<BankTransaction, BankTransaction>> itemProcessors = new ArrayList<>();
        itemProcessors.add(bankTransactionItemProcessor);
        itemProcessors.add(bankTransactionAnalyticsProcessor);
        CompositeItemProcessor<BankTransaction,BankTransaction> compositeItemProcessor = new CompositeItemProcessor<>();
        compositeItemProcessor.setDelegates(itemProcessors);
        return compositeItemProcessor;
    }
    
    @Bean
    public FlatFileItemReader<BankTransaction> fileItemReader(){
        FlatFileItemReader<BankTransaction> fileItemReader = new FlatFileItemReader<>();
        fileItemReader.setName("FILE1");
        fileItemReader.setLinesToSkip(1);
        fileItemReader.setResource(new ClassPathResource(file));
        fileItemReader.setLineMapper(lineMapper());
        return  fileItemReader;

    }

    @Bean
    public LineMapper<BankTransaction> lineMapper() {
        DefaultLineMapper<BankTransaction> lineMapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames("id","accountId","strTransactionDate","transactionType","amount");
        lineMapper.setLineTokenizer(lineTokenizer);
        BeanWrapperFieldSetMapper<BankTransaction> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(BankTransaction.class);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        return lineMapper;
    }
}
