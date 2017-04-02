package dvoraka.avservice.configuration;

import dvoraka.avservice.AvCheckMessageProcessor;
import dvoraka.avservice.CompositeMessageProcessor;
import dvoraka.avservice.FileMessageProcessor;
import dvoraka.avservice.InputConditions;
import dvoraka.avservice.MessageProcessor;
import dvoraka.avservice.ProcessorConfiguration;
import dvoraka.avservice.avprogram.configuration.AvProgramConfig;
import dvoraka.avservice.avprogram.service.AvService;
import dvoraka.avservice.common.Utils;
import dvoraka.avservice.common.data.MessageType;
import dvoraka.avservice.db.configuration.DatabaseConfig;
import dvoraka.avservice.db.service.MessageInfoService;
import dvoraka.avservice.storage.configuration.StorageConfig;
import dvoraka.avservice.storage.service.FileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jmx.export.MBeanExporter;
import org.springframework.jmx.export.annotation.AnnotationMBeanExporter;
import org.springframework.jmx.support.RegistrationPolicy;

import java.util.List;

/**
 * Core Spring configuration.
 */
@Configuration
@PropertySource("classpath:avservice.properties")
@Profile("core")
@Import({
        // AV program
        AvProgramConfig.class,
        // DB
        DatabaseConfig.class,
        // Storage
        StorageConfig.class
})
public class CoreConfig {

    @Value("${avservice.cpuCores:2}")
    private Integer cpuCores;

    @Value("${avservice.serviceId:default1}")
    private String serviceId;


    @Bean
    public MBeanExporter mbeanExporter() {
        MBeanExporter exporter = new AnnotationMBeanExporter();
        exporter.setRegistrationPolicy(RegistrationPolicy.REPLACE_EXISTING);

        return exporter;
    }

    @Bean
    public MessageProcessor checkMessageProcessor(
            AvService avService,
            MessageInfoService messageInfoService
    ) {
        return new AvCheckMessageProcessor(
                cpuCores,
                serviceId,
                avService,
                messageInfoService);
    }

    @Bean
    @Profile("storage")
    public MessageProcessor fileMessageProcessor(FileService fileService) {
        return new FileMessageProcessor(fileService);
    }

    @Bean
    @Profile("storage")
    public MessageProcessor checkAndFileProcessor(
            MessageProcessor checkMessageProcessor,
            MessageProcessor fileMessageProcessor
    ) {
        List<InputConditions> checkConditions =
                new InputConditions.Builder()
                        .originalType(MessageType.REQUEST)
                        .originalType(MessageType.FILE_SAVE)
                        .originalType(MessageType.FILE_UPDATE)
                        .build().toList();

        List<InputConditions> fileSaveUpdateConditions =
                new InputConditions.Builder()
                        .originalType(MessageType.FILE_SAVE)
                        .originalType(MessageType.FILE_UPDATE)
                        .condition((orig, last) -> last.getVirusInfo() != null
                                && last.getVirusInfo().equals(Utils.OK_VIRUS_INFO))
                        .build().toList();

        List<InputConditions> fileLoadDeleteConditions =
                new InputConditions.Builder()
                        .originalType(MessageType.FILE_LOAD)
                        .originalType(MessageType.FILE_DELETE)
                        .build().toList();

        ProcessorConfiguration checkConfig = new ProcessorConfiguration(
                checkMessageProcessor,
                checkConditions,
                true
        );
        ProcessorConfiguration fileSaveUpdateConfig = new ProcessorConfiguration(
                fileMessageProcessor,
                fileSaveUpdateConditions,
                true
        );
        ProcessorConfiguration fileLoadDeleteConfig = new ProcessorConfiguration(
                fileMessageProcessor,
                fileLoadDeleteConditions,
                true
        );

        CompositeMessageProcessor processor = new CompositeMessageProcessor();
        processor.addProcessor(checkConfig);
        processor.addProcessor(fileSaveUpdateConfig);
        processor.addProcessor(fileLoadDeleteConfig);

        processor.start();

        return processor;
    }
}
