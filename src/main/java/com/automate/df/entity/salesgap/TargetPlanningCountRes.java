package com.automate.df.entity.salesgap;

import com.automate.df.model.salesgap.TargetSettingRecord;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class TargetPlanningCountRes {

    String retailTarget;
    String enquiry;
    String testDrive;
    String homeVisit;
    String booking;
    String exchange;
    String finance;
    String insurance;
    String exWarranty;
    String accessories;
    String startDate;
    String endDate;
    String employeeId;
    List<TargetSettingRecord> target;
}
