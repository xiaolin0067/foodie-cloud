package com.zlin.user.controller.center;

import com.zlin.controller.BaseController;
import com.zlin.enums.CacheKey;
import com.zlin.enums.ImageFileSuffix;
import com.zlin.pojo.Result;
import com.zlin.user.pojo.Users;
import com.zlin.user.pojo.bo.center.CenterUserBO;
import com.zlin.user.pojo.vo.UsersVO;
import com.zlin.user.resource.FileUploadConfig;
import com.zlin.user.service.center.CenterUserService;
import com.zlin.utils.CookieUtils;
import com.zlin.utils.DateUtil;
import com.zlin.utils.JsonUtils;
import com.zlin.utils.RedisOperator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author zlin
 * @date 20210121
 */
@Api(value = "用户中信息相关接口", tags = {"用户信息相关接口"})
@RestController
@RequestMapping("userInfo")
public class CentUserController extends BaseController {

    private final static Logger LOGGER = LoggerFactory.getLogger(CentUserController.class);

    @Resource
    private CenterUserService centerUserService;

    @Resource
    private FileUploadConfig config;

    @Resource
    private RedisOperator redisOperator;

    @ApiOperation(value = "更新用户头像", notes = "更新用户头像", httpMethod = "POST")
    @PostMapping("uploadFace")
    public Result uploadFace(
            @ApiParam(name = "userId", value = "用户ID", required = true)
            @RequestParam String userId,
            @RequestBody MultipartFile file,
            HttpServletRequest request, HttpServletResponse response) {
        if (StringUtils.isBlank(userId)) {
            return Result.errorMsg("用户ID为空");
        }
        String uploadPath = config.getImageUserFaceLocation() + File.separator + userId;
        if (file == null || file.isEmpty()) {
            return Result.errorMsg("头像文件为空");
        }
        String faceUrl = config.getImageServerUrl() + "/" + userId;
        String originalFilename = file.getOriginalFilename();
        if (StringUtils.isNotBlank(originalFilename)) {
            String suffix = originalFilename.substring(originalFilename.lastIndexOf(".")+1);
            if (ImageFileSuffix.illegalSuffix(suffix)) {
                return Result.errorMsg("不合法的文件类型");
            }
            String newFileName = "face-" + userId + "-" + originalFilename;
            String finalFacePath = uploadPath + File.separator + newFileName;
            faceUrl += ("/" + newFileName);
            File saveFile = new File(finalFacePath);
            File parentFile = saveFile.getParentFile();
            if (parentFile != null) {
                boolean mkdirResult = parentFile.mkdirs();
                if (mkdirResult) {
                    LOGGER.error("头像上传失败，文件夹创建失败 {}", parentFile.getAbsolutePath());
                    return Result.errorMsg("文件上传失败");
                }
            }
            try (OutputStream outputStream = new FileOutputStream(saveFile);
                 InputStream inputStream = file.getInputStream()) {
                IOUtils.copy(inputStream, outputStream);
            }catch (IOException e) {
                LOGGER.error("上传用户头像文件失败", e);
            }
        }
        // 由于浏览器可能存在缓存导致不能及时刷新，在这里加上时间来保证更新后的图片可以及时刷新
        faceUrl += ("?t=" + DateUtil.getCurrentDateString(DateUtil.DATE_PATTERN));
        Users user = centerUserService.updateUserFace(userId, faceUrl);
//        setNullProperty(user);
        // 缓存会话token，得到VO
        UsersVO usersVO = cacheTokenAndConvertVO(user);
        CookieUtils.setCookie(request, response, CacheKey.USER.value, JsonUtils.objectToJson(usersVO), true);
        return Result.ok();
    }

    @ApiOperation(value = "更新用户信息", notes = "更新用户信息", httpMethod = "POST")
    @PostMapping("update")
    public Result update(@ApiParam(name = "userId", value = "用户ID", required = true)
                         @RequestParam String userId,
                         @Valid @RequestBody CenterUserBO centerUserBO,
                         BindingResult bindingResult,
                         HttpServletRequest request, HttpServletResponse response) {

        if (StringUtils.isBlank(userId)) {
            return Result.errorMsg("用户ID为空");
        }
        if (bindingResult.hasErrors()) {
            Map<String, String> errorMap = getErrors(bindingResult);
            return Result.errorMap(errorMap);
        }

        Users user = centerUserService.updateUserInfo(userId, centerUserBO);
//        setNullProperty(userResult);
        // 缓存会话token，得到VO
        UsersVO usersVO = cacheTokenAndConvertVO(user);
        CookieUtils.setCookie(request, response, CacheKey.USER.value, JsonUtils.objectToJson(usersVO), true);
        return Result.ok();
    }

    private Map<String, String> getErrors(BindingResult bindingResult) {
        Map<String, String> resultMap = new HashMap<>();
        if (bindingResult == null) {
            return resultMap;
        }
        for (FieldError error : bindingResult.getFieldErrors()) {
            String field = error.getField();
            String errorMsg = error.getDefaultMessage();
            resultMap.put(field, errorMsg);
        }
        return resultMap;
    }

    /**
     * 缓存用户会话token并转换为VO
     * @param user 用户
     * @return 用户VO
     */
    public UsersVO cacheTokenAndConvertVO(Users user) {
        String uniqueToken = UUID.randomUUID().toString().trim();
        redisOperator.set(CacheKey.USER_TOKEN.append(user.getId()), uniqueToken);
        UsersVO usersVO = new UsersVO();
        BeanUtils.copyProperties(user,usersVO);
        usersVO.setUserUniqueToken(uniqueToken);
        return usersVO;
    }
}
