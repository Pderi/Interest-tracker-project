package com.interest.tracker.module.travel.dal.mysql;

import com.interest.tracker.framework.mybatis.core.mapper.BaseMapperX;
import com.interest.tracker.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.interest.tracker.module.travel.dal.dataobject.TravelPlaceDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 旅游地点 Mapper
 *
 * @author interest-tracker
 */
@Mapper
public interface TravelPlaceMapper extends BaseMapperX<TravelPlaceDO> {

    /**
     * 根据名称、城市和国家查询地点
     *
     * @param name 地点名称
     * @param city 城市
     * @param country 国家
     * @return 地点
     */
    default TravelPlaceDO selectByNameAndCityAndCountry(String name, String city, String country) {
        LambdaQueryWrapperX<TravelPlaceDO> wrapper = new LambdaQueryWrapperX<>();
        wrapper.eq(TravelPlaceDO::getName, name);
        if (city != null) {
            wrapper.eq(TravelPlaceDO::getCity, city);
        }
        if (country != null) {
            wrapper.eq(TravelPlaceDO::getCountry, country);
        }
        return selectOne(wrapper);
    }

}

