package fk.retail.ip.requirement.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.bind.annotation.XmlRootElement;

import fk.retail.ip.requirement.internal.entities.Requirement;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by nidhigupta.m on 26/01/17.
 */

@XmlRootElement
@Getter
@Setter
public class RequirementDownloadLineItem {

    private String fsn;
    private String warehouse;
    private int pvBand;
    private int salesBand;
    @JsonProperty("salesBucket-0")
    private Integer week0Sale;
    @JsonProperty("salesBucket-1")
    private Integer week1Sale;
    @JsonProperty("salesBucket-2")
    private Integer week2Sale;
    @JsonProperty("salesBucket-3")
    private Integer week3Sale;
    @JsonProperty("salesBucket-4")
    private Integer week4Sale;
    @JsonProperty("salesBucket-5")
    private Integer week5Sale;
    @JsonProperty("salesBucket-6")
    private Integer week6Sale;
    @JsonProperty("salesBucket-7")
    private Integer week7Sale;


    public RequirementDownloadLineItem(Requirement req) {
        this.fsn = req.getFsn();
    }


}
