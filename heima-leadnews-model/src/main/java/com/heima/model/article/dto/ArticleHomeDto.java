package com.heima.model.article.dto;

import lombok.Data;

import java.util.Date;

@Data
public class ArticleHomeDto {
    Date maxBehotTime;

    Date minBehotTime;
    Integer size;
    String tag;

}
