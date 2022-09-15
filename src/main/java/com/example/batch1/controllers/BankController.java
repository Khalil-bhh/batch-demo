package com.example.batch1.controllers;

import com.example.batch1.proccessors.BankTransactionAnalyticsProcessor;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/bank")
public class BankController {

    private final JobLauncher jobLauncher;
    private final Job job;
    private final BankTransactionAnalyticsProcessor analyticsProcessor;
    public BankController(JobLauncher jobLauncher, Job job, BankTransactionAnalyticsProcessor analyticsProcessor) {
        this.jobLauncher = jobLauncher;
        this.job = job;
        this.analyticsProcessor = analyticsProcessor;
    }

    @GetMapping("start-job")
    public BatchStatus load() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        Map<String, JobParameter> parameters = new HashMap<>();
        parameters.put("time", new JobParameter(System.currentTimeMillis()));
        JobParameters jobParameters = new JobParameters(parameters);
        JobExecution jobExecution = jobLauncher.run(job,jobParameters);
        while (jobExecution.isRunning()){
            System.out.println(".......");
        }
        return jobExecution.getStatus();
    }
    @GetMapping("/analytics")
    public Map<String, Double> analytics(){
        Map<String,Double> map = new HashMap<>();
        map.put("totalCredit", analyticsProcessor.getTotalCredit());
        map.put("totalDebit", analyticsProcessor.getTotalDebit());
        return map;
    }
}
