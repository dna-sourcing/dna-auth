package com.github.ontio.ontauth.mapper;

import com.github.ontio.ontauth.model.dao.DeviceCodeDAO;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

@Repository
public interface DeviceCodeMapper extends Mapper<DeviceCodeDAO> {

}
