package com.weiwan.dsp.core.resolve.http;

import com.alibaba.fastjson.JSONObject;
import com.weiwan.dsp.api.pojo.UnresolvedDataRecord;
import com.weiwan.dsp.common.enums.DspResultStatus;
import com.weiwan.dsp.common.utils.CheckTool;
import com.weiwan.dsp.common.utils.ObjectUtil;
import com.weiwan.dsp.core.resolve.DspUnresolvedDataCollector;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author: xiaozhennan
 * @date: 2021/6/2 14:51
 * @description:
 */
public class HttpUnresolvedDataCollector extends DspUnresolvedDataCollector<HttpUnresolvedConfig> {

    private static final Logger logger = LoggerFactory.getLogger(HttpUnresolvedDataCollector.class);

    private String hostname;
    private Integer port;
    private String url;
    private String reportApi;
    private HttpClient httpClient;
    private static final String REPORT_API_FORMAT = "http://%s:%s%s";
    private static final String SIGN = "SIGN";
    private static final String SIGN_HEADER_KEY = "Dsp-Report-Sign";

    @Override
    public void init(HttpUnresolvedConfig collectorConfig) {
        this.hostname = collectorConfig.getHostname();
        this.port = collectorConfig.getPort();
        this.url = collectorConfig.getUrl();
        if (StringUtils.startsWith(url, "/")) {
            this.reportApi = String.format(REPORT_API_FORMAT, hostname, port, url);
        } else {
            this.reportApi = url;
        }
        this.httpClient = HttpClients.createDefault();
    }

    @Override
    public void handler(UnresolvedDataRecord record) {
        /**
         * 1. 需要有一个controller来接受未解析的数据
         * 2. 需要有一张数据表来记录未解析的数据dsp_application_unresolved_log
         * 3. 需要在handler方法中把数据发送给controller
         *      1. 采用httpclient的方式发送请求给controller
         *      2. 使用post请求类型
         *      3. 请求格式: http://hostname:port + url
         * 4. 需要在HttpUnresolvedConfig中添加配置的选项,方便渲染在页面上
         *      需要配置的内容:
         *          1. ip
         *          2. 端口
         *          3. 请求地址
         */
        //校验record是否为空
        if (record == null) {
            if (logger.isDebugEnabled()) {
                logger.debug("unresolved record is null");
            }
            return;
        }
        //如果不为空, 把record转成json
        String jsonRecord = record.toString();
        //创建post对象
        HttpPost req = new HttpPost(reportApi);
        try {
            req.setHeader(SIGN_HEADER_KEY, SIGN);
            req.addHeader("Content-type", "application/json");
            req.setEntity(new StringEntity(jsonRecord));
            HttpResponse response = httpClient.execute(req);
        } catch (Exception e) {
            logger.error("failed to report unresolved record: {}", jsonRecord, e);
        }
    }

    @Override
    public void stop() {
        this.httpClient = null;
    }
}
