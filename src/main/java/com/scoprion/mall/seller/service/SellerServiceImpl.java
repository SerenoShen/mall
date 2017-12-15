package com.scoprion.mall.seller.service;

import com.alibaba.druid.util.StringUtils;
import com.scoprion.constant.Constant;
import com.scoprion.enums.CommonEnum;
import com.scoprion.mall.backstage.mapper.FileOperationMapper;
import com.scoprion.mall.backstage.mapper.RoleMapper;
import com.scoprion.mall.backstage.mapper.UserMapper;
import com.scoprion.mall.domain.MallImage;
import com.scoprion.mall.domain.MallUser;
import com.scoprion.mall.domain.Seller;
import com.scoprion.mall.domain.SysRole;
import com.scoprion.mall.seller.mapper.SellerMapper;
import com.scoprion.result.BaseResult;
import com.scoprion.utils.EncryptUtil;
import com.scoprion.utils.SellerNoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.SimpleTimeZone;
import java.util.concurrent.TimeUnit;


/**
 * @author by hmy
 * @created on 2017/12/7/007.
 */
@SuppressWarnings("ALL")
@Service
public class SellerServiceImpl implements SellerService {
    @Autowired
    private SellerMapper sellerMapper;

    @Autowired
    private FileOperationMapper fileOperationMapper;

    @Resource
    private RedisTemplate redisTemplate;

    @Autowired
    RoleMapper roleMapper;

    /**
     * 商户店铺建立
     *
     * @param seller
     * @return
     * @throws Exception
     */
    @Override
    public BaseResult add(Seller seller) {
        if (seller.getUserId() == null) {
            return BaseResult.parameterError();
        }
        Integer validResult = sellerMapper.validCertification(seller.getUserId());
//        if (validResult == 0) {
//            return BaseResult.error("add_error", "未实名认证，不能创建店铺");
//        }
        Integer validByUserResult = sellerMapper.validByUserId(seller.getUserId());
        if (validByUserResult > 0) {
            return BaseResult.error("ERROR", "不可重复创建店铺");
        }
        Integer validNameResult = sellerMapper.validByName(seller.getSellerName());
        if (validNameResult > 0) {
            return BaseResult.error("ERROR", "店铺名称已存在");
        }
        seller.setSellerNo(SellerNoUtil.getSellerNo());
        Integer result = sellerMapper.add(seller);
        if (result <= 0) {
            return BaseResult.error("ERROR", "新增店铺失败");
        }
        return BaseResult.success("新增店铺成功");
    }

    /**
     * 删除店铺
     *
     * @param id
     * @param status 店铺状态 NORMAL 正常 ,
     *               CLOSE_LEADER 管理员关闭,
     *               CLOSE 关闭，
     *               DELETE 删除状态
     * @param operator 操作人员
     * @return
     */
    @Override
    public BaseResult updateStatus(Long id, String status,String operator) {
        if (StringUtils.isEmpty(id.toString())) {
            return BaseResult.parameterError();
        }
        Seller seller = sellerMapper.findById(id);
        if (CommonEnum.DELETE.getCode().equals(seller.getStatus())) {
            //已经删除的，不能修改
            return BaseResult.error("update_error", "已经删除的，不能修改");
        }
        if (CommonEnum.CLOSE_LEADER.getCode().equals(seller.getStatus())) {
            //被管理员关闭，不能修改
            return BaseResult.error("update_error", "被管理员关闭，不能修改");
        }
        if (CommonEnum.PLATFORM.getCode().equals(operator)){
            Integer updateStatus=sellerMapper.updateStatusAndAudit();
        }
//        if (CommonEnum.CLOSE_LEADER.getCode().equals(seller.getStatus())){
//            int auditResult=sellerMapper.updateAudit(id);
//            if (auditResult<0){
//                return BaseResult.error("ERROR","审核状态修改失败");
//            }
//        }
        int result = sellerMapper.updateStatus(status, id);
        if (result <= 0) {
            return BaseResult.error("ERROR", "修改失败");
        }
        return BaseResult.success("修改成功");
    }

