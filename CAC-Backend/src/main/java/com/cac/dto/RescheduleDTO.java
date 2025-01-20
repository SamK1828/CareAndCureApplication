package com.cac.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.bind.annotation.GetMapping;


@NoArgsConstructor
public class RescheduleDTO{
    public String getNewDate() {
        return newDate;
    }
    public void setNewDate(String newDate) {
        this.newDate = newDate;
    }
    public String getNewTime() {
        return newTime;
    }
    public void setNewTime(String newTime) {
        this.newTime = newTime;
    }
    private  String newDate;
    private  String newTime;
}
