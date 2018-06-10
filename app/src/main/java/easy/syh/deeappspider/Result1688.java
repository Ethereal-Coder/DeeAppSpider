package easy.syh.deeappspider;

import java.util.List;

/**
 * Created by 孙应恒 on 2018/6/8.
 * Description:
 */
public class Result1688 {

  /**
   * detailUrl : //img.alicdn.com/tfscom/TB1ESr0oXOWBuNjy0FiXXXFxVXa
   * imageList : [{"size310x310URL":"https://cbu01.alicdn.com/img/ibank/2018/751/952/8383259157_1010856797.310x310.jpg","originalImageURI":"https://cbu01.alicdn.com/img/ibank/2018/751/952/8383259157_1010856797.jpg"},{"size310x310URL":"https://cbu01.alicdn.com/img/ibank/2018/584/593/8369395485_1010856797.310x310.jpg","originalImageURI":"https://cbu01.alicdn.com/img/ibank/2018/584/593/8369395485_1010856797.jpg"},{"size310x310URL":"https://cbu01.alicdn.com/img/ibank/2018/816/683/8369386618_1010856797.310x310.jpg","originalImageURI":"https://cbu01.alicdn.com/img/ibank/2018/816/683/8369386618_1010856797.jpg"},{"size310x310URL":"https://cbu01.alicdn.com/img/ibank/2018/812/169/8351961218_1010856797.310x310.jpg","originalImageURI":"https://cbu01.alicdn.com/img/ibank/2018/812/169/8351961218_1010856797.jpg"},{"size310x310URL":"https://cbu01.alicdn.com/img/ibank/2018/452/862/8383268254_1010856797.310x310.jpg","originalImageURI":"https://cbu01.alicdn.com/img/ibank/2018/452/862/8383268254_1010856797.jpg"},{"size310x310URL":"https://cbu01.alicdn.com/img/ibank/2018/069/352/8383253960_1010856797.310x310.jpg","originalImageURI":"https://cbu01.alicdn.com/img/ibank/2018/069/352/8383253960_1010856797.jpg"},{"size310x310URL":"https://cbu01.alicdn.com/img/ibank/2018/554/982/8383289455_1010856797.310x310.jpg","originalImageURI":"https://cbu01.alicdn.com/img/ibank/2018/554/982/8383289455_1010856797.jpg"}]
   * skuProps : [{"unit":null,"prop":"颜色","value":[{"imageUrl":"https://cbu01.alicdn.com/img/ibank/2018/069/352/8383253960_1010856797.jpg","name":"卡其"},{"imageUrl":"https://cbu01.alicdn.com/img/ibank/2018/554/982/8383289455_1010856797.jpg","name":"灰蓝"}]},{"unit":null,"prop":"尺码","value":[{"imageUrl":null,"name":"S"},{"imageUrl":null,"name":"M"}]}]
   * subject : [A2A3]2018春季新款韩版修身百搭单排扣虚边斜纹短外套女
   */

  private String detailUrl;
  private String subject;
  private List<ImageListBean> imageList;
  private List<SkuProp> skuProps;

  public String getDetailUrl() {
    return detailUrl;
  }

  public void setDetailUrl(String detailUrl) {
    this.detailUrl = detailUrl;
  }

  public String getSubject() {
    return subject;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  public List<ImageListBean> getImageList() {
    return imageList;
  }

  public void setImageList(List<ImageListBean> imageList) {
    this.imageList = imageList;
  }

  public List<SkuProp> getSkuProps() {
    return skuProps;
  }

  public void setSkuProps(List<SkuProp> skuProps) {
    this.skuProps = skuProps;
  }

  public static class ImageListBean {
    /**
     * size310x310URL : https://cbu01.alicdn.com/img/ibank/2018/751/952/8383259157_1010856797.310x310.jpg
     * originalImageURI : https://cbu01.alicdn.com/img/ibank/2018/751/952/8383259157_1010856797.jpg
     */

    private String size310x310URL;
    private String originalImageURI;

    public String getSize310x310URL() {
      return size310x310URL;
    }

    public void setSize310x310URL(String size310x310URL) {
      this.size310x310URL = size310x310URL;
    }

    public String getOriginalImageURI() {
      return originalImageURI;
    }

    public void setOriginalImageURI(String originalImageURI) {
      this.originalImageURI = originalImageURI;
    }
  }
}