    /**
     * 修改店铺信息
     *
     * @param seller
     * @return
     */
    @Override
    public BaseResult modify(Seller seller) {
        seller.setAudit(CommonEnum.AUDITING.getCode());
        int result = sellerMapper.modify(seller);
        if (result <= 0) {
            return BaseResult.error("ERROR", "修改失败");
        }
        return BaseResult.success("修改成功");
    }

    /**
     * 注册
     *
     * @param mallUser
     * @return
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public BaseResult register(MallUser mallUser, String ip) throws Exception {
        if (mallUser == null) {
            return BaseResult.parameterError();
        }
        if (StringUtils.isEmpty(mallUser.getMobile())) {
            return BaseResult.parameterError();
        }
        if (StringUtils.isEmpty(mallUser.getPassword())) {
            return BaseResult.parameterError();
        }
        Integer nickNameResult = sellerMapper.validByNickName(mallUser.getNickName());
        if (nickNameResult > 0) {
            return BaseResult.error("ERROR", "该昵称已存在");
        }
        if (mallUser.getPassword().length() < Constant.PASSWORD_MIN_LENGTH) {
            return BaseResult.error("ERROR", "密码长度不能小于六位");
        }
        if (mallUser.getMobile().length() < Constant.MOBILE_LENGTH) {
            return BaseResult.error("ERROR", "手机号码有误");
        }
        Integer mobileResult = sellerMapper.validByMobile(mallUser.getMobile());
        if (mobileResult > 0) {
            return BaseResult.error("ERROR", "手机号已存在");
        }
        String password = mallUser.getPassword();
        String encryptPassword = EncryptUtil.encryptMD5(password);
        mallUser.setPassword(encryptPassword);
        if (StringUtils.isEmpty(mallUser.getIdPhotoBgUrl()) &&
                StringUtils.isEmpty(mallUser.getIdPhotoFrontUrl())) {
            mallUser.setCertification(CommonEnum.NOT_AUTH.getCode());
        } else {
            mallUser.setCertification(CommonEnum.AUDITING.getCode());
        }
        Integer result = sellerMapper.register(mallUser);
        if (result <= 0) {
            return BaseResult.error("ERROR", "注册失败");
        }
        //存储证件照片
        saveIdPhotoImage(mallUser);
        //绑定商户角色
        bindRole(mallUser);
        //将用户手机号码作为加密字符串回传
        String tokenStr = EncryptUtil.aesEncrypt(mallUser.getMobile(), "ScorpionMall8888");
        mallUser.setToken(tokenStr);
        return BaseResult.success(tokenStr);
    }

    /**
     * 绑定商户角色
     *
     * @param mallUser
     */
    private void bindRole(MallUser mallUser) {
        SysRole role = roleMapper.findSellerRole();
        //查询普通角色
        if (role != null) {
            roleMapper.addRoleRelation(mallUser.getId(), role.getId());
        }
    }

    /**
     * 存储证件照片
     *
     * @param user
     */
    private void saveIdPhotoImage(MallUser user) {
        System.out.println("存储证件照片: " + user.toString());
        if (!StringUtils.isEmpty(user.getIdPhotoFrontUrl())) {
            MallImage mallImage = new MallImage();
            mallImage.setIdPhotoOwnerId(user.getId());
            mallImage.setUrl(user.getIdPhotoFrontUrl());
            fileOperationMapper.add(mallImage);
        }
        if (!StringUtils.isEmpty(user.getIdPhotoBgUrl())) {
            MallImage mallImage = new MallImage();
            mallImage.setIdPhotoOwnerId(user.getId());
            mallImage.setUrl(user.getIdPhotoBgUrl());
            fileOperationMapper.add(mallImage);
        }
    }

