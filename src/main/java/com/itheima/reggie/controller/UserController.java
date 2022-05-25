package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.User;
import com.itheima.reggie.service.UserService;
import com.itheima.reggie.utils.SMSUtils;
import com.itheima.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.buf.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 用户信息(User)表控制层
 *
 * @author makejava
 * @since 2022-05-22 12:09:19
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    /**
     * 服务对象
     */
    @Resource
    private UserService userService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    /**
     * 发生手机短信验证码
     *
     * @param user
     * @param session
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session) {
        //获取手机号
        String phone = user.getPhone();
        if (phone != null) {
            //随机生成四位的验证码
            Integer code = ValidateCodeUtils.generateValidateCode(4);
            //调用阿里云提供的短信服务API完成发生短信
            //SMSUtils.sendMessage("阿里云短信测试", "SMS_154950909", phone, code.toString());
            log.info(String.valueOf(code));

            //需要将生成的验证码保存到Session
            //session.setAttribute("code", code);

            //需要将生成的验证码放到Redis中缓存
            stringRedisTemplate.opsForValue().set(phone, String.valueOf(code),60, TimeUnit.MINUTES);


            return R.success("手机验证码发送成功");
        }
        return R.error("短信发送失败");
    }
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session) {
        //获取传过来的手机号
        String phone = map.get("phone").toString();
        //获取传过来的验证码
        String code =  map.get("code").toString();

        //获取Session里面的验证码
        //String codeInSession = session.getAttribute("code").toString();
        //从Redis中获取缓存中的验证码
        String codeInSession = stringRedisTemplate.opsForValue().get(phone);


        //进行验证码对比
        if (codeInSession != null && codeInSession.equals(code)) {
            //验证码正确,查询用户
            LambdaQueryWrapper<User> lw = new LambdaQueryWrapper<>();
            lw.eq(User::getPhone, phone);

            User user = userService.getOne(lw);
            //并判断用户是否为新用户,是用户就帮他注册,否则就登录成功
            if (user == null) {
                //为新用户
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }

            session.setAttribute("user",user);
            //如果用户登录成功,就可以删除Redis中的验证码
            stringRedisTemplate.delete(phone);

            return R.success(user);
        }
        return R.error("登录失败！");
    }

//    @PostMapping("/login")
//    public R<User> login(@RequestBody Map map, HttpSession session) {
//        //获取传过来的手机号
//        String phone = map.get("phone").toString();
//        //获取传过来的验证码
//        String code =  map.get("code").toString();
//
//        //获取Session里面的验证码
//        String codeInSession = session.getAttribute("code").toString();
//
//        //进行验证码对比
//        if (codeInSession != null && codeInSession.equals(code)) {
//            //验证码正确,查询用户
//            LambdaQueryWrapper<User> lw = new LambdaQueryWrapper<>();
//            lw.eq(User::getPhone, phone);
//
//            User user = userService.getOne(lw);
//            //并判断用户是否为新用户,是用户就帮他注册,否则就登录成功
//            if (user == null) {
//                //为新用户
//                user = new User();
//                user.setPhone(phone);
//                user.setStatus(1);
//                userService.save(user);
//            }
//
//            session.setAttribute("user",user);
//
//            return R.success(user);
//        }
//       return R.error("登录失败！");
//    }

//    @PostMapping("/login")
//    public R<User> login(@RequestBody Map map, HttpSession session) {
//        //获取传过来的手机号
//        String phone = map.get("phone").toString();
//        //获取传过来的验证码
//        String code =  map.get("code").toString();
//
//        //获取Session里面的验证码
//        //String codeInSession = session.getAttribute("code").toString();
//
//        //进行验证码对比
//            //验证码正确,查询用户
//            LambdaQueryWrapper<User> lw = new LambdaQueryWrapper<>();
//            lw.eq(User::getPhone, phone);
//
//            User user = userService.getOne(lw);
//            //并判断用户是否为新用户,是用户就帮他注册,否则就登录成功
//            if (user == null) {
//                //为新用户
//                user = new User();
//                user.setPhone(phone);
//                user.setStatus(1);
//                userService.save(user);
//            }
//            session.setAttribute("user",user);
//            return R.success(user);
//    }

}

