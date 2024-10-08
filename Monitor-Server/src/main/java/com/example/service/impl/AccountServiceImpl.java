package com.example.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.dto.Account;
import com.example.entity.vo.request.*;
import com.example.entity.vo.response.SubAccountVO;
import com.example.mapper.AccountMapper;
import com.example.service.AccountService;
import com.example.utils.Const;
import com.example.utils.FlowUtils;
import jakarta.annotation.Resource;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 账户信息处理相关服务
 */
@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements AccountService {

    //验证邮件发送冷却时间限制，秒为单位
    @Value("${spring.web.verify.mail-limit}")
    int verifyLimit;

    @Resource
    AmqpTemplate rabbitTemplate;

    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Resource
    PasswordEncoder passwordEncoder;

    @Resource
    FlowUtils flow;

    /**
     * 从数据库中通过用户名或邮箱查找用户详细信息
     * @param username 用户名
     * @return 用户详细信息
     * @throws UsernameNotFoundException 如果用户未找到则抛出此异常
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = this.findAccountByNameOrEmail(username);
        if(account == null)
            throw new UsernameNotFoundException("用户名或密码错误");
        return User
                .withUsername(username)
                .password(account.getPassword())
                .roles(account.getRole())
                .build();
    }

    /**
     * 生成注册验证码存入Redis中，并将邮件发送请求提交到消息队列等待发送
     * @param type 类型
     * @param email 邮件地址
     * @param address 请求IP地址
     * @return 操作结果，null表示正常，否则为错误原因
     */
    public String registerEmailVerifyCode(String type, String email, String address){
        synchronized (address.intern()) {
            if(!this.verifyLimit(address))
                return "请求频繁，请稍后再试";
            Random random = new Random();
            int code = random.nextInt(899999) + 100000;
            Map<String, Object> data = Map.of("type",type,"email", email, "code", code);
            rabbitTemplate.convertAndSend(Const.MQ_MAIL, data);
            stringRedisTemplate.opsForValue()
                    .set(Const.VERIFY_EMAIL_DATA + email, String.valueOf(code), 3, TimeUnit.MINUTES);
            return null;
        }
    }

    /**
     * 邮件验证码重置密码操作，需要检查验证码是否正确
     * @param info 重置基本信息
     * @return 操作结果，null表示正常，否则为错误原因
     */
    @Override
    public String resetEmailAccountPassword(EmailResetVO info) {
        String verify = resetConfirm(new ConfirmResetVO(info.getEmail(), info.getCode()));
        if(verify != null) return verify;
        String email = info.getEmail();
        String password = passwordEncoder.encode(info.getPassword());
        boolean update = this.update().eq("email", email).set("password", password).update();
        if(update) {
            this.deleteEmailVerifyCode(email);
        }
        return update ? null : "更新失败，请联系管理员";
    }

    /**
     * 重置密码确认操作，验证验证码是否正确
     * @param info 验证基本信息
     * @return 操作结果，null表示正常，否则为错误原因
     */
    @Override
    public String resetConfirm(ConfirmResetVO info) {
        String email = info.getEmail();
        String code = this.getEmailVerifyCode(email);
        if(code == null) return "请先获取验证码";
        if(!code.equals(info.getCode())) return "验证码错误，请重新输入";
        return null;
    }

    /**
     * @description: 更改密码
     * @param: [vo, uid]
     * @return: boolean
     * @author Ll
     * @date: 2024/8/13 上午11:29
     */
    @Override
    public boolean changePassword(ChangePasswordVO vo, int uid) {
        Account account = this.getById(uid);
        String oldPassword = account.getPassword();
        if(!passwordEncoder.matches(vo.getPassword(), oldPassword))
            return false;
        this.update(Wrappers.<Account>update().eq("id",uid)
                .set("password",passwordEncoder.encode(vo.getNew_password())));
        return true;
    }

    /**
     * @description: 创建子用户
     * @param: [vo]
     * @return: void
     * @author Ll
     * @date: 2024/8/13 下午12:40
     */
    @Override
    public void createSubAccount(CreateSubAccountVO vo) {
        Account account = this.findAccountByNameOrEmail(vo.getEmail());
        if(account != null)
            throw new IllegalArgumentException("此邮箱已被注册");
        account = this.findAccountByNameOrEmail(vo.getUsername());
        if(account != null)
            throw new IllegalArgumentException("此用户名已被使用");
        account = new Account(null, vo.getUsername(), passwordEncoder.encode(vo.getPassword()),
                vo.getEmail(), Const.ROLE_NORMAL, JSONArray.copyOf(vo.getClients()).toJSONString(), new Date());
        this.save(account);
    }

    /**
     * @description: 删除子账户
     * @param: [uid]
     * @return: void
     * @author Ll
     * @date: 2024/8/13 下午12:52
     */
    @Override
    public void deleteSubAccount(int uid) {
        this.removeById(uid);
    }

    /**
     * @description: 列表所有子账户
     * @param: []
     * @return: java.util.List<com.example.entity.vo.response.SubAccountVO>
     * @author Ll
     * @date: 2024/8/13 下午12:55
     */
    @Override
    public List<SubAccountVO> listSubAccount() {
        return this.list(Wrappers.<Account>query().eq("role", Const.ROLE_NORMAL))
                .stream().map((account) -> {
                    SubAccountVO vo = account.asViewObject(SubAccountVO.class);
                    vo.setClientList(JSONArray.parse(account.getClients()));
                    return vo;
                }).toList();
    }

    /**
     * @description: 更改邮箱(检查验证码是否正确)
     * @param: [uid, vo]
     * @return: void
     * @author Ll
     * @date: 2024/8/14 下午3:46
     */
    @Override
    public String modifyEmail(int uid, ModifyEmailVO vo) {
        String code = this.getEmailVerifyCode(vo.getEmail());
        if(code == null) return "请先获取验证码！";
        if(!code.equals(vo.getCode())) return "验证码错误，请重新输入";
        Account account = this.findAccountByNameOrEmail(vo.getEmail());
        if(account != null && account.getId() != uid) return "该邮箱已被其他用户使用，请重新输入其他邮箱";
        this.deleteEmailVerifyCode(vo.getEmail());
        this.update()
                .eq("id", uid)
                .set("email", vo.getEmail())
                .update();
        return null;
    }

    /**
     * 移除Redis中存储的邮件验证码
     * @param email 电邮
     */
    private void deleteEmailVerifyCode(String email){
        String key = Const.VERIFY_EMAIL_DATA + email;
        stringRedisTemplate.delete(key);
    }

    /**
     * 获取Redis中存储的邮件验证码
     * @param email 电邮
     * @return 验证码
     */
    private String getEmailVerifyCode(String email){
        String key = Const.VERIFY_EMAIL_DATA + email;
        return stringRedisTemplate.opsForValue().get(key);
    }

    /**
     * 针对IP地址进行邮件验证码获取限流
     * @param address 地址
     * @return 是否通过验证
     */
    private boolean verifyLimit(String address) {
        String key = Const.VERIFY_EMAIL_LIMIT + address;
        return flow.limitOnceCheck(key, verifyLimit);
    }

    /**
     * 通过用户名或邮件地址查找用户
     * @param text 用户名或邮件
     * @return 账户实体
     */
    public Account findAccountByNameOrEmail(String text){
        return this.query()
                .eq("username", text).or()
                .eq("email", text)
                .one();
    }

}
