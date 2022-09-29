package com.atguigu.yygh.hosp.controller;

import com.atguigu.yygh.common.result.R;
import com.atguigu.yygh.hosp.service.HospitalSetService;
import com.atguigu.yygh.model.hosp.HospitalSet;
import com.atguigu.yygh.vo.hosp.HospitalSetQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;


@Api(description = "医院设置接口")
@CrossOrigin
@RestController
@RequestMapping("/admin/hosp/hospitalSet")
public class HospitalSetController {

    @Autowired
    HospitalSetService hospitalSetService;

    /**
     * 锁定/解锁
     * @param id
     * @param status
     * @return
     */
    @PutMapping("lockHospitalSet/{id}/{status}")
    public R lockHospitalSet(@PathVariable("id") Long id ,@PathVariable("status") Integer status)
    {
        // status:  0  1
        if(status!=0 && status != 1){
            throw new RuntimeException("status只能为1或0");
        }

        //根据id查询医院设置信息
        HospitalSet hospitalSet = hospitalSetService.getById(id);

        if(hospitalSet==null){
            //医院设置不存在
            throw new RuntimeException("医院设置不存在");
        }

        //设置状态
        hospitalSet.setStatus(status);

        //调用方法
        hospitalSetService.updateById(hospitalSet);

//        hospitalSetService.save()  添加  ，   mongodb数据库中有一个save方法--有id是修改，没有id是新增

        return R.ok();
    }

    /**
     * 批量删除（逻辑删除）
     * @param ids   [ 1,2,5 ]
     * @return
     */
    @DeleteMapping("batchRemove")
    public R batchRemove(@RequestBody List<Long> ids)
    {
        boolean b = hospitalSetService.removeByIds(ids);
        //hospitalSetService.getBaseMapper().deleteBatchIds();
       return b?R.ok():R.error();
    }

    /**
     * 修改id修改医院设置
     * @param hospitalSet
     * @return
     */
    @PostMapping("updateHospSet")
    public R update(@RequestBody HospitalSet hospitalSet){
        if(hospitalSet==null || hospitalSet.getId()==null){
            throw new RuntimeException("参数不能为空");
        }
        boolean b = hospitalSetService.updateById(hospitalSet);
        return b?R.ok():R.error();
    }

    /**
     * 根据id查询医院设置
     * @param id
     * @return
     */
    @GetMapping("getHospSet/{id}")
    public R getById(@PathVariable("id") Long id){
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        return R.ok().data("item",hospitalSet);
    }


    /**
     * 开通医院设置
     * @param hospitalSet
     * @return
     */
    @PostMapping("saveHospSet")
    public R save(@RequestBody HospitalSet hospitalSet){
        hospitalSet.setStatus(1);//将医院设置的状态改成  1 正常， 0 锁定
        hospitalSetService.save(hospitalSet);
        return R.ok();
    }


    /**
     * 带条件的分页查询
     * @param page  查询第几页
     * @param limit  每页显示多少条
     * @param queryVo  查询条件（医院名称（模糊）  医院编号（等值））
     * @return
     */
    @PostMapping("{page}/{limit}")
    public R pageQuery(@PathVariable(value = "page") Long page , @PathVariable("limit")  Long limit ,
                       @RequestBody HospitalSetQueryVo queryVo){

        //1、封装分页参数
        page = page==null?1:page;
        limit = limit==null?10:limit;
        Page<HospitalSet> pageParam = new Page<>(page,limit);

        //2、封装查询条件
        QueryWrapper<HospitalSet> queryWrapper = new QueryWrapper<>();

        //拼接条件时，一定先参数校验，字段值不等于空的情况，再拼条件
        if (queryVo!=null){
            if(!StringUtils.isEmpty(queryVo.getHosname())){
                queryWrapper.like("hosname",queryVo.getHosname());
            }
            if(!StringUtils.isEmpty(queryVo.getHoscode())){
                queryWrapper.eq("hoscode",queryVo.getHoscode());
            }
        }

        //3、调用service中预置的page方法（两个参数）
        hospitalSetService.page(pageParam,queryWrapper);

        //4、获取返回值，数据集合rows + 总记录数total
        List<HospitalSet> records = pageParam.getRecords();
        long total = pageParam.getTotal();

        //5、返回R对象
        return R.ok().data("total",total).data("rows",records);
    }


    @GetMapping("{page}/{limit}")
    public R pageList(@PathVariable(value = "page") Long page ,@PathVariable("limit")  Long limit){

        if(page==null){
            page = 1L;
        }
        if(limit==null){
            limit = 10L;
        }

        Page<HospitalSet> pageParam = new Page<>(page,limit);
        hospitalSetService.page(pageParam);//selectPage(page, queryWrapper)

        //解析返回值（当前页数据集合 + 总记录数）
        List<HospitalSet> records = pageParam.getRecords();
        long total = pageParam.getTotal();

        return R.ok().data("total",total).data("rows",records);
    }


    /**
     * 无条件，无分页  查询所有医院设置
     * @return
     */
    @ApiOperation(value = "查询所有医院设置")
    @GetMapping("findAll")
    public R findAll(){
        List<HospitalSet> list = hospitalSetService.list();
//        R r = R.ok();
//        r.data("list",list);
//        return r;
        return R.ok().data("list",list);
    }

