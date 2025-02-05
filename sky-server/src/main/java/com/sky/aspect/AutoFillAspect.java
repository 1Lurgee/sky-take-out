package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 实现公共字段自动填充的一个切面类
 */
@Component
@Aspect
@Slf4j
public class AutoFillAspect {

    /*表示mapper下面的所欲哦函数，第一个*是指不限制修饰符和返回类型。
     * 第二个*代表当前包的所有类
     * .*(..)：第一个 * 表示匹配任意方法名称，而 (..) 表示该方法接受任意数量和类型的参数。*/

    /**
     * 切面类
     */
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointCut() {
    }

    /**
     * 自动填充公共字段
     *
     * @param joinPoint 连接点
     */
    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint) throws NoSuchMethodException {
        log.info("开启公共字段的自动填充");
        //获取方法类型，获取注解参数，判断是update还是insert
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        AutoFill annotation = signature.getMethod().getAnnotation(AutoFill.class);
        //获取参数
        Object[] args = joinPoint.getArgs();
        //判断参数是否为空或者没有参数
        if (args.length == 0 || args[0] == null) {
            return;
        }
        Object entity = args[0];

        //准备赋值的数据
        LocalDateTime now = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();

        //获取参数里的set方法，通过反射进行属性的赋值
        Class<?> entityClass = entity.getClass();
        Method setUpdateTime = entityClass.getMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
        Method setUpdateUser = entityClass.getMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
        if(annotation.value() == OperationType.INSERT) {
                try {
                    Method setCreateTime = entityClass.getMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                    Method setCreateUser = entityClass.getMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
                    //使用set函数赋值
                    setUpdateTime.invoke(entity,now);
                    setUpdateUser.invoke(entity,currentId);
                    setCreateUser.invoke(entity,currentId);
                    setCreateTime.invoke(entity,now);
                } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
        }else if(annotation.value() == OperationType.UPDATE){
                try {
                    setUpdateTime.invoke(entity,now);
                    setUpdateUser.invoke(entity,currentId);
                } catch (InvocationTargetException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
        }


    }
}
