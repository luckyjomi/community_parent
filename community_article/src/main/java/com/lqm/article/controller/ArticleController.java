package com.lqm.article.controller;

import com.lqm.article.pojo.Article;
import com.lqm.article.pojo.Comment;
import com.lqm.article.service.ArticleService;
import entity.PageResult;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 控制器层
 *
 * @author Administrator
 */
@RestController
@CrossOrigin
@RequestMapping("/article")
@RefreshScope
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 查询全部数据
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public Result findAll() {
        return new Result(true, StatusCode.OK, "查询成功", articleService.findAll());
    }

    /**
     * 根据ID查询
     *
     * @param id ID
     * @return
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Result findById(@PathVariable String id) {
        //从Redis中查询文章是否存在
        Article article = (Article) redisTemplate.boundValueOps("article_" + id).get();
        //如果不存在，从数据库中查询出来，放入redis
        if(article == null){
            article = articleService.findById(id);
            //存入redis
//            redisTemplate.boundValueOps("article_" + id).set(article);

            redisTemplate.opsForValue().set("article_" + id, article,10, TimeUnit.SECONDS);
        }
        //如果存在，直接返回
        return new Result(true, StatusCode.OK, "查询成功", article);
    }


    /**
     * 分页+多条件查询
     *
     * @param searchMap 查询条件封装
     * @param page      页码
     * @param size      页大小
     * @return 分页结果
     */
    @RequestMapping(value = "/search/{page}/{size}", method = RequestMethod.POST)
    public Result findSearch(@RequestBody Map searchMap, @PathVariable int page, @PathVariable int size) {
        Page<Article> pageList = articleService.findSearch(searchMap, page, size);
        return new Result(true, StatusCode.OK, "查询成功", new PageResult<Article>(pageList.getTotalElements(), pageList.getContent()));
    }

    /**
     * 根据条件查询
     *
     * @param searchMap
     * @return
     */
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public Result findSearch(@RequestBody Map searchMap) {
        return new Result(true, StatusCode.OK, "查询成功", articleService.findSearch(searchMap));
    }

    /**
     * 增加
     *
     * @param article
     */
    @RequestMapping(method = RequestMethod.POST)
    public Result add(@RequestBody Article article) {
        articleService.add(article);
        return new Result(true, StatusCode.OK, "增加成功");
    }

    /**
     * 修改
     *
     * @param article
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public Result update(@RequestBody Article article, @PathVariable String id) {
        article.setId(id);
        articleService.update(article);
        return new Result(true, StatusCode.OK, "修改成功");
    }

    /**
     * 删除
     *
     * @param id
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public Result delete(@PathVariable String id) {
        articleService.deleteById(id);
        return new Result(true, StatusCode.OK, "删除成功");
    }


    /**
     * 审核文章
     * @param articleId
     * @return
     */
    @RequestMapping(value = "/examine/{articleId}", method = RequestMethod.PUT)
    public Result examine(@PathVariable String articleId) {
        articleService.examine(articleId);
        return new Result(true, StatusCode.OK, "审核通过");
    }


    /**
     * 文章点赞
     * @param articleId
     * @return
     */
    @RequestMapping(value = "/thumbup/{articleId}",method = RequestMethod.PUT)
    public Result thumbup(@PathVariable String articleId){
        articleService.thumbup(articleId);
        return new Result(true,StatusCode.OK,"文章点赞");
    }

    /**
     * 文章评论
     * @param articleId
     * @return
     */
    @RequestMapping(value = "/comment/{articleId}",method = RequestMethod.POST)
    public Result addComment(@PathVariable String articleId, @RequestBody Comment comment){
        articleService.addComment(articleId,comment);
        return new Result(true,StatusCode.OK,"增加评论成功");
    }
}
