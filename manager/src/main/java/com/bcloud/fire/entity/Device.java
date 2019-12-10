package com.bcloud.fire.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Device extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long type;// 设备类型，从DEVICE_TYPE中选择
    private String name;// 设备名称，从DEVICE_TYPE中选择
    private Long networkType;// 网络类型，1、NB，2、4G，3、WIFI，4、LAN，5、RF
    private String serialNo;// 设备序列号
    private String msisdn;
    private String imsi;
    private String imei;
    private String brand;// 设备品牌
    private String model;// 设备型号
    private Long state;// 设备状态：1、正常，2、告警，3、故障
    private Long valid;// 是否有效
    private Date validDate;// 有效期
    private String position;// 设备位置
    private Long floorId;
    private Long buildingId;
    private Long parkId;
    private String maintain;
    private String account;
    private String reserve1;
    private String reserve2;
    private String reserve3;
    private Long used;// 暂不使用
    private String description;
    private Date createTime;
    private Date deleteTime;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getType() {
        return this.type;
    }

    public void setType(Long type) {
        this.type = type;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getNetworkType() {
        return this.networkType;
    }

    public void setNetworkType(Long networkType) {
        this.networkType = networkType;
    }

    public String getSerialNo() {
        return this.serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public String getMsisdn() {
        return this.msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getImsi() {
        return this.imsi;
    }

    public void setImsi(String imsi) {
        this.imsi = imsi;
    }

    public String getImei() {
        return this.imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getBrand() {
        return this.brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return this.model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Long getState() {
        return this.state;
    }

    public void setState(Long state) {
        this.state = state;
    }

    public Long getValid() {
        return this.valid;
    }

    public void setValid(Long valid) {
        this.valid = valid;
    }

    public Date getValidDate() {
        return this.validDate;
    }

    public void setValidDate(Date validDate) {
        this.validDate = validDate;
    }

    public String getPosition() {
        return this.position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public Long getFloorId() {
        return this.floorId;
    }

    public void setFloorId(Long floorId) {
        this.floorId = floorId;
    }

    public Long getBuildingId() {
        return this.buildingId;
    }

    public void setBuildingId(Long buildingId) {
        this.buildingId = buildingId;
    }

    public Long getParkId() {
        return this.parkId;
    }

    public void setParkId(Long parkId) {
        this.parkId = parkId;
    }

    public String getMaintain() {
        return this.maintain;
    }

    public void setMaintain(String maintain) {
        this.maintain = maintain;
    }

    public String getAccount() {
        return this.account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getReserve1() {
        return this.reserve1;
    }

    public void setReserve1(String reserve1) {
        this.reserve1 = reserve1;
    }

    public String getReserve2() {
        return this.reserve2;
    }

    public void setReserve2(String reserve2) {
        this.reserve2 = reserve2;
    }

    public String getReserve3() {
        return this.reserve3;
    }

    public void setReserve3(String reserve3) {
        this.reserve3 = reserve3;
    }

    public Long getUsed() {
        return this.used;
    }

    public void setUsed(Long used) {
        this.used = used;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getDeleteTime() {
        return this.deleteTime;
    }

    public void setDeleteTime(Date deleteTime) {
        this.deleteTime = deleteTime;
    }
}
