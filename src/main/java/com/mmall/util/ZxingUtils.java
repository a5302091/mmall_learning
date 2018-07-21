package com.mmall.util;

import com.google.common.collect.Maps;
import com.mmall.common.ServerResponse;
import com.mmall.dao.OrderMapper;
import com.mmall.pojo.Order;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * Created by Administrator on 2018/7/21.

public class ZxingUtils {

    @Autowired
    private OrderMapper orderMapper;

    public ServerResponse pay(Long orderNo, Integer userId, String path) throws AlipayApiException {
        Map<String, String> resultMap = Maps.newHashMap();
        Order order = orderMapper.selectByUserIdAndOrderNo(userId, orderNo);
        if (order == null) {
            return ServerResponse.createByErrorMessage("用户没有该订单");
        }
        resultMap.put("orderNo", String.valueOf(order.getOrderNo()));
        String outTradeNo = String.valueOf(order.getOrderNo());
        String subject = new StringBuilder("mmall扫码支付，订单号：").append(orderMapper).toString();
        String totalAmount = order.getPayment().toString();
        String storeId = "test_store_id";
        String providerId = "2088100200300400500";
        ExtendParams extendParams = new ExtendParams();
        extendParams.setSysServiceProviderId(providerId);
        String timeoutExpress = "5m";
        List<GoodsDetail> goodsDetailList = new ArrayList<>();
        List<OrderItem> orderItemList = orderItemMapper.getByOrderNoUserId(orderNo, userId);
        for (OrderItem orderItem : orderItemList) {
            GoodsDetail goods = new GoodsDetail();
            goods.setGoodsId(orderItem.getProductId().toString());
            goods.setGoodsName(orderItem.getProductName());
            goods.setPrice(BigDecimalUtil.mul(orderItem.getCurrentUnitPrice().doubleValue(),
                    new Double(100).doubleValue()).toString());
            goods.setQuantity(Long.valueOf(orderItem.getQuantity().toString()));
            goodsDetailList.add(goods);
        }
        AlipayTradePayModel alipayTradePayModel = new AlipayTradePayModel();
        alipayTradePayModel.setOutTradeNo(outTradeNo);
        alipayTradePayModel.setTotalAmount(totalAmount);
        alipayTradePayModel.setSubject(subject);
        alipayTradePayModel.setStoreId(storeId);
        alipayTradePayModel.setTimeoutExpress(timeoutExpress);
        //PropertiesUtil静态代码块还需要加载zfbinfo.properties
        //props.load(new InputStreamReader(PropertiesUtil.class.getClassLoader().getResourceAsStream("zfbinfo.properties"), "UTF-8"));
        AlipayClient alipayClient = new DefaultAlipayClient(PropertiesUtil.getProperty("open_api_domain"),
                PropertiesUtil.getProperty("appid"),
                PropertiesUtil.getProperty("private_key"),
                AlipayConstants.FORMAT_JSON,
                AlipayConstants.CHARSET_UTF8,
                PropertiesUtil.getProperty("alipay_public_key"),
                AlipayConstants.SIGN_TYPE_RSA2);
        AlipayTradePrecreateRequest request = new AlipayTradePrecreateRequest();
        request.setBizModel(alipayTradePayModel);
        request.setNotifyUrl(PropertiesUtil.getProperty("alipay.callback.url"));
        // 获取当面付应答
        AlipayTradePrecreateResponse response = alipayClient.execute(request);
        String code = response.getCode();
        if (!"10000".equals(code)) {
            logger.info("支付宝预下单失败！！！");
            return ServerResponse.createByErrorMessage("支付宝预下单失败！！！");
        }
        logger.info("支付宝预下单成功：）");
        String body = response.getBody();
        logger.info("支付宝收到响应=======>" + body);
        File folder = new File(path);
        if (!folder.exists()) {
            folder.setWritable(true);
            folder.mkdirs();
        }
        String qrPath = String.format(path + "/qr-%s.png", response.getOutTradeNo());
        String qrFileName = String.format("qr-%s.png", response.getOutTradeNo());
        ZxingUtils.getQRCodeImge(response.getQrCode(), 256, qrPath);
        File targetFile = new File(path, qrFileName);
        try {
            FTPUtil.uploadFile(Lists.newArrayList(targetFile));
        } catch (IOException e) {
            logger.error("上传二维码异常", e);
        }
        logger.info("qrPath: " + qrPath);
        String qrUrl = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFile.getName();
        resultMap.put("qrUrl", qrUrl);
        return ServerResponse.createBySuccess(resultMap);
    }

}
 */