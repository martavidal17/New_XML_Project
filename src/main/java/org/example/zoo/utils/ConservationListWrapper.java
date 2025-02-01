package org.example.zoo.utils;

import org.example.zoo.model.ConservationStatistic;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "zoos")
public class ConservationListWrapper {
    private List<ConservationStatistic> statistics;

    @XmlElement(name = "conservation_statistic")
    public List<ConservationStatistic> getStatistics() {
        return statistics;
    }

    public void setStatistics(List<ConservationStatistic> statistics) {
        this.statistics = statistics;
    }
}
