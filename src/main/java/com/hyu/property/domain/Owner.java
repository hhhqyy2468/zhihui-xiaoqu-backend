package com.hyu.property.domain;

import com.baomidou.mybatisplus.annotation.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Owner Information Table property_owner
 *
 * @author hyu
 */
@TableName("property_owner")
public class Owner implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "owner_id", type = IdType.AUTO)
    private Long ownerId;

    /**
     * Owner Name
     */
    @NotBlank(message = "Owner name cannot be empty")
    @Size(min = 2, max = 20, message = "Owner name length must be between 2-20")
    @TableField("owner_name")
    private String ownerName;

    /**
     * Phone Number
     */
    @NotBlank(message = "Phone number cannot be empty")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "Phone number format is incorrect")
    @TableField("phone")
    private String phone;

    /**
     * ID Card Number
     */
    @NotBlank(message = "ID card cannot be empty")
    @Pattern(regexp = "^[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$", message = "ID card format is incorrect")
    @TableField("id_card")
    private String idCard;

    /**
     * Email
     */
    @Email(message = "Email format is incorrect")
    @TableField("email")
    private String email;

    /**
     * Owner Type
     */
    @NotNull(message = "Owner type cannot be empty")
    @TableField("owner_type")
    private Integer ownerType;

    /**
     * Owner Status
     */
    @NotNull(message = "Owner status cannot be empty")
    @TableField("owner_status")
    private Integer ownerStatus;

    /**
     * Move-in Date
     */
    @TableField("move_in_date")
    private Date moveInDate;

    /**
     * Move-out Date
     */
    @TableField("move_out_date")
    private Date moveOutDate;

    /**
     * Associated Property Information (non-database field, for display)
     */
    @TableField(exist = false)
    private List<HouseInfo> houses;

    /**
     * Remarks
     */
    @Size(max = 500, message = "Remarks length cannot exceed 500")
    @TableField("remark")
    private String remark;

    /**
     * Creator
     */
    @TableField(value = "create_by", fill = FieldFill.INSERT)
    private String createBy;

    /**
     * Create Time
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * Updater
     */
    @TableField(value = "update_by", fill = FieldFill.INSERT_UPDATE)
    private String updateBy;

    /**
     * Update Time
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /**
     * Property Information Inner Class
     */
    public static class HouseInfo {
        private Long houseId;
        private String houseCode;
        private String buildingName;
        private Integer relationType; // 1:Property Owner 2:User

        // Getters and setters for HouseInfo
        public Long getHouseId() { return houseId; }
        public void setHouseId(Long houseId) { this.houseId = houseId; }

        public String getHouseCode() { return houseCode; }
        public void setHouseCode(String houseCode) { this.houseCode = houseCode; }

        public String getBuildingName() { return buildingName; }
        public void setBuildingName(String buildingName) { this.buildingName = buildingName; }

        public Integer getRelationType() { return relationType; }
        public void setRelationType(Integer relationType) { this.relationType = relationType; }
    }

    // Getters and setters for Owner
    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }

    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getIdCard() { return idCard; }
    public void setIdCard(String idCard) { this.idCard = idCard; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Integer getOwnerType() { return ownerType; }
    public void setOwnerType(Integer ownerType) { this.ownerType = ownerType; }

    public Integer getOwnerStatus() { return ownerStatus; }
    public void setOwnerStatus(Integer ownerStatus) { this.ownerStatus = ownerStatus; }

    public Date getMoveInDate() { return moveInDate; }
    public void setMoveInDate(Date moveInDate) { this.moveInDate = moveInDate; }

    public Date getMoveOutDate() { return moveOutDate; }
    public void setMoveOutDate(Date moveOutDate) { this.moveOutDate = moveOutDate; }

    public List<HouseInfo> getHouses() { return houses; }
    public void setHouses(List<HouseInfo> houses) { this.houses = houses; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }

    public String getCreateBy() { return createBy; }
    public void setCreateBy(String createBy) { this.createBy = createBy; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    public String getUpdateBy() { return updateBy; }
    public void setUpdateBy(String updateBy) { this.updateBy = updateBy; }

    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
}