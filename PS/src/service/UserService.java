package service;

import model.User;
import repository.IUserRepository;
import repository.UserRepository;

import java.util.List;

public class UserService implements IUserService{
    private IUserRepository repo= new UserRepository();
    @Override
    public List<User> list() {
        return repo.list();
    }

    @Override
    public void editUser(User user) {
        repo.editUser(user);
    }

    @Override
    public List<User> selectUser() {
        return repo.selectUser();
    }

    @Override
    public int getUser(String name) {
        return repo.getUser(name);
    }
}