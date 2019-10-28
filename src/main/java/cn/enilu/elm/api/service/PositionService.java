package cn.enilu.elm.api.service;

import cn.enilu.elm.api.repository.BaseDao;
import cn.enilu.elm.api.utils.AppConfiguration;
import cn.enilu.elm.api.utils.HttpClients;
import cn.enilu.elm.api.utils.Maps;
import cn.enilu.elm.api.vo.CityInfo;
import org.nutz.json.Json;
import org.nutz.mapl.Mapl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.util.List;
import java.util.Map;


/**
 * Created  on 2017/12/29 0029.
 *
 * @author zt
 */
@Service
public class PositionService {
    private Logger logger = LoggerFactory.getLogger(PositionService.class);
    @Autowired
    private AppConfiguration appConfiguration;
    @Autowired
    private BaseDao baseDao;

    public CityInfo getPostion(String ip) {
        Map<String, String> map = Maps.newHashMap();
        map.put("ip", ip);
        map.put("key", appConfiguration.getTencentKey());
        Map result = null;
        try {
            String str = HttpClients.get(appConfiguration.getApiQqGetLocation(), map);
            result = (Map) Json.fromJson(str);// new JsonParser().parse(str).getAsJsonObject();
        } catch (Exception e) {
            logger.error("获取地理位置异常", e);
        }
        if (result == null || Integer.valueOf(result.get("status").toString()) != 0) {
            try {
                map.put("key", appConfiguration.getTencentKey2());
                String str = HttpClients.get(appConfiguration.getApiQqGetLocation(), map);
                result = (Map) Json.fromJson(str);
            } catch (Exception e) {
                logger.error("获取地理位置异常", e);
            }
        }
        if (result == null || Integer.valueOf(result.get("status").toString()) != 0) {
            try {
                map.put("key", appConfiguration.getTencentKey3());
                String str = HttpClients.get(appConfiguration.getApiQqGetLocation(), map);
                result = (Map) Json.fromJson(str);
            } catch (Exception e) {
                logger.error("获取地理位置异常", e);
            }

        }
        if (Integer.valueOf(result.get("status").toString()) == 0) {
            Map resultData = (Map) result.get("result");

            String lat = String.valueOf(Mapl.cell(resultData, "location.lat"));
            String lng = String.valueOf(Mapl.cell(resultData, "location.lng"));
            String city = (String) Mapl.cell(resultData, "ad_info.city");
            city = city.replace("市", "");
            CityInfo cityInfo = new CityInfo();
            cityInfo.setCity(city);
            cityInfo.setLat(lat);
            cityInfo.setLng(lng);
            return cityInfo;

        }
        return null;
    }

    public List searchPlace(String cityName, String keyword) {
        Map<String, String> params = Maps.newHashMap();
        params.put("key", appConfiguration.getTencentKey());
        params.put("keyword", URLEncoder.encode(keyword));
        params.put("boundary", "region(" + URLEncoder.encode(cityName) + ",0)");
        params.put("page_size", "10");
        try {
            String str = HttpClients.get(appConfiguration.getApiQqSearchPlace(), params);
            Map result = (Map) Json.fromJson(str);
            if (Integer.valueOf(result.get("status").toString()).intValue() == 0) {
                return (List) result.get("data");
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return null;

    }

    public Map findById(Integer id) {
        Map cities = baseDao.findOne("cities");
        Map<String, List> data = (Map) cities.get("data");
        Map result = null;
        for (Map.Entry<String, List> entry : data.entrySet()) {
            List list = entry.getValue();
            for (int i = 0; i < list.size(); i++) {
                Map rec = (Map) list.get(i);
                if (id == Double.valueOf(rec.get("id").toString()).intValue()) {
                    result = rec;
                    break;
                }
            }
        }
        return result;
    }

    public Map findByName(String cityName) {
        Map cities = baseDao.findOne("cities");
        if (Maps.isEmpty(cities)) {
            return null;
        }
        Map<String, List> data = (Map) cities.get("data");
        Map result = null;
        for (Map.Entry<String, List> entry : data.entrySet()) {
            List list = entry.getValue();
            for (int i = 0; i < list.size(); i++) {
                Map rec = (Map) list.get(i);
                if (cityName.equals(rec.get("name"))) {
                    result = rec;
                    break;
                }
            }
        }
        return result;
    }

}
