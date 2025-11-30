package com.hyu.property.domain.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 房产视图对象 HouseVO
 *
 * @author hyu
 */
@Data
public class HouseVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 房产ID */
    private Long id;

    /** 房产编号 */
    private String houseNo;

    /** 楼栋ID */
    private Long buildingId;

    /** 单元ID */
    private Long unitId;

    /** 楼层 */
    private Integer floor;

    /** 房号 */
    private String roomNumber;

    /** 户型 */
    private String houseType;

    /** 建筑面积 */
    private BigDecimal buildingAreaNum;

    /** 使用面积 */
    private BigDecimal usableArea;

    /** 房产状态：1-空置 2-已售 */
    private Integer houseStatus;

    /** 楼栋名称 */
    private String buildingName;

    /** 单元名称 */
    private String unitName;

    /** 产权人姓名 */
    private String propertyOwner;

    /** 产权人ID */
    private Long propertyOwnerId;

    /** 创建时间 */
    private java.util.Date createTime;
}