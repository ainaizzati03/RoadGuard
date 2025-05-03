package com.example.roadguardd;

public class DataClass {

    private String dataVehicleBrand;
    private String dataVehicleReg;
    private String dataVehicleColor;
    private Integer dataNoPass;
    private Integer dataSeniorCitizen;
    private Integer dataAdult;
    private Integer dataChildren;
    private Integer dataInfant;
    private String dataAddDetail;

    public DataClass() {
        // Required by Firebase
    }

    public DataClass(String dataVehicleBrand, String dataVehicleReg, String dataVehicleColor,
                     Integer dataNoPass, Integer dataSeniorCitizen, Integer dataAdult,
                     Integer dataChildren, Integer dataInfant, String dataAddDetail) {

        this.dataVehicleBrand = dataVehicleBrand;
        this.dataVehicleReg = dataVehicleReg;
        this.dataVehicleColor = dataVehicleColor;
        this.dataNoPass = dataNoPass;
        this.dataSeniorCitizen = dataSeniorCitizen;
        this.dataAdult = dataAdult;
        this.dataChildren = dataChildren;
        this.dataInfant = dataInfant;
        this.dataAddDetail = dataAddDetail;
    }

    // Getters...
    public String getDataVehicleBrand() { return dataVehicleBrand; }
    public String getDataVehicleReg() { return dataVehicleReg; }
    public String getDataVehicleColor() { return dataVehicleColor; }
    public Integer getDataNoPass() { return dataNoPass; }
    public Integer getDataSeniorCitizen() { return dataSeniorCitizen; }
    public Integer getDataAdult() { return dataAdult; }
    public Integer getDataChildren() { return dataChildren; }
    public Integer getDataInfant() { return dataInfant; }
    public String getDataAddDetail() { return dataAddDetail; }
}
