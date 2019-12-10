package com.bcloud.fire.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Alarm extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long type;// 信息类型：1、告警，2、故障
    private Long state;// 信息状态：1、未处理，2、已处理
    private Long deviceId;// 设备ID
    private String deviceName;// 设备名称
    private String alarmContent;// 信息内容
    private Date alarmTime;// 告警时间
    private String processContent;// 处理工作内容
    private Date processTime;// 处理时间
    private String processAccount;// 处理人员
    private Long floorId;
    private Long buildingId;
    private Long parkId;
    private String maintain;// 维保公司
    private String account;// 客户
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

    public Long getState() {
        return this.state;
    }

    public void setState(Long state) {
        this.state = state;
    }

    public Long getDeviceId() {
        return this.deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceName() {
        return this.deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getAlarmContent() {
        return this.alarmContent;
    }

    public void setAlarmContent(String alarmContent) {
        this.alarmContent = alarmContent;
    }

    public Date getAlarmTime() {
        return this.alarmTime;
    }

    public void setAlarmTime(Date alarmTime) {
        this.alarmTime = alarmTime;
    }

    public String getProcessContent() {
        return this.processContent;
    }

    public void setProcessContent(String processContent) {
        this.processContent = processContent;
    }

    public Date getProcessTime() {
        return this.processTime;
    }

    public void setProcessTime(Date processTime) {
        this.processTime = processTime;
    }

    public String getProcessAccount() {
        return processAccount;
    }

    public void setProcessAccount(String processAccount) {
        this.processAccount = processAccount;
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
