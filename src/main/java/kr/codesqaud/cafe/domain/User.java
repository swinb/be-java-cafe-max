package kr.codesqaud.cafe.domain;

import kr.codesqaud.cafe.dto.UserDto;

public class User {

    private String nickName;
    private String email;
    private String password;
    private int userId;

    public User(String nickName, String email, String password, int userId) {
        this.nickName = nickName;
        this.email = email;
        this.password = password;
        this.userId = userId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public UserDto ToDTO(){
        return new UserDto(nickName,email,password,userId);
    }
}
