package org.simple.base.nutz.action

import org.nutz.img.Images
import org.nutz.lang.Files
import org.nutz.log.Log
import org.nutz.log.Logs
import org.nutz.mvc.annotation.At
import org.nutz.mvc.annotation.Ok
import org.nutz.mvc.annotation.Param
import org.simple.cfg.api.SysCfg
import org.simple.base.exception.ServiceException
import org.simple.base.util.FilesPlus
import org.simple.base.nutz.constants.NutzConstants
import org.simple.base.nutz.util.WebUtil
import org.simple.base.util.StringUtil

import javax.imageio.ImageIO
import javax.servlet.http.HttpServletResponse
import java.awt.image.BufferedImage

/**
 * @author Jay.Wu
 */
public abstract class CommonAction extends WebUtil {

    private static final Log log = Logs.get()

    protected String saveFile(File f, String role) {
        List<Map> mapList = SysCfg.getValue("upload." + role + ".size")
        String fileName = String.valueOf(new Date().getTime())
        for (Map map : mapList) {
            int width = Integer.valueOf(map.get("width").toString())
            int height = Integer.valueOf(map.get("height").toString())
            saveFile(f, role, width, height, fileName)
        }
        return saveFile(f, role, 0, 0, fileName);//最后调用这个，因为file.rename会删除原文件
    }

    protected String saveFile(File f, String role, int width, int height, String newFileName) {
        if (StringUtil.isBlank(role) || f == null) {
            return null
        }
        String localPath = getPath(role, NutzConstants.LOCAL_PATH)
        log.info("保存的文件路径是：" + localPath)
        String domainPath = getPath(role, NutzConstants.DOMAIN_PATH)
        File localFolder = new File(localPath)
        if (!localFolder.exists() && !localFolder.mkdirs()) {
            throw new ServiceException("上传文件失败！");//文件不存在且创建文件夹失败，抛错
        }

        if (width > 0 && height > 0) {
            newFileName += "_" + width
        }

        localPath += File.separator
        String fileName = newFileName + "." + FilesPlus.getSuffixName(f)
        String pngFile = newFileName + "." + "png"
        File destFile = new File(localPath + fileName)

        if (width > 0 && height > 0) {
            try {
                Images.zoomScale(f, destFile, width, height, null)
            } catch (IOException e) {
                e.printStackTrace()
            }
        } else {
            f.renameTo(destFile)
        }
        try {
            BufferedImage pngImage = ImageIO.read(destFile)
            ImageIO.write(pngImage, "PNG", new File(localPath + pngFile))
        } catch (IOException e) {
            log.error(e.getMessage(), e)
        }

        return domainPath + "/" + pngFile
    }

    protected String getPath(String role, String whichPath) {
        if (StringUtil.isBlank(role))
            return null
        String path = SysCfg.getValue("upload." + whichPath)
        String subFolder = SysCfg.getValue("upload." + role + ".path")
        path += subFolder
        return path
    }

    /**
     * 文件下载，采用文件流输出的方式处理
     */
    protected void download(String filePath, String displayName) {
        download(Files.findFileAsStream(filePath), displayName)
    }

    /**
     * 文件下载，采用文件流输出的方式处理
     */
    protected void download(InputStream ins, String displayName) {
        try {
            OutputStream out = renderFile(displayName).getOutputStream()

            byte[] b = new byte[1024]
            int i

            while ((i = ins.read(b)) > 0) {
                out.write(b, 0, i)
            }

            out.flush()
        } catch (Exception e) {
            log.error(e.getMessage(), e)
        } finally {
            if (ins != null) {
                try {
                    ins.close()
                } catch (IOException e) {
                    log.error(e.getMessage(), e)
                }
            }
        }
    }

    /**
     * 渲染文件下载
     */
    protected HttpServletResponse renderFile(String displayName) {
        try {
            getResponse().reset()
            getResponse().setContentType("application/x-download")
            getResponse().addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(displayName, "UTF-8"))
        } catch (Exception e) {
            log.error(e.getMessage(), e)
        }

        return getResponse()
    }

    @At
    @Ok('jsp:jsp.${obj}')
    public String go(@Param("p") String p) {
        for (Object key : getRequest().getParameterMap().keySet()) {
            transfer(key.toString())
        }

        return p
    }
}
