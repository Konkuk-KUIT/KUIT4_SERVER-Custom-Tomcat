package db;

import model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

//메모리에서 사용자 정보를 저장하고 관리하는 역할
public class MemoryUserRepository implements Repository{
    private final Map<String, User> users = new HashMap<>();
    private static MemoryUserRepository memoryUserRepository;

    private MemoryUserRepository() {
    }

    public static MemoryUserRepository getInstance() {                          //싱글톤 인스턴스를 반환
        if (memoryUserRepository == null) {
            memoryUserRepository = new MemoryUserRepository();
            return memoryUserRepository;
        }
        return memoryUserRepository;
    }

    public void addUser(User user) {
        users.put(user.getUserId(), user);
    }       //새로운 사용자를 추가

    public User findUserById(String userId) {                                   //사용자 ID로 사용자를 검색
        return users.get(userId);
    }

    public Collection<User> findAll() {
        return users.values();
    }                //모든 사용자 목록을 반환
}
