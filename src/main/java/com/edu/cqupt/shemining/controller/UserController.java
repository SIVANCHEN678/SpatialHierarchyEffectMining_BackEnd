package com.edu.cqupt.shemining.controller;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edu.cqupt.shemining.common.AutoLog;
//import com.edu.cqupt.shemining.common.JwtTokenUtils;
import com.edu.cqupt.shemining.common.Result;
import com.edu.cqupt.shemining.mapper.UserMapper;
import com.edu.cqupt.shemining.model.QueryVo.UserQueryVo;
import com.edu.cqupt.shemining.model.User;
import com.edu.cqupt.shemining.model.UserLog;
import com.edu.cqupt.shemining.service.UserLogService;
import com.edu.cqupt.shemining.service.UserService;
import com.edu.cqupt.shemining.util.SecurityUtil;
import com.edu.cqupt.shemining.vo.InsertUserVo;
import com.edu.cqupt.shemining.vo.UpdateStatusVo;
import com.edu.cqupt.shemining.vo.UserPwd;
import com.edu.cqupt.shemining.vo.VerifyUserQ;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Api(tags = "7.用户管理")
@RestController
@RequestMapping("/admin/user")
@CrossOrigin
public class UserController {

    @Autowired
    private UserService userService;

//    @ApiOperation(value = "用户登录")
//    @PostMapping("/login")
//    public Result login(@RequestBody User user) {
//        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(User::getName, user.getName()).eq(User::getPassword, user.getPassword());
//        User userToken = userService.getOne(queryWrapper);
////        String token = JwtTokenUtils.genToken(userToken.getId().toString(), userToken.getPassword());
////        userToken.setToken(token);
////        if (userToken ==null){
////            return Result.fail();
////        }else {
////            return Result.ok(userToken);
////        }
//        if (userToken == null) {
//            return Result.fail();
//        } else {
//            return Result.ok(userToken);
//        }
//    }

    @ApiOperation(value = "用户注册")
    @PostMapping("/register")
    public Result register(@RequestBody User user) {
        String time = DateUtil.now();
        user.setCreateTime(time);
        user.setRole(0);
        boolean save = userService.save(user);
        if (save) {
            return Result.success(200,"注册成功");
        } else {
            return Result.fail(201,"注册失败");
        }
    }


    @ApiOperation(value = "查询所有用户信息")
    @GetMapping("/getAll")
    public List<User> getAll() {
        return userService.list();
    }


    @ApiOperation(value = "删除用户信息")
    @GetMapping("/delete/{id}")
    @AutoLog("删除用户信息")
    public Result delete(@PathVariable int id) {
        boolean b = userService.removeById(id);
        if (b) {
            return Result.success(200,"删除成功");
        } else {
            return Result.fail(201,"删除失败");
        }
    }

    //3条件查询带分页
    @ApiOperation(value = "条件分页")
    @PostMapping("/findUser/{current}/{limit}")
    @AutoLog("查询用户信息")
    public Result findTables(@PathVariable long current,
                                @PathVariable long limit,
                                @RequestBody(required = false) UserQueryVo userQueryVo) {
        //创建page对象，传递当前页，每页记录数
        Page<User> page = new Page<>(current, limit);

        //构建条件
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        String name = userQueryVo.getUserName();
        String userStatus = userQueryVo.getUserStatus();
        Integer role = userQueryVo.getRole();
        if (!name.isEmpty()) {
            wrapper.eq("user_name", userQueryVo.getUserName());
        }if (!userStatus.isEmpty()) {
            wrapper.eq("user_status", userQueryVo.getUserStatus());
        }if (role != null) {
            wrapper.eq("role", userQueryVo.getRole());
        }


        //调用方法实现分页查询
        Page<User> page1 = userService.page(page, wrapper);

        return Result.success(page1);
    }

    /**
     * 用户中心
     */


    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserLogService userLogService;

    /**
     * 获取用户所有信息
     */
    @ApiOperation(value = "！！用户中心——获取用户所有信息")
    @GetMapping("/getmessage/{uid}")
    public Result getall(@PathVariable("uid") Integer uid) {
        User user = userMapper.selectById(uid);
        user.setPassword(null);
        return Result.success(200,"成功获取用户所有信息", user);
    }

