import org.junit.jupiter.api.Test;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class test2 {

    @Test

    void dateTest() throws ParseException {

        Date date = new Date();

        int year = date.getYear()+1900;
        System.out.println("year: "+year);
        int month = date.getMonth();
        String s = Integer.toString(year) + (month<=9?"0"+month:month);

        System.out.println(s);
        DateFormat dateFormat = null;

        String pattern = "yyyyMM";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        Date parse = simpleDateFormat.parse(s);
        System.out.println(parse);


    }

}

