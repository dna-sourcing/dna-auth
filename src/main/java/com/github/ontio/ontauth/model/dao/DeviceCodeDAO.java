package com.github.ontio.ontauth.model.dao;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Table(name="tbl_device")
public class DeviceCodeDAO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ontid")
    private String ontid;

    @Column(name = "device_code")
    private String deviceCode;

    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "update_time")
    private Date updateTime;

}
