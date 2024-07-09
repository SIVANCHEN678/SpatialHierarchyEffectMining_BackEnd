package com.edu.cqupt.shemining.common;

// TODO 公共模块新增
public enum FeatureType {
//    DIAGNOSIS(0, "clinical_representation"),
//    EXAMINE(1, "examine"),
//    PATHOLOGY(2, "living_habit"),
//    VITAL_SIGNS(3, "social_connection");

    //    DIAGNOSIS(0, "diagnosis"),
    DIAGNOSIS(0, "is_demography"),
    //    EXAMINE(1, "examine"),
    EXAMINE(2, "is_physiological"),
    //    PATHOLOGY(2, "pathology"),
    PATHOLOGY(3, "is_sociology");
//    VITAL_SIGNS(3, "vital_signs");

    private final int code;
    private final String name;

    FeatureType(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
