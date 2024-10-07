package model;

import http.HttpRequest;

import java.util.Map;
import java.util.Objects;

import static Constant.UserQueryKey.*;
import static Constant.UserQueryKey.EMAIL;

//사용자의 정보를 담는 도메인 객체
//사용자의 ID, 비밀번호, 이름, 이메일
public class User {
    private String userId;
    private String password;
    private String name;
    private String email;

    public User(String userId, String password, String name, String email) {
        this.userId = userId;
        this.password = password;
        this.name = name;
        this.email = email;
    }

    public static User from(String userId, String password, String name, String email) {
        return new User(userId, password, name, email);
    }

    public static User from(Map<String, String> queryParams){
        String userId = queryParams.get(USERID.getValue());
        String password = queryParams.get(PASSWORD.getValue());
        String name = queryParams.get(NAME.getValue());
        String email = queryParams.get(EMAIL.getValue());

        return new User(userId, password, name, email);
    }
    public static User from(HttpRequest httpRequest){
        String userId = httpRequest.getBody(USERID.getValue());
        String password = httpRequest.getBody(PASSWORD.getValue());
        String name = httpRequest.getBody(NAME.getValue());
        String email = httpRequest.getBody(EMAIL.getValue());

        return new User(userId, password, name, email);
    }

    public String getUserId() {
        return userId;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public boolean equals(Object o) {                   //두 User 객체의 동등성을 정의
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(getUserId(), user.getUserId()) && Objects.equals(getPassword(), user.getPassword()) && Objects.equals(getName(), user.getName()) && Objects.equals(getEmail(), user.getEmail());
    }

    @Override                                           //두 User 객체의 동등성을 정의
    public int hashCode() {
        return Objects.hash(getUserId(), getPassword(), getName(), getEmail());
    }
}
