package com.automate.df.dao.salesgap;

import com.automate.df.model.DmsAddress;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EmployeeAddress {

    private DmsAddress permanentAddress;

    private DmsAddress presentAddress;
}
