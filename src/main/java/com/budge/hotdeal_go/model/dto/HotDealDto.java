package com.budge.hotdeal_go.model.dto;

import lombok.Data;

@Data
public class HotDealDto {
    private String Title;
    private String price;
    private String shippingFee;
    private String purchasingPlace;
    private String url;
    private Long likeCnt;
    private String time;
}