    /**
     * 检查用户名是否重复
     *
     * @param userName
     * @return
     */
    @GetMapping("/checkRepetition/{userName}")
    public Result checkRepetition(@PathVariable("userName") String userName) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("user_name", userName);
        User user = userMapper.selectOne(wrapper);
        if(user != null){
            return Result.success(200, "用户名已存在");
        }else {
            return Result.success(200, "用户名可用");
        }
    }

    //修改个人信息
    @ApiOperation(value = "!!用户中心——修改个人信息")
    @PostMapping("/updateUser")
    public Result updateUser(@RequestBody User user) {
        try {
            // 假设 userMapper 是 MyBatis 的一个 Mapper 接口
            String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            user.setUpdateTime(date);

            QueryWrapper<User> wrapper = new QueryWrapper<>();
            wrapper.eq("uid", user.getUid());
            int updatedRows = userMapper.update(user, wrapper);
            //  操作日志记录

            UserLog userLog = new UserLog();
            // userLog.setId(1);
            userLog.setUserName(user.getUserName());
            userLog.setUid(user.getUid());
            userLog.setOpTime(String.valueOf(new Date()));

            if (updatedRows > 0) {
                // 更新成功，返回成功结果
                userLog.setOpType("用户修改个人信息成功");
                userLogService.save(userLog);
//                  "200", "更新成功"
                return Result.success(200,"修改成功");
            } else {

                userLog.setOpType("用户修改个人信息失败");
                userLogService.save(userLog);
                // 更新失败，没有记录被更新
//                "404", "更新失败，用户不存在"
                return Result.fail(201,"修改失败");
            }
        } catch (Exception e) {
            // 处理可能出现的任何异常，例如数据库连接失败等
            // 记录异常信息，根据实际情况决定是否需要发送错误日志
            // 这里返回一个通用的错误信息
//            "500", "更新失败，发生未知错误"
            return Result.fail(500,"系统异常");
        }
    }


