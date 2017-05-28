package fk.retail.ip.segmentation.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by nidhigupta.m on 05/05/17.
 */

@XmlRootElement
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupDownloadLineItem {
    @JsonProperty("FSN")
    private String fsn;
}
