package src.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by caoshibin on 2018/1/30.
 */
public class User {
    private String userid;
    private String name;
    private String english_name;
    private String mobile;
    private List<Integer> department;
    private String order;
    private String position;
    private String gender;
    private String email;
    private boolean isleader;
    private int enable;
    private String avatar_mediaid;
    private String telephone;

    @Override
    public String toString() {
        return "User{" +
                "userid='" + userid + '\'' +
                ", name='" + name + '\'' +
                ", english_name='" + english_name + '\'' +
                ", mobile='" + mobile + '\'' +
                ", department=" + department +
                ", order='" + order + '\'' +
                ", position='" + position + '\'' +
                ", gender='" + gender + '\'' +
                ", email='" + email + '\'' +
                ", isleader=" + isleader +
                ", enable=" + enable +
                ", avatar_mediaid='" + avatar_mediaid + '\'' +
                ", telephone='" + telephone + '\'' +
                '}';
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEnglish_name() {
        return english_name;
    }

    public void setEnglish_name(String english_name) {
        this.english_name = english_name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public List<Integer> getDepartment() {
        if(department==null)
            department=new ArrayList<Integer>();
        return department;
    }

    public void setDepartment(int department) {
        if(this.department==null)
            this.department=new ArrayList<Integer>();
        this.department.add(department);
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isIsleader() {
        return isleader;
    }

    public void setIsleader(boolean isleader) {
        this.isleader = isleader;
    }

    public int isEnable() {
        return enable;
    }

    public void setEnable(int enable) {
        this.enable=enable;
    }

    public String getAvatar_mediaid() {
        return avatar_mediaid;
    }

    public void setAvatar_mediaid(String avatar_mediaid) {
        this.avatar_mediaid = avatar_mediaid;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }
}
