package com.example.isdmessenger.model;

public class User {
    String userId;
    String userImg;
    String userName;
    String userStatus;
    String userPhone;
    String userEmail;
    String userDoB;
    String userBloodGroup;
    String userWork;
    String userEduInstitute;
    String userAddress;

    public User() {
    }

    public User(String userId, String userImg, String userName, String userPhone, String userEmail, String userDoB, String userBloodGroup, String userWork, String userEduInstitute, String userAddress) {
        this.userId = userId;
        this.userImg = userImg;
        this.userName = userName;
        this.userPhone = userPhone;
        this.userEmail = userEmail;
        this.userDoB = userDoB;
        this.userBloodGroup = userBloodGroup;
        this.userWork = userWork;
        this.userEduInstitute = userEduInstitute;
        this.userAddress = userAddress;
    }

    public User(String userId, String userName, String userPhone, String userEmail) {
        this.userId = userId;
        this.userName = userName;
        this.userPhone = userPhone;
        this.userEmail = userEmail;
    }

    public User(String userId, String userImg, String userDoB, String userBloodGroup, String userWork, String userEduInstitute, String userAddress) {
        this.userId = userId;
        this.userImg = userImg;
        this.userDoB = userDoB;
        this.userBloodGroup = userBloodGroup;
        this.userWork = userWork;
        this.userEduInstitute = userEduInstitute;
        this.userAddress = userAddress;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUserImg(String userImg) {
        this.userImg = userImg;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public void setUserDoB(String userDoB) {
        this.userDoB = userDoB;
    }

    public void setUserBloodGroup(String userBloodGroup) {
        this.userBloodGroup = userBloodGroup;
    }

    public void setUserWork(String userWork) {
        this.userWork = userWork;
    }

    public void setUserEduInstitute(String userEduInstitute) {
        this.userEduInstitute = userEduInstitute;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserImg() {
        return userImg;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserDoB() {
        return userDoB;
    }

    public String getUserBloodGroup() {
        return userBloodGroup;
    }

    public String getUserWork() {
        return userWork;
    }

    public String getUserEduInstitute() {
        return userEduInstitute;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public String getUserStatus() {
        return userStatus;
    }
}
