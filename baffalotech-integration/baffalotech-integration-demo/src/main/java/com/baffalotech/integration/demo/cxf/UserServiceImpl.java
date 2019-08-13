package com.baffalotech.integration.demo.cxf;
import javax.jws.WebService;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@WebService(targetNamespace="http://service.webservicedemo.dbgo.com/",endpointInterface = "com.baffalotech.integration.http.netty.cxf.UserService")
public class UserServiceImpl implements UserService {
    private Map<String, User> userMap = new HashMap<String, User>();
    public UserServiceImpl() {
        System.out.println("向实体类插入数据");
        User user = new User();
        user.setUserId("411001");
        user.setUsername("zhansan");
        user.setAge("20");
        user.setUpdateTime(new Date());
        userMap.put(user.getUserId(), user);

        user = new User();
        user.setUserId("411002");
        user.setUsername("lisi");
        user.setAge("30");
        user.setUpdateTime(new Date());
        userMap.put(user.getUserId(), user);

        user = new User();
        user.setUserId("411003");
        user.setUsername("wangwu");
        user.setAge("40");
        user.setUpdateTime(new Date());
        userMap.put(user.getUserId(), user);
    }
    @Override
    public String getName(String userId) {
        return "liyd-" + userId;
    }
    @Override
    public User getUser(String userId) {
        User user= userMap.get(userId);
        return user;
    }

    @Override
    public ArrayList<User> getAlLUser() {
        ArrayList<User> users=new ArrayList<>();
        userMap.forEach((key,value)->{users.add(value);});
        return users;
    }
    
    public byte[] execute(byte[] data) 
    {
    	try {
			FileOutputStream fileOutputStream = new FileOutputStream("D:\\abc.txt");
			fileOutputStream.write(data);
			fileOutputStream.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return data;
    }
}