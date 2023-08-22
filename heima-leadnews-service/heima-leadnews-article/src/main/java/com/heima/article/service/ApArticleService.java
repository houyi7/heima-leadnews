package com.heima.article.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.article.controller.v1.ArticleHomeControler;
import com.heima.model.article.dto.ArticleDto;
import com.heima.model.article.dto.ArticleHomeDto;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.common.dtos.ResponseResult;
import org.springframework.web.bind.annotation.RequestBody;

public interface ApArticleService extends IService<ApArticle> {
    public ResponseResult load(ArticleHomeDto dto, Short type);
    public ResponseResult saveArticle( ArticleDto dto);
}
