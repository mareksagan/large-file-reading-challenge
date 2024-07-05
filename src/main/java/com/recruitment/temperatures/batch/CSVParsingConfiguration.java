package com.recruitment.temperatures.batch;

import com.recruitment.temperatures.RecruitmentChallengeCSVProperties;
import com.recruitment.temperatures.models.TemperatureModel;
import com.recruitment.temperatures.temperatures.jpa.Temperature;
import com.recruitment.temperatures.temperatures.jpa.TemperatureRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.text.SimpleDateFormat;

/**
 * Defines batch Job for the initial csv sync
 */
@Configuration
public class CSVParsingConfiguration {
    private final RecruitmentChallengeCSVProperties recruitmentChallengeCSVProperties;
    private final DataSource dataSource;
    static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    private final TemperatureRepository temperatureRepository;

    public CSVParsingConfiguration(RecruitmentChallengeCSVProperties recruitmentChallengeCSVProperties, DataSource dataSource, TemperatureRepository temperatureRepository) {
        this.recruitmentChallengeCSVProperties = recruitmentChallengeCSVProperties;
        this.dataSource = dataSource;
        this.temperatureRepository = temperatureRepository;
    }

    @Bean
    public FlatFileItemReader<TemperatureModel> reader() {
        FlatFileItemReader<TemperatureModel> reader = new FlatFileItemReader<>();
        reader.setResource(new FileSystemResource(recruitmentChallengeCSVProperties.getDirectory() + "/" + recruitmentChallengeCSVProperties.getFileName()));
        reader.setLinesToSkip(0);
        reader.setLineMapper(lineMapper());
        return reader;
    }

    private LineMapper<TemperatureModel> lineMapper() {
        DefaultLineMapper<TemperatureModel> defaultLineMapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();

        lineTokenizer.setDelimiter(recruitmentChallengeCSVProperties.getDelimiter());
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames("city", "date", "value");
        BeanWrapperFieldSetMapper<TemperatureModel> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(TemperatureModel.class);

        defaultLineMapper.setLineTokenizer(lineTokenizer);
        defaultLineMapper.setFieldSetMapper(fieldSetMapper);

        return defaultLineMapper;
    }
    @Bean
    public JdbcBatchItemWriter<Temperature> writer() {
        JdbcBatchItemWriter<Temperature> writer = new JdbcBatchItemWriter<>();
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        writer.setSql("INSERT INTO TEMPERATURES (ID,CITY,DATE,VALUE) " +
                "VALUES (:id,:city,:date,:value)");
        writer.setDataSource(dataSource);
        return writer;
    }
    @Bean
    public Job sampleJob(JobRepository jobRepository, Step sampleStep) {
        return new JobBuilder("CSVLoadJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(sampleStep)
                .build();
    }
    @Bean
    public ItemProcessor<TemperatureModel, Temperature> processor() {
        return val -> new Temperature(temperatureRepository.getNextSequence(), val.getCity(), simpleDateFormat.parse(val.getDate()), Double.parseDouble(val.getValue()));
    }
    @Bean
    public Step importData(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager) {
        return new StepBuilder("CSVLoadStep",jobRepository)
                .allowStartIfComplete(true)
                .<TemperatureModel,Temperature> chunk(100,platformTransactionManager)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .build();
    }
}
