package com.project.travel.controller.user;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.project.travel.domain.*;
import com.project.travel.enums.ResultCode;
import com.project.travel.service.*;
import com.project.travel.utils.PasswordUtils;
import com.project.travel.utils.RedisUtils;
import com.project.travel.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.IOException;
import java.util.*;

@Controller
@ResponseBody
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private AttractionsOrderService attractionsOrderService;
    @Autowired
    private CommentsService commentsService;
    @Autowired
    private FavoriteService favoriteService;
    @Autowired
    private HotelOrderService hotelOrderService;

    /** 分页查询用户 */
    @PostMapping("getUserPage")
    public ApiResponse getUserPage(@RequestBody User user) {
        Page<User> page = userService.getUserPage(user);
        return ApiResponse.success(page);
    }

    @GetMapping("getUserCount")
    public ApiResponse getUserCount() {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(User::getUserType,1);
        int count = userService.count(queryWrapper);
        return ApiResponse.success(count);
    }

    /** 根据id查询用户 */
    @GetMapping("getUserById")
    public ApiResponse getUserById(@RequestParam("id") String id) {
        User user = userService.getById(id);
        return ApiResponse.success(user);
    }

    /** 新增用户 */
    @Transactional(rollbackFor = Exception.class)
    @PostMapping("saveUser")
    public ApiResponse saveUser(@RequestBody User user) {
        //先校验登陆账号是否重复
        boolean account = checkAccount(user);
        if (!account) {
            return ApiResponse.fail("登陆账号已存在不可重复！");
        }
        String uuid = IdWorker.get32UUID();
        //密码加盐加密
        String encrypt = PasswordUtils.encrypt(user.getPassword());
        String[] split = encrypt.split("\\$");
        user.setId(uuid);
        user.setPassword(split[0]);
        user.setSalt(split[1]);
        user.setAvatar("/img/1697436716646531073.jpeg");
        user.setPwdUpdateDate(new Date());
        //保存用户
        boolean save = userService.save(user);
        return ApiResponse.success();
    }

    /** 编辑用户 */
    @Transactional(rollbackFor = Exception.class)
    @PostMapping("editUser")
    public ApiResponse editUser(@RequestBody User user) {
        User user1 = userService.getById(user.getId());
        if (!user1.getLoginAccount().equals(user.getLoginAccount())) {
            //先校验登陆账号是否重复
            boolean account = checkAccount(user);
            if (!account) {
                return ApiResponse.fail("登陆账号已存在不可重复！");
            }
        }
        //更新用户
        boolean edit = userService.updateById(user);
        return ApiResponse.success();
    }

    /** 校验用户 */
    public boolean checkAccount(User user) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(User::getLoginAccount,user.getLoginAccount());
        int count = userService.count(queryWrapper);
        return count <= 0;
    }

    /** 删除用户 */
    @Transactional(rollbackFor = Exception.class)
    @GetMapping("removeUser")
    public ApiResponse removeUser(@RequestParam("ids")String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] asList = ids.split(",");
            for (String id : asList) {
                User user = userService.getById(id);
                boolean remove = userService.removeById(id);
                QueryWrapper<AttractionOrder> queryWrapper = new QueryWrapper<>();
                queryWrapper.lambda().eq(AttractionOrder::getUserId,id);
                attractionsOrderService.remove(queryWrapper);
                QueryWrapper<Comments> wrapper = new QueryWrapper<>();
                wrapper.lambda().eq(Comments::getUserId,id);
                commentsService.remove(wrapper);
                QueryWrapper<Favorite> queryWrapper1 = new QueryWrapper<>();
                queryWrapper1.lambda().eq(Favorite::getUserId,id);
                favoriteService.remove(queryWrapper1);
                QueryWrapper<HotelOrder> queryWrapper2 = new QueryWrapper<>();
                queryWrapper2.lambda().eq(HotelOrder::getUserId,id);
                hotelOrderService.remove(queryWrapper2);
            }
            return ApiResponse.success();
        } else {
            return ApiResponse.fail("角色id不能为空！");
        }
    }

    /** 重置密码 */
    @PostMapping("resetPassword")
    public ApiResponse resetPassword(@RequestBody JSONObject json) {
        String id = json.getString("id");
        String newPassword = json.getString("newPassword");
        String encrypt = PasswordUtils.encrypt(newPassword);
        String[] split = encrypt.split("\\$");
        User user = userService.getById(id);
        boolean decrypt = PasswordUtils.decrypt(newPassword, user.getPassword() + "$" + user.getSalt());
        if (decrypt) {
            return ApiResponse.fail("新密码不可和旧密码相同！");
        }
        user.setPassword(split[0]);
        user.setSalt(split[1]);
        user.setPwdUpdateDate(new Date());
        boolean update = userService.updateById(user);
        if (update) {
            return ApiResponse.success();
        } else {
            return ApiResponse.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 获取登陆用户信息 */
    @GetMapping("getUserInfo")
    public ApiResponse getUserInfo() {
        String id = TokenUtils.getUserIdByToken();
        User user = userService.getById(id);
        return ApiResponse.success(user);
    }

    /** 修改个人信息 */
    @PostMapping("setUserInfo")
    public ApiResponse setUserInfo(@RequestBody User user) {
        String id = TokenUtils.getUserIdByToken();
        user.setId(id);
        userService.updateById(user);
        return ApiResponse.success();
    }

    /** 修改个人头像 */
    @PostMapping("setUserAvatar/{id}")
    public ApiResponse setUserAvatar(@PathVariable("id") String id, @RequestParam("file") MultipartFile avatar) {
        if(StringUtils.isBlank(id)){
            return ApiResponse.fail("用户id为空!");
        }
        User apeUser = userService.getById(id);
        if(avatar.isEmpty()){
            return ApiResponse.fail("上传的头像不能为空!");
        }
        String coverType = avatar.getOriginalFilename().substring(avatar.getOriginalFilename().lastIndexOf(".") + 1).toLowerCase();
        if ("jpeg".equals(coverType)  || "gif".equals(coverType) || "png".equals(coverType) || "bmp".equals(coverType)  || "jpg".equals(coverType)) {
            //文件路径
            String filePath = System.getProperty("user.dir")+System.getProperty("file.separator")+"img";
            //文件名=当前时间到毫秒+原来的文件名
            String fileName = System.currentTimeMillis() + "."+ coverType;
            //如果文件路径不存在，新增该路径
            File file1 = new File(filePath);
            if(!file1.exists()){
                boolean mkdir = file1.mkdir();
            }
            //现在的文件地址
            if (StringUtils.isNotBlank(apeUser.getAvatar())) {
                String s = apeUser.getAvatar().split("/")[2];
                File now = new File(filePath + System.getProperty("file.separator") + s);
                boolean delete = now.delete();
            }
            //实际的文件地址
            File dest = new File(filePath + System.getProperty("file.separator") + fileName);
            //存储到数据库里的相对文件地址
            String storeImgPath = "/img/"+fileName;
            try {
                avatar.transferTo(dest);
                //更新头像
                apeUser.setAvatar(storeImgPath);
                userService.updateById(apeUser);
                return ApiResponse.success(storeImgPath);
            } catch (IOException e) {
                return ApiResponse.fail("上传失败");
            }
        } else {
            return ApiResponse.fail("请选择正确的图片格式");
        }
    }

    @PostMapping("changePassword")
    public ApiResponse changePassword(@RequestBody JSONObject json) {
        String id = json.getString("id");
        String password = json.getString("password");
        User user = userService.getById(id);
        boolean decrypt = PasswordUtils.decrypt(password, user.getPassword() + "$" + user.getSalt());
        if (decrypt) {
            String newPassword = json.getString("newPassword");
            String encrypt = PasswordUtils.encrypt(newPassword);
            String[] split = encrypt.split("\\$");
            user.setSalt(split[1]);
            user.setPassword(split[0]);
            user.setPwdUpdateDate(new Date());
            boolean update = userService.updateById(user);
            if (update) {
                return ApiResponse.success();
            } else {
                return ApiResponse.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
            }
        } else {
            return ApiResponse.fail("旧密码不正确");
        }
    }

    @GetMapping("getEmailReg")
    public ApiResponse getEmailReg(@RequestParam("email") String email) {
        String str="abcdefghigklmnopqrstuvwxyzABCDEFGHIGKLMNOPQRSTUVWXYZ0123456789";
        Random r=new Random();
        String arr[]=new String [4];
        String reg="";
        for(int i=0;i<4;i++) {
            int n=r.nextInt(62);
            arr[i]=str.substring(n,n+1);
            reg+=arr[i];
        }
        try {
            redisUtils.set(email + "forget",reg.toLowerCase(),60L);
            JavaMailSenderImpl sender = new JavaMailSenderImpl();
            sender.setPort(25);
            sender.setHost("smtp.qq.com");
            sender.setUsername("1760272627@qq.com");
            sender.setPassword("nwavnzopbtpibchc");
            sender.setDefaultEncoding("utf-8");
            MimeMessage msg = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true);
            helper.setFrom(sender.getUsername());
            helper.setTo(email);
            helper.setSubject("旅游信息推荐系统修改密码验证");
            helper.setText("您的邮箱验证码为："+reg,true);
            sender.send(msg);
        }catch (Exception e){
            ApiResponse.fail("邮件发送失败");
        }
        return ApiResponse.success();
    }

    @PostMapping("forgetPassword")
    public ApiResponse forgetPassword(@RequestBody JSONObject jsonObject) {
        String loginAccount = jsonObject.getString("loginAccount");
        String email = jsonObject.getString("email");
        String password = jsonObject.getString("password");
        String code = jsonObject.getString("code").toLowerCase();
        String s = redisUtils.get(email + "forget");
        if (!code.equals(s)) {
            return ApiResponse.fail("验证码错误");
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(User::getLoginAccount,loginAccount).last("limit 1");
        User user = userService.getOne(queryWrapper);
        //密码加盐加密
        String encrypt = PasswordUtils.encrypt(password);
        String[] split = encrypt.split("\\$");
        user.setPassword(split[0]);
        user.setSalt(split[1]);
        boolean update = userService.updateById(user);
        if (update) {
            return ApiResponse.success();
        } else {
            return ApiResponse.fail("密码修改失败");
        }
    }

}
