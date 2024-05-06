package org.example.Utils;

import org.apache.commons.lang3.StringUtils;
import org.example.enums.VerifyRegexEnum;

import java.lang.reflect.Parameter;
import java.util.BitSet;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VerifyUtils {

    public static Boolean verfiy(String regs,String value){

        if(StringTools.isEmpty(value)){
            return false;
        }

        Pattern pattern = Pattern.compile(regs);

        Matcher matcher = pattern.matcher(value);

        return matcher.matches();

    }
    public static Boolean verfiy(VerifyRegexEnum regs, String value){

        return verfiy(regs.getRegex(), value);

    }




}
