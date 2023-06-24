package com.mdonerprojects.orderservices.deadlines.config;

import org.axonframework.config.Configuration;
import org.axonframework.config.ConfigurationScopeAwareProvider;
import org.axonframework.deadline.DeadlineManager;
import org.axonframework.deadline.SimpleDeadlineManager;
import org.axonframework.spring.messaging.unitofwork.SpringTransactionManager;
import org.springframework.context.annotation.Bean;

@org.springframework.context.annotation.Configuration
public class DeadlineConfig {
    @Bean
    public DeadlineManager deadlineManager(Configuration configuration,
                                           SpringTransactionManager springTransactionManager) {
        return SimpleDeadlineManager
                .builder()
                .scopeAwareProvider(new ConfigurationScopeAwareProvider(configuration))
                .transactionManager(springTransactionManager)
                .build();

    }
}
