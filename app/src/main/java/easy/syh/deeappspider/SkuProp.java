package easy.syh.deeappspider;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by 孙应恒 on 2018/6/8.
 * Description:
 */
public class SkuProp implements Parcelable {
  /**
   * unit : null
   * prop : 颜色
   * value : [{"imageUrl":"https://cbu01.alicdn.com/img/ibank/2018/069/352/8383253960_1010856797.jpg","name":"卡其"},{"imageUrl":"https://cbu01.alicdn.com/img/ibank/2018/554/982/8383289455_1010856797.jpg","name":"灰蓝"}]
   */

  private String unit;
  private String prop;
  private List<ValueBean> value;

  public String getUnit() {
    return unit;
  }

  public void setUnit(String unit) {
    this.unit = unit;
  }

  public String getProp() {
    return prop;
  }

  public void setProp(String prop) {
    this.prop = prop;
  }

  public List<ValueBean> getValue() {
    return value;
  }

  public void setValue(List<ValueBean> value) {
    this.value = value;
  }



  public static class ValueBean implements Parcelable {
    /**
     * imageUrl : https://cbu01.alicdn.com/img/ibank/2018/069/352/8383253960_1010856797.jpg
     * name : 卡其
     */

    private String imageUrl;
    private String name;

    public String getImageUrl() {
      return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
      this.imageUrl = imageUrl;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    @Override public int describeContents() {
      return 0;
    }

    @Override public void writeToParcel(Parcel dest, int flags) {
      dest.writeString(this.imageUrl);
      dest.writeString(this.name);
    }

    public ValueBean() {
    }

    protected ValueBean(Parcel in) {
      this.imageUrl = in.readString();
      this.name = in.readString();
    }

    public static final Creator<ValueBean> CREATOR =
        new Creator<ValueBean>() {
          @Override public ValueBean createFromParcel(Parcel source) {
            return new ValueBean(source);
          }

          @Override public ValueBean[] newArray(int size) {
            return new ValueBean[size];
          }
        };
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.unit);
    dest.writeString(this.prop);
    dest.writeTypedList(this.value);
  }

  public SkuProp() {
  }

  protected SkuProp(Parcel in) {
    this.unit = in.readString();
    this.prop = in.readString();
    this.value = in.createTypedArrayList(ValueBean.CREATOR);
  }

  public static final Creator<SkuProp> CREATOR = new Creator<SkuProp>() {
    @Override public SkuProp createFromParcel(Parcel source) {
      return new SkuProp(source);
    }

    @Override public SkuProp[] newArray(int size) {
      return new SkuProp[size];
    }
  };
}
