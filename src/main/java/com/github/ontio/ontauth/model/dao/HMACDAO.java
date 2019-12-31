package com.github.ontio.ontauth.model.dao;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Table(name="tbl_hmac")
public class HMACDAO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ontid")
    private String ontid;

    @Column(name = "app_id")
    private String appid;  // todo unique

    @Column(name = "app_key")
    private String appkey;  // todo update

    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "update_time")
    private Date updateTime;

}
