package com.weiwan.dsp.console;

import com.weiwan.dsp.console.mapper.PermissionMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author: xiaozhennan
 * @email: xiaozhennan1995@gmail.com
 * @date: 2021/4/23 13:56
 * @description:
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MyBatisToJpaTest {

    @Autowired
    private PermissionMapper permissionMapper;
    @Test
    public void testJpa(){
        System.out.println(permissionMapper);
    }

}
