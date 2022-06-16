package com.automate.df.model.salesgap;

import java.io.Serializable;
import java.util.Objects;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor

public class TargetDropDownV2 implements Serializable {

	String code;
	String name;
	String parentId;
	Integer order;
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TargetDropDownV2 other = (TargetDropDownV2) obj;
		return Objects.equals(code, other.code) && Objects.equals(name, other.name);
	}
	@Override
	public int hashCode() {
		return Objects.hash(code, name);
	}
	
	
	
}
