package cn.enilu.elm.api.controller;

import cn.enilu.elm.api.repository.BaseDao;
import cn.enilu.elm.api.service.PositionService;
import cn.enilu.elm.api.vo.CityInfo;
import cn.enilu.elm.api.vo.Constants;
import cn.enilu.elm.api.vo.Rets;
import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

/**
 * Created  on 2017/12/29 0029.
 *
 * @author zt
 */
@RestController
public class PositionController extends BaseController {
    @Autowired
    private BaseDao baseDao;
    @Autowired
    private PositionService positionService;

    @RequestMapping(value = "/v1/cities", method = RequestMethod.GET)

    public Object cities(@RequestParam("type") String type) {
        if (Constants.CityType.guess.name().equals(type)) {
            CityInfo cityInfo = positionService.getPostion(getIp());
            String city = cityInfo.getCity();
            if (Strings.isNullOrEmpty(city)) {
                return Rets.failure();
            }
            return positionService.findByName(city);
        }

        Map result = baseDao.findOne("cities");
        Optional<Map> data = Optional.ofNullable(result).map(re -> (Map) re.get("data"));
        switch (type) {
            case "hot":
                return data.map(re -> re.get("hotCities")).orElse(null);
            case "group":
                return data.orElse(null);
            default:
                break;
        }
        return Rets.failure();

    }

    @RequestMapping(value = "/v1/cities/{id}", method = RequestMethod.GET)

    public Object getCity(@PathVariable("id") Integer id) {
        return positionService.findById(id);
    }

    @RequestMapping(value = "/v1/pois", method = RequestMethod.GET)

    public Object getPoiByCityAndKeyword(@RequestParam(value = "type", defaultValue = "search") String type,
                                         @RequestParam("city_id") Integer cityId,
                                         @RequestParam("keyword") String keyword) {

        Map map = positionService.findById(cityId);
        return positionService.searchPlace(map.get("name").toString(), keyword);
    }

    //todo 未完成
    @RequestMapping(value = "/v2/pois/{geoHash}", method = RequestMethod.GET)

    public Object getPoiByGeoHash(@PathVariable("geoHash") String geoHash) {
        return Rets.failure();
    }
}
