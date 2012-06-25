package net.xaethos.lib.calico.integration.models;

import net.xaethos.lib.calico.annotations.Getter;
import net.xaethos.lib.calico.annotations.ModelInfo;
import net.xaethos.lib.calico.annotations.Setter;
import net.xaethos.lib.calico.integration.MyProvider;
import net.xaethos.lib.calico.models.CalicoModel;
import net.xaethos.lib.calico.models.ModelManager;

@ModelInfo(
        tableName = "polymorphs",
        authority = MyProvider.AUTHORITY,
        contentType = "vnd.xaethos.test.polymorph"
)
public interface Polymorph extends
        CalicoModel<Polymorph>,
        ModelManager.Timestamps
{
    public static final String VALUE = "value";

    @Getter(VALUE) public String  getStringValue();
    @Setter(VALUE) public void    setStringValue(String value);

    @Getter(VALUE) public Boolean getBooleanValue();
    @Getter(VALUE) public Byte    getByteValue();
    @Getter(VALUE) public Short   getShortValue();
    @Getter(VALUE) public Integer getIntegerValue();
    @Getter(VALUE) public Long    getLongValue();
    @Getter(VALUE) public Float   getFloatValue();
    @Getter(VALUE) public Double  getDoubleValue();
    @Getter(VALUE) public byte[]  getBlobValue();
}