    /**
     * 修改个人信息
     *
     * @param mallUser
     * @return
     */
    @Override
    public BaseResult alter(MallUser mallUser) {
        Integer result = sellerMapper.validByNickName(mallUser.getNickName());
        if (result > 0) {
            return BaseResult.error("ERROR", "该昵称已存在");
        }
        Integer alterResult = sellerMapper.alter(mallUser);
        if (alterResult <= 0) {
            return BaseResult.error("ERROR", "修改失败");
        }
        return BaseResult.success("修改成功");
    }

    /**
     * 退出登录
     *
     * @param mobile
     * @param email
     * @return
     */
    @Override
    public BaseResult logout(String mobile, String email) {
        if (StringUtils.isEmpty(mobile)) {
            Boolean emailResult = redisTemplate.hasKey("Login:" + email);
            if (emailResult) {
                return BaseResult.success("退出成功");
            }
            return BaseResult.error("ERROR", "没有该账号");
        }
        Boolean result = redisTemplate.hasKey("Login:" + mobile);
        if (result) {
            redisTemplate.delete("Login:" + mobile);
            return BaseResult.success("退出成功");
        }
        return BaseResult.error("ERROR", "没有该账号");
    }

    /**
     * 店铺详情
     *
     * @param userId
     * @return
     */
    @Override
    public BaseResult findByUserId(Long userId) {
        if (userId == null) {
            return BaseResult.parameterError();
        }
        Seller seller = sellerMapper.findByUserId(userId);
        if (seller == null) {
            return BaseResult.notFound();
        }
        return BaseResult.success(seller);
    }

    /**
     * 微信商户登录
     *
     * @param mallUser
     * @param ip
     * @return
     */
    @Override
    public BaseResult login(MallUser mallUser, String ip) throws Exception {
        if (StringUtils.isEmpty(mallUser.getMobile()) && StringUtils.isEmpty(mallUser.getEmail())) {
            return BaseResult.parameterError();
        }
        if (StringUtils.isEmpty(mallUser.getPassword())) {
            return BaseResult.parameterError();
        }
        if (mallUser.getMobile() != null) {
            if (mallUser.getMobile().length() != Constant.MOBILE_LENGTH) {
                return BaseResult.error("ERROR", "输入的手机号码小于十一位");
            }
        } else if (mallUser.getEmail() != null) {
            boolean matchResult = mallUser.getEmail().matches(Constant.REGEX);
            if (!matchResult) {
                return BaseResult.error("ERROR", "请输入正确的邮箱格式");
            }
        } else {
            return BaseResult.parameterError();
        }
        if (mallUser.getPassword().length() < Constant.PASSWORD_MIN_LENGTH) {
            return BaseResult.error("ERROR", "输入的密码小于六位");
        }
        //MD5加密
        String encryptPassword = EncryptUtil.encryptMD5(mallUser.getPassword());

        //登录
        MallUser user = sellerMapper.login(mallUser.getEmail(), mallUser.getMobile(), encryptPassword);
        if (user == null) {
            return BaseResult.error("登录失败", "输入的账号或邮箱或密码错误");
        }
        Long id = user.getId();
        //更新商户最后登录ip地址
        sellerMapper.updateLoginIpAddress(id, ip);
        //将用户手机号作为加密字符回传
        String tokenStr = EncryptUtil.aesEncrypt(user.getMobile(), "ScoprionMall8888");
        mallUser.setToken(tokenStr);
        //设置用户登录有效期为30分钟
        redisTemplate.opsForValue().set("Login:" + user.getMobile(), user.toString(), 30, TimeUnit.MINUTES);
        return BaseResult.success(mallUser);
    }

    public static void setAge(MallUser mallUser){
        int leg=mallUser.getCertificateId().length();
        String dates="";
        if (leg==18){
            int se=Integer.valueOf(mallUser.getCertificateId().substring(leg-1)) % 2;
            dates=mallUser.getCertificateId().substring(6,10);
            SimpleDateFormat df=new SimpleDateFormat("yyyy");
            String year=df.format(new Date());
            int u=Integer.parseInt(year)-Integer.parseInt(dates);
        }else {

        }
    }
}

