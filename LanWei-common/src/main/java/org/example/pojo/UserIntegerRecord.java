package org.example.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.enums.UserIntegralOperTypeEnum;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;

@Component

@NoArgsConstructor
@AllArgsConstructor
public class UserIntegerRecord  implements Serializable {

    int record_id;

    String user_id;

    int oper_type;
   String operTypeName;

    public String getOperTypeName() {
        UserIntegralOperTypeEnum operTypeEnum = UserIntegralOperTypeEnum.getByType(oper_type);
        return operTypeEnum ==null?"":operTypeEnum.getDesc();
    }

    public void setOperTypeName(String operTypeName) {
        this.operTypeName = operTypeName;
    }

    public int getRecord_id() {
        return record_id;
    }

    public void setRecord_id(int record_id) {
        this.record_id = record_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public int getOper_type() {

        return oper_type;
    }

    public void setOper_type(int oper_type) {
        this.oper_type = oper_type;
    }

    public int getIntegral() {
        return integral;
    }

    public void setIntegral(int integral) {
        this.integral = integral;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }

    int integral;

    Date create_time;
}
