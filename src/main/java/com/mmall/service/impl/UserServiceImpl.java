package com.mmall.service.impl;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.common.TokenCache;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service("iuserService")
public class UserServiceImpl implements IUserService {
    @Autowired
    private UserMapper userMapper;
    @Override
    public ServerResponse<User> login(String username, String password) {
        int resultCount=userMapper.checkUsername(username);
        if(resultCount==0){
            return ServerResponse.createByErrorMeaasge("用户名不存在");
        }
        String MD5password=MD5Util.MD5EncodeUtf8(password);
        User user=userMapper.selectlogin(username,MD5password);
        if(user==null){
            return ServerResponse.createByErrorMeaasge("密码错误");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess("登录成功",user);

    }

    public ServerResponse<String> register(User user){
        ServerResponse valibResponse=this.checkValid(user.getUsername(),Const.USERNAME);
        if(!valibResponse.isSuccess()){
            return valibResponse;
        }
        valibResponse=this.checkValid(user.getEmail(),Const.EMAIL);
        if(!valibResponse.isSuccess()){
            return valibResponse;
        }
        user.setRole(Const.Role.ROLE_CUSTOMER);
        //MD5加密
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        int resultCount=userMapper.insert(user);
        if(resultCount==0){
            return ServerResponse.createByErrorMeaasge("注册失败");
        }
        return ServerResponse.createBySuccessMessage("注册成功");

    }

    public ServerResponse<String> checkValid(String str,String type){
        if(type!=null&type!=""){
            if(Const.USERNAME.equals(type)){
                int resultCount=userMapper.checkUsername(str);
                if(resultCount > 0){
                    return ServerResponse.createByErrorMeaasge("用户名已存在");
                }
            }
            if(Const.EMAIL.equals(type)){
                int resultCount=userMapper.checkEmail(str);
                if(resultCount > 0){
                    return ServerResponse.createByErrorMeaasge("email已存在");
                }
            }
        }else {
            return ServerResponse.createByErrorMeaasge("参数错误");
        }
        return ServerResponse.createBySuccessMessage("校验成功");
    }


    public ServerResponse<String> selectQuestion(String username){
        ServerResponse validResponse=this.checkValid(username,Const.USERNAME);
        if(validResponse.isSuccess()){
            //用户名不存在的
            return ServerResponse.createByErrorMeaasge("用户不存在");
        }
        String question=userMapper.selectQuestionByUsername(username);
        if(org.apache.commons.lang3.StringUtils.isNotBlank(question)){
            return ServerResponse.createBySuccessMessage(question);
        }
        return ServerResponse.createByErrorMeaasge("找回密码的问题是空的");
    }

    public ServerResponse<String> checkAnswer(String username,String question,String answer){
        int resultCount=userMapper.checkAnswer(username,question,answer);
        if(resultCount>0){
            //用户回答对问题
            String forgrToken= UUID.randomUUID().toString();
            TokenCache.setkey(TokenCache.TOKEN_PREFIX+username,forgrToken);
            return ServerResponse.createBySuccess(forgrToken);
        }
        return ServerResponse.createByErrorMeaasge("问题的答案错误");
    }

    public ServerResponse<String> forgetResetPassword(String username,String passwordNew,String forgetToken){
        if(org.apache.commons.lang3.StringUtils.isBlank(forgetToken)){
            return ServerResponse.createByErrorMeaasge("参数错误，token需要传递");
        }
        ServerResponse validResponse=this.checkValid(username,Const.USERNAME);
        if(validResponse.isSuccess()){
            //用户名不存在的
            return ServerResponse.createByErrorMeaasge("用户不存在");
        }
        String token=TokenCache.getkey(TokenCache.TOKEN_PREFIX+username);
        if(org.apache.commons.lang3.StringUtils.isBlank(token)){
            return ServerResponse.createByErrorMeaasge("token无效或者过期");
        }
        if(org.apache.commons.lang3.StringUtils.equals(forgetToken,token)){
            String md5Password=MD5Util.MD5EncodeUtf8(passwordNew);
            System.out.println(md5Password);
            int rowCount=userMapper.updatePasswordByUsername(username,md5Password);
            if(rowCount>0){
                return ServerResponse.createBySuccessMessage("修改密码成功");
            }
            else {
                return ServerResponse.createByErrorMeaasge("token错误，请从新获取充值密码的token");
            }
        }
        return ServerResponse.createByErrorMeaasge("修改密码失败");
    }

    public ServerResponse<String> resetPassword(String passwordOld,String passwordNew,User user){
        //防止横向越权，检查一下用户的旧密码，一定要指向这个用户，因为我们会查询一个COUNT(1)，如果不指定id，那么结果就是true
        int resultCount=userMapper.checkPassword(MD5Util.MD5EncodeUtf8(passwordOld),user.getId());
        if(resultCount==0){
            return ServerResponse.createByErrorMeaasge("旧密码错误");
        }
        user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
        int updateCount=userMapper.updateByPrimaryKeySelective(user);
        if(updateCount>0){
            return ServerResponse.createBySuccessMessage("密码更新成功");
        }
        return ServerResponse.createByErrorMeaasge("密码更新失败");
    }

    public ServerResponse<User> updateIformation(User user){
        int resultCount=userMapper.checkEmailByUserId(user.getEmail(),user.getId());
        if(resultCount>0){
            return  ServerResponse.createByErrorMeaasge("email已被使用，请重新输入新的email再次进行尝试");
        }
        User updateUser=new User();
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());
        int updateCount=userMapper.updateByPrimaryKeySelective(updateUser);
        if(updateCount>0){
            return ServerResponse.createBySuccess("个人信息更新成功",updateUser);
        }
        return ServerResponse.createByErrorMeaasge("个人信息更新失败");
    }

    public ServerResponse<User> getInformation(Integer userId){
        User user=userMapper.selectByPrimaryKey(userId);
        if(user==null){
            return ServerResponse.createByErrorMeaasge("找不大当前用户");
        }
        user.setPassword(org.apache.commons.lang3.StringUtils.EMPTY);
        return ServerResponse.createBySuccess(user);
    }

    //endback
    public  ServerResponse checkAdminRole(User user){
        if(user!=null&&user.getRole().intValue()==Const.Role.ROL_ADMIN){
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }
}
