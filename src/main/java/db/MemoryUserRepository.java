package db;

import model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MemoryUserRepository implements Repository{
    private final Map<String, User> users = new HashMap<>();
    private static MemoryUserRepository memoryUserRepository;


    private MemoryUserRepository() {
    }

    public static MemoryUserRepository getInstance() {
        if (memoryUserRepository == null) {
            memoryUserRepository = new MemoryUserRepository();
            //새로 유저 레포 생성해서 넣어줌
            return memoryUserRepository;
        }
        return memoryUserRepository;
    }

    //해쉬맵에 유저 추가
    public void addUser(User user) {
        users.put(user.getUserId(), user);
    }

    public User findUserById(String userId) {
        return users.get(userId);
    }

    //맵에 있는 모든 유저 검색
    public Collection<User> findAll() {
        return users.values();
    }
}
