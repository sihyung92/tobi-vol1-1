package springbook.user.dao;

import springbook.user.domain.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDao {
    private static UserDao INSTANCE;

    private Connection currentConnection;
    private User currentUser;

    private final SimpleConnectionMaker simpleConnectionMaker;

    private UserDao(SimpleConnectionMaker simpleConnectionMaker) {
        this.simpleConnectionMaker = simpleConnectionMaker;
    }

    public static synchronized UserDao getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new UserDao(new BaeminSimpleConnectionMaker());
        }
        return INSTANCE;
    }


    public void add(User user) throws ClassNotFoundException, SQLException {
        this.currentConnection = simpleConnectionMaker.makeNewConnection();

        PreparedStatement preparedStatement = currentConnection.prepareStatement("INSERT INTO users (id, name, password) VALUES (?,?,?)");
        preparedStatement.setString(1, user.getId());
        preparedStatement.setString(2, user.getName());
        preparedStatement.setString(3, user.getPassword());

        preparedStatement.execute();

        preparedStatement.close();
        currentConnection.close();
    }

    public User get(String id) throws ClassNotFoundException, SQLException {
        this.currentConnection = simpleConnectionMaker.makeNewConnection();

        PreparedStatement preparedStatement = currentConnection.prepareStatement("SELECT * FROM users WHERE id = ?");
        preparedStatement.setString(1, id);

        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.next();
        User user = new User();
        user.setId(resultSet.getString("id"));
        user.setName(resultSet.getString("name"));
        user.setPassword(resultSet.getString("password"));
        this.currentUser = user;

        resultSet.close();
        preparedStatement.close();
        currentConnection.close();

        return this.currentUser;
    }

    public List<User> getAll() throws ClassNotFoundException, SQLException {
        this.currentConnection = simpleConnectionMaker.makeNewConnection();

        ArrayList<User> users = new ArrayList<>();

        try (PreparedStatement preparedStatement = currentConnection.prepareStatement("SELECT * FROM users");
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                User user = new User();
                user.setId(resultSet.getString("id"));
                user.setName(resultSet.getString("name"));
                user.setPassword(resultSet.getString("password"));

                users.add(user);
            }
        }
        return users;
    }

    public void delete() throws SQLException, ClassNotFoundException {
        this.currentConnection = simpleConnectionMaker.makeNewConnection();

        PreparedStatement preparedStatement = currentConnection.prepareStatement("DELETE FROM users");
        preparedStatement.executeUpdate();

        preparedStatement.close();
        currentConnection.close();
    }
}
