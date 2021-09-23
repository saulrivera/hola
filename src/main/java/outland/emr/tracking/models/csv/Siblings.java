package outland.emr.tracking.models.csv;

import com.opencsv.bean.CsvBindByName;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Siblings {
    @CsvBindByName(column = "id")
    private Long gatewayId;
    @CsvBindByName(column = "siblings")
    private String siblings;

    public Long getGatewayId() {
        return gatewayId;
    }

    public void setGatewayId(Long gatewayId) {
        this.gatewayId = gatewayId;
    }

    public String getSiblings() {
        return siblings;
    }

    public void setSiblings(String siblings) {
        this.siblings = siblings;
    }

    public List<Long> getSiblingsList() {
        return Arrays.stream(this.siblings.split(","))
                .map(Long::valueOf)
                .collect(Collectors.toList());
    }
}
