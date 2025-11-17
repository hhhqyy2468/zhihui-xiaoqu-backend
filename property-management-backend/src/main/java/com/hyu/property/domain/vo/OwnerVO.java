package com.hyu.property.domain.vo;

import java.util.Date;
import java.util.List;

/**
 * Owner View Object OwnerVO
 *
 * @author hyu
 */
public class OwnerVO {

    /** User ID */
    private Long userId;

    /** Username */
    private String username;

    /** Password */
    private String password;

    /** Real Name */
    private String realName;

    /** Phone */
    private String phone;

    /** ID Card */
    private String idCard;

    /** Email */
    private String email;

  /** Gender: 0-Female 1-Male */
    private Integer gender;

    /** Avatar URL */
    private String avatar;

    /** Resident Type: 1-Owner 2-Tenant */
    private Integer residentType;

    /** Check-in Date */
    private Date checkInDate;

    /** Resident Status: 1-Living 2-Moved Out */
    private Integer residentStatus;

    /** Emergency Contact */
    private String emergencyContact;

    /** Emergency Phone */
    private String emergencyPhone;

    /** Status: 0-Disabled 1-Enabled */
    private Integer status;

    /** Remarks */
    private String remark;

    /** Role ID Array */
    private Long[] roleIds;

    /** Create Time */
    private Date createTime;

    /** Update Time */
    private Date updateTime;

    /** Create Time Start Range (for query) */
    private String beginTime;

    /** Create Time End Range (for query) */
    private String endTime;

    /** Role List */
    private List<String> roles;

    /** Permission List */
    private List<String> permissions;

    /** Associated Property Information */
    private List<HouseInfo> houseList;

    /** Wallet Balance */
    private java.math.BigDecimal walletBalance;

    /** Creator */
    private String createBy;

    /** Updater */
    private String updateBy;

    public static class HouseInfo {
        private Long houseId;
        private String houseNo;
        private String buildingName;
        private String unitName;
        private String buildingArea;
        private String houseType;
        private Double buildingAreaNum;
        private Double usableArea;
        private Integer houseStatus;
        private String relationType; // Relation Type: 1-Owner 2-Tenant
        private Integer relationTypeNum; // Relation Type as number
        private Date startDate;
        private Date endDate;
        private Boolean isCurrent;

        // Getters and setters
        public Long getHouseId() { return houseId; }
        public void setHouseId(Long houseId) { this.houseId = houseId; }

        public String getHouseNo() { return houseNo; }
        public void setHouseNo(String houseNo) { this.houseNo = houseNo; }

        public String getBuildingName() { return buildingName; }
        public void setBuildingName(String buildingName) { this.buildingName = buildingName; }

        public String getUnitName() { return unitName; }
        public void setUnitName(String unitName) { this.unitName = unitName; }

        public String getBuildingArea() { return buildingArea; }
        public void setBuildingArea(String buildingArea) { this.buildingArea = buildingArea; }

        public String getHouseType() { return houseType; }
        public void setHouseType(String houseType) { this.houseType = houseType; }

        public Double getBuildingAreaNum() { return buildingAreaNum; }
        public void setBuildingAreaNum(Double buildingAreaNum) { this.buildingAreaNum = buildingAreaNum; }

        public Double getUsableArea() { return usableArea; }
        public void setUsableArea(Double usableArea) { this.usableArea = usableArea; }

        public Integer getHouseStatus() { return houseStatus; }
        public void setHouseStatus(Integer houseStatus) { this.houseStatus = houseStatus; }

        public String getRelationType() { return relationType; }
        public void setRelationType(String relationType) { this.relationType = relationType; }

        public Integer getRelationTypeNum() { return relationTypeNum; }
        public void setRelationTypeNum(Integer relationTypeNum) { this.relationTypeNum = relationTypeNum; }

        public Date getStartDate() { return startDate; }
        public void setStartDate(Date startDate) { this.startDate = startDate; }

        public Date getEndDate() { return endDate; }
        public void setEndDate(Date endDate) { this.endDate = endDate; }

        public Boolean getIsCurrent() { return isCurrent; }
        public void setIsCurrent(Boolean isCurrent) { this.isCurrent = isCurrent; }
    }

    // Getters and setters for OwnerVO
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRealName() { return realName; }
    public void setRealName(String realName) { this.realName = realName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getIdCard() { return idCard; }
    public void setIdCard(String idCard) { this.idCard = idCard; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Integer getGender() { return gender; }
    public void setGender(Integer gender) { this.gender = gender; }

    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }

    public Integer getResidentType() { return residentType; }
    public void setResidentType(Integer residentType) { this.residentType = residentType; }

    public Date getCheckInDate() { return checkInDate; }
    public void setCheckInDate(Date checkInDate) { this.checkInDate = checkInDate; }

    public Integer getResidentStatus() { return residentStatus; }
    public void setResidentStatus(Integer residentStatus) { this.residentStatus = residentStatus; }

    public String getEmergencyContact() { return emergencyContact; }
    public void setEmergencyContact(String emergencyContact) { this.emergencyContact = emergencyContact; }

    public String getEmergencyPhone() { return emergencyPhone; }
    public void setEmergencyPhone(String emergencyPhone) { this.emergencyPhone = emergencyPhone; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }

    public Long[] getRoleIds() { return roleIds; }
    public void setRoleIds(Long[] roleIds) { this.roleIds = roleIds; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }

    public String getBeginTime() { return beginTime; }
    public void setBeginTime(String beginTime) { this.beginTime = beginTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    public List<String> getRoles() { return roles; }
    public void setRoles(List<String> roles) { this.roles = roles; }

    public List<String> getPermissions() { return permissions; }
    public void setPermissions(List<String> permissions) { this.permissions = permissions; }

    public List<HouseInfo> getHouseList() { return houseList; }
    public void setHouseList(List<HouseInfo> houseList) { this.houseList = houseList; }

    public java.math.BigDecimal getWalletBalance() { return walletBalance; }
    public void setWalletBalance(java.math.BigDecimal walletBalance) { this.walletBalance = walletBalance; }

    public String getCreateBy() { return createBy; }
    public void setCreateBy(String createBy) { this.createBy = createBy; }

    public String getUpdateBy() { return updateBy; }
    public void setUpdateBy(String updateBy) { this.updateBy = updateBy; }

    /** 房产数量(计算字段) */
    private Integer houseCount;

    /** 入住时间 */
    private String checkInTime;

    /** 最后登录时间 */
    private Date lastLoginTime;

    // Getters and setters for new fields
    public Integer getHouseCount() { return houseCount; }
    public void setHouseCount(Integer houseCount) { this.houseCount = houseCount; }

    public String getCheckInTime() { return checkInTime; }
    public void setCheckInTime(String checkInTime) { this.checkInTime = checkInTime; }

    public Date getLastLoginTime() { return lastLoginTime; }
    public void setLastLoginTime(Date lastLoginTime) { this.lastLoginTime = lastLoginTime; }
}