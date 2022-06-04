package com.graduation.healthapp.data.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Index;

@Entity
public class LaunchPageAd {
    @Id(autoincrement = true)
    private Long keyid;
    @Index(unique = true)
    private Long id;
    private String introduce;
    private String url;
    private Boolean isable;
    private String createdtime;
    private String updatedtime;
    private int effectiveday;


    @Generated(hash = 308054721)
    public LaunchPageAd(Long keyid, Long id, String introduce, String url,
            Boolean isable, String createdtime, String updatedtime,
            int effectiveday) {
        this.keyid = keyid;
        this.id = id;
        this.introduce = introduce;
        this.url = url;
        this.isable = isable;
        this.createdtime = createdtime;
        this.updatedtime = updatedtime;
        this.effectiveday = effectiveday;
    }


    @Generated(hash = 423577769)
    public LaunchPageAd() {
    }


    @Override
    public String toString() {
        return "LaunchPageAd{" +
                "keyid=" + keyid +
                ", id=" + id +
                ", introduce='" + introduce + '\'' +
                ", url='" + url + '\'' +
                ", isable=" + isable +
                ", createdtime='" + createdtime + '\'' +
                ", updatedtime='" + updatedtime + '\'' +
                ", effectiveday=" + effectiveday +
                '}';
    }


    public Long getKeyid() {
        return this.keyid;
    }


    public void setKeyid(Long keyid) {
        this.keyid = keyid;
    }


    public Long getId() {
        return this.id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    public String getIntroduce() {
        return this.introduce;
    }


    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }


    public String getUrl() {
        return this.url;
    }


    public void setUrl(String url) {
        this.url = url;
    }


    public Boolean getIsable() {
        return this.isable;
    }


    public void setIsable(Boolean isable) {
        this.isable = isable;
    }


    public String getCreatedtime() {
        return this.createdtime;
    }


    public void setCreatedtime(String createdtime) {
        this.createdtime = createdtime;
    }


    public String getUpdatedtime() {
        return this.updatedtime;
    }


    public void setUpdatedtime(String updatedtime) {
        this.updatedtime = updatedtime;
    }


    public int getEffectiveday() {
        return this.effectiveday;
    }


    public void setEffectiveday(int effectiveday) {
        this.effectiveday = effectiveday;
    }
}
