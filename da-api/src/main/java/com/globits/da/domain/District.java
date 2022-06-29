package com.globits.da.domain;

import com.globits.core.domain.BaseObject;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@Entity
@Table(name = "tbl_district")
@XmlRootElement
public class District extends BaseObject {
    @Column(name = "code")
    private String code;

    @Column(name = "name")
    private String name;

    @ManyToOne
    @JoinColumn(name="province_id", nullable=false)
    private Province province;

    @OneToMany(mappedBy = "district", cascade = CascadeType.ALL)
    private List<Commune> communes;

    public District() {}

    public District(String code, String name, Province province) {
        this.code = code;
        this.name = name;
        this.province = province;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Province getProvince() {
        return province;
    }

    public void setProvince(Province province) {
        this.province = province;
    }

    public List<Commune> getCommunes() {
        return communes;
    }

    public void setCommunes(List<Commune> communes) {
        this.communes = communes;
    }
}
