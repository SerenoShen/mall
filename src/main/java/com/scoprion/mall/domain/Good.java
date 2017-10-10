package com.scoprion.mall.domain;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created on 2017/9/28.
 */
public class Good {

    //主键
    private Long id;

    //类目id
    private Long categoryId;

    //商品名称
    private String name;

    //商品描述
    private String description;

    //促销价格
    private BigDecimal promotion;

    //价格
    private BigDecimal price;

    //销量
    private int saleVolume;

    //折扣
    private int discount;

    //库存
    private int stock;

    //上下架  1上架  0下架    默认上架
    private String isOnSale;

    //热销   1是   0否
    private String isHot;

    //新品  1是  0否
    private String isNew;

    //创建时间
    private Date createDate;

    //最后更新时间
    private Date lastUpdateDate;

    //是否包邮  1是  0否
    private String isFreight;

    //所属品牌
    private Long brandId;

    //商户ID
    private Long sellerId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPromotion() {
        return promotion;
    }

    public void setPromotion(BigDecimal promotion) {
        this.promotion = promotion;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getSaleVolume() {
        return saleVolume;
    }

    public void setSaleVolume(int saleVolume) {
        this.saleVolume = saleVolume;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getIsOnSale() {
        return isOnSale;
    }

    public void setIsOnSale(String isOnSale) {
        this.isOnSale = isOnSale;
    }

    public String getIsHot() {
        return isHot;
    }

    public void setIsHot(String isHot) {
        this.isHot = isHot;
    }

    public String getIsNew() {
        return isNew;
    }

    public void setIsNew(String isNew) {
        this.isNew = isNew;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public String getIsFreight() {
        return isFreight;
    }

    public void setIsFreight(String isFreight) {
        this.isFreight = isFreight;
    }

    public Long getBrandId() {
        return brandId;
    }

    public void setBrandId(Long brandId) {
        this.brandId = brandId;
    }

    public Long getSellerId() {
        return sellerId;
    }

    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }

    @Override
    public String toString() {
        return "Good{" +
                "id=" + id +
                ", categoryId=" + categoryId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", promotion=" + promotion +
                ", price=" + price +
                ", saleVolume=" + saleVolume +
                ", discount=" + discount +
                ", stock=" + stock +
                ", isOnSale='" + isOnSale + '\'' +
                ", isHot='" + isHot + '\'' +
                ", isNew='" + isNew + '\'' +
                ", createDate=" + createDate +
                ", lastUpdateDate=" + lastUpdateDate +
                ", isFreight='" + isFreight + '\'' +
                ", brandId=" + brandId +
                ", sellerId=" + sellerId +
                '}';
    }
}