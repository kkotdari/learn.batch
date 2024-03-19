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
import org.springframework.batch.core.job.builder.SimpleJobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableBatchProcessing
public class BatchConfig {
    private static final List<TestType> typeList = Arrays.asList(TestType.TEST_1, TestType.TEST_2);
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
//    public DataSource dataSource() {
//        return DataSourceBuilder.create()
//                .url("jdbc:h2:mem:testdb")
//                .driverClassName("org.h2.Driver")
//                .username("sa")
//                .password("")
//                .build();
//    }
//
//    @Bean
//    public ResourceDatabasePopulator initializeDatabase() {
//        ResourceDatabasePopulator populator = new ResourceDatabasePopulator(new ClassPathResource("classpath:org/springframework/batch/core/schema-h2.sql"));
//        populator.execute(dataSource());
//        return populator;
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
    protected List<Step> getStep() {
        List<Step> stepList = new ArrayList<>();
        for (TestType t : typeList) {
            TaskletStep step = new StepBuilder((typeList.indexOf(t) + 2) + ". " + t.getValue() + " step", repository)
                    .tasklet((ctr, ctx) -> {
                        getTestService().setTestType(t);
                        getTestService().run();
                        return null;
                    }, manager)
                    .build();
            stepList.add(step);
        }
        return stepList;
    }

    @Bean
    public Job getJob() {
        JobBuilder builder = new JobBuilder("job", repository);
        SimpleJobBuilder start = builder.start(getStep().get(0));
        for (Step s : getStep()) {
            start.next(s);
        }
        return start.build();
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
