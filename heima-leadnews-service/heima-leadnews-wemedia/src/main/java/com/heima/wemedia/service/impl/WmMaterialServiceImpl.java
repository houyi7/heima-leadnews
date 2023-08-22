package com.heima.wemedia.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.file.service.FileStorageService;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.dtos.WmMaterialDto;
import com.heima.model.wemedia.pojos.WmMaterial;
import com.heima.utils.thread.WmThreadLocalUtil;
import com.heima.wemedia.mapper.WmMaterialMapper;
import com.heima.wemedia.service.WmMaterialService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.UUID;


@Slf4j
@Service
@Transactional
public class WmMaterialServiceImpl extends ServiceImpl<WmMaterialMapper, WmMaterial> implements WmMaterialService {
    @Autowired
    private FileStorageService fileStorageService;

    public ResponseResult uploadPicture(MultipartFile multipartFile){

       if(multipartFile==null||multipartFile.getSize()==0){
           return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
       }
        //上传图片到minIo中
       String fileName= UUID.randomUUID().toString().replace("-","");

       String originalFilename=multipartFile.getOriginalFilename();
        String substring = originalFilename.substring(originalFilename.lastIndexOf("."));
        String fileId=null;
        try{
            fileId=fileStorageService.uploadImgFile("",fileName+substring,multipartFile.getInputStream());
            log.info("上传成功");
        } catch (Exception e) {
           throw new RuntimeException(e);
       }


        //保存到数据库中
        //3.保存到数据库中
        WmMaterial wmMaterial = new WmMaterial();
        wmMaterial.setUserId(WmThreadLocalUtil.getUser().getId());
        wmMaterial.setUrl(fileId);
        wmMaterial.setIsCollection((short)0);
        wmMaterial.setType((short)0);
        wmMaterial.setCreatedTime(new Date());
        save(wmMaterial);

        //4.返回结果

        return ResponseResult.okResult(wmMaterial);
    }

    @Override
    public ResponseResult findList(WmMaterialDto dto) {
       dto.checkParam();
        IPage page=new Page(dto.getPage(),dto.getSize());
        LambdaQueryWrapper<WmMaterial>queryWrapper=new LambdaQueryWrapper<>();

        //是否收藏
        if(dto.getIsCollection()!=null&&dto.getIsCollection()==1){
            queryWrapper.eq(WmMaterial::getIsCollection,dto.getIsCollection());
        }

        queryWrapper.eq(WmMaterial::getUserId,WmThreadLocalUtil.getUser().getId());
        queryWrapper.orderByAsc(WmMaterial::getCreatedTime);


        page=page(page,queryWrapper);
        ResponseResult responseResult=new PageResponseResult(dto.getPage(),dto.getSize(),(int)page.getTotal());
        responseResult.setData(page.getRecords());
        return responseResult;

    }
}
