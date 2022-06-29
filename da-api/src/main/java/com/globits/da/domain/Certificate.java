package com.globits.da.domain;

import com.globits.core.domain.BaseObject;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "tbl_certificate")
@XmlRootElement
public class Certificate extends BaseObject {
    @Column(name = "name")
    private String name;

    @Column(name = "code")
    private String code;

    public Certificate() {}

    public Certificate(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
