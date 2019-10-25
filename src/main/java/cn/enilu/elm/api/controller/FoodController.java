package cn.enilu.elm.api.controller;

import cn.enilu.elm.api.entity.Food;
import cn.enilu.elm.api.entity.Ids;
import cn.enilu.elm.api.entity.KeyValue;
import cn.enilu.elm.api.entity.SpecFood;
import cn.enilu.elm.api.repository.BaseDao;
import cn.enilu.elm.api.service.IdsService;
import cn.enilu.elm.api.utils.Maps;
import cn.enilu.elm.api.vo.Rets;
import com.google.common.base.Strings;
import org.nutz.json.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created  on 2017/12/29 0029.
 *
 * @author zt
 */
@RestController
@RequestMapping("/shopping")
public class FoodController extends BaseController {

    @Autowired
    private BaseDao baseDao;

    @Autowired
    private IdsService idsService;

    @RequestMapping(value = "addfood",method = RequestMethod.GET)

    public Object add(HttpServletRequest request) {
        String json = getRequestPayload();
        Food food = Json.fromJson(Food.class, json);
        food.setItem_id(idsService.getId(Ids.ITEM_ID));
        List<SpecFood> specFoods = new ArrayList<SpecFood>(2);
        specFoods.add(buidSpecFood(food));
        food.setSpecfoods(specFoods);
        setTips(food);
        food.setSatisfy_rate(new BigDecimal(Math.ceil(Math.random() * 100)).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue());
        food.setSatisfy_count(new BigDecimal(Math.ceil(Math.random() * 1000)).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue());
        food.setRating(new BigDecimal(Math.random() * 5).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue());
        baseDao.save(food);
        return Rets.success();
    }

    @RequestMapping(value = "/v2/foods", method = RequestMethod.GET)
    public Object list(@RequestParam("restaurant_id") String restaurantId,
                       @RequestParam(value = "offset", defaultValue = "0") Integer offset,
                       @RequestParam(value = "limit", defaultValue = "20") Integer limit) {
        restaurantId = "11";
        if (Strings.isNullOrEmpty(restaurantId) || "undefined".equals(restaurantId)) {
            return baseDao.findAll(Food.class);
        } else {
            return baseDao.findAll(Food.class, "restaurant_id", Long.valueOf(restaurantId));
        }
    }

    @RequestMapping(value = "/v2/foods/count", method = RequestMethod.GET)

    public Object count() {
        long count = baseDao.count("foods");
        return Rets.success("count", count);
    }

    @RequestMapping(value = "/v2/food/{id}", method = RequestMethod.DELETE)

    public Object delete(@PathVariable("id") Long id) {
        baseDao.delete("foods", Maps.newHashMap("item_id", id));
        return Rets.success();
    }

    //todo 未完成
    @RequestMapping(value = "/v2/updatefood", method = RequestMethod.POST)
    public Object update(HttpServletRequest request) {
        Map<String, Object> data = getRequestPayload(Map.class);
        System.out.println(Json.toJson(data));
        return Rets.success();
    }


    private void setTips(Food food) {
        Double ratingCount = Math.ceil(Math.random() * 1000);
        Double monthSales = Math.ceil(Math.random() * 1000);
        food.setRating_count(ratingCount.intValue());
        food.setMonth_sales(monthSales.intValue());
        food.setTips(ratingCount.intValue() + "评价 月售" + monthSales.intValue() + "份");


    }

    private SpecFood buidSpecFood(Food food) {
        SpecFood specFood = new SpecFood();
        specFood.setItem_id(food.getItem_id());
        specFood.setFood_id(idsService.getId(Ids.FOOD_ID));
        specFood.setName(food.getName());
        specFood.setRestaurant_id(food.getRestaurant_id());
        BigDecimal recentRating = new BigDecimal(Math.random() * 5).setScale(BigDecimal.ROUND_HALF_DOWN, 1);
        specFood.setRecent_rating(recentRating.doubleValue());
        BigDecimal recentPopularity = new BigDecimal(Math.random() * 1000).setScale(BigDecimal.ROUND_HALF_DOWN, 1);
        specFood.setRecent_popularity(recentPopularity.doubleValue());
        specFood.setSpecs(new ArrayList<KeyValue>());
        return specFood;

    }


}
