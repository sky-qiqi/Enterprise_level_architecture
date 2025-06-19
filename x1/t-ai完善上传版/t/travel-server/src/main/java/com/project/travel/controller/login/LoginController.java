package com.project.travel.controller.login;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.project.travel.domain.ApiResponse;
import com.project.travel.domain.AttractionOrder;
import com.project.travel.domain.HotelOrder;
import com.project.travel.domain.User;
import com.project.travel.service.AttractionsOrderService;
import com.project.travel.service.HotelOrderService;
import com.project.travel.service.UserService;
import com.project.travel.utils.JwtUtil;
import com.project.travel.utils.PasswordUtils;
import com.project.travel.utils.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Controller
@ResponseBody
@RequestMapping("login")
public class LoginController {

    @Autowired
    private UserService userService;
    @Autowired
    private HotelOrderService hotelOrderService;
    @Autowired
    private AttractionsOrderService attractionsOrderService;

    @Autowired
    private RedisUtils redisUtils;

    @PostMapping()
    public ApiResponse login(HttpServletRequest request, @RequestBody JSONObject jsonObject) {
        String username = jsonObject.getString("loginAccount");
        String password = jsonObject.getString("password");
        QueryWrapper<User> query = new QueryWrapper<>();
        query.lambda().eq(User::getLoginAccount,username);
        User user = userService.getOne(query);
        if (user == null) {
            return ApiResponse.fail("用户名不存在！");
        }
        //比较加密后得密码
        boolean decrypt = PasswordUtils.decrypt(password, user.getPassword() + "$" + user.getSalt());
        if (!decrypt) {
            return ApiResponse.fail("用户名或密码错误！");
        }
        if (user.getStatus() == 1) {
            return ApiResponse.fail("用户被禁用！");
        }
        //密码正确生成token返回
        String token = JwtUtil.sign(user.getId(), user.getPassword());
        JSONObject json = new JSONObject();
        json.put("token", token);
        return ApiResponse.success(json);
    }

    @GetMapping("logout")
    public ApiResponse logout() {
        return ApiResponse.success();
    }

    @GetMapping("getManageData")
    public ApiResponse getManageData() {
        JSONObject jsonObject = new JSONObject();

        List<String> dates = new ArrayList<>();//存放近七日的日期字符串
        List<Integer> nums = new ArrayList<>();//保存对应的景点预约数量
        List<Integer> orders = new ArrayList<>();//存储酒店预约数量

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        for (int i = 0; i < 7; i++) {
            Date date = calendar.getTime();
            String formattedDate = dateFormat.format(date);
            //最近七日景点预约
            QueryWrapper<AttractionOrder> wrapper = new QueryWrapper<>();
            wrapper.lambda().ge(AttractionOrder::getCreateTime,formattedDate + " 00:00:00")
                    .le(AttractionOrder::getCreateTime,formattedDate + " 23:59:59");
            nums.add(attractionsOrderService.count(wrapper));
            //最近七日酒店预约
            QueryWrapper<HotelOrder> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().ge(HotelOrder::getCreateTime,formattedDate + " 00:00:00")
                    .le(HotelOrder::getCreateTime,formattedDate + " 23:59:59");
            orders.add(hotelOrderService.count(queryWrapper));
            dates.add(formattedDate);
            calendar.add(Calendar.DAY_OF_YEAR, -1);
        }

        jsonObject.put("dates",dates);
        jsonObject.put("nums",nums);
        jsonObject.put("orders",orders);
        return ApiResponse.success(jsonObject);
    }

}
