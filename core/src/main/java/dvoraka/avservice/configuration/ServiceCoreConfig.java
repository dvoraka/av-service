package dvoraka.avservice.configuration;

import dvoraka.avservice.AvCheckMessageProcessor;
import dvoraka.avservice.CompositeMessageProcessor;
import dvoraka.avservice.FileMessageProcessor;
import dvoraka.avservice.MessageProcessor;
import dvoraka.avservice.ProcessorConfiguration;
import dvoraka.avservice.avprogram.AvProgram;
import dvoraka.avservice.common.Utils;
import dvoraka.avservice.common.data.AvMessage;
import dvoraka.avservice.common.data.MessageType;
import dvoraka.avservice.db.service.MessageInfoService;
import dvoraka.avservice.service.AvService;
import dvoraka.avservice.service.DefaultAvService;
import dvoraka.avservice.storage.service.FileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.context.annotation.Profile;
import org.springframework.jmx.export.MBeanExporter;
import org.springframework.jmx.export.annotation.AnnotationMBeanExporter;
import org.springframework.jmx.support.RegistrationPolicy;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;

/**
 * Core configuration
 */
@Configuration
@EnableMBeanExport
@Profile("core")
public class ServiceCoreConfig {

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
    public AvService avService(AvProgram avProgram) {
        return new DefaultAvService(avProgram);
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
        BiPredicate<? super AvMessage, ? super AvMessage> typeAndCheck = (original, last) -> {
            MessageType origType = original.getType();

            return (origType == MessageType.FILE_SAVE || origType == MessageType.FILE_UPDATE)
                    && last.getVirusInfo().equals(Utils.OK_VIRUS_INFO);
        };

        List<BiPredicate<? super AvMessage, ? super AvMessage>> fileConditions = new ArrayList<>();
        fileConditions.add(typeAndCheck);

        ProcessorConfiguration checkConfig = new ProcessorConfiguration(checkMessageProcessor);
        ProcessorConfiguration fileConfig = new ProcessorConfiguration(
                fileMessageProcessor,
                fileConditions,
                true
        );

        CompositeMessageProcessor processor = new CompositeMessageProcessor();
        processor.addProcessor(checkConfig);
        processor.addProcessor(fileConfig);

        processor.start();

        return processor;
    }
}
