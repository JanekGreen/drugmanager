package pl.pwojcik.drugmanager.model.restEntity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Drug {

@SerializedName("id")
@Expose
private Integer id;
@SerializedName("internalId")
@Expose
private Integer internalId;
@SerializedName("name")
@Expose
private String name;
@SerializedName("commonName")
@Expose
private String commonName;
@SerializedName("type")
@Expose
private String type;
@SerializedName("targetSpec")
@Expose
private String targetSpec;
@SerializedName("okrKar")
@Expose
private String okrKar;
@SerializedName("dosage")
@Expose
private String dosage;
@SerializedName("expirationDate")
@Expose
private String expirationDate;
@SerializedName("procedureType")
@Expose
private String procedureType;
@SerializedName("permissionNumber")
@Expose
private Integer permissionNumber;
@SerializedName("atc")
@Expose
private String atc;
@SerializedName("producer")
@Expose
private String producer;
@SerializedName("packQuantity")
@Expose
private String packQuantity;
@SerializedName("activeSubstance")
@Expose
private String activeSubstance;
@SerializedName("feaflet")
@Expose
private String feaflet;
@SerializedName("characteristics")
@Expose
private String characteristics;
@SerializedName("usageType")
@Expose
private String usageType;

public Integer getId() {
return id;
}

public void setId(Integer id) {
this.id = id;
}

public Integer getInternalId() {
return internalId;
}

public void setInternalId(Integer internalId) {
this.internalId = internalId;
}

public String getName() {
return name;
}

public void setName(String name) {
this.name = name;
}

public String getCommonName() {
return commonName;
}

public void setCommonName(String commonName) {
this.commonName = commonName;
}

public String getType() {
return type;
}

public void setType(String type) {
this.type = type;
}

public String getTargetSpec() {
return targetSpec;
}

public void setTargetSpec(String targetSpec) {
this.targetSpec = targetSpec;
}

public String getOkrKar() {
return okrKar;
}

public void setOkrKar(String okrKar) {
this.okrKar = okrKar;
}

public String getDosage() {
return dosage;
}

public void setDosage(String dosage) {
this.dosage = dosage;
}

public String getExpirationDate() {
return expirationDate;
}

public void setExpirationDate(String expirationDate) {
this.expirationDate = expirationDate;
}

public String getProcedureType() {
return procedureType;
}

public void setProcedureType(String procedureType) {
this.procedureType = procedureType;
}

public Integer getPermissionNumber() {
return permissionNumber;
}

public void setPermissionNumber(Integer permissionNumber) {
this.permissionNumber = permissionNumber;
}

public String getAtc() {
return atc;
}

public void setAtc(String atc) {
this.atc = atc;
}

public String getProducer() {
return producer;
}

public void setProducer(String producer) {
this.producer = producer;
}

public String getPackQuantity() {
return packQuantity;
}

public void setPackQuantity(String packQuantity) {
this.packQuantity = packQuantity;
}

public String getActiveSubstance() {
return activeSubstance;
}

public void setActiveSubstance(String activeSubstance) {
this.activeSubstance = activeSubstance;
}

public String getFeaflet() {
return feaflet;
}

public void setFeaflet(String feaflet) {
this.feaflet = feaflet;
}

public String getCharacteristics() {
return characteristics;
}

public void setCharacteristics(String characteristics) {
this.characteristics = characteristics;
}

public String getUsageType() {
return usageType;
}

public void setUsageType(String usageType) {
this.usageType = usageType;
}

}