package com.heima.tarticle.test;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.heima.article.ArticleApplication;
import com.heima.article.mapper.ApArticleContentMapper;
import com.heima.article.mapper.ApArticleMapper;
import com.heima.file.service.FileStorageService;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.article.pojos.ApArticleContent;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.nntp.Article;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest(classes = ArticleApplication.class)
@RunWith(SpringRunner.class)

public class ArticleFreeworkTest {
        @Autowired
        private Configuration configuration;
        @Autowired
        private ApArticleMapper apArticleMapper;
        @Autowired
        private ApArticleContentMapper apArticleContentMapper;
         @Autowired
         private FileStorageService fileStorageService;



    @Test
    public void createStaticUrlTest() throws Exception {
        //1.获取文章内容
        ApArticleContent apArticleContent = apArticleContentMapper.selectOne(Wrappers.<ApArticleContent>lambdaQuery()
                .eq(ApArticleContent::getArticleId,1383827888816836609L));

        if(apArticleContent!=null&&StringUtils.isNotBlank(apArticleContent.getContent())){
            //2.文章内容通过freemarker生成html文件
            StringWriter out = new StringWriter();
            Template template=configuration.getTemplate("article.ftl");

            Map<String,Object>map=new HashMap<>();
            map.put("content",JSONArray.parseArray(apArticleContent.getContent()));

            template.process(map,out);

            //3.把html文件上传到minio中
            InputStream is = new ByteArrayInputStream(out.toString().getBytes());
            String url=fileStorageService.uploadHtmlFile("",apArticleContent.getArticleId()+".html",is);

            //4修改apArticle内容
            ApArticle apArticle=new ApArticle();
            apArticle.setId(apArticleContent.getArticleId());
            apArticle.setStaticUrl(url);

            apArticleMapper.updateById(apArticle);


        }


        }
}
