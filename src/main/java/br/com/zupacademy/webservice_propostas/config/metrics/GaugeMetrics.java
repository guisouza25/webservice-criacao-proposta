package br.com.zupacademy.webservice_propostas.config.metrics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

import org.springframework.stereotype.Component;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;

@Component
public class GaugeMetrics {

    private final MeterRegistry meterRegistry;

    private final Collection<String> strings = new ArrayList<>();

    private final Random random = new Random();

    public GaugeMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        criarGauge();
    }

    public void criarGauge() {
        Collection<Tag> tags = new ArrayList<>();
        tags.add(Tag.of("emissora", "Mastercard"));
        tags.add(Tag.of("banco", "Itaú"));

        this.meterRegistry.gauge("meu_gauge", tags, strings, Collection::size);
    }
    
    public void removeString() {
        strings.removeIf(Objects::nonNull);
    }

    public void addString() {
        strings.add(UUID.randomUUID().toString());
    }
    
    //@Scheduled(fixedDelay = 1000)
    public void simulandoGauge() {
        double randomNumber = random.nextInt();
        if (randomNumber % 2 == 0) {
            addString();
        } else {
        	removeString();
        }
    }

}