////修改密码，根据用户名匹配密码是否正确
    @ApiOperation(value = "!!修改密码，根据用户名匹配密码是否正确")
    @PostMapping("/VerifyPas")
    public Result VerifyPas (@RequestBody Map< String, String > request){
        String userName = request.get("userName");
        String password = request.get("password");
        System.out.println(userName);
        User name = userService.getOne(new QueryWrapper<User>().eq("user_name", userName));

        if (!password.equals(name.getPassword())) {
            return Result.fail(200, "密码不匹配", false);
        }
        return Result.success(200, "密码匹配", true);
    }

    //修改密码
    @ApiOperation(value = "!!修改密码")
    @PostMapping("/updatePas")
    public Result updatePas (@RequestBody Map < String, String > requests){
        try {
            String userName = requests.get("userName");
            String password = requests.get("password");
            // 假设 userMapper 是 MyBatis 的一个 Mapper 接口
            int updatedRows = userMapper.updateByname(password, userName);

            //  操作日志记录

            UserLog userLog = new UserLog();

            QueryWrapper queryWrapper1 = new QueryWrapper<>();
            queryWrapper1.eq("user_name", userName);

            User one = userService.getOne(queryWrapper1);
            Integer uid = one.getUid();
            userLog.setUserName(userName);
            // userLog.setId(1);
            userLog.setUid(uid);
            userLog.setOpTime(String.valueOf(new Date()));


            if (updatedRows > 0) {
                userLog.setOpType("用户修改密码成功");

                userLogService.save(userLog);
                // 更新成功，返回成功结果
                return Result.success(200, "更新成功");//
            } else {
                userLog.setOpType("用户修改密码失败");

                userLogService.save(userLog);
                // 更新失败，没有记录被更新
                return Result.fail(404, "更新失败，用户不存在或密码未更改");//"404", "更新失败，用户不存在或密码未更改"
            }
        } catch (Exception e) {
            String userName = requests.get("userName");
            UserLog userLog = new UserLog();
            QueryWrapper queryWrapper1 = new QueryWrapper<>();
            queryWrapper1.eq("user_name", userName);
            User one = userService.getOne(queryWrapper1);
            Integer uid = one.getUid();
            // userLog.setId(1);
            userLog.setUid(uid);
            userLog.setOpTime(String.valueOf(new Date()));
            userLog.setOpType("用户修改密码失败，发生未知错误");
            userLogService.save(userLog);
            // 处理可能出现的任何异常，例如数据库连接失败等
            // 记录异常信息，根据实际情况决定是否需要发送错误日志
            // 这里返回一个通用的错误信息
            return Result.fail(500, "更新失败，发生未知错误");//
        }
    }

    /**
     * 软件1版本
     *
     */

    @ApiOperation(value = "1.检验用户是否存在")
    @GetMapping("/querUserNameExist")
    public Result querUserNameExist(@RequestParam String userName){
        User existUser = userService.getOne(new QueryWrapper<User>().eq("user_name", userName));
        if (existUser != null){
            return Result.fail(500,"用户已经存在",null);
        }
        return Result.success(200, "用户名可用" , null);
    }

    @ApiOperation(value = "2.注册")
    @PostMapping("/signUp")
    public Result signUp(@RequestBody User user) throws ParseException {

        System.out.println(user);
        // 检查用户名是否已经存在
        user.setUid(0);
        User existUser = userService.getOne(new QueryWrapper<User>().eq("user_name", user.getUserName()));
        if (existUser != null){
            return Result.fail(500,"用户已经存在",null);
        }
        String pwd = user.getPassword();
        // 对密码进行加密处理
        String password = SecurityUtil.hashDataSHA256(pwd);
        user.setUserName(user.getUserName());
        user.setPassword(password);
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        user.setCreateTime(date);
        user.setUpdateTime(null);
        user.setRole(0);
        user.setUid(new Random().nextInt());
        user.setUploadSize(200);
        userService.save(user);
        //  操作日志记录
        UserLog userLog = new UserLog();

        User one = userService.getOne(new QueryWrapper<User>().eq("user_name", user.getUserName()));
        Integer uid = one.getUid();
//       userLog.setId(new Random().nextInt());
        userLog.setUid(uid);
        userLog.setOpTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        userLog.setOpType("用户注册");
        userLogService.save(userLog);
        return Result.success(200,"成功",null);
    }



    @ApiOperation(value = "3.用户登录")
    @PostMapping("/login")
    public Result login(@RequestBody User user, HttpServletResponse response, HttpServletRequest request){

        System.out.println(request);
        // 判断验证编码
        String code = request.getSession().getAttribute("code").toString();
        if(code==null) return Result.fail(500,"验证码已过期！");
        if(user.getCode()==null || !user.getCode().equals(code)) {
            return Result.fail(500, "验证码错误!");
        }

        String userName = user.getUserName();
        User getUser = userService.getOne(new QueryWrapper<User>().eq("user_name", user.getUserName()));
        String password = getUser.getPassword();
        if (getUser != null){
            // 用户状态校验
            // 判断用户是否激活
            if (getUser.getUserStatus().equals("0")){
                return Result.fail("该账户未激活");
            }
            if (getUser.getUserStatus().equals("2")){
                return Result.fail("该账户已经被禁用");
            }

            String userStatus = getUser.getUserStatus();
            if(userStatus.equals("0")){ // 待激活
                return Result.fail(500,"账户未激活！");
            }else if(userStatus.equals("2")){
                return Result.fail(500,"用户已被禁用!");
            }

            // 进行验证密码
            String pwd = user.getPassword();
            String sha256 = SecurityUtil.hashDataSHA256(pwd);
            if (sha256.equals(password)){
                // 验证成功
                UserLog userLog = new UserLog();
                userLog.setUid(getUser.getUid());
                userLog.setOpTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                userLog.setOpType("登录系统");
                userLog.setUserName(userName);
                System.out.println("userlog:"+userLog);
                userLogService.save(userLog);
                // session认证
                HttpSession session = request.getSession();
                session.setAttribute("userName",user.getUserName());
                session.setAttribute("userId",getUser.getUid());
                return Result.success(200,"登录成功",getUser);
            }else {
                return Result.fail(500,"密码错误请重新输入",null);
            }
        }else {
            return Result.fail(500,"用户不存在",null);
        }
    }


    @ApiOperation(value = "4.退出登录")
    @PostMapping("/logout")
    public Result logout(HttpServletRequest request){

        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");
        session.invalidate();
        return Result.success(200,"退出成功",null);
    }


    /**
     * 管理员中心查看得所有用户信息
     *
     * @return
     */
    @ApiOperation(value = "查询任务并分页")
    @GetMapping("/allUser")
    public Result allUser(@RequestParam(defaultValue = "1") int current,
                          @RequestParam(defaultValue = "10") int limit){
        Page<User> page = new Page<>(current, limit);

        //构建条件
        QueryWrapper<User> wrapper = new QueryWrapper<>();

        Page<User> page1 = userService.page(page, wrapper);


        return Result.success(page1);

    }


    @GetMapping("/querUser")
    public List<User> querUser(){

        return userService.list();

    }

    /**
     *
     *  管理员修改用户状态
     * @return
     */
    @PostMapping("updateStatus")
    public Result  updateStatus(@RequestBody UpdateStatusVo updateStatusVo){
        // 根据 id  修改用户状态   角色
        User user = new User();
        user.setRole(updateStatusVo.getUid());
        user.setUploadSize(updateStatusVo.getUploadSize());
        user.setUserStatus(updateStatusVo.getStatus());
        boolean b = userService.save(user);
        if (b) return  Result.success(200 , "修改用户状态成功");
        return  Result.fail("修改失败");
    }


    @PostMapping("delUser")
    public Result delUser(@RequestBody UpdateStatusVo updateStatusVo){

        Integer uid = updateStatusVo.getUid();
        boolean b = userService.removeById(uid);
        if (b) return Result.success(200 , "删除成功");
        return Result.fail(200 , "删除失败");
    }



    // TODO 目前不需要
    @PostMapping("insertUser")
    public Result insertUser(@RequestBody InsertUserVo user) throws ParseException {
        User user1 = new User();
        user1.setUserName(user.getUserName());
        user1.setPassword(user.getPassword());
        user1.setCreateTime(user.getCreateTime());
        user1.setUpdateTime(user.getUpdateTime());
        user1.setRole(user.getRole());
        user1.setUserStatus(user.getUserStatus());
        boolean b = userService.save(user1);
        if (b) return Result.success(200 , "删除成功");
        return Result.fail(200 , "删除失败");
    }


    // 忘记密码功能
    @GetMapping("/queryQuestions")
    public Result  forgotPwd(@RequestParam String userName){
        User user = userService.getOne(new QueryWrapper<User>().eq("user_name", userName));
        String answer1 = user.getAnswer1().split(":")[0];
        String answer2 = user.getAnswer2().split(":")[0];
        String answer3 = user.getAnswer3().split(":")[0];
        List<String> answers = new ArrayList<>();
        answers.add(answer1);
        answers.add(answer2);
        answers.add(answer3);
        return  Result.success(200, "查询用户密保问题成功",answers );
    }


    // 验证问题


    @PostMapping("/verify")
    public Result verify(@RequestBody VerifyUserQ verifyUserQ){
        // 用户名   密保问题 和 答案
        QueryWrapper queryWrapper = new QueryWrapper<>()
                .eq("user_name",verifyUserQ.getUserName())
                .eq("answer_1" , verifyUserQ.getQ1()).eq("answer_2" , verifyUserQ.getQ2()).eq("answer_3" , verifyUserQ.getQ3());
        User user = userService.getOne(queryWrapper);

        if (user == null){
            return Result.fail("验证失败");
        }else {
            return Result.success(200 ," 验证成功，请重置密码");
        }

    }

    @PostMapping("updatePwd")
    public Result  updatePwd(@RequestBody UserPwd user){
        String password = user.getPassword();
        String sha256 = SecurityUtil.hashDataSHA256(password);
        user.setPassword(sha256);
        System.out.println(user);

        User user1 = new User();
        user1.setUserName(user.getUserName());
        user1.setPassword(user.getPassword());
        user1.setUpdateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

        userService.update(user1, new QueryWrapper<User>().eq("user_name", user.getUserName()));
        return Result.success(200 , "修改密码成功");
    }

    /**
     * 5.9添加
     */
    // 新增可共享用户列表
    @GetMapping("/getTransferUserList")
    public Result getTransferUserList(@RequestParam("uid") Integer uid) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.ne("uid", uid);
        List<User> userList = userMapper.selectList(queryWrapper);
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (User user : userList) {
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("key", user.getUid());
            resultMap.put("label", user.getUserName());
            resultList.add(resultMap);
        }
        return  Result.success(200,"获得成功",resultList);
    }


}
