package org.simple.base.util

import org.nutz.lang.Streams

import java.nio.charset.Charset

public class StreamsPlus extends Streams {

    /**
     *
     /** 根据一个文件路径建立一个 enc编码的 文本输入流
     *
     * @param file
     *            文件
     * @param enc
     *            编码
     * @return 文本输入流
     */
    public static Reader fileInr(File file, String enc) {
        return new InputStreamReader(fileIn(file), Charset.forName(enc))
    }
}