package db;

import model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MemoryUserRepository implements Repository{
    private final Map<String, User> users = new HashMap<>();
    private static MemoryUserRepository memoryUserRepository;

    // todo User Instance를 생성해서 MemoryUserRepository에 저장
    private MemoryUserRepository() {
    }

    // 싱글톤
    public static MemoryUserRepository getInstance() {
        if (memoryUserRepository == null) {
            memoryUserRepository = new MemoryUserRepository();
            return memoryUserRepository;
        }
        return memoryUserRepository;
    }

    // MemoryUserRepository에 추가
    public void addUser(User user) {
        users.put(user.getUserId(), user);
    }

    public User findUserById(String userId) {
        return users.get(userId);
    }

    public Collection<User> findAll() {
        return users.values();
    }
}