    /**
     * 逻辑删除
     * @param id
     * @return
     */
    @ApiOperation(value = "根据id删除")
    @DeleteMapping("{id}")
    public R removeById( @ApiParam(name = "id",value = "医院设置主键") @PathVariable("id") Long id){
        boolean b = hospitalSetService.removeById(id);
        return b?R.ok():R.error();
    }

}

/*
package com.atguigu.yygh.hosp.controller;

import com.atguigu.yygh.common.result.R;
import com.atguigu.yygh.model.hosp.HospitalSet;
import com.atguigu.yygh.hosp.service.HospitalSetService;
import com.atguigu.yygh.vo.hosp.HospitalSetQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

*/
/**
 * @createTime : 2022/9/29 12:28
 *//*

//医院接口设置
@Api(description = "医院设置接口")
@CrossOrigin //跨域
@RestController
@RequestMapping("/admin/hosp/hospitalSet")
public class HospitalSetController {

    @Autowired
    private HospitalSetService hospitalSetService;

    //查询所有医院设置
    @ApiOperation(value="医院设置列表")
    @GetMapping("findAll")
    public R findAll() {
        List<HospitalSet> list = hospitalSetService.list();
        return R.ok().data("list",list);
    }

    */
/**
     * @Autowired(required=true)：
     * 当使用@Autowired注解的时候，其实默认就是@Autowired(required=true)，
     * 表示注入的时候，该bean必须存在，否则就会注入失败。
     * @param id
     * @return
     *//*

    //逻辑删除
    @ApiOperation(value = "医院设置删除")
    @DeleteMapping("{id}")
    public R removeById(@ApiParam(name="id",value="医院设置编号",required = true) @PathVariable Long id){
        boolean b = hospitalSetService.removeById(id);
        return R.ok();
    }

    //开通医院设置
    @ApiOperation(value="开通医院设置")
    @PostMapping("saveHospSet")
    public R save(@RequestBody HospitalSet hospitalSet)
    {
        //设置状态  1.正常  0.锁定
        hospitalSet.setStatus(1);
        hospitalSetService.save(hospitalSet);

        return R.ok();
    }

    */
/**
     * 根据id查询医院设置
     * @param id
     * @return
     *//*

    @ApiOperation(value="根据id查询医院设置")
    @GetMapping("getHospSet/{id}")
    public R getById(@PathVariable("id") Long id){
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        return R.ok().data("item",hospitalSet);
    }

    @ApiOperation(value="根据id修改医院设置")
    @PostMapping("undateHospSet")
    public R update(@RequestBody HospitalSet hospitalSet){
        if (hospitalSet == null || hospitalSet.getId() == null){
            throw new RuntimeException("参数不能为空");
        }

        boolean b = hospitalSetService.updateById(hospitalSet);
        return b?R.ok():R.error();
    }

    */
/**
     * 带条件的分页查询
     * @param page 查询第几页
     * @param limit 每页显示多少条数据
     * @param queryVo 查询条件 （医院名称（模糊））  医院编号
     * @return
     *//*

    @GetMapping("{page}/{limit}")//哪一页的几条数据
    public R pageQuery(@PathVariable("page") Long page, @PathVariable("limit") Long limit,
                       @RequestBody HospitalSetQueryVo queryVo)
    {
       //1。封装分页参数
        page = page == null?1:limit;
        limit= limit == null?10:limit;
        Page<HospitalSet> pageParam = new Page<>(page,limit);

        //2.封装查询条件
        QueryWrapper<HospitalSet> queryWrapper = new QueryWrapper<>();

        //拼接条件时，一定·要先校验参数，字段值不等于空的情况下，再拼接条件
        if (queryVo != null){
            if (!StringUtils.isEmpty(queryVo.getHosname())){
                queryWrapper.like("hosname", queryVo.getHosname());
            }
            if (!StringUtils.isEmpty(queryVo.getHoscode())){
                queryWrapper.eq("hoscode", queryVo.getHoscode());
            }
        }

        //3.调用service中预制的page方法（两个参数）
        hospitalSetService.page(pageParam,queryWrapper);

        //4.获取返回值，数据集合rows + 总记录数total
        List<HospitalSet> records = pageParam.getRecords();
        long total = pageParam.getTotal();

        //5.返回R对象
        return R.ok().data("total",total).data("rows",records);
    }

    //无条件分页查询 2

    */
/**
     *   Page<User> page = new Page(1,3);
     *     Page<User> userPage = userMapper.selectPage(page, null);
     *     //返回对象得到分页所有数据
     *     long pages = userPage.getPages(); //总页数
     *     long current = userPage.getCurrent(); //当前页
     *     List<User> records = userPage.getRecords(); //查询数据集合
     *     long total = userPage.getTotal(); //总记录数
     *     boolean hasNext = userPage.hasNext();  //下一页
     *     boolean hasPrevious = userPage.hasPrevious(); //上一页
     * @param page
     * @param limit
     * @return
     *//*

//    @GetMapping("{page}/{limit}")//哪一页的几条数据
//    public R pageList(@PathVariable("page") Long page, @PathVariable("limit") Long limit)
//    {
//        if(page == null){
//            page = 1L;
//        }
//        if(limit ==null){
//            limit = 10L;
//        }
//        Page<HospitalSet> pageParam = new Page<>(page,limit);
//        hospitalSetService.page(pageParam,null);
//
//        List<HospitalSet> rows = pageParam.getRecords();//userPage.getRecords(); //查询数据集合
//        long total = pageParam.getTotal();//userPage.getTotal(); //总记录数
//
//        return R.ok().data("total",total).data("rows",rows);
//    }



}*/
