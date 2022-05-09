/*
 *      Copyright [2020] [xiaozhennan1995@gmail.com]
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 *      http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.weiwan.dsp.common.utils;

import java.io.UnsupportedEncodingException;

/**
 * @Author: xiaozhennan
 * @Date: 2020/7/7 16:53
 * @Package: com.hopson.dc.common.utils
 * @ClassName: HexHashUtil
 * @Description:
 **/
public class HexHashUtil {

    /**
     * 字符串转换为16进制字符串
     *
     * @param charsetName 用于编码 String 的 Charset
     */
    public static String str2hexStr(String str, String charsetName) {
        byte[] bytes = new byte[0];
        String hexString = "0123456789abcdef";
        // 使用给定的 charset 将此 String 编码到 byte 序列
        if (!charsetName.equals("")) {
            try {
                bytes = str.getBytes(charsetName);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {
            // 根据默认编码获取字节数组
            bytes = str.getBytes();
        }
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        // 将字节数组中每个字节拆解成2位16进制整数
        for (int i = 0; i < bytes.length; i++) {
            sb.append(hexString.charAt((bytes[i] & 0xf0) >> 4));
            sb.append(hexString.charAt((bytes[i] & 0x0f)));
        }
        return sb.toString();
    }


    /**
     * 16进制字符串转换为字符串
     *
     * @param charsetName 用于编码 String 的 Charset
     */
    public static String hexStr2Str(String hexStr, String charsetName) {
        hexStr = hexStr.toUpperCase();
        // hexStr.replace(" ", "");
        String str = "0123456789ABCDEF";
        char[] hexs = hexStr.toCharArray();
        byte[] bytes = new byte[hexStr.length() / 2];
        int n;

        for (int i = 0; i < bytes.length; i++) {
            n = str.indexOf(hexs[2 * i]) * 16;
            n += str.indexOf(hexs[2 * i + 1]);
            bytes[i] = (byte) (n & 0xFF);
        }
        String returnStr = "";// 返回的字符串
        if (charsetName == null) {
            // 编译器默认解码指定的 byte 数组，构造一个新的 String,
            // 比如我的集成开发工具即编码器android studio的默认编码格式为"utf-8"
            returnStr = new String(bytes);
        } else {
            // 指定的 charset 解码指定的 byte 数组，构造一个新的 String
            // utf-8中文字符占三个字节，GB18030兼容GBK兼容GB2312中文字符占两个字节，ISO8859-1是拉丁字符（ASCII字符）占一个字节
            try {
                returnStr = new String(bytes, charsetName);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        // charset还有utf-8,gbk随需求改变
        return returnStr;
    }


    /**
     * bytes转换成十六进制字符串
     *
     * @param b byte数组
     */
    public static String Bytes2hexStr(byte[] b) {
        String stmp = "";
        StringBuilder sb = new StringBuilder("");

        for (int i = 0; i < b.length; i++) {
            stmp = Integer.toHexString(b[i] & 0xFF);
            sb.append((stmp.length() == 1) ? "0" + stmp : stmp);
            // sb.append(" ");//每个Byte值之间空格分隔
        }
        return sb.toString().toUpperCase().trim();
    }

    /**
     * 十六进制字符串转bytes
     *
     * @param src 16进制字符串
     * @return 字节数组
     */
    public static byte[] hexStr2Bytes(String src) {
        int l = src.length() / 2;
        byte[] ret = new byte[l];
        for (int i = 0; i < l; i++) {
            ret[i] = (byte) Integer
                    .valueOf(src.substring(i * 2, i * 2 + 2), 16).byteValue();
        }
        return ret;

    }


    /**
     * 由于硬件发出来得信息是16进制字符串转换的byte数组，并且格式是"gb2312"所以我要将它解析才能得到我们用户能看懂的字符串。
     *
     * 拓展为啥要用十六进制字符串
     *
     * 一、原理：
     * 　　Java中byte用二进制表示占用8位，而我们知道16进制的每个字符需要用4位二进制位来表示（2^3+2^2+2^1+2^0=15），所以我们就可以把每个byte转换成两个相应的16进制字符，即把byte的高4位和低4位分别转换成相应的16进制字符H和L，并组合起来得到byte转换到16进制字符串的结果new String(H) + new String(L)。即byte用十六进制表示只占2位。 同理，相反的转换也是将两个16进制字符转换成一个byte，原理同上。
     * 　　根据以上原理，就可以将byte数组转换为16进制字符串了，当然也可以将16进制字符串转换为byte数组了。　　
     * 　　二 、16进制的意义：
     * 　　1、用于计算机领域的一种重要的数制。
     * 　　2、对计算机理论的描述，计算机硬件电路的设计都是很有益的。比如逻辑电路设计中，既要考虑功能的完备，还要考虑用尽可能少的硬件，十六进制就能起到一些理论分析的作用。比如四位二进制电路，最多就是十六种状态，也就是一种十六进制形式，只有这十六种状态都被用上了或者尽可能多的被用上，硬件资源才发挥了尽可能大的作用。
     * 　　3、十六进制更简短，因为换算的时候一位16进制数可以顶4位2进制数。
     *
     * 　　Java中byte用二进制表示占用8位，而我们知道16进制的每个字符需要用4位二进制位来表示（2^3+2^2+2^1+2^0=15），所以我们就可以把每个byte转换成两个相应的16进制字符，即把byte的高4位和低4位分别转换成相应的16进制字符H和L，并组合起来得到byte转换到16进制字符串的结果new String(H) + new String(L)。即byte用十六进制表示只占2位。 同理，相反的转换也是将两个16进制字符转换成一个byte，原理同上。
     * 　　根据以上原理，就可以将byte数组转换为16进制字符串了，当然也可以将16进制字符串转换为byte数组了。　　
     * 　　二 、16进制的意义：
     * 　　1、用于计算机领域的一种重要的数制。
     * 　　2、对计算机理论的描述，计算机硬件电路的设计都是很有益的。比如逻辑电路设计中，既要考虑功能的完备，还要考虑用尽可能少的硬件，十六进制就能起到一些理论分析的作用。比如四位二进制电路，最多就是十六种状态，也就是一种十六进制形式，只有这十六种状态都被用上了或者尽可能多的被用上，硬件资源才发挥了尽可能大的作用。
     * 　　3、十六进制更简短，因为换算的时候一位16进制数可以顶4位2进制数。
     * 拿汉字的例子来说吧
     * 每个汉字在机器内部的机内码是唯一的
     *
     * 按你说的，“我”的机内码是CED2，这样的话，是要在输出的时候进行转换成汉字，输出包括屏幕和打印机输出等。
     *
     * 一般是按照规则将机内码转换成区位码，然后到区位码这个“表”中去查询即可。
     * 机内码转换：CED2 转换成 十六进制区位码为 2E32
     * 2E32每两位换成十进制的区位码就是4650
     *
     * 也就是说你要把区位码存在一个表中或数组中，然后用区位码进行查询，区位码就行号和列号，这样就可以输出汉字字符了。通常这个区位码表是按自己的需求，来进行优化的，因为可能不需要存所有的汉字，这个表通常叫做字模库或者字形库，都是为了输出用的。
     * ————————————————
     * 版权声明：本文为CSDN博主「ChampionDragon」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
     * 原文链接：https://blog.csdn.net/xxdw1992/article/details/78818153
     */
}

