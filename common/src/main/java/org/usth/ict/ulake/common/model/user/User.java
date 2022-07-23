package org.usth.ict.ulake.common.model.user;

import java.util.Set;

public class User {
    public Long id;
    public String userName;
    public String firstName;
    public String lastName;
    public Boolean isAdmin;
    public String email;
    public Long registerTime;
    public Boolean status;
    public Department department;
    public Set<UserGroup> groups;

    public User(){}
}
