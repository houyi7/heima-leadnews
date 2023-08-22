package com.heima.user.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.user.dtos.LoginDto;
import com.heima.model.user.pojos.ApUser;
import com.heima.user.mapper.ApUserMapper;
import com.heima.user.service.ApUserService;
import com.heima.utils.common.AppJwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
@Slf4j

public class ApUserServiceImpl extends ServiceImpl<ApUserMapper, ApUser> implements ApUserService {
    @Override
    public ResponseResult login(LoginDto dto) {
        //1正常登录 查询用户名和密码
          //1.1 查询根据手机号用户信息
            if(StringUtils.isNoneBlank(dto.getPassword())&&StringUtils.isNotBlank(dto.getPhone())){
                ApUser apuser = getOne(Wrappers.<ApUser>lambdaQuery().eq(ApUser::getPhone, dto.getPhone()));
                if(apuser==null)
                {
                    return ResponseResult.errorResult(AppHttpCodeEnum.AP_USER_DATA_NOT_EXIST);
                }

                //1.2 比对密码
                String salt=apuser.getSalt();
                String ps=dto.getPassword();
                String pswd=DigestUtils.md5DigestAsHex((ps+salt).getBytes());

                if(!pswd.equals(apuser.getPassword())){
                    return ResponseResult.errorResult(AppHttpCodeEnum.LOGIN_PASSWORD_ERROR);
                }
                String token= AppJwtUtil.getToken(apuser.getId().longValue());
                //1.3返回数据
                Map<String,Object>map=new HashMap<>();
                map.put("token",token);
                map.put("user",apuser);
                return ResponseResult.okResult(map);
            }else{
                Map<String,Object>map=new HashMap<>();
                map.put("token",AppJwtUtil.getToken(0L));
                return ResponseResult.okResult(map);
            }



        //2有课登录

    }
}
