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
            return memoryUserRepository;
        }
        return memoryUserRepository;
    }

    public void addUser(User user) {
        users.put(user.getUserId(), user);
        System.out.println(user.getUserId()+"님 이 회원가입 하였습니다.");
    }

    public User findUserById(String userId) {
        return users.get(userId);
    }

    public Collection<User> findAll() {
        return users.values();
    }
}
