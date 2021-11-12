package com.hdsx.geohome.geocoding.utile;

import java.util.Collection;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 
 * @author jingzh
 * 
 * @createtime 2015/11/24
 * 
 * @email jingzh@hdsxtech.com
 *
 * @description 字符串工具类
 */
public class StringUtile {
	
	/**
	 * 判断对象是否为空:
	 * @param obj
	 * @return
	 */
	public static boolean isEmpty(Object obj){
		if (obj == null) { return true;}
		else if (obj instanceof String && (obj.equals(""))) {return true;}
		else if (obj instanceof Number && ((Number) obj).doubleValue() == 0) {return true; }
		else if (obj instanceof Boolean && !((Boolean) obj)) {return true;}
		else if (obj instanceof Collection && ((Collection<?>) obj).isEmpty()){return true;}
		else if (obj instanceof Map && ((Map<?,?>) obj).isEmpty()) {return true;}
		else return obj instanceof Object[] && ((Object[]) obj).length == 0;
    }
	
	/**
	 * 全角转半角:
	 * @param fullStr
	 * @return
	 */
	public static final String full2Half(String fullStr) {
		if(isEmpty(fullStr)){
			return fullStr;
		}
		char[] c = fullStr.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] >= 65281 && c[i] <= 65374) {
				c[i] = (char) (c[i] - 65248);
			} else if (c[i] == 12288) { // 空格
				c[i] = (char) 32;
			}
		}
		return new String(c);
	}

	/**
	 * 半角转全角
	 * @param halfStr
	 * @return
	 */
	public static final String half2Full(String halfStr) {
		if(isEmpty(halfStr)){
			return halfStr;
		}
		char[] c = halfStr.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == 32) {
				c[i] = (char) 12288;
			} else if (c[i] < 127) {
				c[i] = (char) (c[i] + 65248);
			}
		}
		return new String(c);
	}
	/**
	 * 判断字符是否是中文
	 *
	 * @param c 字符
	 * @return 是否是中文
	 */
	public static boolean isChinese(char c) {
	    Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
		return ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
				|| ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
				|| ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
				|| ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS;
	}
	 
	/**
	 * 判断字符串是否是乱码
	 *
	 * @param strName 字符串
	 * @return 是否是乱码
	 */
	public static boolean isMessyCode(String strName) {
	    Pattern p = Pattern.compile("\\s*|\t*|\r*|\n*");
	    Matcher m = p.matcher(strName);
	    String after = m.replaceAll("");
	    String temp = after.replaceAll("\\p{P}", "");
	    char[] ch = temp.trim().toCharArray();
	    float chLength = ch.length;
	    float count = 0;
	    for (int i = 0; i < ch.length; i++) {
	        char c = ch[i];
	        if (!Character.isLetterOrDigit(c)) {
	            if (!isChinese(c)) {
	                count = count + 1;
	            }
	        }
	    }
	    float result = count / chLength;
//	    System.out.println("result:"+result);
		return result > 0.4;
	 
	}
}
