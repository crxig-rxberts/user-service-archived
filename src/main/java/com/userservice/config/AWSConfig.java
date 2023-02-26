package com.userservice.config;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.rds.AmazonRDS;
import com.amazonaws.services.rds.AmazonRDSClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AWSConfig {

    @Bean
    public AWSCredentialsProvider awsCredentialsProvider() {
        return DefaultAWSCredentialsProviderChain.getInstance();
    }

    @Bean
    public AmazonEC2 amazonEC2(AWSCredentialsProvider awsCredentialsProvider) {
        return AmazonEC2ClientBuilder.standard()
                .withCredentials(awsCredentialsProvider)
                .build();
    }

    @Bean
    public AmazonRDS amazonRDS(AWSCredentialsProvider awsCredentialsProvider) {
        return AmazonRDSClientBuilder.standard()
                .withCredentials(awsCredentialsProvider)
                .build();
    }
}