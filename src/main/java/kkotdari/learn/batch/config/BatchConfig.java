package kkotdari.learn.batch.config;

import kkotdari.learn.batch.service.TestService;
import kkotdari.learn.batch.service.TestSubService;
import kkotdari.learn.batch.type.TestType;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
public class BatchConfig {
    private final JobLauncher launcher;
    private final JobRepository repository;
    private final PlatformTransactionManager manager;

    @Autowired
    public BatchConfig(JobLauncher launcher, JobRepository repository, PlatformTransactionManager manager) {
        this.launcher = launcher;
        this.repository = repository;
        this.manager = manager;
    }

//    @Bean
//    public DataSource getH2DataSource() {
//        return new EmbeddedDatabaseBuilder()
//                .setType(EmbeddedDatabaseType.H2)
//                .addScript("classpath:org/springframework/batch/core/schema-h2.sql")
//                .build();
//    }

//    @Bean
//    public DataSource dataSource() {
//        return DataSourceBuilder.create()
//                .url("jdbc:h2:mem:testdb")
//                .driverClassName("org.h2.Driver")
//                .username("sa")
//                .password("")
//                .build();
//    }

    @Bean
    protected TestSubService getTestSubService() {
        return new TestSubService();
    }

    @Bean
    protected TestService getTestService() {
        return new TestService(getTestSubService());
    }

    @Bean
    protected Step getStep() {
        return new StepBuilder("step", repository)
                .tasklet((ctr, ctx) -> {
                    TestType[] values = {TestType.TEST_1, TestType.TEST_2};
                    for (TestType t : values) {
                        getTestService().setTestType(t);
                        getTestService().run();
                    }
                    return null;
                }, manager)
                .build();

    }

    @Bean
    public Job getJob() throws Exception {
        return new JobBuilder("job", repository)
                .start(getStep())
                .build();
    }

    public void runJob() {
        try {
            launcher.run(getJob(), new JobParameters());
        } catch (JobExecutionAlreadyRunningException e) {
            System.out.println("JobExecutionAlreadyRunning");
        } catch (JobRestartException e) {
            System.out.println("JobRestart");
        } catch (JobInstanceAlreadyCompleteException e) {
            System.out.println("JobInstanceAlreadyComplete");
        } catch (JobParametersInvalidException e) {
            System.out.println("JobParametersInvalid");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
